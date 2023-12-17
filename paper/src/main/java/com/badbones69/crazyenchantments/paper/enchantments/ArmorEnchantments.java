package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.PluginSupport;
import com.badbones69.crazyenchantments.paper.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.events.AuraActiveEvent;
import com.badbones69.crazyenchantments.paper.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.paper.api.managers.ArmorEnchantmentManager;
import com.badbones69.crazyenchantments.paper.api.objects.ArmorEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.PotionEffects;
import com.badbones69.crazyenchantments.paper.api.support.anticheats.NoCheatPlusSupport;
import com.badbones69.crazyenchantments.paper.api.support.anticheats.SpartanSupport;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.paper.processors.ArmorMoveProcessor;
import com.badbones69.crazyenchantments.paper.processors.Processor;
import com.badbones69.crazyenchantments.paper.utilities.misc.EnchantUtils;
import com.badbones69.crazyenchantments.paper.utilities.misc.EventUtils;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.*;

public class ArmorEnchantments implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    // Settings.
    private final ProtectionCrystalSettings protectionCrystalSettings = starter.getProtectionCrystalSettings();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();
    private final EnchantmentSettings enchantmentSettings = starter.getEnchantmentSettings();

    // Plugin Support.
    private final NoCheatPlusSupport noCheatPlusSupport = starter.getNoCheatPlusSupport();
    private final SpartanSupport spartanSupport = starter.getSpartanSupport();

    private final PluginSupport pluginSupport = starter.getPluginSupport();

    // Plugin Managers.
    private final ArmorEnchantmentManager armorEnchantmentManager = starter.getArmorEnchantmentManager();

    private final Processor<PlayerMoveEvent> armorMoveProcessor = new ArmorMoveProcessor();

    public ArmorEnchantments() {
        armorMoveProcessor.start();
    }

    public void stop() {
        armorMoveProcessor.stop();
    }

    @EventHandler
    public void onEquip(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = event.getNewItem();
        ItemStack oldItem = event.getOldItem();

        if (newItem.hasItemMeta() // Added to prevent armor change event being called on damage.
            && oldItem.hasItemMeta()
            && newItem.getItemMeta().getPersistentDataContainer().has(DataKeys.ENCHANTMENTS.getKey())
            && oldItem.getItemMeta().getPersistentDataContainer().has(DataKeys.ENCHANTMENTS.getKey())
            && Objects.equals(newItem.getItemMeta().getPersistentDataContainer().get(DataKeys.ENCHANTMENTS.getKey(), PersistentDataType.STRING),
                              oldItem.getItemMeta().getPersistentDataContainer().get(DataKeys.ENCHANTMENTS.getKey(), PersistentDataType.STRING))
        ) return;

        if (enchantmentBookSettings.hasEnchantments(oldItem)) { // Removing the potion effects.
            for (CEnchantments enchantment : crazyManager.getEnchantmentPotions().keySet()) {
                if (enchantment.isActivated() && enchantmentBookSettings.hasEnchantment(oldItem, enchantment.getEnchantment())) {
                    Map<PotionEffectType, Integer> effects = crazyManager.getUpdatedEffects(player, new ItemStack(Material.AIR), oldItem, enchantment);
                    methods.checkPotions(effects, player);
                }
            }
        }

        if (enchantmentBookSettings.hasEnchantments(newItem)) { // Adding the potion effects.
            for (CEnchantments enchantment : crazyManager.getEnchantmentPotions().keySet()) {
                if (enchantment.isActivated() && enchantmentBookSettings.hasEnchantment(newItem, enchantment.getEnchantment())) {
                    Map<PotionEffectType, Integer> effects = crazyManager.getUpdatedEffects(player, newItem, oldItem, enchantment);

                    EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(player, enchantment.getEnchantment(), newItem);
                    plugin.getPluginManager().callEvent(enchantmentUseEvent);
                    if (!enchantmentUseEvent.isCancelled()) methods.checkPotions(effects, player);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (EventUtils.isIgnoredEvent(event) || EventUtils.isIgnoredUUID(event.getDamager().getUniqueId())) return;
        if (pluginSupport.isFriendly(event.getDamager(), event.getEntity())) return;

        if (!(event.getDamager() instanceof LivingEntity damager) || !(event.getEntity() instanceof Player player)) return;

        for (ItemStack armor : player.getEquipment().getArmorContents()) {
            if (!enchantmentBookSettings.hasEnchantments(armor)) continue;
            Map<CEnchantment, Integer> enchants = enchantmentBookSettings.getEnchantments(armor);

            for (ArmorEnchantment armorEnchantment : armorEnchantmentManager.getArmorEnchantments()) {
                CEnchantments enchantment = armorEnchantment.getEnchantment();

                if (EnchantUtils.isEventActive(enchantment, player, armor, enchants)) {

                    if (armorEnchantment.isPotionEnchantment()) {
                        for (PotionEffects effect : armorEnchantment.getPotionEffects()) {
                            damager.addPotionEffect(new PotionEffect(effect.potionEffect(), effect.duration(), (armorEnchantment.isLevelAddedToAmplifier() ? crazyManager.getLevel(armor, enchantment) : 0) + effect.amplifier()));
                        }
                    } else {
                        event.setDamage(event.getDamage() * ((armorEnchantment.isLevelAddedToAmplifier() ? crazyManager.getLevel(armor, enchantment) : 0) + armorEnchantment.getDamageAmplifier()));
                    }
                }
            }

            if (EnchantUtils.isEventActive(CEnchantments.MANEUVER, player, armor, enchants)) {
                event.setCancelled(true);
                return;
            }

            if (player.isSneaking() && EnchantUtils.isEventActive(CEnchantments.CROUCH, player, armor, enchants)) {
                double percentageReduced = (CEnchantments.CROUCH.getChance() + (CEnchantments.CROUCH.getChanceIncrease() * enchants.get(CEnchantments.CROUCH.getEnchantment()))) / 100.0;
                double newDamage = event.getFinalDamage() * (1 - percentageReduced);

                if (newDamage < 0) newDamage = 0;
                event.setDamage(newDamage);
            }

            if (EnchantUtils.isEventActive(CEnchantments.SHOCKWAVE, player, armor, enchants)) {
                damager.setVelocity(player.getLocation().getDirection().multiply(2).setY(1.25));

            }

            if (player.getHealth() <= event.getFinalDamage() && EnchantUtils.isEventActive(CEnchantments.SYSTEMREBOOT, player, armor, enchants)) {
                player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                event.setCancelled(true);
                return;
            }

            if (player.getHealth() <= 4 && EnchantUtils.isEventActive(CEnchantments.ADRENALINE, player, armor, enchants)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3 + (crazyManager.getLevel(armor, CEnchantments.ADRENALINE)) * 20, 10));
            }

            if (player.getHealth() <= 8 && EnchantUtils.isEventActive(CEnchantments.ROCKET, player, armor, enchants)) {
                // Anti cheat support here with AAC or any others.
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.setVelocity(player.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize().setY(1)), 1);
                enchantmentSettings.addFallenPlayer(player);
                player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation(), 1);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> enchantmentSettings.removeFallenPlayer(player), 8 * 20);
            }

            if (player.getHealth() > 0 && EnchantUtils.isEventActive(CEnchantments.ENLIGHTENED, player, armor, enchants)) {

                double heal = crazyManager.getLevel(armor, CEnchantments.ENLIGHTENED);
                // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
                double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

                if (player.getHealth() + heal < maxHealth) player.setHealth(player.getHealth() + heal);

                if (player.getHealth() + heal >= maxHealth) player.setHealth(maxHealth);
            }

            if (EnchantUtils.isEventActive(CEnchantments.INSOMNIA, player, armor, enchants)) damager.damage(event.getDamage() + crazyManager.getLevel(armor, CEnchantments.INSOMNIA));

            if (EnchantUtils.isEventActive(CEnchantments.MOLTEN, player, armor, enchants)) damager.setFireTicks((crazyManager.getLevel(armor, CEnchantments.MOLTEN) * 2) * 20);

            if (EnchantUtils.isEventActive(CEnchantments.SAVIOR, player, armor, enchants)) event.setDamage(event.getDamage() / 2);

            if (EnchantUtils.isEventActive(CEnchantments.CACTUS, player, armor, enchants)) damager.damage(crazyManager.getLevel(armor, CEnchantments.CACTUS));

            if (EnchantUtils.isEventActive(CEnchantments.STORMCALLER, player, armor, enchants)) {

                methods.lightning(damager);
                // AntiCheat Support.
                if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) noCheatPlusSupport.allowPlayer(player);

                if (SupportedPlugins.SPARTAN.isPluginLoaded()) spartanSupport.cancelNoSwing(player);

                for (LivingEntity en : methods.getNearbyLivingEntities(2D, player)) {
                    EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(player, en, DamageCause.CUSTOM, 5D);
                    methods.entityEvent(player, en, damageByEntityEvent);
                }

                if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) noCheatPlusSupport.allowPlayer(player);

                damager.damage(5D);
            }
        }

        if (!(damager instanceof Player)) return;

        for (ItemStack armor : Objects.requireNonNull(damager.getEquipment()).getArmorContents()) {

            Map<CEnchantment, Integer> enchants = enchantmentBookSettings.getEnchantments(armor);
            if (!enchants.containsKey(CEnchantments.LEADERSHIP.getEnchantment())) continue;

            int radius = 4 + enchants.get(CEnchantments.LEADERSHIP.getEnchantment());
            int players = (int) damager.getNearbyEntities(radius, radius, radius).stream().filter(entity -> entity instanceof Player && pluginSupport.isFriendly(damager, entity)).count();

            if (players > 0 && EnchantUtils.isEventActive(CEnchantments.LEADERSHIP, player, armor, enchants)) {
                event.setDamage(event.getDamage() + (players / 2d));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAura(AuraActiveEvent event) {
        Player player = event.getPlayer();
        Player other = event.getOther();

        if (!player.canSee(other) || !other.canSee(player)) return;
        if (pluginSupport.isVanished(player) || pluginSupport.isVanished(other)) return;

        CEnchantments enchant = event.getEnchantment();
        int level = event.getLevel();

        if (!pluginSupport.allowCombat(other.getLocation()) || pluginSupport.isFriendly(player, other) || methods.hasPermission(other, "bypass.aura", false)) return;

        Calendar cal = Calendar.getInstance();

        HashMap<CEnchantments, Calendar> finalEffect = enchantmentSettings.containsTimerPlayer(other) ? enchantmentSettings.getTimerPlayer(other) : new HashMap<>();

        Map<CEnchantment, Integer> enchantments = Map.of(enchant.getEnchantment(), level);

        switch (enchant) {
            case BLIZZARD -> {
                if (EnchantUtils.isAuraActive(player, enchant, enchantments)) other.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 20, level - 1));'
            }

            case INTIMIDATE -> {
                if (EnchantUtils.isAuraActive(player, enchant, enchantments)) other.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 3 * 20, level - 1));
            }

            case ACIDRAIN -> {
                if (EnchantUtils.isAuraActive(player, enchant, enchantments) && timerStuff(cal, other, enchant)) {
                    other.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 4 * 20, 1));
                    int time = 35 - (level * 5);
                    cal.add(Calendar.SECOND, time > 0 ? time : 5);
                    finalEffect.put(enchant, cal);
                }
            }

            case SANDSTORM -> {
                if (EnchantUtils.isAuraActive(player, enchant, enchantments) && timerStuff(cal, other, enchant)) {
                    other.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 0));
                    int time = 35 - (level * 5);
                    cal.add(Calendar.SECOND, time > 0 ? time : 5);
                    finalEffect.put(enchant, cal);
                }
            }

            case RADIANT -> {
                if (EnchantUtils.isAuraActive(player, enchant, enchantments) && timerStuff(cal, other, enchant)) {
                    other.setFireTicks(5 * 20);
                    int time = 20 - (level * 5);
                    cal.add(Calendar.SECOND, Math.max(time, 0));
                    finalEffect.put(enchant, cal);
                }
            }

            default -> {}
        }

        enchantmentSettings.addTimerPlayer(player, finalEffect); // TODO Recheck usage and fix it.
    }

    private boolean timerStuff(Calendar cal, Player other, CEnchantments enchant) { // No clue what this did, so just moved it as it was a large mess. -TDL
        return (!enchantmentSettings.containsTimerPlayer(other) ||
                (enchantmentSettings.containsTimerPlayer(other) && !enchantmentSettings.getTimerPlayer(other).containsKey(enchant)) ||
                (enchantmentSettings.containsTimerPlayer(other) && enchantmentSettings.getTimerPlayer(other).containsKey(enchant) &&
                        cal.after(enchantmentSettings.getTimerPlayer(other).get(enchant))));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMovement(PlayerMoveEvent event) {
        if (event.getFrom() == event.getTo()) return;

        armorMoveProcessor.add(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getKiller() == null) return;

        Player killer = player.getKiller();

        if (!pluginSupport.allowCombat(player.getLocation())) return;

        if (CEnchantments.SELFDESTRUCT.isActivated()) {
            for (ItemStack item : Objects.requireNonNull(player.getEquipment()).getArmorContents()) {
                if (enchantmentBookSettings.hasEnchantments(item) && enchantmentBookSettings.hasEnchantment(item, CEnchantments.SELFDESTRUCT.getEnchantment())) {

                    EnchantmentUseEvent useEvent = new EnchantmentUseEvent(player, CEnchantments.SELFDESTRUCT.getEnchantment(), item);
                    plugin.getServer().getPluginManager().callEvent(useEvent);

                    if (!useEvent.isCancelled()) {
                        methods.explode(player);
                        List<ItemStack> items = new ArrayList<>();

                        for (ItemStack drop : event.getDrops()) {
                            if (drop != null && ProtectionCrystalSettings.isProtected(drop) && protectionCrystalSettings.isProtectionSuccessful(player)) items.add(drop);
                        }

                        event.getDrops().clear();
                        event.getDrops().addAll(items);
                    }
                }
            }
        }

        if (CEnchantments.RECOVER.isActivated()) {
            for (ItemStack item : Objects.requireNonNull(killer.getEquipment()).getArmorContents()) {
                if (enchantmentBookSettings.hasEnchantments(item) && crazyManager.hasEnchantment(item, CEnchantments.RECOVER)) {
                    EnchantmentUseEvent useEvent = new EnchantmentUseEvent(player, CEnchantments.RECOVER.getEnchantment(), item);
                    plugin.getServer().getPluginManager().callEvent(useEvent);

                    if (!useEvent.isCancelled()) {
                        killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 8 * 20, 2));
                        killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 1));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!enchantmentSettings.containsFallenPlayer(player)) return;
        if (!DamageCause.FALL.equals(event.getCause())) return;

        event.setCancelled(true);
    }

}
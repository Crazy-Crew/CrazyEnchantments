package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.events.AuraActiveEvent;
import com.badbones69.crazyenchantments.paper.api.managers.ArmorEnchantmentManager;
import com.badbones69.crazyenchantments.paper.api.objects.ArmorEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.PotionEffects;
import com.badbones69.crazyenchantments.paper.api.utils.EnchantUtils;
import com.badbones69.crazyenchantments.paper.api.utils.EventUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.paper.support.PluginSupport;
import com.badbones69.crazyenchantments.paper.tasks.processors.ArmorProcessor;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.stream.Collectors;

public class ArmorEnchantments implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this.starter.getMethods();

    @NotNull
    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    // Settings.
    @NotNull
    private final ProtectionCrystalSettings protectionCrystalSettings = this.starter.getProtectionCrystalSettings();

    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    // Plugin Support.
    @NotNull
    private final PluginSupport pluginSupport = this.starter.getPluginSupport();

    // Plugin Managers.
    @NotNull
    private final ArmorEnchantmentManager armorEnchantmentManager = this.starter.getArmorEnchantmentManager();

    private final ArmorProcessor armorProcessor = new ArmorProcessor();

    private final List<UUID> fallenPlayers = new ArrayList<>();

    private final UUID HEALTH_MODIFIER_UUID = UUID.fromString("439c7897-8c83-4528-ad54-251099622807");
    private final UUID SPEED_MODIFIER_UUID = UUID.fromString("1f49ea04-8aac-4a37-a927-08f2dc9afa19");
    private final UUID DAMAGE_MODIFIER_UUID = UUID.fromString("2263841f-1bb6-4cfa-947f-1b07c6e651d2");

    private final String HEALTH_MODIFIER_KEY = "ce_health_boost";
    private final String SPEED_MODIFIER_KEY = "ce_speed_boost";
    private final String DAMAGE_MODIFIER_KEY = "ce_damage_boost";

    public ArmorEnchantments() {
        armorProcessor.start();
    }

    public void stop() {
        armorProcessor.stop();
    }

    @EventHandler
    public void onDeath(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        player.getScheduler().runDelayed(this.plugin, (task) -> {
            updateAllEffects(player);
        }, null, 10);
    }

    @EventHandler
    public void onEquip(EntityEquipmentChangedEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        final NamespacedKey key = DataKeys.enchantments.getNamespacedKey();

        event.getEquipmentChanges().forEach((slot, action) -> {
            if (slot.isHand()) return;

            final ItemStack newItem = action.newItem();
            final ItemStack oldItem = action.oldItem();

            final PersistentDataContainerView newView = newItem.getPersistentDataContainer();
            final PersistentDataContainerView oldView = oldItem.getPersistentDataContainer();

            if (!newView.has(key) && !oldView.has(key)) return;

            if (Objects.equals(newView.get(key, PersistentDataType.STRING), oldView.get(key, PersistentDataType.STRING))) return;

            updateEffects(player, newItem, oldItem);
        });
    }

    /**
     * Force update all effects for a player.
     */
    public void updateAllEffects(@NotNull Player player) {
        final Map<CEnchantment, Integer> topEnchants = getUpperEnchants(player);
        for (final Map.Entry<PotionEffectType, Integer> effect : getTopPotionEffects(topEnchants).entrySet()) {
            player.addPotionEffect(new PotionEffect(effect.getKey(), PotionEffect.INFINITE_DURATION, effect.getValue() - 1));
        }
        updateAttributes(player, topEnchants);
    }

    /**
     * Checks the players current armor and updates any needed effects that are created by CrazyEnchantments.
     * Removes all effects that should no longer be on the player and adds the highest level for the others
     * based on their armor.
     * @param player The player for whom to update effects.
     * @param newItem The new item equipped.
     * @param oldItem The item that had previously been equipped.
     */
    private void updateEffects(@NotNull Player player, @NotNull ItemStack newItem, @NotNull ItemStack oldItem) {
        final Map<CEnchantment, Integer> topEnchants = getCurrentEnchants(player, newItem);

        // Remove all effects that they no longer should have from the armor.
        if (!oldItem.isEmpty()) {
            getTopPotionEffects(this.enchantmentBookSettings.getEnchantments(oldItem)
                    .entrySet().stream()
                    .filter(enchant -> !topEnchants.containsKey(enchant.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b)))
                    .keySet()
                    .forEach(player::removePotionEffect);
        }

        // Add all new effects that said player should now have.
        for (final Map.Entry<PotionEffectType, Integer> effect : getTopPotionEffects(topEnchants).entrySet()) {
            for (final PotionEffect currentEffect : player.getActivePotionEffects()) {
                if (!currentEffect.getType().equals(effect.getKey())) continue;
                if (currentEffect.getAmplifier() >= effect.getValue() - 1) break;

                player.removePotionEffect(effect.getKey());

                break;
            }

            player.addPotionEffect(new PotionEffect(effect.getKey(), PotionEffect.INFINITE_DURATION, effect.getValue() - 1));
        }

        updateAttributes(player, topEnchants);
    }

    private void updateAttributes(Player player, Map<CEnchantment, Integer> enchants) {
        double healthValue = 0;
        if (enchants.containsKey(CEnchantments.OVERLOAD.getEnchantment())) {
            healthValue += enchants.get(CEnchantments.OVERLOAD.getEnchantment()) * 4.0;
        }
        if (enchants.containsKey(CEnchantments.NINJA.getEnchantment())) {
            healthValue += enchants.get(CEnchantments.NINJA.getEnchantment()) * 4.0;
        }
        applyCrossVersionModifier(player, getHealthAttribute(), HEALTH_MODIFIER_KEY, HEALTH_MODIFIER_UUID, healthValue);

        double speedValue = 0;
        if (enchants.containsKey(CEnchantments.GEARS.getEnchantment())) {
            speedValue += enchants.get(CEnchantments.GEARS.getEnchantment()) * 0.02;
        }
        if (enchants.containsKey(CEnchantments.NINJA.getEnchantment())) {
            speedValue += enchants.get(CEnchantments.NINJA.getEnchantment()) * 0.02;
        }
        if (enchants.containsKey(CEnchantments.CYBORG.getEnchantment())) {
            speedValue += 0.02;
        }
        applyCrossVersionModifier(player, getSpeedAttribute(), SPEED_MODIFIER_KEY, SPEED_MODIFIER_UUID, speedValue);

        double damageValue = 0;
        if (enchants.containsKey(CEnchantments.HULK.getEnchantment())) {
            damageValue += enchants.get(CEnchantments.HULK.getEnchantment()) * 3.0;
        }
        if (enchants.containsKey(CEnchantments.DRUNK.getEnchantment())) {
            damageValue += enchants.get(CEnchantments.DRUNK.getEnchantment()) * 3.0;
        }
        if (enchants.containsKey(CEnchantments.CYBORG.getEnchantment())) {
            damageValue += 3.0;
        }
        applyCrossVersionModifier(player, getDamageAttribute(), DAMAGE_MODIFIER_KEY, DAMAGE_MODIFIER_UUID, damageValue);

        double maxHP = player.getAttribute(getHealthAttribute()).getValue();
        if (player.getHealth() > maxHP) {
            player.setHealth(maxHP);
        }
    }

    private Attribute getHealthAttribute() {
        try { return Attribute.valueOf("MAX_HEALTH"); } catch (Exception e) { return Attribute.valueOf("GENERIC_MAX_HEALTH"); }
    }

    private Attribute getSpeedAttribute() {
        try { return Attribute.valueOf("MOVEMENT_SPEED"); } catch (Exception e) { return Attribute.valueOf("GENERIC_MOVEMENT_SPEED"); }
    }

    private Attribute getDamageAttribute() {
        try { return Attribute.valueOf("ATTACK_DAMAGE"); } catch (Exception e) { return Attribute.valueOf("GENERIC_ATTACK_DAMAGE"); }
    }

    private void applyCrossVersionModifier(Player player, Attribute attribute, String keyName, UUID legacyUuid, double value) {
        var attributeInstance = player.getAttribute(attribute);
        if (attributeInstance == null) return;

        attributeInstance.getModifiers().forEach(modifier -> {
            try {
                if (modifier.getKey().getKey().equals(keyName)) {
                    attributeInstance.removeModifier(modifier);
                }
            } catch (NoSuchMethodError e) {
                if (modifier.getUniqueId().equals(legacyUuid) || modifier.getName().equals(keyName)) {
                    attributeInstance.removeModifier(modifier);
                }
            }
        });

        if (value > 0) {
            AttributeModifier modifier;
            try {
                modifier = new AttributeModifier(new NamespacedKey(plugin, keyName), value, AttributeModifier.Operation.ADD_NUMBER);
            } catch (NoClassDefFoundError | NoSuchMethodError e) {
                modifier = new AttributeModifier(legacyUuid, keyName, value, AttributeModifier.Operation.ADD_NUMBER);
            }
            attributeInstance.addModifier(modifier);
        }
    }

    /**
     * Pulls the data off of all of the enchantments provided and filters out the worst ones.
     * @param topEnchants A list of {@link CEnchantment}'s to filter.
     * @return Returns a list of top potion effects from the provided list of enchantments.
     */
    @NotNull
    private Map<PotionEffectType, Integer> getTopPotionEffects(@NotNull Map<CEnchantment, Integer> topEnchants) {
        Map<CEnchantments, HashMap<PotionEffectType, Integer>> enchantmentPotions = this.crazyManager.getEnchantmentPotions();
        HashMap<PotionEffectType, Integer> topPotions = new HashMap<>();

        topEnchants.forEach((key, value) -> enchantmentPotions.entrySet()
                .stream().filter(enchantedPotion -> enchantedPotion.getKey().getEnchantment().equals(key))
                .forEach(enchantedPotion -> enchantedPotion.getValue().entrySet().stream()
                        .filter(pot -> !topPotions.containsKey(pot.getKey()) || (topPotions.get(pot.getKey()) != -1 && topPotions.get(pot.getKey()) <= pot.getValue()))
                        .forEach(pot -> topPotions.put(pot.getKey(), value))));

        return topPotions;
    }

    /**
     *
     * @param player The player to check.
     * @param newItem The equipped item.
     * @return Returns a map of all current active enchants on the specified player.
     */
    @NotNull
    private Map<CEnchantment, Integer> getCurrentEnchants(@NotNull Player player, @NotNull ItemStack newItem) {
        Map<CEnchantment, Integer> toAdd = getUpperEnchants(player);

        if (!newItem.isEmpty()) {
            this.enchantmentBookSettings.getEnchantments(newItem).entrySet().stream()
                    .filter(ench -> !toAdd.containsKey(ench.getKey()) || toAdd.get(ench.getKey()) <= ench.getValue())
                    .filter(ench -> EnchantUtils.isArmorEventActive(player, CEnchantments.valueOf(ench.getKey().getName().toUpperCase().replace("-", "_")), newItem))
                    .forEach(ench -> toAdd.put(ench.getKey(), ench.getValue()));
        }

        return toAdd;
    }

    /**
     *
     * @param player The player to check for {@link CEnchantments}.
     * @return A list of {@link CEnchantments}'s on the player.
     */
    @NotNull
    private HashMap<CEnchantment, Integer> getUpperEnchants(@NotNull Player player) {
        HashMap<CEnchantment, Integer> topEnchants = new HashMap<>();

        Arrays.stream(player.getEquipment().getArmorContents())
                .filter(Objects::nonNull)
                .map(this.enchantmentBookSettings::getEnchantments)
                .forEach(enchantments -> enchantments.entrySet().stream()
                        .filter(ench -> !topEnchants.containsKey(ench.getKey()) || topEnchants.get(ench.getKey()) <= ench.getValue())
                        .forEach(ench -> topEnchants.put(ench.getKey(), ench.getValue())));
        return topEnchants;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (EventUtils.isIgnoredEvent(event) || EventUtils.isIgnoredUUID(event.getDamager().getUniqueId())) return;
        if (this.pluginSupport.isFriendly(event.getDamager(), event.getEntity())) return;

        if (!(event.getDamager() instanceof LivingEntity damager) || !(event.getEntity() instanceof Player player)) return;

        for (ItemStack armor : player.getEquipment().getArmorContents()) {
            Map<CEnchantment, Integer> enchants = this.enchantmentBookSettings.getEnchantments(armor);
            if (enchants.isEmpty()) continue;

            for (ArmorEnchantment armorEnchantment : this.armorEnchantmentManager.getArmorEnchantments()) {
                CEnchantments enchantment = armorEnchantment.getEnchantment();

                if (EnchantUtils.isEventActive(enchantment, player, armor, enchants)) {

                    if (armorEnchantment.isPotionEnchantment()) {
                        for (PotionEffects effect : armorEnchantment.getPotionEffects()) {
                            damager.addPotionEffect(new PotionEffect(effect.potionEffect(), effect.duration(), (armorEnchantment.isLevelAddedToAmplifier() ? enchants.get(enchantment.getEnchantment()) : 0) + effect.amplifier()));
                        }
                    } else {
                        event.setDamage(event.getDamage() * ((armorEnchantment.isLevelAddedToAmplifier() ? enchants.get(enchantment.getEnchantment()) : 0) + armorEnchantment.getDamageAmplifier()));
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
                player.setHealth(player.getAttribute(getHealthAttribute()).getValue());
                event.setCancelled(true);

                return;
            }

            if (player.getHealth() <= 4 && EnchantUtils.isEventActive(CEnchantments.ADRENALINE, player, armor, enchants)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3 + (enchants.get(CEnchantments.ADRENALINE.getEnchantment())) * 20, 10));
            }

            if (player.getHealth() <= 8 && EnchantUtils.isEventActive(CEnchantments.ROCKET, player, armor, enchants)) {
                player.getScheduler().runDelayed(this.plugin, (task) -> {
                    player.setVelocity(player.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize().setY(1));
                }, null, 1);

                this.fallenPlayers.add(player.getUniqueId());

                player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 1);

                player.getScheduler().runDelayed(this.plugin, (task) -> {
                    fallenPlayers.remove(player.getUniqueId());
                }, null, 8 * 20);
            }

            if (player.getHealth() > 0 && EnchantUtils.isEventActive(CEnchantments.ENLIGHTENED, player, armor, enchants)) {
                double heal = enchants.get(CEnchantments.ENLIGHTENED.getEnchantment());
                double maxHealth = player.getAttribute(getHealthAttribute()).getValue();

                if (player.getHealth() + heal < maxHealth) player.setHealth(player.getHealth() + heal);

                if (player.getHealth() + heal >= maxHealth) player.setHealth(maxHealth);
            }

            if (EnchantUtils.isEventActive(CEnchantments.INSOMNIA, player, armor, enchants)) damager.damage(event.getDamage() + enchants.get(CEnchantments.INSOMNIA.getEnchantment()));

            if (EnchantUtils.isEventActive(CEnchantments.MOLTEN, player, armor, enchants)) damager.setFireTicks((enchants.get(CEnchantments.MOLTEN.getEnchantment()) * 2) * 20);

            if (EnchantUtils.isEventActive(CEnchantments.SAVIOR, player, armor, enchants)) event.setDamage(event.getDamage() / 2);

            if (EnchantUtils.isEventActive(CEnchantments.CACTUS, player, armor, enchants)) damager.damage(enchants.get(CEnchantments.CACTUS.getEnchantment()));

            if (EnchantUtils.isEventActive(CEnchantments.STORMCALLER, player, armor, enchants)) {
                Entity lightning = this.methods.lightning(damager);

                for (LivingEntity en : this.methods.getNearbyLivingEntities(2D, player)) {
                    EntityDamageEvent damageByEntityEvent = new EntityDamageEvent(en, DamageCause.LIGHTNING, DamageSource.builder(DamageType.LIGHTNING_BOLT).withCausingEntity(player).withDirectEntity(lightning).build(), 5D);
                    this.methods.entityEvent(player, en, damageByEntityEvent);
                }

                damager.damage(5D);
            }
        }

        if (!(damager instanceof Player)) return;

        for (ItemStack armor : Objects.requireNonNull(damager.getEquipment()).getArmorContents()) {
            Map<CEnchantment, Integer> enchants = this.enchantmentBookSettings.getEnchantments(armor);
            if (!enchants.containsKey(CEnchantments.LEADERSHIP.getEnchantment())) continue;

            int radius = 4 + enchants.get(CEnchantments.LEADERSHIP.getEnchantment());
            int playersCount = (int) damager.getNearbyEntities(radius, radius, radius).stream().filter(entity -> entity instanceof Player && this.pluginSupport.isFriendly(damager, entity)).count();

            if (playersCount > 0 && EnchantUtils.isEventActive(CEnchantments.LEADERSHIP, player, armor, enchants)) {
                event.setDamage(event.getDamage() + (playersCount / 2d));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAura(AuraActiveEvent event) {
        Player player = event.getPlayer();
        Player other = event.getOther();

        if (!player.canSee(other) || !other.canSee(player)) return;
        if (this.pluginSupport.isVanished(player) || this.pluginSupport.isVanished(other)) return;

        CEnchantments enchant = event.getEnchantment();
        int level = event.getLevel();

        if (!this.pluginSupport.allowCombat(other.getLocation()) || this.pluginSupport.isFriendly(player, other) || this.methods.hasPermission(other, "bypass.aura", false)) return;

        Map<CEnchantment, Integer> enchantments = Map.of(enchant.getEnchantment(), level);

        switch (enchant) {
            case BLIZZARD -> {
                if (EnchantUtils.isAuraActive(player, enchant, enchantments)) other.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, level - 1));
            }

            case INTIMIDATE -> {
                if (EnchantUtils.isAuraActive(player, enchant, enchantments)) other.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 3 * 20, level - 1));
            }

            case ACIDRAIN -> {
                if (EnchantUtils.isAuraActive(player, enchant, enchantments)) other.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 4 * 20, 1));
            }

            case SANDSTORM -> {
                if (EnchantUtils.isAuraActive(player, enchant, enchantments)) other.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 0));
            }

            case RADIANT -> {
                if (EnchantUtils.isAuraActive(player, enchant, enchantments)) other.setFireTicks(5 * 20);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerMovement(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) return;

        armorProcessor.add(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getKiller() == null) return;

        Player killer = player.getKiller();

        if (!this.pluginSupport.allowCombat(player.getLocation())) return;

        for (ItemStack item : player.getEquipment().getArmorContents()) {
            Map<CEnchantment, Integer> enchantments = this.enchantmentBookSettings.getEnchantments(item);

            if (EnchantUtils.isEventActive(CEnchantments.SELFDESTRUCT, player, item, enchantments)) {
                this.methods.explode(player);

                List<ItemStack> items = event.getDrops().stream().filter(drop -> ProtectionCrystalSettings.isProtected(drop.getPersistentDataContainer()) && this.protectionCrystalSettings.isProtectionSuccessful(player)).toList();

                event.getDrops().clear();
                event.getDrops().addAll(items);
            }

            if (EnchantUtils.isEventActive(CEnchantments.RECOVER, player, item, enchantments)) {
                killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 8 * 20, 2));
                killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 1));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (!this.fallenPlayers.contains(player.getUniqueId())) return;

        if (!DamageCause.FALL.equals(event.getCause())) return;

        event.setCancelled(true);
    }
}
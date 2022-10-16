package com.badbones69.crazyenchantments.enchantments;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.economy.Currency;
import com.badbones69.crazyenchantments.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.api.events.DisarmerUseEvent;
import com.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.api.events.RageBreakEvent;
import com.badbones69.crazyenchantments.api.support.anticheats.NoCheatPlusSupport;
import com.badbones69.crazyenchantments.api.objects.CEPlayer;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

public class SwordEnchantments implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    private final Methods methods = plugin.getStarter().getMethods();

    private final PluginSupport pluginSupport = plugin.getStarter().getPluginSupport();

    private final NoCheatPlusSupport noCheatPlusSupport = plugin.getNoCheatPlusSupport();

    private final CurrencyAPI currencyAPI = plugin.getStarter().getCurrencyAPI();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (crazyManager.isIgnoredEvent(e)) return;
        if (crazyManager.isIgnoredUUID(e.getDamager().getUniqueId())) return;
        if (pluginSupport.isFriendly(e.getDamager(), e.getEntity())) return;

        if (crazyManager.isBreakRageOnDamageOn() && e.getEntity() instanceof Player player) {
            CEPlayer cePlayer = crazyManager.getCEPlayer(player);

            if (cePlayer != null) {
                RageBreakEvent event = new RageBreakEvent(player, e.getDamager(), methods.getItemInHand(player));
                plugin.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled() && cePlayer.hasRage()) {
                    cePlayer.getRageTask().cancel();
                    cePlayer.setRageMultiplier(0.0);
                    cePlayer.setRageLevel(0);
                    cePlayer.setRage(false);

                    if (Messages.RAGE_DAMAGED.getMessage().length() > 0) e.getEntity().sendMessage(Messages.RAGE_DAMAGED.getMessage());
                }
            }
        }
        if (!(e.getEntity() instanceof LivingEntity en)) return;
        if (!(e.getDamager() instanceof final Player damager)) return;

        CEPlayer cePlayer = crazyManager.getCEPlayer(damager);
        ItemStack item = methods.getItemInHand(damager);

        if (e.getEntity().isDead()) return;

        List<CEnchantment> enchantments = crazyManager.getEnchantmentsOnItem(item);
        boolean isEntityPlayer = e.getEntity() instanceof Player;

        if (isEntityPlayer && enchantments.contains(CEnchantments.DISARMER.getEnchantment())) {
            Player player = (Player) e.getEntity();

            if (CEnchantments.DISARMER.chanceSuccessful(item)) {
                EquipmentSlot equipmentSlot = getSlot(methods.percentPick(4, 0));

                ItemStack armor = switch (equipmentSlot) {
                    case HEAD -> player.getEquipment().getHelmet();
                    case CHEST -> player.getEquipment().getChestplate();
                    case LEGS -> player.getEquipment().getLeggings();
                    case FEET -> player.getEquipment().getBoots();
                    default -> null;
                };

                if (armor != null) {
                    DisarmerUseEvent event = new DisarmerUseEvent(player, damager, armor);
                    plugin.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {

                        switch (equipmentSlot) {
                            case HEAD -> player.getEquipment().setHelmet(null);
                            case CHEST -> player.getEquipment().setChestplate(null);
                            case LEGS -> player.getEquipment().setLeggings(null);
                            case FEET -> player.getEquipment().setBoots(null);
                        }

                        if (methods.isInventoryFull(player)) {
                            player.getWorld().dropItemNaturally(player.getLocation(), armor);
                        } else {
                            player.getInventory().addItem(armor);
                        }
                    }
                }
            }
        }

        if (isEntityPlayer && enchantments.contains(CEnchantments.DISORDER.getEnchantment()) && CEnchantments.DISORDER.chanceSuccessful(item)) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.DISORDER, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                Player player = (Player) e.getEntity();
                Inventory inventory = player.getInventory();
                List<ItemStack> items = new ArrayList<>();
                List<Integer> slots = new ArrayList<>();

                for (int i = 0; i < 9; i++) {
                    ItemStack inventoryItem = inventory.getItem(i);

                    if (inventoryItem != null) {
                        items.add(inventoryItem);
                        inventory.setItem(i, new ItemStack(Material.AIR));
                    }

                    slots.add(i);
                }

                Collections.shuffle(items);
                Collections.shuffle(slots);

                for (int i = 0; i < items.size(); i++) {
                    inventory.setItem(slots.get(i), items.get(i));
                }

                if (!Messages.DISORDERED_ENEMY_HOT_BAR.getMessageNoPrefix().isEmpty()) damager.sendMessage(Messages.DISORDERED_ENEMY_HOT_BAR.getMessage());
            }
        }

        // Check if CEPlayer is null as plugins like citizen use Player objects.
        if (enchantments.contains(CEnchantments.RAGE.getEnchantment()) && cePlayer != null) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.RAGE, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                if (cePlayer.hasRage()) {
                    cePlayer.getRageTask().cancel();

                    if (cePlayer.getRageMultiplier() <= crazyManager.getRageMaxLevel()) cePlayer.setRageMultiplier(cePlayer.getRageMultiplier() + (crazyManager.getLevel(item, CEnchantments.RAGE) * 0.1));

                    int rageUp = cePlayer.getRageLevel() + 1;

                    if (cePlayer.getRageMultiplier().intValue() == rageUp) {

                        if (Messages.RAGE_RAGE_UP.getMessage().length() > 0) {
                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Level%", rageUp + "");
                            damager.sendMessage(Messages.RAGE_RAGE_UP.getMessage(placeholders));
                        }

                        cePlayer.setRageLevel(rageUp);
                    }

                    e.setDamage(e.getDamage() * cePlayer.getRageMultiplier());
                }

                if (!cePlayer.hasRage()) {
                    cePlayer.setRageMultiplier(1.0);
                    cePlayer.setRage(true);
                    cePlayer.setRageLevel(1);

                    if (Messages.RAGE_BUILDING.getMessage().length() > 0) damager.sendMessage(Messages.RAGE_BUILDING.getMessage());
                }

                cePlayer.setRageTask(new BukkitRunnable() {
                    @Override
                    public void run() {
                        cePlayer.setRageMultiplier(0.0);
                        cePlayer.setRage(false);
                        cePlayer.setRageLevel(0);

                        if (Messages.RAGE_COOLED_DOWN.getMessage().length() > 0) damager.sendMessage(Messages.RAGE_COOLED_DOWN.getMessage());
                    }
                }.runTaskLater(plugin, 80));
            }
        }

        if (en instanceof Player player && enchantments.contains(CEnchantments.SKILLSWIPE.getEnchantment())) {
            int amount = 4 + crazyManager.getLevel(item, CEnchantments.SKILLSWIPE);

            if (player.getTotalExperience() > 0) {
                EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.SKILLSWIPE, item);
                plugin.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {

                    if (currencyAPI.getCurrency(player, Currency.XP_TOTAL) >= amount) {
                        currencyAPI.takeCurrency(player, Currency.XP_TOTAL, amount);
                    } else {
                        player.setTotalExperience(0);
                    }

                    currencyAPI.giveCurrency(damager, Currency.XP_TOTAL, amount);
                }
            }
        }

        if (enchantments.contains(CEnchantments.LIFESTEAL.getEnchantment()) && CEnchantments.LIFESTEAL.chanceSuccessful(item) && damager.getHealth() > 0) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.LIFESTEAL, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                int steal = crazyManager.getLevel(item, CEnchantments.LIFESTEAL);
                // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
                double maxHealth = damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

                if (damager.getHealth() + steal < maxHealth) damager.setHealth(damager.getHealth() + steal);

                if (damager.getHealth() + steal >= maxHealth) damager.setHealth(maxHealth);
            }
        }

        if (enchantments.contains(CEnchantments.NUTRITION.getEnchantment()) && CEnchantments.NUTRITION.chanceSuccessful(item)) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.NUTRITION, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {

                // if (PluginSupport.SupportedPlugins.SPARTAN.isPluginLoaded()) SpartanSupport.cancelFastEat(damager);

                if (damager.getSaturation() + (2 * crazyManager.getLevel(item, CEnchantments.NUTRITION)) <= 20) damager.setSaturation(damager.getSaturation() + (2 * crazyManager.getLevel(item, CEnchantments.NUTRITION)));

                if (damager.getSaturation() + (2 * crazyManager.getLevel(item, CEnchantments.NUTRITION)) >= 20) damager.setSaturation(20);
            }
        }

        if (enchantments.contains(CEnchantments.VAMPIRE.getEnchantment()) && CEnchantments.VAMPIRE.chanceSuccessful(item) && damager.getHealth() > 0) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.VAMPIRE, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
                double maxHealth = damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

                if (damager.getHealth() + e.getDamage() / 2 < maxHealth) damager.setHealth(damager.getHealth() + e.getDamage() / 2);

                if (damager.getHealth() + e.getDamage() / 2 >= maxHealth) damager.setHealth(maxHealth);
            }
        }

        if (enchantments.contains(CEnchantments.BLINDNESS.getEnchantment()) && CEnchantments.BLINDNESS.chanceSuccessful(item)) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.BLINDNESS, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) en.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, crazyManager.getLevel(item, CEnchantments.BLINDNESS) - 1));
        }

        if (enchantments.contains(CEnchantments.CONFUSION.getEnchantment()) && CEnchantments.CONFUSION.chanceSuccessful(item)) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.CONFUSION, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) en.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 5 + (crazyManager.getLevel(item, CEnchantments.CONFUSION)) * 20, 0));
        }

        if (enchantments.contains(CEnchantments.DOUBLEDAMAGE.getEnchantment()) && CEnchantments.DOUBLEDAMAGE.chanceSuccessful(item)) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.DOUBLEDAMAGE, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) e.setDamage((e.getDamage() * 2));
        }

        if (enchantments.contains(CEnchantments.EXECUTE.getEnchantment()) && en.getHealth() <= 2) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.EXECUTE, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) damager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3 + (crazyManager.getLevel(item, CEnchantments.EXECUTE)) * 20, 3));
        }

        if (enchantments.contains(CEnchantments.FASTTURN.getEnchantment()) && CEnchantments.FASTTURN.chanceSuccessful(item)) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.FASTTURN, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) e.setDamage(e.getDamage() + (e.getDamage() / 3));
        }

        if (enchantments.contains(CEnchantments.LIGHTWEIGHT.getEnchantment()) && CEnchantments.LIGHTWEIGHT.chanceSuccessful(item)) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.LIGHTWEIGHT, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) damager.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 5 * 20, crazyManager.getLevel(item, CEnchantments.LIGHTWEIGHT) - 1));
        }

        if (enchantments.contains(CEnchantments.OBLITERATE.getEnchantment()) && CEnchantments.OBLITERATE.chanceSuccessful(item)) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.OBLITERATE, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {

                //if (e.getEntity() instanceof Player && PluginSupport.SupportedPlugins.SPARTAN.isPluginLoaded()) {
                    //SpartanSupport.cancelSpeed((Player) e.getEntity());
                    //SpartanSupport.cancelNormalMovements((Player) e.getEntity());
                    //SpartanSupport.cancelNoFall((Player) e.getEntity());
                //}

                e.getEntity().setVelocity(damager.getLocation().getDirection().multiply(2).setY(1.25));
            }
        }

        if (enchantments.contains(CEnchantments.PARALYZE.getEnchantment()) && CEnchantments.PARALYZE.chanceSuccessful(item)) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.PARALYZE, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                if (PluginSupport.SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) noCheatPlusSupport.allowPlayer(damager);

                //if (SupportedPlugins.SPARTAN.isPluginLoaded()) SpartanSupport.cancelNoSwing(damager);

                for (LivingEntity entity :methods.getNearbyLivingEntities(methods.checkEntity(en), 2D, damager)) {
                    EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(damager, entity, EntityDamageEvent.DamageCause.CUSTOM, 5D);
                    methods.entityEvent(damager, entity, damageByEntityEvent);
                }

                en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 2));
                en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3 * 20, 2));

                if (PluginSupport.SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) noCheatPlusSupport.denyPlayer(damager);
            }
        }

        if (enchantments.contains(CEnchantments.SLOWMO.getEnchantment()) && CEnchantments.SLOWMO.chanceSuccessful(item)) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.SLOWMO, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, crazyManager.getLevel(item, CEnchantments.SLOWMO)));
        }

        if (enchantments.contains(CEnchantments.SNARE.getEnchantment()) && CEnchantments.SNARE.chanceSuccessful(item)) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.SNARE, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 0));
                en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3 * 20, 0));
            }
        }

        if (enchantments.contains(CEnchantments.TRAP.getEnchantment()) && CEnchantments.TRAP.chanceSuccessful(item)) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.TRAP, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 2));
        }

        if (enchantments.contains(CEnchantments.VIPER.getEnchantment()) && CEnchantments.VIPER.chanceSuccessful(item)) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.VIPER, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5 * 20, crazyManager.getLevel(item, CEnchantments.VIPER)));
        }

        if (enchantments.contains(CEnchantments.WITHER.getEnchantment()) && CEnchantments.WITHER.chanceSuccessful(item)) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.WITHER, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) en.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 2 * 20, 2));
        }

        if (enchantments.contains(CEnchantments.FAMISHED.getEnchantment()) && CEnchantments.FAMISHED.chanceSuccessful(item)) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.FAMISHED, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) en.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 10 * 20, 1));
        }


    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;

        Player damager = event.getEntity().getKiller();
        Player player = event.getEntity();
        ItemStack item = methods.getItemInHand(damager);

        if (crazyManager.hasEnchantment(item, CEnchantments.HEADLESS) && CEnchantments.HEADLESS.chanceSuccessful(item)) {
            EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.HEADLESS, item);
            plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) {
                ItemStack head = new ItemBuilder().setMaterial("PLAYER_HEAD").setPlayerName(player.getName()).build();
                event.getDrops().add(head);
            }
        }

        if (CEnchantments.REVENGE.isActivated()) {
            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (!pluginSupport.isFriendly(entity, player)) continue;
                Player ally = (Player) entity;
                ItemStack itemStack = methods.getItemInHand(ally);

                if (crazyManager.hasEnchantment(itemStack, CEnchantments.REVENGE)) {
                    ally.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 1));
                    ally.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 0));
                    ally.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 5 * 20, 1));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {

        if (event.getEntity().getKiller() != null) {
            Player damager = event.getEntity().getKiller();
            ItemStack item = methods.getItemInHand(damager);

            if (crazyManager.hasEnchantments(item)) {
                if (crazyManager.hasEnchantment(item, CEnchantments.INQUISITIVE) && CEnchantments.INQUISITIVE.chanceSuccessful(item)) {
                    EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.INQUISITIVE, item);
                    plugin.getServer().getPluginManager().callEvent(useEvent);

                    if (!useEvent.isCancelled()) event.setDroppedExp(event.getDroppedExp() * (crazyManager.getLevel(item, CEnchantments.INQUISITIVE) + 1));
                }

                // The entity that is killed is a player.
                if (event.getEntity() instanceof Player && crazyManager.hasEnchantment(item, CEnchantments.CHARGE)) {
                    EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.CHARGE, item);
                    plugin.getServer().getPluginManager().callEvent(useEvent);

                    if (!useEvent.isCancelled()) {
                        int radius = 4 + crazyManager.getLevel(item, CEnchantments.CHARGE);
                        damager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 1));

                        for (Entity entity : damager.getNearbyEntities(radius, radius, radius)) {
                            if (pluginSupport.isFriendly(entity, damager)) ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 1));
                        }
                    }
                }
            }
        }
    }

    private EquipmentSlot getSlot(int slot) {
        return switch (slot) {
            case 1 -> EquipmentSlot.CHEST;
            case 2 -> EquipmentSlot.LEGS;
            case 3 -> EquipmentSlot.FEET;
            default -> EquipmentSlot.HEAD;
        };
    }
}
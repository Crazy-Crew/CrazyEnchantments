package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.PluginSupport;
import com.badbones69.crazyenchantments.paper.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.events.DisarmerUseEvent;
import com.badbones69.crazyenchantments.paper.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.paper.api.events.RageBreakEvent;
import com.badbones69.crazyenchantments.paper.api.support.anticheats.NoCheatPlusSupport;
import com.badbones69.crazyenchantments.paper.api.objects.CEPlayer;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.support.anticheats.SpartanSupport;
import com.badbones69.crazyenchantments.paper.controllers.BossBarController;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.utilities.misc.EventUtils;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SwordEnchantments implements Listener {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Starter starter = this.plugin.getStarter();

    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private final Methods methods = this.starter.getMethods();

    // Plugin Support.
    private final PluginSupport pluginSupport = this.starter.getPluginSupport();

    private final NoCheatPlusSupport noCheatPlusSupport = this.starter.getNoCheatPlusSupport();
    private final SpartanSupport spartanSupport = this.starter.getSpartanSupport();

    private final BossBarController bossBarController = this.plugin.getBossBarController();

    // Economy Management.
    private final CurrencyAPI currencyAPI = this.starter.getCurrencyAPI();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (EventUtils.isIgnoredEvent(event) || EventUtils.isIgnoredUUID(event.getDamager().getUniqueId())) return;
        if (this.pluginSupport.isFriendly(event.getDamager(), event.getEntity())) return;

        if (this.crazyManager.isBreakRageOnDamageOn() && event.getEntity() instanceof Player player) {
            CEPlayer cePlayer = this.crazyManager.getCEPlayer(player);

            if (cePlayer != null) {
                RageBreakEvent rageBreakEvent = new RageBreakEvent(player, event.getDamager(), this.methods.getItemInHand(player));
                this.plugin.getServer().getPluginManager().callEvent(rageBreakEvent);

                if (!rageBreakEvent.isCancelled() && cePlayer.hasRage()) {
                    cePlayer.getRageTask().cancel();
                    cePlayer.setRageMultiplier(0.0);
                    cePlayer.setRageLevel(0);
                    cePlayer.setRage(false);

                    rageInformPlayer(player, Messages.RAGE_DAMAGED, 0f);
                }
            }
        }

        if (!(event.getEntity() instanceof LivingEntity en)) return;
        if (!(event.getDamager() instanceof final Player damager)) return;

        CEPlayer cePlayer = this.crazyManager.getCEPlayer(damager);
        ItemStack item = this.methods.getItemInHand(damager);

        if (event.getEntity().isDead()) return;

        List<CEnchantment> enchantments = this.enchantmentBookSettings.getEnchantmentsOnItem(item);
        boolean isEntityPlayer = event.getEntity() instanceof Player;

        if (isEntityPlayer && enchantments.contains(CEnchantments.DISARMER.getEnchantment())) {
            Player player = (Player) event.getEntity();

            if (CEnchantments.DISARMER.chanceSuccessful(item)) {
                EquipmentSlot equipmentSlot = getSlot(this.methods.percentPick(4, 0));

                ItemStack armor = switch (equipmentSlot) {
                    case HEAD -> player.getEquipment().getHelmet();
                    case CHEST -> player.getEquipment().getChestplate();
                    case LEGS -> player.getEquipment().getLeggings();
                    case FEET -> player.getEquipment().getBoots();
                    default -> null;
                };

                if (armor != null) {
                    DisarmerUseEvent disarmerUseEvent = new DisarmerUseEvent(player, damager, armor);
                    this.plugin.getServer().getPluginManager().callEvent(disarmerUseEvent);

                    if (!disarmerUseEvent.isCancelled()) {

                        switch (equipmentSlot) {
                            case HEAD -> player.getEquipment().setHelmet(null);
                            case CHEST -> player.getEquipment().setChestplate(null);
                            case LEGS -> player.getEquipment().setLeggings(null);
                            case FEET -> player.getEquipment().setBoots(null);
                        }

                        if (this.methods.isInventoryFull(player)) {
                            player.getWorld().dropItemNaturally(player.getLocation(), armor);
                        } else {
                            player.getInventory().addItem(armor);
                        }
                    }
                }
            }
        }

        if (isEntityPlayer && enchantments.contains(CEnchantments.DISORDER.getEnchantment()) && CEnchantments.DISORDER.chanceSuccessful(item)) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.DISORDER, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) {
                Player player = (Player) event.getEntity();
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
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.RAGE, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) {
                if (cePlayer.hasRage()) {
                    cePlayer.getRageTask().cancel();

                    if (cePlayer.getRageMultiplier() <= crazyManager.getRageMaxLevel()) cePlayer.setRageMultiplier(cePlayer.getRageMultiplier() + (this.crazyManager.getLevel(item, CEnchantments.RAGE) * 0.1));

                    int rageUp = cePlayer.getRageLevel() + 1;

                    if (cePlayer.getRageMultiplier().intValue() == rageUp) {
                        rageInformPlayer(damager, Messages.RAGE_RAGE_UP, Map.of("%Level%", String.valueOf(rageUp)), ((float) rageUp / (float) (this.crazyManager.getRageMaxLevel()+1)));
                        cePlayer.setRageLevel(rageUp);
                    }

                    event.setDamage(event.getDamage() * cePlayer.getRageMultiplier());
                }

                if (!cePlayer.hasRage()) {
                    cePlayer.setRageMultiplier(1.0);
                    cePlayer.setRage(true);
                    cePlayer.setRageLevel(1);

                    rageInformPlayer(damager, Messages.RAGE_BUILDING, ((float) cePlayer.getRageLevel() / (float) this.crazyManager.getRageMaxLevel()));
                }

                cePlayer.setRageTask(new BukkitRunnable() {
                    @Override
                    public void run() {
                        cePlayer.setRageMultiplier(0.0);
                        cePlayer.setRage(false);
                        cePlayer.setRageLevel(0);

                        rageInformPlayer(damager, Messages.RAGE_COOLED_DOWN, 0f);
                    }
                }.runTaskLater(this.plugin, 80));
            }
        }

        if (en instanceof Player player && enchantments.contains(CEnchantments.SKILLSWIPE.getEnchantment())) {
            int amount = 4 + this.crazyManager.getLevel(item, CEnchantments.SKILLSWIPE);

            if (player.getTotalExperience() > 0) {
                EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.SKILLSWIPE, item);
                this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

                if (!enchantmentUseEvent.isCancelled()) {

                    if (this.currencyAPI.getCurrency(player, Currency.XP_TOTAL) >= amount) {
                        this.currencyAPI.takeCurrency(player, Currency.XP_TOTAL, amount);
                    } else {
                        player.setTotalExperience(0);
                    }

                    this.currencyAPI.giveCurrency(damager, Currency.XP_TOTAL, amount);
                }
            }
        }

        if (enchantments.contains(CEnchantments.LIFESTEAL.getEnchantment()) && CEnchantments.LIFESTEAL.chanceSuccessful(item) && damager.getHealth() > 0) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.LIFESTEAL, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) {
                int steal = this.crazyManager.getLevel(item, CEnchantments.LIFESTEAL);
                // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
                double maxHealth = damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

                if (damager.getHealth() + steal < maxHealth) damager.setHealth(damager.getHealth() + steal);

                if (damager.getHealth() + steal >= maxHealth) damager.setHealth(maxHealth);
            }
        }

        if (enchantments.contains(CEnchantments.NUTRITION.getEnchantment()) && CEnchantments.NUTRITION.chanceSuccessful(item)) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.NUTRITION, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) {
                if (SupportedPlugins.SPARTAN.isPluginLoaded()) this.spartanSupport.cancelFastEat(damager);

                if (damager.getSaturation() + (2 * this.crazyManager.getLevel(item, CEnchantments.NUTRITION)) <= 20) damager.setSaturation(damager.getSaturation() + (2 * this.crazyManager.getLevel(item, CEnchantments.NUTRITION)));

                if (damager.getSaturation() + (2 * this.crazyManager.getLevel(item, CEnchantments.NUTRITION)) >= 20) damager.setSaturation(20);
            }
        }

        if (enchantments.contains(CEnchantments.VAMPIRE.getEnchantment()) && CEnchantments.VAMPIRE.chanceSuccessful(item) && damager.getHealth() > 0) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.VAMPIRE, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) {
                // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
                double maxHealth = damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

                if (damager.getHealth() + event.getDamage() / 2 < maxHealth) damager.setHealth(damager.getHealth() + event.getDamage() / 2);

                if (damager.getHealth() + event.getDamage() / 2 >= maxHealth) damager.setHealth(maxHealth);
            }
        }

        if (enchantments.contains(CEnchantments.BLINDNESS.getEnchantment()) && CEnchantments.BLINDNESS.chanceSuccessful(item)) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.BLINDNESS, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) en.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, crazyManager.getLevel(item, CEnchantments.BLINDNESS) - 1));
        }

        if (enchantments.contains(CEnchantments.CONFUSION.getEnchantment()) && CEnchantments.CONFUSION.chanceSuccessful(item)) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.CONFUSION, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) en.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 5 + (crazyManager.getLevel(item, CEnchantments.CONFUSION)) * 20, 0));
        }

        if (enchantments.contains(CEnchantments.DOUBLEDAMAGE.getEnchantment()) && CEnchantments.DOUBLEDAMAGE.chanceSuccessful(item)) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.DOUBLEDAMAGE, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) event.setDamage((event.getDamage() * 2));
        }

        if (enchantments.contains(CEnchantments.EXECUTE.getEnchantment()) && en.getHealth() <= 2) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.EXECUTE, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) damager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3 + (this.crazyManager.getLevel(item, CEnchantments.EXECUTE)) * 20, 3));
        }

        if (enchantments.contains(CEnchantments.FASTTURN.getEnchantment()) && CEnchantments.FASTTURN.chanceSuccessful(item)) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.FASTTURN, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) event.setDamage(event.getDamage() + (event.getDamage() / 3));
        }

        if (enchantments.contains(CEnchantments.LIGHTWEIGHT.getEnchantment()) && CEnchantments.LIGHTWEIGHT.chanceSuccessful(item)) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.LIGHTWEIGHT, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) damager.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 5 * 20, this.crazyManager.getLevel(item, CEnchantments.LIGHTWEIGHT) - 1));
        }

        if (enchantments.contains(CEnchantments.OBLITERATE.getEnchantment()) && CEnchantments.OBLITERATE.chanceSuccessful(item)) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.OBLITERATE, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) {
                if (event.getEntity() instanceof Player && SupportedPlugins.SPARTAN.isPluginLoaded()) {
                    spartanSupport.cancelSpeed((Player) event.getEntity());
                    spartanSupport.cancelNormalMovements((Player) event.getEntity());
                    spartanSupport.cancelNoFall((Player) event.getEntity());
                }

                event.getEntity().setVelocity(damager.getLocation().getDirection().multiply(2).setY(1.25));
            }
        }

        if (enchantments.contains(CEnchantments.PARALYZE.getEnchantment()) && CEnchantments.PARALYZE.chanceSuccessful(item)) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.PARALYZE, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) {
                if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) this.noCheatPlusSupport.allowPlayer(damager);

                if (SupportedPlugins.SPARTAN.isPluginLoaded()) this.spartanSupport.cancelNoSwing(damager);

                for (LivingEntity entity :methods.getNearbyLivingEntities(2D, damager)) {
                    EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(damager, entity, EntityDamageEvent.DamageCause.CUSTOM, 5D);
                    this.methods.entityEvent(damager, entity, damageByEntityEvent);
                }

                en.getWorld().strikeLightningEffect(en.getLocation());
                en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 2));
                en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3 * 20, 2));

                if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) this.noCheatPlusSupport.denyPlayer(damager);
            }
        }

        if (enchantments.contains(CEnchantments.SLOWMO.getEnchantment()) && CEnchantments.SLOWMO.chanceSuccessful(item)) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.SLOWMO, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, crazyManager.getLevel(item, CEnchantments.SLOWMO)));
        }

        if (enchantments.contains(CEnchantments.SNARE.getEnchantment()) && CEnchantments.SNARE.chanceSuccessful(item)) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.SNARE, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) {
                en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 0));
                en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3 * 20, 0));
            }
        }

        if (enchantments.contains(CEnchantments.TRAP.getEnchantment()) && CEnchantments.TRAP.chanceSuccessful(item)) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.TRAP, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 2));
        }

        if (enchantments.contains(CEnchantments.VIPER.getEnchantment()) && CEnchantments.VIPER.chanceSuccessful(item)) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.VIPER, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5 * 20, crazyManager.getLevel(item, CEnchantments.VIPER)));
        }

        if (enchantments.contains(CEnchantments.WITHER.getEnchantment()) && CEnchantments.WITHER.chanceSuccessful(item)) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.WITHER, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) en.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 2 * 20, 2));
        }

        if (enchantments.contains(CEnchantments.FAMISHED.getEnchantment()) && CEnchantments.FAMISHED.chanceSuccessful(item)) {
            EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(damager, CEnchantments.FAMISHED, item);
            this.plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

            if (!enchantmentUseEvent.isCancelled()) en.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 10 * 20, 1));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;

        Player damager = event.getEntity().getKiller();
        Player player = event.getEntity();
        ItemStack item = this.methods.getItemInHand(damager);

        if (this.crazyManager.hasEnchantment(item, CEnchantments.HEADLESS) && CEnchantments.HEADLESS.chanceSuccessful(item)) {
            EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.HEADLESS, item);
            this.plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) {
                ItemStack head = new ItemBuilder().setMaterial("PLAYER_HEAD").setPlayerName(player.getName()).build();
                event.getDrops().add(head);
            }
        }

        if (CEnchantments.REVENGE.isActivated()) {
            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (!this.pluginSupport.isFriendly(entity, player)) continue;
                Player ally = (Player) entity;
                ItemStack itemStack = this.methods.getItemInHand(ally);

                if (this.crazyManager.hasEnchantment(itemStack, CEnchantments.REVENGE)) {
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
            ItemStack item = this.methods.getItemInHand(damager);

            if (this.enchantmentBookSettings.hasEnchantments(item)) {
                if (this.crazyManager.hasEnchantment(item, CEnchantments.INQUISITIVE) && CEnchantments.INQUISITIVE.chanceSuccessful(item)) {
                    EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.INQUISITIVE, item);
                    this.plugin.getServer().getPluginManager().callEvent(useEvent);

                    if (!useEvent.isCancelled()) event.setDroppedExp(event.getDroppedExp() * (this.crazyManager.getLevel(item, CEnchantments.INQUISITIVE) + 1));
                }

                // The entity that is killed is a player.
                if (event.getEntity() instanceof Player && this.crazyManager.hasEnchantment(item, CEnchantments.CHARGE)) {
                    EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.CHARGE, item);
                    this.plugin.getServer().getPluginManager().callEvent(useEvent);

                    if (!useEvent.isCancelled()) {
                        int radius = 4 + this.crazyManager.getLevel(item, CEnchantments.CHARGE);
                        damager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 1));

                        for (Entity entity : damager.getNearbyEntities(radius, radius, radius)) {
                            if (this.pluginSupport.isFriendly(entity, damager)) ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 1));
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

    private void rageInformPlayer(Player player, Messages message, Map<String, String> placeholders, float progress) {
        if (message.getMessageNoPrefix().isBlank()) return;

        if (this.crazyManager.useRageBossBar()) {
            this.bossBarController.updateBossBar(player, message.getMessageNoPrefix(placeholders), progress);
        } else {
            player.sendMessage(message.getMessage(placeholders));
        }
    }
    private void rageInformPlayer(Player player, Messages message, float progress) {
        if (message.getMessageNoPrefix().isBlank()) return;

        if (this.crazyManager.useRageBossBar()) {
            this.bossBarController.updateBossBar(player, message.getMessageNoPrefix(), progress);
        } else {
            player.sendMessage(message.getMessage());
        }
    }
}

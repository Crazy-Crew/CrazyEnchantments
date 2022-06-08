package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyManager;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.currencyapi.Currency;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.events.DisarmerUseEvent;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.api.events.RageBreakEvent;
import me.badbones69.crazyenchantments.api.objects.CEPlayer;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.multisupport.Support;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import me.badbones69.crazyenchantments.multisupport.anticheats.SpartanSupport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Swords implements Listener {
    
    private CrazyManager ce = CrazyManager.getInstance();
    private Support support = Support.getInstance();
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (!e.isCancelled() && !ce.isIgnoredEvent(e) && !ce.isIgnoredUUID(e.getDamager().getUniqueId()) && !support.isFriendly(e.getDamager(), e.getEntity())) {
            if (ce.isBreakRageOnDamageOn() && e.getEntity() instanceof Player) {
                Player player = (Player) e.getEntity();
                CEPlayer cePlayer = ce.getCEPlayer(player);
                if (cePlayer != null) {
                    RageBreakEvent event = new RageBreakEvent(player, e.getDamager(), Methods.getItemInHand(player));
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled() && cePlayer.hasRage()) {
                        cePlayer.getRageTask().cancel();
                        cePlayer.setRageMultiplier(0.0);
                        cePlayer.setRageLevel(0);
                        cePlayer.setRage(false);
                        if (Messages.RAGE_DAMAGED.getMessage().length() > 0) {
                            e.getEntity().sendMessage(Messages.RAGE_DAMAGED.getMessage());
                        }
                    }
                }
            }
            if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Player) {
                final Player damager = (Player) e.getDamager();
                CEPlayer cePlayer = ce.getCEPlayer(damager);
                LivingEntity en = (LivingEntity) e.getEntity();
                ItemStack item = Methods.getItemInHand(damager);
                if (!e.getEntity().isDead()) {
                    List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
                    boolean isEntityPlayer = e.getEntity() instanceof Player;
                    if (isEntityPlayer && enchantments.contains(CEnchantments.DISARMER.getEnchantment())) {
                        Player player = (Player) e.getEntity();
                        if (CEnchantments.DISARMER.chanceSuccessful(item)) {
                            EquipmentSlot equipmentSlot = getSlot(Methods.percentPick(4, 0));
                            ItemStack armor = null;
                            switch (equipmentSlot) {
                                case HEAD:
                                    armor = player.getEquipment().getHelmet();
                                    break;
                                case CHEST:
                                    armor = player.getEquipment().getChestplate();
                                    break;
                                case LEGS:
                                    armor = player.getEquipment().getLeggings();
                                    break;
                                case FEET:
                                    armor = player.getEquipment().getBoots();
                                    break;
                            }
                            if (armor != null) {
                                DisarmerUseEvent event = new DisarmerUseEvent(player, damager, armor);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    switch (equipmentSlot) {
                                        case HEAD:
                                            player.getEquipment().setHelmet(null);
                                            break;
                                        case CHEST:
                                            player.getEquipment().setChestplate(null);
                                            break;
                                        case LEGS:
                                            player.getEquipment().setLeggings(null);
                                            break;
                                        case FEET:
                                            player.getEquipment().setBoots(null);
                                            break;
                                    }
                                    if (Methods.isInventoryFull(player)) {
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
                        Bukkit.getPluginManager().callEvent(event);
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
                            if (!Messages.DISORDERED_ENEMY_HOT_BAR.getMessageNoPrefix().isEmpty()) {
                                damager.sendMessage(Messages.DISORDERED_ENEMY_HOT_BAR.getMessage());
                            }
                        }
                    }
                    //Check if CEPlayer is null as plugins like citizen use Player objects.
                    if (enchantments.contains(CEnchantments.RAGE.getEnchantment()) && cePlayer != null) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.RAGE, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            if (cePlayer.hasRage()) {
                                cePlayer.getRageTask().cancel();
                                if (cePlayer.getRageMultiplier() <= ce.getRageMaxLevel()) {
                                    cePlayer.setRageMultiplier(cePlayer.getRageMultiplier() + (ce.getLevel(item, CEnchantments.RAGE) * 0.1));
                                }
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
                                if (Messages.RAGE_BUILDING.getMessage().length() > 0) {
                                    damager.sendMessage(Messages.RAGE_BUILDING.getMessage());
                                }
                            }
                            cePlayer.setRageTask(new BukkitRunnable() {
                                @Override
                                public void run() {
                                    cePlayer.setRageMultiplier(0.0);
                                    cePlayer.setRage(false);
                                    cePlayer.setRageLevel(0);
                                    if (Messages.RAGE_COOLED_DOWN.getMessage().length() > 0) {
                                        damager.sendMessage(Messages.RAGE_COOLED_DOWN.getMessage());
                                    }
                                }
                            }.runTaskLater(ce.getPlugin(), 80));
                        }
                    }
                    if (en instanceof Player && enchantments.contains(CEnchantments.SKILLSWIPE.getEnchantment())) {
                        Player player = (Player) en;
                        int amount = 4 + ce.getLevel(item, CEnchantments.SKILLSWIPE);
                        if (player.getTotalExperience() > 0) {
                            EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.SKILLSWIPE, item);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                if (CurrencyAPI.getCurrency(player, Currency.XP_TOTAL) >= amount) {
                                    CurrencyAPI.takeCurrency(player, Currency.XP_TOTAL, amount);
                                } else {
                                    player.setTotalExperience(0);
                                }
                                CurrencyAPI.giveCurrency(damager, Currency.XP_TOTAL, amount);
                            }
                        }
                    }
                    if (enchantments.contains(CEnchantments.LIFESTEAL.getEnchantment()) && CEnchantments.LIFESTEAL.chanceSuccessful(item) && damager.getHealth() > 0) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.LIFESTEAL, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            int steal = ce.getLevel(item, CEnchantments.LIFESTEAL);
                            //Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
                            double maxHealth = damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                            if (damager.getHealth() + steal < maxHealth) {
                                damager.setHealth(damager.getHealth() + steal);
                            }
                            if (damager.getHealth() + steal >= maxHealth) {
                                damager.setHealth(maxHealth);
                            }
                        }
                    }
                    if (enchantments.contains(CEnchantments.NUTRITION.getEnchantment()) && CEnchantments.NUTRITION.chanceSuccessful(item)) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.NUTRITION, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
                                SpartanSupport.cancelFastEat(damager);
                            }
                            if (damager.getSaturation() + (2 * ce.getLevel(item, CEnchantments.NUTRITION)) <= 20) {
                                damager.setSaturation(damager.getSaturation() + (2 * ce.getLevel(item, CEnchantments.NUTRITION)));
                            }
                            if (damager.getSaturation() + (2 * ce.getLevel(item, CEnchantments.NUTRITION)) >= 20) {
                                damager.setSaturation(20);
                            }
                        }
                    }
                    if (enchantments.contains(CEnchantments.VAMPIRE.getEnchantment()) && CEnchantments.VAMPIRE.chanceSuccessful(item) && damager.getHealth() > 0) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.VAMPIRE, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            //Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
                            double maxHealth = damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

                            if (damager.getHealth() + e.getDamage() / 2 < maxHealth) {
                                damager.setHealth(damager.getHealth() + e.getDamage() / 2);
                            }
                            if (damager.getHealth() + e.getDamage() / 2 >= maxHealth) {
                                damager.setHealth(maxHealth);
                            }
                        }
                    }
                    if (enchantments.contains(CEnchantments.BLINDNESS.getEnchantment()) && CEnchantments.BLINDNESS.chanceSuccessful(item)) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.BLINDNESS, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            en.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, ce.getLevel(item, CEnchantments.BLINDNESS) - 1));
                        }
                    }
                    if (enchantments.contains(CEnchantments.CONFUSION.getEnchantment()) && CEnchantments.CONFUSION.chanceSuccessful(item)) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.CONFUSION, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            en.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 5 + (ce.getLevel(item, CEnchantments.CONFUSION)) * 20, 0));
                        }
                    }
                    if (enchantments.contains(CEnchantments.DOUBLEDAMAGE.getEnchantment()) && CEnchantments.DOUBLEDAMAGE.chanceSuccessful(item)) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.DOUBLEDAMAGE, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            e.setDamage((e.getDamage() * 2));
                        }
                    }
                    if (enchantments.contains(CEnchantments.EXECUTE.getEnchantment()) && en.getHealth() <= 2) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.EXECUTE, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            damager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3 + (ce.getLevel(item, CEnchantments.EXECUTE)) * 20, 3));
                        }
                    }
                    if (enchantments.contains(CEnchantments.FASTTURN.getEnchantment()) && CEnchantments.FASTTURN.chanceSuccessful(item)) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.FASTTURN, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            e.setDamage(e.getDamage() + (e.getDamage() / 3));
                        }
                    }
                    if (enchantments.contains(CEnchantments.LIGHTWEIGHT.getEnchantment()) && CEnchantments.LIGHTWEIGHT.chanceSuccessful(item)) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.LIGHTWEIGHT, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            damager.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 5 * 20, ce.getLevel(item, CEnchantments.LIGHTWEIGHT) - 1));
                        }
                    }
                    if (enchantments.contains(CEnchantments.OBLITERATE.getEnchantment()) && CEnchantments.OBLITERATE.chanceSuccessful(item)) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.OBLITERATE, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            if (e.getEntity() instanceof Player && SupportedPlugins.SPARTAN.isPluginLoaded()) {
                                SpartanSupport.cancelSpeed((Player) e.getEntity());
                                SpartanSupport.cancelNormalMovements((Player) e.getEntity());
                                SpartanSupport.cancelNoFall((Player) e.getEntity());
                            }
                            e.getEntity().setVelocity(damager.getLocation().getDirection().multiply(2).setY(1.25));
                        }
                    }
                    if (enchantments.contains(CEnchantments.PARALYZE.getEnchantment()) && CEnchantments.PARALYZE.chanceSuccessful(item)) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.PARALYZE, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            Location loc = en.getLocation();
                            loc.getWorld().spigot().strikeLightningEffect(loc, true);
                            int lightningSoundRange = Files.CONFIG.getFile().getInt("Settings.EnchantmentOptions.Lightning-Sound-Range", 160);
                            try {
                                loc.getWorld().playSound(loc, ce.getSound("ENTITY_LIGHTNING_BOLT_IMPACT"), (float) lightningSoundRange / 16f, 1);
                            } catch (Exception ignore) {}

                            if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
                                SpartanSupport.cancelNoSwing(damager);
                            }

                            for (LivingEntity entity : Methods.getNearbyLivingEntities(loc, 2D, damager)) {
                                EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(damager, entity, DamageCause.CUSTOM, 5D);
                                ce.addIgnoredEvent(damageByEntityEvent);
                                ce.addIgnoredUUID(damager.getUniqueId());
                                Bukkit.getPluginManager().callEvent(damageByEntityEvent);
                                if (!damageByEntityEvent.isCancelled() && support.allowsPVP(entity.getLocation()) && !support.isFriendly(damager, entity)) {
                                    entity.damage(5D);
                                }
                                ce.removeIgnoredEvent(damageByEntityEvent);
                                ce.removeIgnoredUUID(damager.getUniqueId());
                            }
                            en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 2));
                            en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3 * 20, 2));
                        }
                    }
                    if (enchantments.contains(CEnchantments.SLOWMO.getEnchantment()) && CEnchantments.SLOWMO.chanceSuccessful(item)) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.SLOWMO, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, ce.getLevel(item, CEnchantments.SLOWMO)));
                        }
                    }
                    if (enchantments.contains(CEnchantments.SNARE.getEnchantment()) && CEnchantments.SNARE.chanceSuccessful(item)) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.SNARE, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 0));
                            en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3 * 20, 0));
                        }
                    }
                    if (enchantments.contains(CEnchantments.TRAP.getEnchantment()) && CEnchantments.TRAP.chanceSuccessful(item)) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.TRAP, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 2));
                        }
                    }
                    if (enchantments.contains(CEnchantments.VIPER.getEnchantment()) && CEnchantments.VIPER.chanceSuccessful(item)) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.VIPER, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5 * 20, ce.getLevel(item, CEnchantments.VIPER)));
                        }
                    }
                    if (enchantments.contains(CEnchantments.WITHER.getEnchantment()) && CEnchantments.WITHER.chanceSuccessful(item)) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.WITHER, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            en.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 2 * 20, 2));
                        }
                    }
                    if (enchantments.contains(CEnchantments.FAMISHED.getEnchantment()) && CEnchantments.FAMISHED.chanceSuccessful(item)) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.FAMISHED, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            en.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 10 * 20, 1));
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (e.getEntity().getKiller() instanceof Player) {
            Player damager = e.getEntity().getKiller();
            Player player = e.getEntity();
            ItemStack item = Methods.getItemInHand(damager);
            if (ce.hasEnchantment(item, CEnchantments.HEADLESS) && CEnchantments.HEADLESS.chanceSuccessful(item)) {
                EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.HEADLESS, item);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    ItemStack head = new ItemBuilder().setMaterial("PLAYER_HEAD").setPlayerName(player.getName()).build();
                    e.getDrops().add(head);
                }
            }
            if (CEnchantments.REVENGE.isActivated()) {
                for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                    if (support.isFriendly(entity, player)) {
                        Player ally = (Player) entity;
                        ItemStack itemStack = Methods.getItemInHand(ally);
                        if (ce.hasEnchantment(itemStack, CEnchantments.REVENGE)) {
                            ally.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 1));
                            ally.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 0));
                            ally.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 5 * 20, 1));
                            
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() instanceof Player) {
            Player damager = e.getEntity().getKiller();
            ItemStack item = Methods.getItemInHand(damager);
            if (ce.hasEnchantments(item)) {
                if (ce.hasEnchantment(item, CEnchantments.INQUISITIVE) && CEnchantments.INQUISITIVE.chanceSuccessful(item)) {
                    EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.INQUISITIVE, item);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        e.setDroppedExp(e.getDroppedExp() * (ce.getLevel(item, CEnchantments.INQUISITIVE) + 1));
                    }
                }
                //The entity that is killed is a player.
                if (e.getEntity() instanceof Player && ce.hasEnchantment(item, CEnchantments.CHARGE)) {
                    EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.CHARGE, item);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        int radius = 4 + ce.getLevel(item, CEnchantments.CHARGE);
                        damager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 1));
                        for (Entity entity : damager.getNearbyEntities(radius, radius, radius)) {
                            if (support.isFriendly(entity, damager)) {
                                ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 1));
                            }
                        }
                    }
                }
            }
        }
    }
    
    private EquipmentSlot getSlot(int slot) {
        switch (slot) {
            case 1:
                return EquipmentSlot.CHEST;
            case 2:
                return EquipmentSlot.LEGS;
            case 3:
                return EquipmentSlot.FEET;
            default:
                return EquipmentSlot.HEAD;
        }
    }
}
package com.badbones69.crazyenchantments.enchantments;

import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.api.managers.BowEnchantmentManager;
import com.badbones69.crazyenchantments.api.multisupport.anticheats.NoCheatPlusSupport;
import com.badbones69.crazyenchantments.api.multisupport.anticheats.SpartanSupport;
import com.badbones69.crazyenchantments.api.objects.*;
import com.badbones69.crazyenchantments.utilities.BowUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Bows implements Listener {

    private final PluginSupport pluginSupport = PluginSupport.INSTANCE;

    private final CrazyManager crazyManager = CrazyManager.getInstance();

    private final BowUtils bowUtils = BowUtils.getInstance();

    private final BowEnchantmentManager bowEnchantmentManager = BowEnchantmentManager.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBowShoot(final EntityShootBowEvent e) {
        if (e.isCancelled() || crazyManager.isIgnoredEvent(e) || crazyManager.isIgnoredUUID(e.getEntity().getUniqueId())) return;

        ItemStack bow = e.getBow();
        Entity entity = e.getEntity();

        if (e.getProjectile() instanceof Arrow arrow && crazyManager.hasEnchantments(bow) && pluginSupport.allowsCombat(entity.getLocation())) {

            // Add the arrow to the list.
            bowUtils.addArrow(arrow, entity, bow);

            if (bowUtils.isBowEnchantActive(CEnchantments.MULTIARROW, bow)) {
                int power = crazyManager.getLevel(bow, CEnchantments.MULTIARROW);

                if (entity instanceof Player) {

                    EnchantmentUseEvent event = new EnchantmentUseEvent((Player) entity, CEnchantments.MULTIARROW, bow);
                    entity.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        for (int i = 1; i <= power; i++) {
                            // Handle the spawned arrows.
                            bowUtils.spawnedArrow(entity, e.getProjectile(), bow);
                        }
                    }

                    return;
                }

                for (int i = 1; i <= power; i++) {
                    // Handle the spawned arrows.
                    bowUtils.spawnedArrow(entity, e.getProjectile(), bow);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLand(ProjectileHitEvent e) {
            if (e.getEntity() instanceof Arrow && pluginSupport.allowsCombat(e.getEntity().getLocation())) {
                EnchantedArrow arrow = bowUtils.enchantedArrow((Arrow) e.getEntity());

                // Spawn webs related to STICKY_SHOT
                bowUtils.spawnWebs(e.getEntity(), e.getHitEntity());

                if (CEnchantments.BOOM.isActivated() && arrow != null) {
                    if (arrow.hasEnchantment(CEnchantments.BOOM) && CEnchantments.BOOM.chanceSuccessful(arrow.getBow())) {
                        Methods.explode(arrow.getShooter(), arrow.getArrow());
                        arrow.getArrow().remove();
                    }
                }

                if (CEnchantments.LIGHTNING.isActivated() && arrow != null) {
                    if (arrow.hasEnchantment(CEnchantments.LIGHTNING) && CEnchantments.LIGHTNING.chanceSuccessful(arrow.getBow())) {
                        Location location = arrow.getArrow().getLocation();

                        Player shooter = (Player) arrow.getShooter();
                        location.getWorld().spigot().strikeLightningEffect(location, true);

                        int lightningSoundRange = Files.CONFIG.getFile().getInt("Settings.EnchantmentOptions.Lightning-Sound-Range", 160);

                        try {
                            location.getWorld().playSound(location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, (float) lightningSoundRange / 16f, 1);
                        } catch (Exception ignore) {}

                        // AntiCheat Support.

                        if (PluginSupport.SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) {
                            NoCheatPlusSupport.allowPlayer(shooter);
                        }

                        if (PluginSupport.SupportedPlugins.SPARTAN.isPluginLoaded()) {
                            SpartanSupport.cancelNoSwing(shooter);
                        }

                        for (LivingEntity entity : Methods.getNearbyLivingEntities(location, 2D, arrow.getArrow())) {
                            EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(shooter, entity, DamageCause.CUSTOM, 5D);

                            crazyManager.addIgnoredEvent(damageByEntityEvent);
                            crazyManager.addIgnoredUUID(shooter.getUniqueId());
                            shooter.getServer().getPluginManager().callEvent(damageByEntityEvent);

                            if (!damageByEntityEvent.isCancelled() && !pluginSupport.isFriendly(arrow.getShooter(), entity)
                                    && !arrow.getShooter().getUniqueId().equals(entity.getUniqueId())) {
                                entity.damage(5D);
                            }

                            crazyManager.removeIgnoredEvent(damageByEntityEvent);
                            crazyManager.removeIgnoredUUID(shooter.getUniqueId());
                        }

                        if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) {
                            NoCheatPlusSupport.denyPlayer(shooter);
                        }
                    }
                }

                // Removes the arrow from the list after 5 ticks. This is done because the onArrowDamage event needs the arrow in the list, so it can check.
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        bowUtils.removeArrow();
                    }
                }.runTaskLater(crazyManager.getPlugin(), 5);
            }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArrowDamage(EntityDamageByEntityEvent e) {
        if (!crazyManager.isIgnoredEvent(e) && e.getDamager() instanceof Arrow && e.getEntity() instanceof LivingEntity entity) {
            EnchantedArrow arrow = bowUtils.enchantedArrow((Arrow) e.getDamager());

            if (arrow != null && pluginSupport.allowsCombat(arrow.getArrow().getLocation())) {
                ItemStack bow = arrow.getBow();
                // Damaged player is friendly.

                if (CEnchantments.DOCTOR.isActivated() && arrow.hasEnchantment(CEnchantments.DOCTOR) && pluginSupport.isFriendly(arrow.getShooter(), e.getEntity())) {
                    int heal = 1 + arrow.getLevel(CEnchantments.DOCTOR);
                    // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
                    double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

                    if (entity.getHealth() < maxHealth) {
                        if (entity instanceof Player) {
                            EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.DOCTOR, bow);
                            entity.getServer().getPluginManager().callEvent(event);

                            if (!event.isCancelled()) {
                                if (entity.getHealth() + heal < maxHealth) {
                                    entity.setHealth(entity.getHealth() + heal);
                                }

                                if (entity.getHealth() + heal >= maxHealth) {
                                    entity.setHealth(maxHealth);
                                }
                            }
                        } else {
                            if (entity.getHealth() + heal < maxHealth) {
                                entity.setHealth(entity.getHealth() + heal);
                            }

                            if (entity.getHealth() + heal >= maxHealth) {
                                entity.setHealth(maxHealth);
                            }
                        }
                    }
                }

                // Damaged player is an enemy.
                if (!e.isCancelled() && !pluginSupport.isFriendly(arrow.getShooter(), entity)) {

                    bowUtils.spawnWebs(arrow.getShooter(), e.getEntity());

                    if (CEnchantments.PULL.isActivated() && arrow.hasEnchantment(CEnchantments.PULL) && CEnchantments.PULL.chanceSuccessful(bow)) {
                        Vector v = arrow.getShooter().getLocation().toVector().subtract(entity.getLocation().toVector()).normalize().multiply(3);

                        if (entity instanceof Player) {
                            EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.PULL, bow);
                            entity.getServer().getPluginManager().callEvent(event);

                            Player player = (Player) e.getEntity();

                            if (!event.isCancelled()) {

                                if (PluginSupport.SupportedPlugins.SPARTAN.isPluginLoaded()) {
                                    SpartanSupport.cancelSpeed(player);
                                    SpartanSupport.cancelNormalMovements(player);
                                    SpartanSupport.cancelNoFall(player);
                                }

                                entity.setVelocity(v);
                            }
                        } else {
                            entity.setVelocity(v);
                        }
                    }

                    for (BowEnchantment bowEnchantment : bowEnchantmentManager.getBowEnchantments()) {
                        CEnchantments enchantment = bowEnchantment.getEnchantment();

                        // No need to check if its active as if it is not then Bow Manager doesn't add it to the list of enchantments.
                        if (arrow.hasEnchantment(enchantment) && enchantment.chanceSuccessful(bow)) {

                            if (entity instanceof Player) {
                                EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), enchantment, bow);
                                entity.getServer().getPluginManager().callEvent(event);

                                if (event.isCancelled()) {
                                    // If the EnchantmentUseEvent is cancelled then no need to keep going with this enchantment.
                                    continue;
                                }
                            }

                            // Code is ran if entity is not a player or if the entity is a player and the EnchantmentUseEvent is not cancelled.
                            // Checks if the enchantment is for potion effects or for damage amplifying.
                            if (bowEnchantment.isPotionEnchantment()) {
                                for (PotionEffects effect : bowEnchantment.getPotionEffects()) {
                                    entity.addPotionEffect(new PotionEffect(effect.getPotionEffect(), effect.getDuration(), (bowEnchantment.isLevelAddedToAmplifier() ? arrow.getLevel(enchantment) : 0) + effect.getAmplifier()));
                                }
                            } else {
                                // Sets the new damage amplifier. If isLevelAddedToAmplifier() is true it adds the level to the damage amplifier.
                                e.setDamage(e.getDamage() * ((bowEnchantment.isLevelAddedToAmplifier() ? arrow.getLevel(enchantment) : 0) + bowEnchantment.getDamageAmplifier()));
                            }
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onWebBreak(BlockBreakEvent e) {
        if (!crazyManager.isIgnoredEvent(e) && bowUtils.getWeBlocks().contains(e.getBlock())) e.setCancelled(true);
    }
}
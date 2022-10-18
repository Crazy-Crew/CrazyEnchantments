package com.badbones69.crazyenchantments.enchantments;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.api.managers.BowEnchantmentManager;
import com.badbones69.crazyenchantments.api.support.anticheats.NoCheatPlusSupport;
import com.badbones69.crazyenchantments.api.objects.*;
import com.badbones69.crazyenchantments.api.support.anticheats.SpartanSupport;
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
import org.bukkit.util.Vector;

public class BowEnchantments implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    // Plugin Support.
    private final PluginSupport pluginSupport = starter.getPluginSupport();

    private final NoCheatPlusSupport noCheatPlusSupport = starter.getNoCheatPlusSupport();
    private final SpartanSupport spartanSupport = starter.getSpartanSupport();

    // Plugin Managers.
    private final BowEnchantmentManager bowEnchantmentManager = starter.getBowEnchantmentManager();

    private final BowUtils bowUtils = starter.getBowUtils();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBowShoot(final EntityShootBowEvent event) {
        if (crazyManager.isIgnoredEvent(event) || crazyManager.isIgnoredUUID(event.getEntity().getUniqueId())) return;
        if (!(event.getProjectile() instanceof Arrow arrow)) return;

        ItemStack bow = event.getBow();
        Entity entity = event.getEntity();

        if (!crazyManager.hasEnchantments(bow)) return;
        if (bowUtils.allowsCombat(entity)) return;
        if (arrow.getShooter() instanceof Player) return;

        // Add the arrow to the list.
        bowUtils.addArrow(arrow, entity, bow);

        if (!bowUtils.isBowEnchantActive(CEnchantments.MULTIARROW, bow, arrow)) return;
        int power = crazyManager.getLevel(bow, CEnchantments.MULTIARROW);

        if (entity instanceof Player) {
            EnchantmentUseEvent useEvent = new EnchantmentUseEvent((Player) entity, CEnchantments.MULTIARROW, bow);
            plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) {
                for (int i = 1; i <= power; i++) {
                    // Handle the spawned arrows.
                    bowUtils.spawnArrows(entity, arrow, bow);
                }
            }

            return;
        }

        for (int i = 1; i <= power; i++) {
            // Handle the spawned arrows.
            bowUtils.spawnArrows(entity, arrow, bow);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLand(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow entityArrow)) return;
        if (bowUtils.allowsCombat(e.getEntity())) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;
        EnchantedArrow arrow = bowUtils.enchantedArrow(entityArrow);

        // Spawn webs related to STICKY_SHOT.
        bowUtils.spawnWebs(e.getEntity(), e.getHitEntity(), arrow, entityArrow);

        if (CEnchantments.BOOM.isActivated() && arrow != null) {
            if (arrow.hasEnchantment(CEnchantments.BOOM) && CEnchantments.BOOM.chanceSuccessful(arrow.getBow())) {
                methods.explode(arrow.getShooter(), arrow.getArrow());
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
                if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) noCheatPlusSupport.allowPlayer(shooter);

                if (SupportedPlugins.SPARTAN.isPluginLoaded()) spartanSupport.cancelNoSwing(shooter);

                for (LivingEntity entity : methods.getNearbyLivingEntities(location, 2D, arrow.getArrow())) {
                    EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(shooter, entity, DamageCause.CUSTOM, 5D);

                    crazyManager.addIgnoredEvent(damageByEntityEvent);
                    crazyManager.addIgnoredUUID(shooter.getUniqueId());
                    shooter.getServer().getPluginManager().callEvent(damageByEntityEvent);

                    if (!damageByEntityEvent.isCancelled() && !pluginSupport.isFriendly(arrow.getShooter(), entity) && !arrow.getShooter().getUniqueId().equals(entity.getUniqueId())) entity.damage(5D);

                    crazyManager.removeIgnoredEvent(damageByEntityEvent);
                    crazyManager.removeIgnoredUUID(shooter.getUniqueId());
                }

                if (PluginSupport.SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) noCheatPlusSupport.denyPlayer(shooter);
            }
        }

        // Removes the arrow from the list after 5 ticks. This is done because the onArrowDamage event needs the arrow in the list, so it can check.
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> bowUtils.removeArrow(arrow), 5);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArrowDamage(EntityDamageByEntityEvent e) {
        if (crazyManager.isIgnoredEvent(e)) return;
        if (!(e.getDamager() instanceof Arrow entityArrow)) return;
        if (!(e.getEntity() instanceof LivingEntity entity)) return;

        EnchantedArrow arrow = bowUtils.enchantedArrow(entityArrow);
        if (arrow == null) return;

        if (!pluginSupport.allowCombat(arrow.getArrow().getLocation())) return;
        ItemStack bow = arrow.getBow();
        // Damaged player is friendly.

        if (CEnchantments.DOCTOR.isActivated() && arrow.hasEnchantment(CEnchantments.DOCTOR) && pluginSupport.isFriendly(arrow.getShooter(), e.getEntity())) {
            int heal = 1 + arrow.getLevel(CEnchantments.DOCTOR);
            // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
            double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

            if (entity.getHealth() < maxHealth) {
                if (entity instanceof Player) {
                    EnchantmentUseEvent useEvent = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.DOCTOR, bow);
                    plugin.getServer().getPluginManager().callEvent(useEvent);

                    if (!useEvent.isCancelled()) {
                        if (entity.getHealth() + heal < maxHealth) entity.setHealth(entity.getHealth() + heal);

                        if (entity.getHealth() + heal >= maxHealth) entity.setHealth(maxHealth);
                    }
                } else {
                    if (entity.getHealth() + heal < maxHealth) entity.setHealth(entity.getHealth() + heal);

                    if (entity.getHealth() + heal >= maxHealth) entity.setHealth(maxHealth);
                }
            }
        }

        // Damaged player is an enemy.
        if (!pluginSupport.isFriendly(arrow.getShooter(), entity)) {

            bowUtils.spawnWebs(arrow.getShooter(), e.getEntity(), arrow, entityArrow);

            if (CEnchantments.PULL.isActivated() && arrow.hasEnchantment(CEnchantments.PULL) && CEnchantments.PULL.chanceSuccessful(bow)) {
                Vector v = arrow.getShooter().getLocation().toVector().subtract(entity.getLocation().toVector()).normalize().multiply(3);

                if (entity instanceof Player) {
                    EnchantmentUseEvent useEvent = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.PULL, bow);
                    plugin.getServer().getPluginManager().callEvent(useEvent);

                    Player player = (Player) e.getEntity();

                    if (!useEvent.isCancelled()) {
                        if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
                            spartanSupport.cancelSpeed(player);
                            spartanSupport.cancelNormalMovements(player);
                            spartanSupport.cancelNoFall(player);
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
                        EnchantmentUseEvent useEvent = new EnchantmentUseEvent((Player) e.getEntity(), enchantment, bow);
                        plugin.getServer().getPluginManager().callEvent(useEvent);

                        if (useEvent.isCancelled()) continue;
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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWebBreak(BlockBreakEvent event) {
        if (!crazyManager.isIgnoredEvent(event) && bowUtils.getWebBlocks().contains(event.getBlock())) event.setCancelled(true);
    }
}
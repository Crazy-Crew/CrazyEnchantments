package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.PluginSupport;
import com.badbones69.crazyenchantments.paper.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.paper.api.managers.BowEnchantmentManager;
import com.badbones69.crazyenchantments.paper.api.objects.BowEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.EnchantedArrow;
import com.badbones69.crazyenchantments.paper.api.objects.PotionEffects;
import com.badbones69.crazyenchantments.paper.api.support.anticheats.NoCheatPlusSupport;
import com.badbones69.crazyenchantments.paper.api.support.anticheats.SpartanSupport;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.utilities.BowUtils;
import com.badbones69.crazyenchantments.paper.utilities.misc.EventUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class BowEnchantments implements Listener {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Starter starter = this.plugin.getStarter();

    private final Methods methods = this.starter.getMethods();

    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    // Plugin Support.
    private final PluginSupport pluginSupport = this.starter.getPluginSupport();

    private final NoCheatPlusSupport noCheatPlusSupport = this.starter.getNoCheatPlusSupport();
    private final SpartanSupport spartanSupport = this.starter.getSpartanSupport();

    // Plugin Managers.
    private final BowEnchantmentManager bowEnchantmentManager = this.starter.getBowEnchantmentManager();

    private final BowUtils bowUtils = this.starter.getBowUtils();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBowShoot(final EntityShootBowEvent event) {
        if (EventUtils.isIgnoredEvent(event) || EventUtils.isIgnoredUUID(event.getEntity().getUniqueId())) return;
        if (!(event.getProjectile() instanceof Arrow arrow)) return;

        ItemStack bow = event.getBow();
        Entity entity = event.getEntity();

        if (!this.enchantmentBookSettings.hasEnchantments(bow)) return;
        if (this.bowUtils.allowsCombat(entity)) return;
        if (!(arrow.getShooter() instanceof Player)) return;

        // Add the arrow to the list.
        this.bowUtils.addArrow(arrow, entity, bow);

        if (!this.bowUtils.isBowEnchantActive(CEnchantments.MULTIARROW, bow, arrow)) return;
        int power = this.crazyManager.getLevel(bow, CEnchantments.MULTIARROW);

        if (entity instanceof Player) {
            EnchantmentUseEvent useEvent = new EnchantmentUseEvent((Player) entity, CEnchantments.MULTIARROW, bow);
            this.plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) {
                for (int i = 1; i <= power; i++) {
                    // Handle the spawned arrows.
                    this.bowUtils.spawnArrows(entity, arrow, bow);
                }
            }

            return;
        }

        for (int i = 1; i <= power; i++) {
            // Handle the spawned arrows.
            this.bowUtils.spawnArrows(entity, arrow, bow);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLand(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow entityArrow)) return;
        if (this.bowUtils.allowsCombat(e.getEntity())) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;
        EnchantedArrow arrow = this.bowUtils.enchantedArrow(entityArrow);

        // Spawn webs related to STICKY_SHOT.
        this.bowUtils.spawnWebs(e.getHitEntity(), arrow, entityArrow);

        if (CEnchantments.BOOM.isActivated() && arrow != null) {
            if (arrow.hasEnchantment(CEnchantments.BOOM) && CEnchantments.BOOM.chanceSuccessful(arrow.getBow())) {
                this.methods.explode(arrow.getShooter(), arrow.getArrow());
                arrow.getArrow().remove();
            }
        }

        if (CEnchantments.LIGHTNING.isActivated() && arrow != null) {
            if (arrow.hasEnchantment(CEnchantments.LIGHTNING) && CEnchantments.LIGHTNING.chanceSuccessful(arrow.getBow())) {
                Location location = arrow.getArrow().getLocation();

                Player shooter = (Player) arrow.getShooter();

                location.getWorld().spigot().strikeLightningEffect(location, true);

                int lightningSoundRange = Files.CONFIG.getFile().getInt("Settings.EnchantmentOptions.Lightning-Sound-Range", 160);

                location.getWorld().playSound(location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.AMBIENT, (float) lightningSoundRange / 16f, 1f);

                // AntiCheat Support.
                if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) this.noCheatPlusSupport.allowPlayer(shooter);

                if (SupportedPlugins.SPARTAN.isPluginLoaded()) this.spartanSupport.cancelNoSwing(shooter);

                for (LivingEntity entity : this.methods.getNearbyLivingEntities(2D, arrow.getArrow())) {
                    EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(shooter, entity, DamageCause.CUSTOM, 5D);

                    EventUtils.addIgnoredEvent(damageByEntityEvent);
                    EventUtils.addIgnoredUUID(shooter.getUniqueId());
                    shooter.getServer().getPluginManager().callEvent(damageByEntityEvent);

                    if (!damageByEntityEvent.isCancelled() && !this.pluginSupport.isFriendly(arrow.getShooter(), entity) && !arrow.getShooter().getUniqueId().equals(entity.getUniqueId())) entity.damage(5D);

                    EventUtils.removeIgnoredEvent(damageByEntityEvent);
                    EventUtils.removeIgnoredUUID(shooter.getUniqueId());
                }

                if (PluginSupport.SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) this.noCheatPlusSupport.denyPlayer(shooter);
            }
        }

        // Removes the arrow from the list after 5 ticks. This is done because the onArrowDamage event needs the arrow in the list, so it can check.
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> this.bowUtils.removeArrow(arrow), 5);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArrowDamage(EntityDamageByEntityEvent event) {
        if (EventUtils.isIgnoredEvent(event)) return;
        if (!(event.getDamager() instanceof Arrow entityArrow)) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        EnchantedArrow arrow = this.bowUtils.enchantedArrow(entityArrow);
        if (arrow == null) return;

        if (!this.pluginSupport.allowCombat(arrow.getArrow().getLocation())) return;
        ItemStack bow = arrow.getBow();
        // Damaged player is friendly.

        if (CEnchantments.DOCTOR.isActivated() && arrow.hasEnchantment(CEnchantments.DOCTOR) && this.pluginSupport.isFriendly(arrow.getShooter(), event.getEntity())) {
            int heal = 1 + arrow.getLevel(CEnchantments.DOCTOR);
            // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
            double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

            if (entity.getHealth() < maxHealth) {
                if (entity instanceof Player) {
                    EnchantmentUseEvent useEvent = new EnchantmentUseEvent((Player) event.getEntity(), CEnchantments.DOCTOR, bow);
                    this.plugin.getServer().getPluginManager().callEvent(useEvent);

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
        if (!this.pluginSupport.isFriendly(arrow.getShooter(), entity)) {
            this.bowUtils.spawnWebs(event.getEntity(), arrow, entityArrow);

            if (CEnchantments.PULL.isActivated() && arrow.hasEnchantment(CEnchantments.PULL) && CEnchantments.PULL.chanceSuccessful(bow)) {
                Vector v = arrow.getShooter().getLocation().toVector().subtract(entity.getLocation().toVector()).normalize().multiply(3);

                if (entity instanceof Player) {
                    EnchantmentUseEvent useEvent = new EnchantmentUseEvent((Player) event.getEntity(), CEnchantments.PULL, bow);
                    this.plugin.getServer().getPluginManager().callEvent(useEvent);

                    Player player = (Player) event.getEntity();

                    if (!useEvent.isCancelled()) {
                        if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
                            this.spartanSupport.cancelSpeed(player);
                            this.spartanSupport.cancelNormalMovements(player);
                            this.spartanSupport.cancelNoFall(player);
                        }

                        entity.setVelocity(v);
                    }
                } else {
                    entity.setVelocity(v);
                }
            }

            for (BowEnchantment bowEnchantment : this.bowEnchantmentManager.getBowEnchantments()) {
                CEnchantments enchantment = bowEnchantment.getEnchantment();

                // No need to check if its active as if it is not then Bow Manager doesn't add it to the list of enchantments.
                if (arrow.hasEnchantment(enchantment) && enchantment.chanceSuccessful(bow)) {

                    if (entity instanceof Player player) {
                        EnchantmentUseEvent useEvent = new EnchantmentUseEvent(player, enchantment, bow);
                        this.plugin.getServer().getPluginManager().callEvent(useEvent);

                        if (useEvent.isCancelled()) continue;
                    }

                    // Code is ran if entity is not a player or if the entity is a player and the EnchantmentUseEvent is not cancelled.
                    // Checks if the enchantment is for potion effects or for damage amplifying.
                    if (bowEnchantment.isPotionEnchantment()) {
                        for (PotionEffects effect : bowEnchantment.getPotionEffects()) {
                            entity.addPotionEffect(new PotionEffect(effect.potionEffect(), effect.duration(), (bowEnchantment.isLevelAddedToAmplifier() ? arrow.getLevel(enchantment) : 0) + effect.amplifier()));
                        }
                    } else {
                        // Sets the new damage amplifier. If isLevelAddedToAmplifier() is true it adds the level to the damage amplifier.
                        event.setDamage(event.getDamage() * ((bowEnchantment.isLevelAddedToAmplifier() ? arrow.getLevel(enchantment) : 0) + bowEnchantment.getDamageAmplifier()));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWebBreak(BlockBreakEvent event) {
        if (!EventUtils.isIgnoredEvent(event) && this.bowUtils.getWebBlocks().contains(event.getBlock())) event.setCancelled(true);
    }
}
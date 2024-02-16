package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.PluginSupport;
import com.badbones69.crazyenchantments.paper.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.paper.api.managers.BowEnchantmentManager;
import com.badbones69.crazyenchantments.paper.api.objects.BowEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.EnchantedArrow;
import com.badbones69.crazyenchantments.paper.api.support.anticheats.NoCheatPlusSupport;
import com.badbones69.crazyenchantments.paper.api.support.anticheats.SpartanSupport;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.utilities.BowUtils;
import com.badbones69.crazyenchantments.paper.utilities.misc.EnchantUtils;
import com.badbones69.crazyenchantments.paper.utilities.misc.EventUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Arrow;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.Map;

public class BowEnchantments implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    // Plugin Support.
    private final PluginSupport pluginSupport = starter.getPluginSupport();

    private final NoCheatPlusSupport noCheatPlusSupport = starter.getNoCheatPlusSupport();
    private final SpartanSupport spartanSupport = starter.getSpartanSupport();

    // Plugin Managers.
    private final BowEnchantmentManager bowEnchantmentManager = starter.getBowEnchantmentManager();

    private final BowUtils bowUtils = starter.getBowUtils();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBowShoot(final EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player entity)) return;
        if (EventUtils.isIgnoredEvent(event) || EventUtils.isIgnoredUUID(event.getEntity().getUniqueId())) return;
        if (!(event.getProjectile() instanceof Arrow arrow)) return;

        ItemStack bow = event.getBow();

        if (!bowUtils.allowsCombat(entity)) return;

        Map<CEnchantment, Integer> enchants = enchantmentBookSettings.getEnchantments(bow);
        if (enchants.isEmpty()) return;

        // Add the arrow to the list.
        bowUtils.addArrow(arrow, bow, enchants);

        // MultiArrow only code below.
        if (!enchants.containsKey(CEnchantments.MULTIARROW.getEnchantment())) return;

        int power = enchants.get(CEnchantments.MULTIARROW.getEnchantment());

        EnchantmentUseEvent useEvent = new EnchantmentUseEvent(entity, CEnchantments.MULTIARROW, bow);
        plugin.getServer().getPluginManager().callEvent(useEvent);

        if (!useEvent.isCancelled()) for (int i = 1; i <= power; i++) bowUtils.spawnArrows(entity, arrow, bow);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLand(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player shooter)) return;
        if (!(event.getEntity() instanceof Arrow entityArrow)) return;
        if (!bowUtils.allowsCombat(event.getEntity())) return;
        EnchantedArrow arrow = bowUtils.getEnchantedArrow(entityArrow);
        if (arrow == null) return;

        // Spawn webs related to STICKY_SHOT.
        bowUtils.spawnWebs(event.getHitEntity(), arrow, entityArrow);

        if (EnchantUtils.isEventActive(CEnchantments.BOOM, shooter, arrow.bow(), arrow.enchantments())) {
            methods.explode(arrow.getShooter(), arrow.arrow());
            arrow.arrow().remove();
        }

        if (EnchantUtils.isEventActive(CEnchantments.LIGHTNING, shooter, arrow.bow(), arrow.enchantments())) {
            Location location = arrow.arrow().getLocation();

            location.getWorld().strikeLightningEffect(location);

            int lightningSoundRange = Files.CONFIG.getFile().getInt("Settings.EnchantmentOptions.Lightning-Sound-Range", 160);

            try {
                location.getWorld().playSound(location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, (float) lightningSoundRange / 16f, 1);
            } catch (Exception ignore) {
            }

            // AntiCheat Support.
            if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) noCheatPlusSupport.allowPlayer(shooter);

            if (SupportedPlugins.SPARTAN.isPluginLoaded()) spartanSupport.cancelNoSwing(shooter);

            for (LivingEntity entity : methods.getNearbyLivingEntities(2D, arrow.arrow())) {
                EntityDamageByEntityEvent damageByEntityEvent = Methods.entityDamageByEntityEvent(shooter, entity, DamageCause.LIGHTNING, DamageType.LIGHTNING_BOLT);

                EventUtils.addIgnoredEvent(damageByEntityEvent);
                EventUtils.addIgnoredUUID(shooter.getUniqueId());
                shooter.getServer().getPluginManager().callEvent(damageByEntityEvent);

                if (!damageByEntityEvent.isCancelled() && !pluginSupport.isFriendly(arrow.getShooter(), entity) && !arrow.getShooter().getUniqueId().equals(entity.getUniqueId()))
                    entity.damage(5D);

                EventUtils.removeIgnoredEvent(damageByEntityEvent);
                EventUtils.removeIgnoredUUID(shooter.getUniqueId());
            }

            if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) noCheatPlusSupport.denyPlayer(shooter);
        }

        // Removes the arrow from the list after 5 ticks. This is done because the onArrowDamage event needs the arrow in the list, so it can check.
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> bowUtils.removeArrow(arrow), 5);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArrowDamage(EntityDamageByEntityEvent event) {
        if (EventUtils.isIgnoredEvent(event)) return;
        if (!(event.getDamager() instanceof Arrow entityArrow)) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        EnchantedArrow arrow = bowUtils.getEnchantedArrow(entityArrow);
        if (arrow == null) return;

        if (!pluginSupport.allowCombat(arrow.arrow().getLocation())) return;
        ItemStack bow = arrow.bow();
        // Damaged player is friendly.

        if (EnchantUtils.isEventActive(CEnchantments.DOCTOR, arrow.getShooter(), arrow.bow(), arrow.enchantments()) && pluginSupport.isFriendly(arrow.getShooter(), event.getEntity())) {
            int heal = 1 + arrow.getLevel(CEnchantments.DOCTOR);
            // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
            double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

            if (entity.getHealth() < maxHealth) {
                if (entity.getHealth() + heal < maxHealth) entity.setHealth(entity.getHealth() + heal);
                if (entity.getHealth() + heal >= maxHealth) entity.setHealth(maxHealth);
            }
        }

        // Damaged player is an enemy.
        if (pluginSupport.isFriendly(arrow.getShooter(), entity)) return;

        bowUtils.spawnWebs(event.getEntity(), arrow, entityArrow);

        if (EnchantUtils.isEventActive(CEnchantments.PULL, arrow.getShooter(), arrow.bow(), arrow.enchantments())) {
            Vector v = arrow.getShooter().getLocation().toVector().subtract(entity.getLocation().toVector()).normalize().multiply(3);

            if (entity instanceof Player player && SupportedPlugins.SPARTAN.isPluginLoaded()) {
                spartanSupport.cancelSpeed(player);
                spartanSupport.cancelNormalMovements(player);
                spartanSupport.cancelNoFall(player);
            }
            entity.setVelocity(v);
        }

        for (BowEnchantment bowEnchantment : bowEnchantmentManager.getBowEnchantments()) {
            CEnchantments enchantment = bowEnchantment.getEnchantment();

            if (!EnchantUtils.isEventActive(enchantment, arrow.getShooter(), arrow.bow(), arrow.enchantments())) continue;

            if (bowEnchantment.isPotionEnchantment()) {
                bowEnchantment.getPotionEffects().forEach(effect -> entity.addPotionEffect(new PotionEffect(effect.potionEffect(), effect.duration(),
                        (bowEnchantment.isLevelAddedToAmplifier() ? arrow.getLevel(enchantment) : 0) + effect.amplifier())));
            } else {
                event.setDamage(event.getDamage() * ((bowEnchantment.isLevelAddedToAmplifier() ? arrow.getLevel(enchantment) : 0) + bowEnchantment.getDamageAmplifier()));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWebBreak(BlockBreakEvent event) {
        if (!EventUtils.isIgnoredEvent(event) && bowUtils.getWebBlocks().contains(event.getBlock())) event.setCancelled(true);
    }
}
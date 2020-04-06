package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.api.managers.BowEnchantmentManager;
import me.badbones69.crazyenchantments.api.objects.*;
import me.badbones69.crazyenchantments.multisupport.Support;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import me.badbones69.crazyenchantments.multisupport.Version;
import me.badbones69.crazyenchantments.multisupport.anticheats.AACSupport;
import me.badbones69.crazyenchantments.multisupport.anticheats.NoCheatPlusSupport;
import me.badbones69.crazyenchantments.multisupport.anticheats.SpartanSupport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Bows implements Listener {
    
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private Support support = Support.getInstance();
    private List<EnchantedArrow> enchantedArrows = new ArrayList<>();
    private Material web = new ItemBuilder().setMaterial("COBWEB", "WEB").getMaterial();
    private List<Block> webBlocks = new ArrayList<>();
    private boolean isv1_14_Up = Version.isNewer(Version.v1_13_R2);
    private BowEnchantmentManager manager = ce.getBowManager();
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBowShoot(final EntityShootBowEvent e) {
        if (e.isCancelled() || ce.isIgnoredEvent(e) || ce.isIgnoredUUID(e.getEntity().getUniqueId())) return;
        ItemStack bow = e.getBow();
        if (e.getProjectile() instanceof Arrow && ce.hasEnchantments(bow)) {
            Arrow arrow = (Arrow) e.getProjectile();
            List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(bow);
            enchantedArrows.add(new EnchantedArrow(arrow, e.getEntity(), bow, enchantments));
            if (CEnchantments.MULTIARROW.isActivated() && ce.hasEnchantment(bow, CEnchantments.MULTIARROW)) {
                int power = ce.getLevel(bow, CEnchantments.MULTIARROW);
                if (CEnchantments.MULTIARROW.chanceSuccessful(bow)) {
                    if (e.getEntity() instanceof Player) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.MULTIARROW, bow);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            for (int i = 1; i <= power; i++) {
                                Arrow spawnedArrow = e.getEntity().getWorld().spawn(e.getProjectile().getLocation(), Arrow.class);
                                enchantedArrows.add(new EnchantedArrow(spawnedArrow, e.getEntity(), bow, enchantments));
                                spawnedArrow.setShooter(e.getEntity());
                                spawnedArrow.setBounce(false);
                                Vector v = new Vector(randomSpred(), 0, randomSpred());
                                spawnedArrow.setVelocity(e.getProjectile().getVelocity().add(v));
                                if (((Arrow) e.getProjectile()).isCritical()) {
                                    spawnedArrow.setCritical(true);
                                }
                                if (e.getProjectile().getFireTicks() > 0) {
                                    spawnedArrow.setFireTicks(e.getProjectile().getFireTicks());
                                }
                                if (isv1_14_Up) {
                                    spawnedArrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                                }
                            }
                        }
                    } else {
                        for (int i = 1; i <= power; i++) {
                            Arrow spawnedArrow = e.getEntity().getWorld().spawn(e.getProjectile().getLocation(), Arrow.class);
                            spawnedArrow.setShooter(e.getEntity());
                            spawnedArrow.setBounce(false);
                            Vector v = new Vector(randomSpred(), 0, randomSpred());
                            spawnedArrow.setVelocity(e.getProjectile().getVelocity().add(v));
                            if (((Arrow) e.getProjectile()).isCritical()) {
                                spawnedArrow.setCritical(true);
                            }
                            if (e.getProjectile().getFireTicks() > 0) {
                                spawnedArrow.setFireTicks(e.getProjectile().getFireTicks());
                            }
                            if (isv1_14_Up) {
                                spawnedArrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onland(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow) {
            EnchantedArrow arrow = getEnchantedArrow((Arrow) e.getEntity());
            if (arrow != null && CEnchantments.STICKY_SHOT.isActivated() && arrow.hasEnchantment(CEnchantments.STICKY_SHOT) && CEnchantments.STICKY_SHOT.chanceSuccessful(arrow.getBow())) {
                if (Version.isNewer(Version.v1_10_R1)) {
                    if (e.getHitEntity() == null) {//If the arrow hits a block.
                        Location entityLocation = e.getEntity().getLocation();
                        if (entityLocation.getBlock().getType() == Material.AIR) {
                            entityLocation.getBlock().setType(web);
                            webBlocks.add(entityLocation.getBlock());
                            e.getEntity().remove();
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    entityLocation.getBlock().setType(Material.AIR);
                                    webBlocks.remove(entityLocation.getBlock());
                                }
                            }.runTaskLater(ce.getPlugin(), 5 * 20);
                        }
                    } else {//If the arrow hits an entity.
                        List<Location> locations = getSquareArea(e.getHitEntity().getLocation());
                        for (Location location : locations) {
                            if (location.getBlock().getType() == Material.AIR) {
                                location.getBlock().setType(web);
                                webBlocks.add(location.getBlock());
                            }
                        }
                        e.getEntity().remove();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (Location location : locations) {
                                    if (location.getBlock().getType() == web) {
                                        location.getBlock().setType(Material.AIR);
                                        webBlocks.remove(location.getBlock());
                                    }
                                }
                            }
                        }.runTaskLater(ce.getPlugin(), 5 * 20);
                    }
                } else {//If the arrow hits something.
                    if (e.getEntity().getNearbyEntities(.5, .5, .5).isEmpty()) {//Checking to make sure it doesn't hit an entity.
                        Location entityLocation = e.getEntity().getLocation();
                        if (entityLocation.getBlock().getType() == Material.AIR) {
                            entityLocation.getBlock().setType(web);
                            webBlocks.add(entityLocation.getBlock());
                            e.getEntity().remove();
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    entityLocation.getBlock().setType(Material.AIR);
                                    webBlocks.remove(entityLocation.getBlock());
                                }
                            }.runTaskLater(ce.getPlugin(), 5 * 20);
                        }
                    }
                }
                if (CEnchantments.BOOM.isActivated() && arrow.hasEnchantment(CEnchantments.BOOM) && CEnchantments.BOOM.chanceSuccessful(arrow.getBow())) {
                    Methods.explode(arrow.getShooter(), arrow.getArrow());
                    arrow.getArrow().remove();
                }
                if (CEnchantments.LIGHTNING.isActivated() && arrow.hasEnchantment(CEnchantments.LIGHTNING) && CEnchantments.LIGHTNING.chanceSuccessful(arrow.getBow())) {
                    Location location = arrow.getArrow().getLocation();
                    Player shooter = (Player) arrow.getShooter();
                    location.getWorld().spigot().strikeLightningEffect(location, true);
                    int lightningSoundRange = Files.CONFIG.getFile().getInt("Settings.EnchantmentOptions.Lightning-Sound-Range", 160);
                    try {
                        location.getWorld().playSound(location, ce.getSound("ENTITY_LIGHTNING_BOLT_IMPACT", "ENTITY_LIGHTNING_IMPACT"), (float) lightningSoundRange / 16f, 1);
                    } catch (Exception ignore) {
                    }
                    if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) {
                        NoCheatPlusSupport.exemptPlayer(shooter);
                    }
                    if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
                        SpartanSupport.cancelNoSwing(shooter);
                    }
                    if (SupportedPlugins.AAC.isPluginLoaded()) {
                        AACSupport.exemptPlayer(shooter);
                    }
                    for (LivingEntity entity : Methods.getNearbyLivingEntities(location, 2D, arrow.getArrow())) {
                        EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(shooter, entity, DamageCause.CUSTOM, 5D);
                        ce.addIgnoredEvent(damageByEntityEvent);
                        ce.addIgnoredUUID(shooter.getUniqueId());
                        Bukkit.getPluginManager().callEvent(damageByEntityEvent);
                        if (!damageByEntityEvent.isCancelled() && support.allowsPVP(entity.getLocation()) && !support.isFriendly(arrow.getShooter(), entity) && !arrow.getShooter().getUniqueId().equals(entity.getUniqueId())) {
                            entity.damage(5D);
                        }
                        ce.removeIgnoredEvent(damageByEntityEvent);
                        ce.removeIgnoredUUID(shooter.getUniqueId());
                    }
                    if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) {
                        NoCheatPlusSupport.unexemptPlayer(shooter);
                    }
                    if (SupportedPlugins.AAC.isPluginLoaded()) {
                        AACSupport.unexemptPlayer(shooter);
                    }
                }
                //Removes the arrow from the list after 5 ticks. This is done because the onArrowDamage event needs the arrow in the list so it can check.
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        enchantedArrows.remove(arrow);// Removes it from the list.
                    }
                }.runTaskLaterAsynchronously(ce.getPlugin(), 5);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArrowDamage(EntityDamageByEntityEvent e) {
        if (!ce.isIgnoredEvent(e) && e.getDamager() instanceof Arrow && e.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) e.getEntity();
            EnchantedArrow arrow = getEnchantedArrow((Arrow) e.getDamager());
            if (arrow != null) {
                ItemStack bow = arrow.getBow();
                // Damaged player is friendly.
                if (CEnchantments.DOCTOR.isActivated() && arrow.hasEnchantment(CEnchantments.DOCTOR) && support.isFriendly(arrow.getShooter(), e.getEntity())) {
                    int heal = 1 + arrow.getLevel(CEnchantments.DOCTOR);
                    //Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
                    double maxHealth = ce.useHealthAttributes() ? entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() : entity.getMaxHealth();
                    if (entity.getHealth() < maxHealth) {
                        if (entity instanceof Player) {
                            EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.DOCTOR, bow);
                            Bukkit.getPluginManager().callEvent(event);
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
                if (!e.isCancelled() && !support.isFriendly(arrow.getShooter(), entity)) {
                    if (CEnchantments.STICKY_SHOT.isActivated() && arrow.hasEnchantment(CEnchantments.STICKY_SHOT) && CEnchantments.STICKY_SHOT.chanceSuccessful(bow)) {
                        List<Location> locations = getSquareArea(entity.getLocation());
                        for (Location location : locations) {
                            if (location.getBlock().getType() == Material.AIR) {
                                location.getBlock().setType(web);
                                webBlocks.add(location.getBlock());
                            }
                        }
                        arrow.getArrow().remove();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (Location location : locations) {
                                    if (location.getBlock().getType() == web) {
                                        location.getBlock().setType(Material.AIR);
                                        webBlocks.remove(location.getBlock());
                                    }
                                }
                            }
                        }.runTaskLater(ce.getPlugin(), 5 * 20);
                    }
                    if (CEnchantments.PULL.isActivated() && arrow.hasEnchantment(CEnchantments.PULL) && CEnchantments.PULL.chanceSuccessful(bow)) {
                        Vector v = arrow.getShooter().getLocation().toVector().subtract(entity.getLocation().toVector()).normalize().multiply(3);
                        if (entity instanceof Player) {
                            EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.PULL, bow);
                            Bukkit.getPluginManager().callEvent(event);
                            Player player = (Player) e.getEntity();
                            if (!event.isCancelled()) {
                                if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
                                    SpartanSupport.cancelSpeed(player);
                                    SpartanSupport.cancelFly(player);
                                    SpartanSupport.cancelClip(player);
                                    SpartanSupport.cancelNormalMovements(player);
                                    SpartanSupport.cancelNoFall(player);
                                    SpartanSupport.cancelJesus(player);
                                }
                                if (SupportedPlugins.AAC.isPluginLoaded()) {
                                    AACSupport.exemptPlayerTime(player);
                                }
                                entity.setVelocity(v);
                            }
                        } else {
                            entity.setVelocity(v);
                        }
                    }
                    for (BowEnchantment bowEnchantment : manager.getBowEnchantments()) {
                        CEnchantments enchantment = bowEnchantment.getEnchantment();
                        //No need to check if its active as if it is not then Bow Manager doesn't add it to the list of enchantments.
                        if (arrow.hasEnchantment(enchantment) && enchantment.chanceSuccessful(bow)) {
                            if (entity instanceof Player) {
                                EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), enchantment, bow);
                                Bukkit.getPluginManager().callEvent(event);
                                if (event.isCancelled()) {
                                    //If the EnchantmentUseEvent is cancelled then no need to keep going with this enchantment.
                                    continue;
                                }
                            }
                            //Code is ran if entity is not a player or if the entity is a player and the EnchantmentUseEvent is not cancelled.
                            //Checks if the enchantment is for potion effects or for damage amplifying.
                            if (bowEnchantment.isPotionEnchantment()) {
                                for (PotionEffects effect : bowEnchantment.getPotionEffects()) {
                                    entity.addPotionEffect(new PotionEffect(effect.getPotionEffect(), effect.getDuration(), (bowEnchantment.isLevelAddedToAmplifier() ? arrow.getLevel(enchantment) : 0) + effect.getAmplifier()));
                                }
                            } else {
                                //Sets the new damage amplifier. If isLevelAddedToAmplifier() is true it adds the level to the damage amplifier.
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
        if (!ce.isIgnoredEvent(e) && webBlocks.contains(e.getBlock())) {
            e.setCancelled(true);
        }
    }
    
    private EnchantedArrow getEnchantedArrow(Arrow arrow) {
        for (EnchantedArrow enchantedArrow : enchantedArrows) {
            if (enchantedArrow != null && enchantedArrow.getArrow() != null && enchantedArrow.getArrow().equals(arrow)) {
                return enchantedArrow;
            }
        }
        return null;
    }
    
    private List<Location> getSquareArea(Location location) {
        List<Location> locations = new ArrayList<>();
        locations.add(location.clone().add(1, 0, 1));//Top Left
        locations.add(location.clone().add(1, 0, 0));//Top Middle
        locations.add(location.clone().add(1, 0, -1));//Top Right
        locations.add(location.clone().add(0, 0, 1));//Center Left
        locations.add(location);//Center Middle
        locations.add(location.clone().add(0, 0, -1));//Center Right
        locations.add(location.clone().add(-1, 0, 1));//Bottom Left
        locations.add(location.clone().add(-1, 0, 0));//Bottom Middle
        locations.add(location.clone().add(-1, 0, -1));//Bottom Right
        return locations;
    }
    
    private float randomSpred() {
        float spread = (float) .2;
        return -spread + (float) (Math.random() * (spread - -spread));
    }
    
}
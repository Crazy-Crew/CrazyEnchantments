package com.badbones69.crazyenchantments.utilities;

import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.api.objects.EnchantedArrow;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.List;

public class BowUtils {

    private final PluginSupport pluginSupport = PluginSupport.INSTANCE;

    private final CrazyManager crazyManager = CrazyManager.getInstance();

    private static final BowUtils instance = new BowUtils();

    public static BowUtils getInstance() {
        return instance;
    }

    // Related to Sticky Shot
    private final List<Block> webBlocks = new ArrayList<>();

    private final List<EnchantedArrow> enchantedArrows = new ArrayList<>();

    private Arrow arrow;

    public void addArrow(Arrow arrow, Entity entity, ItemStack bow) {
        if (arrow == null) return;

        List<CEnchantment> enchantments = crazyManager.getEnchantmentsOnItem(bow);

        this.arrow = arrow;

        enchantedArrows.add(new EnchantedArrow(this.arrow, entity, bow, enchantments));
    }

    public void removeArrow() {
        if (arrow == null || !enchantedArrows.contains(arrow)) return;

        enchantedArrows.remove(arrow);
    }

    public boolean isBowEnchantActive(CEnchantments customEnchant, ItemStack itemStack) {
        return customEnchant.isActivated() && customEnchant.chanceSuccessful(itemStack) && crazyManager.hasEnchantment(itemStack, customEnchant);
    }

    public boolean isBowEnchantActive(CEnchantments customEnchants) {
        return customEnchants.isActivated() && customEnchants.chanceSuccessful(arrow.getItemStack());
    }

    public boolean allowsCombat() {
        return pluginSupport.allowsCombat(arrow.getLocation());
    }

    public boolean hasEnchant(CEnchantments customEnchant) {
        return enchantedArrow(arrow).hasEnchantment(customEnchant);
    }

    public EnchantedArrow enchantedArrow(Arrow arrow) {
        for (EnchantedArrow enchArrow : enchantedArrows) {
            if (enchArrow != null && enchArrow.getArrow() != null && enchArrow.getArrow().equals(arrow)) {
                return enchArrow;
            }
        }

        return null;
    }

    // Multi Arrow Start!

    public void spawnedArrow(Entity entity, Entity projectile, ItemStack bow) {
        Arrow spawnedArrow = entity.getWorld().spawn(projectile.getLocation(), Arrow.class);

        addArrow(spawnedArrow, entity, bow);

        spawnedArrow.setShooter((ProjectileSource) entity);

        Vector vector = new Vector(randomSpread(), 0, randomSpread());

        spawnedArrow.setVelocity(projectile.getVelocity().add(vector));

        if (((Arrow) projectile).isCritical()) spawnedArrow.setCritical(true);

        if (projectile.getFireTicks() > 0) spawnedArrow.setFireTicks(projectile.getFireTicks());

        spawnedArrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
    }

    private float randomSpread() {
        float spread = (float) .2;
        return -spread + (float) (Math.random() * (spread - -spread));
    }

    // Multi Arrow End!

    // Sticky Shot Start!
    public List<Block> getWeBlocks() {
        return webBlocks;
    }

    public void spawnWebs(Entity entity, Entity hitEntity) {
        if (arrow == null) return;

        if (isBowEnchantActive(CEnchantments.STICKY_SHOT)) {
            if (hitEntity == null) {
                Location entityLocation = entity.getLocation();

                if (entityLocation.getBlock().getType() != Material.AIR) return;

                entityLocation.getBlock().setType(Material.COBWEB);
                webBlocks.add(entityLocation.getBlock());

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        entityLocation.getBlock().setType(Material.AIR);
                        webBlocks.remove(entityLocation.getBlock());
                    }
                }.runTaskLater(crazyManager.getPlugin(), 5 * 20);
            } else {
                setWebBlocks(hitEntity);
            }
        }
    }

    private void setWebBlocks(Entity hitEntity) {

        for (Location location : getSquareArea(hitEntity.getLocation())) {
            if (location.getBlock().getType() == Material.COBWEB) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        location.getBlock().setType(Material.AIR);
                        webBlocks.remove(location.getBlock());
                    }
                }.runTaskLater(crazyManager.getPlugin(), 5 * 20);
            } else {
                location.getBlock().setType(Material.COBWEB);
                webBlocks.add(location.getBlock());
            }
        }
    }

    // Sticky Shot End!

    private List<Location> getSquareArea(Location location) {
        final List<Location> locationList = new ArrayList<>();

        locationList.add(location.clone().add(1, 0, 1)); // Top Left
        locationList.add(location.clone().add(1, 0, 0)); // Top Middle
        locationList.add(location.clone().add(1, 0, -1)); // Top Right
        locationList.add(location.clone().add(0, 0, 1)); // Center Left
        locationList.add(location); // Center Middle
        locationList.add(location.clone().add(0, 0, -1)); // Center Right
        locationList.add(location.clone().add(-1, 0, 1)); // Bottom Left
        locationList.add(location.clone().add(-1, 0, 0)); // Bottom Middle
        locationList.add(location.clone().add(-1, 0, -1)); // Bottom Right

        return locationList;
    }
}
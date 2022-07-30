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

    private EnchantedArrow enchantedArrow;

    public void addArrow(Arrow arrow, Entity entity, ItemStack bow) {
        if (arrow == null) return;

        List<CEnchantment> enchantments = crazyManager.getEnchantmentsOnItem(bow);

        EnchantedArrow enchantedArrow = new EnchantedArrow(arrow, entity, bow, enchantments);

        enchantedArrows.add(enchantedArrow);

        this.enchantedArrow = enchantedArrow;
    }

    public void removeArrow() {
        if (!enchantedArrows.contains(enchantedArrow) || enchantedArrow == null) return;

        enchantedArrows.remove(enchantedArrow);
    }

    public boolean isBowEnchantActive(CEnchantments customEnchant, ItemStack itemStack) {
        return customEnchant.isActivated() && customEnchant.chanceSuccessful(itemStack) &&
                crazyManager.hasEnchantment(itemStack, customEnchant);
    }

    public boolean isBowEnchantActive(CEnchantments customEnchant) {
        return customEnchant.isActivated() &&
                enchantedArrow.hasEnchantment(customEnchant) &&
                customEnchant.chanceSuccessful(enchantedArrow.getBow()) && crazyManager.hasEnchantments(enchantedArrow.getBow());
    }

    public boolean allowsCombat(Entity entity) {
        return pluginSupport.allowsCombat(entity.getLocation());
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

    public void spawnArrows(Entity entity, Entity projectile, ItemStack bow) {
        Arrow spawnedArrow = entity.getWorld().spawn(projectile.getLocation(), Arrow.class);

        EnchantedArrow enchantedMultiArrow = new EnchantedArrow(spawnedArrow, entity, bow, crazyManager.getEnchantmentsOnItem(bow));

        enchantedArrows.add(enchantedMultiArrow);

        spawnedArrow.setShooter((ProjectileSource) entity);

        Vector vector = new Vector(randomSpread(), 0, randomSpread());

        spawnedArrow.setVelocity(projectile.getVelocity().add(vector));

        if (((Arrow) projectile).isCritical()) spawnedArrow.setCritical(true);

        if (projectile.getFireTicks() > 0) spawnedArrow.setFireTicks(projectile.getFireTicks());

        spawnedArrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

        enchantedArrows.remove(enchantedMultiArrow);
    }

    private float randomSpread() {
        float spread = (float) .2;
        return -spread + (float) (Math.random() * (spread + spread));
    }

    // Multi Arrow End!

    // Sticky Shot Start!
    public List<Block> getWeBlocks() {
        return webBlocks;
    }

    public void spawnWebs(Entity entity, Entity hitEntity) {
        if (enchantedArrow == null) return;

        if (isBowEnchantActive(CEnchantments.STICKY_SHOT)) {
            if (hitEntity == null) {
                Location entityLocation = entity.getLocation();

                if (entityLocation.getBlock().getType() != Material.AIR) return;

                entityLocation.getBlock().setType(Material.COBWEB);
                webBlocks.add(entityLocation.getBlock());

                System.out.println("Why are we here?");
                System.out.println(isBowEnchantActive(CEnchantments.STICKY_SHOT));

                entity.remove();

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        entityLocation.getBlock().setType(Material.AIR);
                        webBlocks.remove(entityLocation.getBlock());
                    }
                }.runTaskLater(crazyManager.getPlugin(), 5 * 20);
            } else {
                entity.remove();

                setWebBlocks(hitEntity);
            }
        }
    }

    private void setWebBlocks(Entity hitEntity) {
        for (Block block : getCube(hitEntity.getLocation(), 1)) {

            block.setType(Material.COBWEB);
            webBlocks.add(block);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (block.getType() == Material.COBWEB) {
                        block.setType(Material.AIR);
                        webBlocks.remove(block);
                    }
                }
            }.runTaskLater(crazyManager.getPlugin(), 5 * 20);
        }
    }

    // Sticky Shot End!

    private List<Block> getCube(Location start, int radius) {
        List<Block> newBlocks = new ArrayList<>();

        for (double x = start.getX() - radius; x <= start.getX() + radius; x++) {
            for (double z = start.getZ() - radius; z <= start.getZ() + radius; z++) {
                Location loc = new Location(start.getWorld(), x, start.getY(), z);
                newBlocks.add(loc.getBlock());
            }
        }

        return newBlocks;
    }
}
package com.badbones69.crazyenchantments.utilities;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.api.objects.EnchantedArrow;
import com.badbones69.crazyenchantments.controllers.settings.EnchantmentBookSettings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.List;

public class BowUtils {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    // Sticky Shot
    private final List<Block> webBlocks = new ArrayList<>();

    private final List<EnchantedArrow> enchantedArrows = new ArrayList<>();

    public void addArrow(Arrow arrow, Entity entity, ItemStack bow) {
        if (arrow == null) return;

        List<CEnchantment> enchantments = enchantmentBookSettings.getEnchantmentsOnItem(bow);

        EnchantedArrow enchantedArrow = new EnchantedArrow(arrow, entity, bow, enchantments);

        enchantedArrows.add(enchantedArrow);
    }

    public void removeArrow(EnchantedArrow enchantedArrow) {
        if (!enchantedArrows.contains(enchantedArrow) || enchantedArrow == null) return;

        enchantedArrows.remove(enchantedArrow);
    }

    public boolean isBowEnchantActive(CEnchantments customEnchant, ItemStack itemStack, Arrow arrow) {
        return arrow != null &&
                customEnchant.isActivated() &&
                crazyManager.hasEnchantment(itemStack, customEnchant) &&
                customEnchant.chanceSuccessful(itemStack);
    }

    public boolean isBowEnchantActive(CEnchantments customEnchant, EnchantedArrow enchantedArrow, Arrow arrow) {
        return customEnchant.isActivated() &&
                enchantedArrow(arrow).hasEnchantment(customEnchant) &&
                customEnchant.chanceSuccessful(enchantedArrow.getBow()) &&
                enchantmentBookSettings.hasEnchantments(enchantedArrow.getBow());
    }

    public boolean allowsCombat(Entity entity) {
        return !starter.getPluginSupport().allowCombat(entity.getLocation());
    }

    public EnchantedArrow enchantedArrow(Arrow arrow) {
        for (EnchantedArrow enchArrow : enchantedArrows) {
            if (enchArrow != null && enchArrow.getArrow() != null && enchArrow.getArrow().equals(arrow)) return enchArrow;
        }

        return null;
    }

    // Multi Arrow Start!

    public void spawnArrows(Entity entity, Entity projectile, ItemStack bow) {
        Arrow spawnedArrow = entity.getWorld().spawn(projectile.getLocation(), Arrow.class);

        EnchantedArrow enchantedMultiArrow = new EnchantedArrow(spawnedArrow, entity, bow, enchantmentBookSettings.getEnchantmentsOnItem(bow));

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
    public List<Block> getWebBlocks() {
        return webBlocks;
    }

    public void spawnWebs(Entity hitEntity, EnchantedArrow enchantedArrow, Arrow arrow) {
        if (enchantedArrow == null) return;

        if (!isBowEnchantActive(CEnchantments.STICKY_SHOT, enchantedArrow, arrow)) return;

        if (hitEntity == null) {
            Location entityLocation = arrow.getLocation();

            if (entityLocation.getBlock().getType() != Material.AIR) return;

            entityLocation.getBlock().setType(Material.COBWEB);
            webBlocks.add(entityLocation.getBlock());

            arrow.remove();

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                entityLocation.getBlock().setType(Material.AIR);
                webBlocks.remove(entityLocation.getBlock());
            }, 5 * 20);
        } else {
            arrow.remove();
            setWebBlocks(hitEntity);
        }
    }

    private void setWebBlocks(Entity hitEntity) {
        for (Block block : getCube(hitEntity.getLocation())) {

            block.setType(Material.COBWEB);
            webBlocks.add(block);

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (block.getType() == Material.COBWEB) {
                    block.setType(Material.AIR);
                    webBlocks.remove(block);
                }
            }, 5 * 20);
        }
    }

    // Sticky Shot End!

    private List<Block> getCube(Location start) {
        List<Block> newBlocks = new ArrayList<>();

        for (double x = start.getX() - 1; x <= start.getX() + 1; x++) {
            for (double z = start.getZ() - 1; z <= start.getZ() + 1; z++) {
                Location loc = new Location(start.getWorld(), x, start.getY(), z);
                if (loc.getBlock().getType() == Material.AIR) newBlocks.add(loc.getBlock());
            }
        }

        return newBlocks;
    }
}
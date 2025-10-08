package com.badbones69.crazyenchantments.paper.api.utils;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.EnchantedArrow;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BowUtils {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CrazyInstance instance = this.plugin.getInstance();

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    // Sticky Shot
    private final List<Block> webBlocks = new ArrayList<>();

    private final List<EnchantedArrow> enchantedArrows = new ArrayList<>();

    public void addArrow(@Nullable final Arrow arrow, @NotNull final ItemStack bow, @NotNull final Map<CEnchantment, Integer> enchantments) {
        if (arrow == null) return;

        EnchantedArrow enchantedArrow = new EnchantedArrow(arrow, bow, enchantments);

        this.enchantedArrows.add(enchantedArrow);
    }

    public void removeArrow(@Nullable final EnchantedArrow enchantedArrow) {
        if (enchantedArrow == null || !this.enchantedArrows.contains(enchantedArrow)) return;

        this.enchantedArrows.remove(enchantedArrow);
    }

    public boolean isBowEnchantActive(@NotNull final CEnchantments customEnchant, @NotNull final EnchantedArrow enchantedArrow) {
        return customEnchant.isActivated() &&
                enchantedArrow.hasEnchantment(customEnchant) &&
                customEnchant.chanceSuccessful(enchantedArrow.getLevel(customEnchant));
    }

    public boolean allowsCombat(@NotNull final Entity entity) {
        return this.starter.getPluginSupport().allowCombat(entity.getLocation());
    }

    public EnchantedArrow getEnchantedArrow(@NotNull final Arrow arrow) {
        return this.enchantedArrows.stream().filter((enchArrow) -> enchArrow != null && enchArrow.arrow() != null && enchArrow.arrow().equals(arrow)).findFirst().orElse(null);
    }

    // Multi Arrow Start!

    public void spawnArrows(@NotNull final LivingEntity shooter, @NotNull final Entity projectile, @NotNull final ItemStack bow) {
        Arrow spawnedArrow = shooter.getWorld().spawn(projectile.getLocation(), Arrow.class);

        EnchantedArrow enchantedMultiArrow = new EnchantedArrow(spawnedArrow, bow, this.instance.getEnchantments(bow));

        this.enchantedArrows.add(enchantedMultiArrow);

        spawnedArrow.setShooter(shooter);

        Vector vector = new Vector(randomSpread(), 0, randomSpread());

        spawnedArrow.setVelocity(projectile.getVelocity().add(vector));

        if (((Arrow) projectile).isCritical()) spawnedArrow.setCritical(true);

        if (projectile.getFireTicks() > 0) spawnedArrow.setFireTicks(projectile.getFireTicks());

        spawnedArrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
    }

    private float randomSpread() {
        float spread = (float) .2;
        return -spread + (float) (Math.random() * (spread * 2));
    }

    // Multi Arrow End!

    // Sticky Shot Start!
    public List<Block> getWebBlocks() {
        return this.webBlocks;
    }

    public void spawnWebs(@Nullable final Entity hitEntity, @Nullable final EnchantedArrow enchantedArrow) {
        if (enchantedArrow == null) return;

        Arrow arrow = enchantedArrow.getArrow();

        if (!(EnchantUtils.isEventActive(CEnchantments.STICKY_SHOT, enchantedArrow.getShooter(), enchantedArrow.arrow().getWeapon(), enchantedArrow.getEnchantments()))) return; //todo() null check

        if (hitEntity == null) {
            Location entityLocation = arrow.getLocation();

            if (entityLocation.getBlock().getType() != Material.AIR) return;

            entityLocation.getBlock().setType(Material.COBWEB);
            this.webBlocks.add(entityLocation.getBlock());

            new FoliaScheduler(this.plugin, entityLocation) {
                @Override
                public void run() {
                    entityLocation.getBlock().setType(Material.AIR);
                    webBlocks.remove(entityLocation.getBlock());
                }
            }.runDelayed(5 * 20);
        } else {
            setWebBlocks(hitEntity);
        }

        arrow.remove();
    }

    private void setWebBlocks(@NotNull final Entity hitEntity) {
        final Location location = hitEntity.getLocation();

        new FoliaScheduler(this.plugin, location) {
            @Override
            public void run() {
                for (final Block block : getCube(hitEntity.getLocation())) {
                    block.setType(Material.COBWEB);

                    webBlocks.add(block);

                    new FoliaScheduler(plugin, block.getLocation()) {
                        @Override
                        public void run() {
                            if (block.getType() == Material.COBWEB) {
                                block.setType(Material.AIR);
                                webBlocks.remove(block);
                            }
                        }
                    }.runDelayed(5 * 20);
                }
            }
        }.execute();
    }
    // Sticky Shot End!

    private List<Block> getCube(@NotNull final Location start) {
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
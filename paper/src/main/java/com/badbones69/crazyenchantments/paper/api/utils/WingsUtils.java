package com.badbones69.crazyenchantments.paper.api.utils;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.managers.WingsManager;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.support.SupportUtils;
import com.ryderbelserion.fusion.paper.builders.folia.Scheduler;
import com.ryderbelserion.fusion.paper.builders.folia.FoliaScheduler;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class WingsUtils {

    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private static final Server server = plugin.getServer();

    private static final CrazyPlatform platform = plugin.getPlatform();

    private static final SupportUtils support = platform.getSupport();

    @NotNull
    private static final Starter starter = plugin.getStarter();

    private static final EnchantmentBookSettings settings = starter.getEnchantmentBookSettings();

    @NotNull
    private static final WingsManager wingsManager = starter.getWingsManager();

    public static void startWings() {
        if (!wingsManager.isWingsEnabled()) wingsManager.endWingsTask();

        wingsManager.setWingsTask(new FoliaScheduler(plugin, Scheduler.async_scheduler, TimeUnit.MILLISECONDS) {
            @Override
            public void run() {
                for (UUID uuid : wingsManager.getFlyingPlayers()) {
                    final Player player = server.getPlayer(uuid);

                    if (player == null) return;

                    new FoliaScheduler(plugin, null, player) {
                        @Override
                        public void run() {
                            if (player.isFlying() && player.getEquipment().getBoots() != null && settings.getEnchantments(player.getEquipment().getBoots()).containsKey(CEnchantments.WINGS.getEnchantment())) {
                                final Location location = player.getLocation().subtract(0, .25, 0);

                                if (wingsManager.isCloudsEnabled()) player.getWorld().spawnParticle(Particle.CLOUD, location, 100, .25, 0, .25, 0);
                            }
                        }
                    }.runNextTick();
                }
            }
        }.runAtFixedRate(50L, 50L));
    }

    public static void checkArmor(ItemStack newArmorPiece, boolean newArmor, ItemStack oldArmorPiece, Player player) {
        CEnchantments wings = CEnchantments.WINGS;

        if (newArmor) {
            if (settings.getEnchantments(newArmorPiece).containsKey(wings.getEnchantment()) && canFly(player) && checkGameMode(player)) player.setAllowFlight(true);

            return;
        }

        if (settings.getEnchantments(oldArmorPiece).containsKey(wings.getEnchantment()) && checkGameMode(player)) player.setAllowFlight(false);
    }

    public static boolean checkGameMode(Player player) {
        return player.getGameMode() == GameMode.SURVIVAL;
    }

    public static boolean canFly(@NonNull final Player player) {
        if (wingsManager.inLimitlessFlightWorld(player) || wingsManager.inWhitelistedWorld(player)) {
            return true; // they can fly
        }

        if (wingsManager.inBlacklistedWorld(player)) {
            return false; // they can't fly
        }

        final Location location = player.getLocation();

        boolean isTerritory = false; // they can't fly

        for (final String region : wingsManager.getRegions()) {
            if (support.isTerritory(region, location)) {
                isTerritory = true; // they can fly

                break;
            }
        }

        if (!isTerritory && support.isTerritory(player, location)) {
            isTerritory = wingsManager.canOwnersFly() && support.isOwner(player) || wingsManager.canMembersFly() && support.isMember(player);
        }

        return isTerritory; // true, they can fly, false, they can't fly
    }

    public static boolean isEnemiesNearby(Player player) {
        if (wingsManager.isEnemyCheckEnabled() && !wingsManager.inLimitlessFlightWorld(player)) {
            for (final Player otherPlayer : getNearbyPlayers(player, wingsManager.getEnemyRadius())) {
                //todo() update this
                if (!(player.hasPermission("crazyenchantments.bypass.wings") && support.isFriendly(player, otherPlayer))) return true;
            }
        }

        return false;
    }

    private static List<Player> getNearbyPlayers(Player player, int radius) {
        List<Player> players = new ArrayList<>();

        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player) players.add((Player) entity);
        }

        return players;
    }
}
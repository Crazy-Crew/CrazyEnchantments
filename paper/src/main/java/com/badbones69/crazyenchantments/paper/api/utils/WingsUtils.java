package com.badbones69.crazyenchantments.paper.api.utils;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.managers.WingsManager;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.support.PluginSupport;
import com.badbones69.crazyenchantments.paper.support.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.paper.support.interfaces.claims.WorldGuardVersion;
import com.ryderbelserion.fusion.paper.scheduler.Scheduler;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class WingsUtils {

    @NotNull
    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private static final Server server = plugin.getServer();

    @NotNull
    private static final Starter starter = plugin.getStarter();

    private static final EnchantmentBookSettings settings = starter.getEnchantmentBookSettings();

    @NotNull
    private static final PluginSupport pluginSupport = starter.getPluginSupport();

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

    public static void checkArmor(@NotNull final ItemStack newArmorPiece, final boolean newArmor, @NotNull final ItemStack oldArmorPiece, @NotNull final Player player) {
        CEnchantments wings = CEnchantments.WINGS;

        if (newArmor) {
            if (newArmorPiece.isEmpty()) return;

            if (starter.getEnchantmentBookSettings().getEnchantments(newArmorPiece).containsKey(wings.getEnchantment()) && checkRegion(player) && checkGameMode(player)) player.setAllowFlight(true);

            return;
        }

        if (oldArmorPiece.isEmpty()) return;

        if (starter.getEnchantmentBookSettings().getEnchantments(oldArmorPiece).containsKey(wings.getEnchantment()) && checkGameMode(player)) player.setAllowFlight(false);
    }

    public static boolean checkGameMode(@NotNull final Player player) {
        return player.getGameMode() == GameMode.SURVIVAL;
    }

    private static boolean inWingsRegion(@NotNull final Player player) {
        if (!SupportedPlugins.WORLDGUARD.isPluginLoaded()) return true;

        WorldGuardVersion worldGuardVersion = starter.getPluginSupport().getWorldGuardUtils().getWorldGuardSupport();

        for (String region : wingsManager.getRegions()) {
            if (worldGuardVersion.inRegion(region, player.getLocation())) {
                return true;
            } else {
                if (wingsManager.canOwnersFly() && worldGuardVersion.isOwner(player)) return true;

                if (wingsManager.canMembersFly() && worldGuardVersion.isMember(player)) return true;
            }
        }

        return false;
    }

    public static boolean checkRegion(@NotNull final Player player) {
        return wingsManager.inLimitlessFlightWorld(player) || (!wingsManager.inBlacklistedWorld(player) && (pluginSupport.inTerritory(player) || inWingsRegion(player) || wingsManager.inWhitelistedWorld(player)));
    }

    public static boolean isEnemiesNearby(@NotNull final Player player) {
        if (wingsManager.isEnemyCheckEnabled() && !wingsManager.inLimitlessFlightWorld(player)) {
            for (final Player otherPlayer : getNearbyPlayers(player, wingsManager.getEnemyRadius())) {
                //todo() update this
                if (!(player.hasPermission("crazyenchantments.bypass.wings") && pluginSupport.isFriendly(player, otherPlayer))) return true;
            }
        }

        return false;
    }

    private static List<Player> getNearbyPlayers(@NotNull final Player player, final int radius) {
        List<Player> players = new ArrayList<>();

        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player) players.add((Player) entity);
        }

        return players;
    }
}
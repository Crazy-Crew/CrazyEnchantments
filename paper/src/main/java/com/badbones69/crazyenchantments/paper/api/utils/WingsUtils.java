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
import net.kyori.adventure.util.TriState;
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

        if (wingsManager.isEnemyCheckEnabled() && !player.hasPermission("crazyenchantments.bypass.wings")) {
            final List<Player> players = getNearbyPlayers(player, wingsManager.getEnemyRadius());

            for (final Player target : players) {
                if (!target.hasLineOfSight(player)) {
                    continue;
                }

                if (!support.isFriendly(player, target)) {
                    if (player.isFlying()) {
                        wingsManager.removeFlyingPlayer(player);

                        player.setFlyingFallDamage(TriState.FALSE);
                        player.setFlying(false);
                    }

                    if (target.isFlying()) {
                        wingsManager.removeFlyingPlayer(target);

                        target.setFlyingFallDamage(TriState.FALSE);
                        target.setFlying(false);
                    }

                    return false;
                }

                return true; // true, they can fly, false, they can't fly
            }
        }

        final Location location = player.getLocation();

        for (final String region : wingsManager.getRegions()) {
            if (support.isTerritory(region, location)) {
                return true;
            }
        }

        if (wingsManager.canOwnersFly() && support.isOwner(player)) {
            return true; // they can fly
        }

        if (wingsManager.canMembersFly() && support.isMember(player)) {
            return true; // they can fly
        }

        if (support.isTerritory(player, location)) {
            return true; // they can fly
        }

        return false;
    }

    private static List<Player> getNearbyPlayers(Player player, int radius) {
        final List<Player> players = new ArrayList<>();

        for (final Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player target) {
                players.add(target);
            }
        }

        return players;
    }
}
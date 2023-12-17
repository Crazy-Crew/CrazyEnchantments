package com.badbones69.crazyenchantments.paper.utilities;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.PluginSupport;
import com.badbones69.crazyenchantments.paper.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.managers.WingsManager;
import com.badbones69.crazyenchantments.paper.api.support.interfaces.claims.WorldGuardVersion;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.List;

public class WingsUtils {

    private static final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private static final Starter starter = plugin.getStarter();

    private static final CrazyManager crazyManager = starter.getCrazyManager();

    private static final PluginSupport pluginSupport = starter.getPluginSupport();

    private static final WingsManager wingsManager = starter.getWingsManager();

    public static void startWings() {
        if (!wingsManager.isWingsEnabled()) wingsManager.endWingsTask();

        wingsManager.setWingsTask(new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : wingsManager.getFlyingPlayers()) {
                    if (player.isFlying() && player.getEquipment().getBoots() != null && starter.getEnchantmentBookSettings().getEnchantments(player.getEquipment().getBoots()).containsKey(CEnchantments.WINGS.getEnchantment())) {
                        Location location = player.getLocation().subtract(0, .25, 0);

                        if (wingsManager.isCloudsEnabled())
                            player.getWorld().spawnParticle(Particle.CLOUD, location, 100, .25, 0, .25, 0);
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 1, 1));
    }

    public static void checkArmor(ItemStack newArmorPiece, boolean newArmor, ItemStack oldArmorPiece, Player player) {
        CEnchantments wings = CEnchantments.WINGS;

        if (newArmor) {
            if (starter.getEnchantmentBookSettings().getEnchantments(newArmorPiece).containsKey(wings.getEnchantment()) && checkRegion(player) && checkGameMode(player)) player.setAllowFlight(true);

            return;
        }

        if (starter.getEnchantmentBookSettings().getEnchantments(oldArmorPiece).containsKey(wings.getEnchantment()) && checkGameMode(player)) player.setAllowFlight(false);
    }

    public static boolean checkGameMode(Player player) {
        return player.getGameMode() == GameMode.SURVIVAL;
    }

    private static boolean inWingsRegion(Player player) {
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

    public static boolean checkRegion(Player player) {
        return wingsManager.inLimitlessFlightWorld(player) || (!wingsManager.inBlacklistedWorld(player) && (pluginSupport.inTerritory(player) || inWingsRegion(player) || wingsManager.inWhitelistedWorld(player)));
    }

    public static boolean isEnemiesNearby(Player player) {
        if (wingsManager.isEnemyCheckEnabled() && !wingsManager.inLimitlessFlightWorld(player)) {
            for (Player otherPlayer : getNearbyPlayers(player, wingsManager.getEnemyRadius())) {
                if (!(player.hasPermission("crazyenchantments.bypass.wings") && pluginSupport.isFriendly(player, otherPlayer))) return true;
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
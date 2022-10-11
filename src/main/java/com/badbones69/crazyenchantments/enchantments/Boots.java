package com.badbones69.crazyenchantments.enchantments;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.events.ArmorEquipEvent;
import com.badbones69.crazyenchantments.api.managers.WingsManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.List;

public class Boots implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    private final PluginSupport pluginSupport = plugin.getStarter().getPluginSupport();

    private final WingsManager wingsManager = plugin.getStarter().getWingsManager();

    public void startWings() {
        if (wingsManager.isWingsEnabled()) {
            wingsManager.setWingsTask(new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : wingsManager.getFlyingPlayers()) {
                        if (player.isFlying() && crazyManager.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS) && player.getEquipment().getBoots() != null) {
                            Location location = player.getLocation().subtract(0, .25, 0);

                            if (wingsManager.isCloudsEnabled()) player.getWorld().spawnParticle(Particle.CLOUD, location, 100, .25, 0, .25, 0);
                        }
                    }
                }
            }.runTaskTimerAsynchronously(plugin, 1, 1));
        } else {
            wingsManager.endWingsTask();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEquip(ArmorEquipEvent event) {
        if (!wingsManager.isWingsEnabled()) return;
        Player player = event.getPlayer();

        if (crazyManager.hasEnchantment(event.getNewArmorPiece(), CEnchantments.WINGS) && regionCheck(player) && gameModeCheck(player)) player.setAllowFlight(true);

        if (crazyManager.hasEnchantment(event.getOldArmorPiece(), CEnchantments.WINGS) && gameModeCheck(player)) player.setAllowFlight(false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFly(PlayerToggleFlightEvent event) {
        if (!wingsManager.isWingsEnabled()) return;
        Player player = event.getPlayer();
        if (player.getEquipment().getBoots() == null) return;
        if (!crazyManager.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS)) return;
        if (!regionCheck(player)) return;
        if (areEnemiesNearby(player)) return;

        // if (PluginSupport.SupportedPlugins.SPARTAN.isPluginLoaded()) SpartanSupport.cancelNormalMovements(player);

        if (event.isFlying()) {
            if (player.getAllowFlight()) {
                event.setCancelled(true);
                player.setFlying(true);
                wingsManager.addFlyingPlayer(player);
            }
        } else {
            wingsManager.removeFlyingPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        // if (!event.hasChangedBlock()) return;

        Player player = event.getPlayer();
        boolean isFlying = player.isFlying();

        if (wingsManager.isWingsEnabled() && crazyManager.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS)) {
            if (regionCheck(player)) {
                if (!areEnemiesNearby(player)) {
                    player.setAllowFlight(true);
                } else {
                    if (isFlying && gameModeCheck(player)) {
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        wingsManager.removeFlyingPlayer(player);
                    }
                }
            } else {
                if (isFlying && gameModeCheck(player)) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    wingsManager.removeFlyingPlayer(player);
                }
            }

            if (isFlying) wingsManager.addFlyingPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        if (!wingsManager.isWingsEnabled()) return;
        Player player = event.getPlayer();
        if (!crazyManager.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS)) return;
        if (!regionCheck(player)) return;
        if (areEnemiesNearby(player)) return;

        // if (PluginSupport.SupportedPlugins.SPARTAN.isPluginLoaded()) SpartanSupport.cancelNormalMovements(player);

        player.setAllowFlight(true);
        wingsManager.addFlyingPlayer(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!wingsManager.isWingsEnabled()) return;
        if (!wingsManager.isFlyingPlayer(player)) return;
        player.setFlying(false);
        player.setAllowFlight(false);
        wingsManager.removeFlyingPlayer(player);
    }

    private boolean gameModeCheck(Player player) {
        return player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.ADVENTURE;
    }

    private boolean regionCheck(Player player) {
        return wingsManager.inLimitlessFlightWorld(player) || (!wingsManager.inBlacklistedWorld(player) && (pluginSupport.inTerritory(player) || pluginSupport.inWingsRegion(player) || wingsManager.inWhitelistedWorld(player)));
    }

    private boolean areEnemiesNearby(Player player) {
        if (wingsManager.isEnemyCheckEnabled() && !wingsManager.inLimitlessFlightWorld(player)) {
            for (Player otherPlayer : getNearbyPlayers(player, wingsManager.getEnemyRadius())) {
                if (!(player.hasPermission("crazyenchantments.bypass.wings") && pluginSupport.isFriendly(player, otherPlayer))) return true;
            }
        }

        return false;
    }

    private List<Player> getNearbyPlayers(Player player, int radius) {
        List<Player> players = new ArrayList<>();

        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player) players.add((Player) entity);
        }

        return players;
    }
}
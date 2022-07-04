package com.badbones69.crazyenchantments.enchantments;

import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.events.ArmorEquipEvent;
import com.badbones69.crazyenchantments.api.managers.WingsManager;
import com.badbones69.crazyenchantments.api.multisupport.anticheats.SpartanSupport;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.List;

public class Boots implements Listener {

    private final static CrazyManager ce = CrazyManager.getInstance();
    private final PluginSupport support = PluginSupport.INSTANCE;
    private final static WingsManager manager = ce.getWingsManager();

    public static void startWings() {
        if (manager.isWingsEnabled()) {
            manager.setWingsTask(new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : manager.getFlyingPlayers()) {
                        if (player.isFlying() && ce.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS) && player.getEquipment().getBoots() != null) {
                            Location location = player.getLocation().subtract(0, .25, 0);
                            if (manager.isCloudsEnabled()) player.getWorld().spawnParticle(Particle.CLOUD, location, 100, .25, 0, .25, 0);
                        }
                    }
                }
            }.runTaskTimerAsynchronously(ce.getPlugin(), 1, 1));
        } else {
            manager.endWingsTask();
        }
    }

    @EventHandler
    public void onEquip(ArmorEquipEvent e) {
        Player player = e.getPlayer();
        if (manager.isWingsEnabled()) {

            if (ce.hasEnchantment(e.getNewArmorPiece(), CEnchantments.WINGS) && regionCheck(player) && gamemodeCheck(player)) {
                player.setAllowFlight(true);
            }

            if (ce.hasEnchantment(e.getOldArmorPiece(), CEnchantments.WINGS) && gamemodeCheck(player)) {
                player.setAllowFlight(false);
            }
        }
    }

    @EventHandler
    public void onFly(PlayerToggleFlightEvent e) {
        Player player = e.getPlayer();
        if (manager.isWingsEnabled() && ce.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS) && regionCheck(player) && !areEnemiesNearby(player) && player.getEquipment().getBoots() != null) {

            if (PluginSupport.SupportedPlugins.SPARTAN.isPluginLoaded()) {
                SpartanSupport.cancelNormalMovements(player);
            }

            if (e.isFlying()) {
                if (player.getAllowFlight()) {
                    e.setCancelled(true);
                    player.setFlying(true);
                    manager.addFlyingPlayer(player);
                }
            } else {
                manager.removeFlyingPlayer(player);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if ((e.getFrom().getBlockX() != e.getTo().getBlockX()) || (e.getFrom().getBlockY() != e.getTo().getBlockY()) || (e.getFrom().getBlockZ() != e.getTo().getBlockZ())) {
            Player player = e.getPlayer();
            boolean isFlying = player.isFlying();
            if (manager.isWingsEnabled() && ce.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS)) {
                if (regionCheck(player)) {
                    if (!areEnemiesNearby(player)) {
                        player.setAllowFlight(true);
                    } else {
                        if (isFlying && gamemodeCheck(player)) {
                            player.setFlying(false);
                            player.setAllowFlight(false);
                            manager.removeFlyingPlayer(player);
                        }
                    }
                } else {
                    if (isFlying && gamemodeCheck(player)) {
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        manager.removeFlyingPlayer(player);
                    }
                }

                if (isFlying) {
                    manager.addFlyingPlayer(player);
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (manager.isWingsEnabled() && ce.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS) && regionCheck(player) && !areEnemiesNearby(player)) {

            if (PluginSupport.SupportedPlugins.SPARTAN.isPluginLoaded()) {
                SpartanSupport.cancelNormalMovements(player);
            }

            player.setAllowFlight(true);
            manager.addFlyingPlayer(player);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (manager.isWingsEnabled() && manager.isFlyingPlayer(player)) {
            player.setFlying(false);
            player.setAllowFlight(false);
            manager.removeFlyingPlayer(player);
        }
    }

    private boolean gamemodeCheck(Player player) {
        return player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.ADVENTURE;
    }

    private boolean regionCheck(Player player) {
        return manager.inLimitlessFlightWorld(player) || (!manager.inBlacklistedWorld(player) && (support.inTerritory(player) || support.inWingsRegion(player) || manager.inWhitelistedWorld(player)));
    }

    private boolean areEnemiesNearby(Player player) {
        if (manager.isEnemyCheckEnabled() && !manager.inLimitlessFlightWorld(player)) {
            for (Player otherPlayer : getNearByPlayers(player, manager.getEnemyRadius())) {
                if (!(player.hasPermission("crazyenchantments.bypass.wings") && support.isFriendly(player, otherPlayer))) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Player> getNearByPlayers(Player player, int radius) {
        List<Player> players = new ArrayList<>();
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player) {
                players.add((Player) entity);
            }
        }
        return players;
    }
}
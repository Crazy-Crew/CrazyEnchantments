package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.api.CrazyManager;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.ArmorEquipEvent;
import me.badbones69.crazyenchantments.api.managers.WingsManager;
import me.badbones69.crazyenchantments.multisupport.anticheats.SpartanSupport;
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
    
    private static CrazyManager ce = CrazyManager.getInstance();
    private PluginSupport pluginSupport = PluginSupport.getInstance();
    private static WingsManager manager = ce.getWingsManager();
    
    public static void startWings() {
        if (manager.isCloudsEnabled() && manager.isWingsEnabled()) {
            manager.setWingsTask(new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : manager.getFlyingPlayers()) {
                        if (player.isFlying()) {
                            Location location = player.getLocation().subtract(0, .25, 0);
                            player.getLocation().getWorld().spawnParticle(Particle.CLOUD, location, 30, .4F, .5F, .4F);
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
            if (ce.hasEnchantment(e.getNewArmorPiece(), CEnchantments.WINGS) && regionCheck(player) && gameModeCheck(player)) {
                player.setAllowFlight(true);
            }
            if (ce.hasEnchantment(e.getOldArmorPiece(), CEnchantments.WINGS) && gameModeCheck(player)) {
                player.setAllowFlight(false);
            }
        }
    }
    
    @EventHandler
    public void onFly(PlayerToggleFlightEvent e) {
        Player player = e.getPlayer();
        if (manager.isWingsEnabled() && ce.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS) && regionCheck(player) && !areEnemiesNearby(player)) {
            if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
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
                        if (isFlying && gameModeCheck(player)) {
                            player.setFlying(false);
                            player.setAllowFlight(false);
                            manager.removeFlyingPlayer(player);
                        }
                    }
                } else {
                    if (isFlying && gameModeCheck(player)) {
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
            if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
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
    
    private boolean gameModeCheck(Player player) {
        return player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.ADVENTURE;
    }
    
    private boolean regionCheck(Player player) {
        return manager.inLimitlessFlightWorld(player) || (!manager.inBlacklistedWorld(player) && (pluginSupport.inTerritory(player) || pluginSupport.inWingsRegion(player) || manager.inWhitelistedWorld(player)));
    }
    
    private boolean areEnemiesNearby(Player player) {
        if (manager.isEnemyCheckEnabled() && !manager.inLimitlessFlightWorld(player)) {
            for (Player otherPlayer : getNearByPlayers(player, manager.getEnemyRadius())) {
                if (!(player.hasPermission("crazyenchantments.bypass.wings") && pluginSupport.isFriendly(player, otherPlayer))) {
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
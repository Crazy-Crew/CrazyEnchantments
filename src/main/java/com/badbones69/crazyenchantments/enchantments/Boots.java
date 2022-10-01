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

    private final static CrazyManager crazyManager = CrazyManager.getInstance();
    private final PluginSupport support = PluginSupport.INSTANCE;
    private final static WingsManager manager = crazyManager.getWingsManager();

    public static void startWings() {
        if (manager.isWingsEnabled()) {
            manager.setWingsTask(new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : manager.getFlyingPlayers()) {
                        if (player.isFlying() && crazyManager.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS) && player.getEquipment().getBoots() != null) {
                            Location location = player.getLocation().subtract(0, .25, 0);

                            if (manager.isCloudsEnabled())
                                player.getWorld().spawnParticle(Particle.CLOUD, location, 100, .25, 0, .25, 0);
                        }
                    }
                }
            }.runTaskTimerAsynchronously(crazyManager.getPlugin(), 1, 1));
        } else {
            manager.endWingsTask();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEquip(ArmorEquipEvent event) {
        if (!manager.isWingsEnabled()) return;
        Player player = event.getPlayer();

        if (crazyManager.hasEnchantment(event.getNewArmorPiece(), CEnchantments.WINGS) && regionCheck(player) && gamemodeCheck(player)) {
            player.setAllowFlight(true);
        }

        if (crazyManager.hasEnchantment(event.getOldArmorPiece(), CEnchantments.WINGS) && gamemodeCheck(player)) {
            player.setAllowFlight(false);
        }

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFly(PlayerToggleFlightEvent event) {
        if (!manager.isWingsEnabled()) return;
        Player player = event.getPlayer();
        if (player.getEquipment().getBoots() == null) return;
        if (!crazyManager.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS)) return;
        if (!regionCheck(player)) return;
        if (areEnemiesNearby(player)) return;

        if (PluginSupport.SupportedPlugins.SPARTAN.isPluginLoaded()) {
            SpartanSupport.cancelNormalMovements(player);
        }

        if (event.isFlying()) {
            if (player.getAllowFlight()) {
                event.setCancelled(true);
                player.setFlying(true);
                manager.addFlyingPlayer(player);
            }
        } else {
            manager.removeFlyingPlayer(player);
        }

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;
        Player player = event.getPlayer();
        boolean isFlying = player.isFlying();

        if (manager.isWingsEnabled() && crazyManager.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS)) {
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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        if (!manager.isWingsEnabled()) return;
        Player player = event.getPlayer();
        if (!crazyManager.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS)) return;
        if (!regionCheck(player)) return;
        if (areEnemiesNearby(player)) return;

        if (PluginSupport.SupportedPlugins.SPARTAN.isPluginLoaded()) {
            SpartanSupport.cancelNormalMovements(player);
        }

        player.setAllowFlight(true);
        manager.addFlyingPlayer(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!manager.isWingsEnabled()) return;
        if (!manager.isFlyingPlayer(player)) return;
        player.setFlying(false);
        player.setAllowFlight(false);
        manager.removeFlyingPlayer(player);
    }

    private boolean gamemodeCheck(Player player) {
        return player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.ADVENTURE;
    }

    private boolean regionCheck(Player player) {
        return manager.inLimitlessFlightWorld(player) || (!manager.inBlacklistedWorld(player) && (support.inTerritory(player) || support.inWingsRegion(player) || manager.inWhitelistedWorld(player)));
    }

    private boolean areEnemiesNearby(Player player) {
        if (manager.isEnemyCheckEnabled() && !manager.inLimitlessFlightWorld(player)) {
            for (Player otherPlayer : getNearbyPlayers(player, manager.getEnemyRadius())) {
                if (!(player.hasPermission("crazyenchantments.bypass.wings") && support.isFriendly(player, otherPlayer))) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Player> getNearbyPlayers(Player player, int radius) {
        List<Player> players = new ArrayList<>();

        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player) {
                players.add((Player) entity);
            }
        }

        return players;
    }

}
package me.badbones69.crazyenchantments.enchantments

import me.badbones69.crazyenchantments.api.CrazyManager
import me.badbones69.crazyenchantments.api.PluginSupport
import me.badbones69.crazyenchantments.api.enums.CEnchantments
import me.badbones69.crazyenchantments.api.events.ArmorEquipEvent
import me.badbones69.crazyenchantments.getPlugin
import me.badbones69.crazyenchantments.multisupport.anticheats.SpartanSupport
import org.bukkit.GameMode
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.scheduler.BukkitRunnable

object Boots : Listener {

    private val ce = CrazyManager.getInstance()
    private val pluginSupport = PluginSupport
    private val manager = ce.wingsManager

    fun startWings() {
        if (manager.isCloudsEnabled && manager.isWingsEnabled) {
            manager.setWingsTask(object : BukkitRunnable() {
                override fun run() {
                    for (player in manager.flyingPlayers) {
                        if (player.isFlying) {
                            val location = player.location.subtract(0.0, .25, 0.0)
                            player.location.world!!.spawnParticle(Particle.CLOUD, location, 30, .4, .5, .4)
                        }
                    }
                }
            }.runTaskTimerAsynchronously(getPlugin(), 1, 1))
        } else {
            manager.endWingsTask()
        }
    }

    @EventHandler
    private fun onEquip(e: ArmorEquipEvent) {
        val player = e.player
        if (manager.isWingsEnabled) {
            if (ce.hasEnchantment(e.newArmorPiece, CEnchantments.WINGS) && regionCheck(player) && gameModeCheck(player)) {
                player.allowFlight = true
            }

            if (ce.hasEnchantment(e.oldArmorPiece, CEnchantments.WINGS) && gameModeCheck(player)) {
                player.allowFlight = false
            }
        }
    }

    @EventHandler
    private fun onFly(e: PlayerToggleFlightEvent) {
        val player = e.player
        if (manager.isWingsEnabled && ce.hasEnchantment(player.equipment!!.boots, CEnchantments.WINGS) && regionCheck(player) && !areEnemiesNearby(player)) {

            if (PluginSupport.SupportedPlugins.SPARTAN.isPluginLoaded(getPlugin())) {
                SpartanSupport.cancelNormalMovements(player)
            }

            if (e.isFlying) {
                if (player.allowFlight) {
                    e.isCancelled = true
                    player.isFlying = true
                    manager.addFlyingPlayer(player)
                }
            } else {
                manager.removeFlyingPlayer(player)
            }
        }
    }

    @EventHandler
    private fun onMove(e: PlayerMoveEvent) {
        if (e.from.blockX != e.to!!.blockX || e.from.blockY != e.to!!.blockY || e.from.blockZ != e.to!!.blockZ) {
            val player = e.player
            val isFlying = player.isFlying
            if (manager.isWingsEnabled && ce.hasEnchantment(player.equipment!!.boots, CEnchantments.WINGS)) {
                if (regionCheck(player)) {
                    if (!areEnemiesNearby(player)) {
                        player.allowFlight = true
                    } else {
                        if (isFlying && gameModeCheck(player)) {
                            player.isFlying = false
                            player.allowFlight = false
                            manager.removeFlyingPlayer(player)
                        }
                    }
                } else {
                    if (isFlying && gameModeCheck(player)) {
                        player.isFlying = false
                        player.allowFlight = false
                        manager.removeFlyingPlayer(player)
                    }
                }
                if (isFlying) {
                    manager.addFlyingPlayer(player)
                }
            }
        }
    }

    @EventHandler
    private fun onJoin(e: PlayerJoinEvent) {
        val player = e.player
        if (manager.isWingsEnabled && ce.hasEnchantment(player.equipment!!.boots, CEnchantments.WINGS) && regionCheck(player) && !areEnemiesNearby(player)) {

            if (PluginSupport.SupportedPlugins.SPARTAN.isPluginLoaded(getPlugin())) {
                SpartanSupport.cancelNormalMovements(player)
            }

            player.allowFlight = true
            manager.addFlyingPlayer(player)
        }
    }

    @EventHandler
    private fun onLeave(e: PlayerQuitEvent) {
        val player = e.player
        if (manager.isWingsEnabled && manager.isFlyingPlayer(player)) {
            player.isFlying = false
            player.allowFlight = false
            manager.removeFlyingPlayer(player)
        }
    }

    private fun gameModeCheck(player: Player): Boolean {
        return player.gameMode != GameMode.CREATIVE && player.gameMode != GameMode.ADVENTURE
    }

    private fun regionCheck(player: Player): Boolean {
        return manager.inLimitlessFlightWorld(player) || !manager.inBlacklistedWorld(player) && (pluginSupport.inTerritory(player) || pluginSupport.inWingsRegion(player) || manager.inWhitelistedWorld(player))
    }

    private fun areEnemiesNearby(player: Player): Boolean {
        if (manager.isEnemyCheckEnabled && !manager.inLimitlessFlightWorld(player)) {
            for (otherPlayer in getNearByPlayers(player, manager.enemyRadius)) {
                if (!(player.hasPermission("crazyenchantments.bypass.wings") && pluginSupport.isFriendly(player, otherPlayer))) {
                    return true
                }
            }
        }
        return false
    }

    private fun getNearByPlayers(player: Player, radius: Int): List<Player> {
        val players: MutableList<Player> = ArrayList()
        for (entity in player.getNearbyEntities(radius.toDouble(), radius.toDouble(), radius.toDouble())) {
            if (entity is Player) players.add(entity)
        }
        return players
    }
}
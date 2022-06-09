package me.badbones69.crazyenchantments.api

import me.badbones69.crazyenchantments.getPlugin
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object PluginSupport {

    private val manager = CrazyManager.getInstance()

    private val wings = manager.wingsManager

    private val worldGuardVersion = manager.worldGuardSupport

    private val plotSquaredVersion = manager.plotSquaredSupport

    fun inTerritory(player: Player): Boolean {
        return SupportedPlugins.PLOTSQUARED.isPluginLoaded(getPlugin()) && plotSquaredVersion.inTerritory(player)
    }

    fun isFriendly(player: Player, other: Player) {

    }

    fun isVanished(player: Player): Boolean {
        player.getMetadata("vanished").forEach {
            if (it.asBoolean()) return true
        }
        return false
    }

    fun allowsCombat(location: Location): Boolean {
        return !SupportedPlugins.WORLDEDIT.isPluginLoaded(getPlugin()) || !SupportedPlugins.WORLDGUARD.isPluginLoaded(getPlugin()) || worldGuardVersion.allowsPVP(location)
    }

    fun allowsDestruction(location: Location): Boolean {
        return !SupportedPlugins.WORLDEDIT.isPluginLoaded(getPlugin()) || !SupportedPlugins.WORLDGUARD.isPluginLoaded(getPlugin()) || worldGuardVersion.allowsBreak(location)
    }

    fun allowsExplosions(location: Location): Boolean {
        return !SupportedPlugins.WORLDEDIT.isPluginLoaded(getPlugin()) || !SupportedPlugins.WORLDGUARD.isPluginLoaded(getPlugin()) || worldGuardVersion.allowsExplosions(location)
    }

    fun inWingsRegion(player: Player) {

        if (!SupportedPlugins.WORLDEDIT.isPluginLoaded(getPlugin()) && !SupportedPlugins.WORLDGUARD.isPluginLoaded(getPlugin())) return

        wings.regions.forEach {
            if (worldGuardVersion.inRegion(it, player.location)) return

            if (wings.canOwnersFly() && worldGuardVersion.isOwner(player)) return

            if (wings.canMembersFly() && worldGuardVersion.isMember(player)) return
        }
    }

    fun noStack(entity: Entity) {

    }

    enum class SupportedPlugins(private val pluginName: String) {

        // Economy Plugins
        VAULT("Vault"),

        // Spawner Plugins
        SILKSPAWNERS("SilkSpawners"),

        // Anti Cheats
        SPARTAN("Spartan"),

        // Faction Plugins
        FACTIONSUUID("FactionsUUID"),
        FACTIONSX("FactionsX"),

        // Region Protection
        WORLDGUARD("WorldGuard"),
        WORLDEDIT("WorldEdit"),

        PLOTSQUARED("PlotSquared");

        fun getName() = pluginName

        fun isPluginLoaded(plugin: JavaPlugin) = plugin.server.pluginManager.isPluginEnabled(pluginName)

    }
}
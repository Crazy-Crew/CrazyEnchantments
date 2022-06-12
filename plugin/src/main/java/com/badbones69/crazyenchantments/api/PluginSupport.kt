package com.badbones69.crazyenchantments.api

import com.badbones69.crazyenchantments.getPlugin
import com.badbones69.crazyenchantments.multisupport.factions.FactionsVersion
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object PluginSupport {

    private val manager = CrazyManager.getInstance()

    private val wings = manager.wingsManager

    private val factionPlugin: FactionsVersion? = null

    private val worldGuardVersion = manager.worldGuardSupport

    private val plotSquaredVersion = manager.plotSquaredSupport

    fun inTerritory(player: Player): Boolean {
        return SupportedPlugins.PLOTSQUARED.isPluginLoaded(getPlugin()) && plotSquaredVersion.inTerritory(player)
    }

    fun isFriendly(entity: Entity, other: Entity): Boolean {
        val player = entity as Player
        val secondPlayer = other as Player

        if (factionPlugin != null && factionPlugin.isFriendly(player, secondPlayer)) return true

        // Check if Superior skyBlock is loaded.
        //             if (SupportedPlugins.SUPERIOR_SKYBLOCK.isPluginLoaded() &&
        //             SuperiorSkyblockSupport.isFriendly(player, other)) {

        // Check if MCMMO is loaded.
        // SupportedPlugins.MCMMO.isPluginLoaded() && MCMMOParty.isFriendly(player, other);

        return false
    }

    fun isVanished(player: Player): Boolean {
        player.getMetadata("vanished").forEach {
            if (it.asBoolean()) return true
        }
        return false
    }

    fun canBreakBlock(player: Player, block: Block): Boolean {
        if ((factionPlugin != null) && !factionPlugin.canBreakBlock(player, block)) return false

        return true
    }

    fun allowsCombat(location: Location): Boolean {

        //        if (SupportedPlugins.TOWNY.isPluginLoaded() &&
        //        !TownySupport.allowsPvP(location)) {
        //            return false;
        //        }

        return !SupportedPlugins.WORLDEDIT.isPluginLoaded(getPlugin()) || !SupportedPlugins.WORLDGUARD.isPluginLoaded(getPlugin()) || worldGuardVersion.allowsPVP(location)
    }

    fun allowsDestruction(location: Location): Boolean {
        return !SupportedPlugins.WORLDEDIT.isPluginLoaded(getPlugin()) || !SupportedPlugins.WORLDGUARD.isPluginLoaded(getPlugin()) || worldGuardVersion.allowsBreak(location)
    }

    fun allowsExplosions(location: Location): Boolean {
        return !SupportedPlugins.WORLDEDIT.isPluginLoaded(getPlugin()) || !SupportedPlugins.WORLDGUARD.isPluginLoaded(getPlugin()) || worldGuardVersion.allowsExplosions(location)
    }

    fun inWingsRegion(player: Player): Boolean {

        if (!SupportedPlugins.WORLDEDIT.isPluginLoaded(getPlugin()) && !SupportedPlugins.WORLDGUARD.isPluginLoaded(getPlugin())) return true

        wings.regions.forEach {
            if (worldGuardVersion.inRegion(it, player.location)) return true

            if (wings.canOwnersFly() && worldGuardVersion.isOwner(player)) return true

            if (wings.canMembersFly() && worldGuardVersion.isMember(player)) return true
        }

        return false
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
        VULCAN("Vulcan"),

        // Faction Plugins
        FACTIONSUUID("FactionsUUID"),

        // Not yet developed by me.
        FACTIONSX("FactionsX"),

        // Region Protection
        WORLDGUARD("WorldGuard"),
        WORLDEDIT("WorldEdit"),

        TOWNYADVANCED("TownyAdvanced"),
        PLOTSQUARED("PlotSquared");

        fun getName() = pluginName

        fun isPluginLoaded(plugin: JavaPlugin) = plugin.server.pluginManager.isPluginEnabled(pluginName)

    }
}
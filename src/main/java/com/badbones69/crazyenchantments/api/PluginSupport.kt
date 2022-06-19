package com.badbones69.crazyenchantments.api

import com.badbones69.crazyenchantments.api.multisupport.interfaces.factions.FactionsVersion
import com.badbones69.crazyenchantments.api.multisupport.misc.TownySupport
import com.badbones69.crazyenchantments.api.multisupport.misc.mobstacker.StackMobAntiSupport
import com.badbones69.crazyenchantments.api.multisupport.skyblock.SuperiorSkyBlockSupport
import com.badbones69.crazyenchantments.getPlugin
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
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
        if (entity !is Player || other !is Player) return true

        if (factionPlugin != null && factionPlugin.isFriendly(entity, other)) return true

        if (SupportedPlugins.SUPERIORSKYBLOCK.isPluginLoaded(getPlugin()) && SuperiorSkyBlockSupport.isFriendly(
                entity,
                other
            )) return true

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
        if (SupportedPlugins.TOWNYADVANCED.isPluginLoaded(getPlugin()) && !TownySupport.allowsCombat(location)) return false

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

        wings.regions.forEach {region ->
            if (worldGuardVersion.inRegion(region, player.location)) {
                return true
            } else {
                if (wings.canOwnersFly() && worldGuardVersion.isOwner(player)) {
                    return true
                }

                if (wings.canMembersFly() && worldGuardVersion.isMember(player)) {
                    return true
                }
            }
        }
        return false
    }

    fun noStack(entity: LivingEntity) {
        if (SupportedPlugins.STACKMOB.isPluginLoaded(getPlugin())) StackMobAntiSupport().preventStacking(entity)
    }

    enum class SupportedPlugins(private val pluginName: String) {

        // Economy Plugins
        VAULT("Vault"),

        // Spawner Plugins
        SILKSPAWNERS("SilkSpawners"),

        // Stacker Plugins

        // StackMob by Anti Person
        STACKMOB("StackMob"),

        // Anti Cheats
        SPARTAN("Spartan"),
        VULCAN("Vulcan"),

        // Faction Plugins
        FACTIONSUUID("FactionsUUID"),

        // Sky Block Plugins
        SUPERIORSKYBLOCK("SuperiorSkyblock2"),

        // Region Protection
        WORLDGUARD("WorldGuard"),
        WORLDEDIT("WorldEdit"),

        TOWNYADVANCED("TownyAdvanced"),
        PLOTSQUARED("PlotSquared");

        fun getName() = pluginName

        fun isPluginLoaded(plugin: JavaPlugin) = plugin.server.pluginManager.getPlugin(name) != null

    }
}
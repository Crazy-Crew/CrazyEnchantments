package com.badbones69.crazyenchantments.api

import com.badbones69.crazyenchantments.Methods
import com.badbones69.crazyenchantments.api.multisupport.factions.FactionsUUIDSupport
import com.badbones69.crazyenchantments.api.multisupport.interfaces.factions.FactionsVersion
import com.badbones69.crazyenchantments.api.multisupport.misc.TownySupport
import com.badbones69.crazyenchantments.api.multisupport.skyblock.SuperiorSkyBlockSupport
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object PluginSupport {

    private val crazyManager = CrazyManager.getInstance()

    private var factionPlugin: FactionsVersion? = null

    fun inTerritory(player: Player): Boolean {
        if (factionPlugin != null && factionPlugin?.inTerritory(player) == true) return true

        if (SupportedPlugins.SUPERIORSKYBLOCK.isPluginLoaded() && SuperiorSkyBlockSupport.inTerritory(player)) return true

        return SupportedPlugins.PLOTSQUARED.isPluginLoaded() && crazyManager.plotSquaredSupport.inTerritory(player)
    }

    fun isFriendly(entity: Entity, other: Entity): Boolean {
        if (entity !is Player || other !is Player) return false

        if (factionPlugin != null && factionPlugin?.isFriendly(entity, other) == true) return true

        if (SupportedPlugins.SUPERIORSKYBLOCK.isPluginLoaded() && SuperiorSkyBlockSupport.isFriendly(
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
        if ((factionPlugin != null) && factionPlugin?.canBreakBlock(player, block) == true) return false

        return true
    }

    fun allowsCombat(location: Location): Boolean {
        if (SupportedPlugins.TOWNYADVANCED.isPluginLoaded() && !TownySupport.allowsCombat(location)) return false
        return !SupportedPlugins.WORLDEDIT.isPluginLoaded() || !SupportedPlugins.WORLDGUARD.isPluginLoaded() || crazyManager.worldGuardSupport.allowsPVP(location)
    }

    fun allowsDestruction(location: Location): Boolean {
        return !SupportedPlugins.WORLDEDIT.isPluginLoaded() || !SupportedPlugins.WORLDGUARD.isPluginLoaded() || crazyManager.worldGuardSupport.allowsBreak(location)
    }

    fun allowsExplosions(location: Location): Boolean {
        return !SupportedPlugins.WORLDEDIT.isPluginLoaded() || !SupportedPlugins.WORLDGUARD.isPluginLoaded() || crazyManager.worldGuardSupport.allowsExplosions(location)
    }

    fun inWingsRegion(player: Player): Boolean {

        if (!SupportedPlugins.WORLDEDIT.isPluginLoaded() && !SupportedPlugins.WORLDGUARD.isPluginLoaded()) return true

        val wings = crazyManager.wingsManager
        val worldGuardVersion = crazyManager.worldGuardSupport

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

    }

    enum class SupportedPlugins(private val pluginName: String) {

        // Economy Plugins
        VAULT("Vault"),

        // Spawner Plugins
        SILKSPAWNERS("SilkSpawners"),

        // Stacker Plugins

        // WildStacker by Ome_R
        // WILD_STACKER("WildStacker"),

        // Anti Cheats
        SPARTAN("Spartan"),
        NO_CHEAT_PLUS("NoCheatPlus"),
        VULCAN("Vulcan"),

        // Faction Plugins
        FACTIONS_UUID("Factions"),

        // Sky Block Plugins
        SUPERIORSKYBLOCK("SuperiorSkyblock2"),

        // Region Protection
        WORLDGUARD("WorldGuard"),
        WORLDEDIT("WorldEdit"),

        TOWNYADVANCED("TownyAdvanced"),
        PLOTSQUARED("PlotSquared");

        companion object {
            private val cachedPluginState = hashMapOf<SupportedPlugins, Boolean>()

            fun updatePluginStates() {
                if (cachedPluginState.isNotEmpty()) cachedPluginState.clear()

                fun getPlugin(pluginName: String) = crazyManager.plugin.server.pluginManager.getPlugin(pluginName)?.isEnabled == true && crazyManager.plugin.server.pluginManager.getPlugin(pluginName) != null

                values().forEach { supportedPlugins ->

                    if (getPlugin(supportedPlugins.pluginName)) {

                        val plugin = crazyManager.plugin.server.pluginManager.getPlugin(supportedPlugins.pluginName)

                        //val authors = plugin?.description?.authors
                        //val version = plugin?.description?.version
                        val website = plugin?.description?.website

                        when (supportedPlugins) {
                            FACTIONS_UUID -> {
                                cachedPluginState[supportedPlugins] = website.equals("https://www.spigotmc.org/resources/factionsuuid.1035/", ignoreCase = true)
                                return@forEach
                            }
                            else -> {
                                cachedPluginState[supportedPlugins] = true
                                return@forEach
                            }
                        }
                    } else {
                        cachedPluginState[supportedPlugins] = false
                    }
                }

                updateFactionsPlugins()
            }

            fun printHooks() {
                if (cachedPluginState.isEmpty()) updatePluginStates()

                crazyManager.plugin.server.consoleSender.sendMessage(Methods.color("&4&lActive CrazyEnchantment Hooks:"))

                cachedPluginState.keys.forEach {
                    if (it.isPluginLoaded()) crazyManager.plugin.server.consoleSender.sendMessage(Methods.color("&6&l ${it.name} : &a&lENABLED"))
                }
            }

            private fun updateFactionsPlugins() {
                values().forEach {
                    if (it.isPluginLoaded()) {
                        when (it) {
                            FACTIONS_UUID -> {
                                factionPlugin = FactionsUUIDSupport()
                            }
                            else -> {}
                        }
                    }
                }
            }
        }

        fun getName() = pluginName

        fun isPluginLoaded(): Boolean {
            return cachedPluginState[this] == true
        }
    }
}

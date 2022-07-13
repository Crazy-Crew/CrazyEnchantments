package com.badbones69.crazyenchantments

import com.badbones69.crazyenchantments.api.CrazyManager
import com.badbones69.crazyenchantments.api.FileManager
import com.badbones69.crazyenchantments.api.FileManager.Files
import com.badbones69.crazyenchantments.api.PluginSupport.SupportedPlugins
import com.badbones69.crazyenchantments.api.economy.CurrencyAPI
import com.badbones69.crazyenchantments.api.multisupport.misc.spawners.SilkSpawnerSupport
import com.badbones69.crazyenchantments.commands.*
import com.badbones69.crazyenchantments.controllers.*
import com.badbones69.crazyenchantments.enchantments.*
import io.papermc.lib.PaperLib
import org.bstats.bukkit.Metrics
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class CrazyEnchantments : JavaPlugin(), Listener {

    private val crazyManager = CrazyManager.getInstance()

    private val fileManager = FileManager.getInstance()

    private var pluginEnabled = false

    private var armor: Armor? = null

    // Avoid using @this
    private val plugin = this

    private val generic = Attribute.GENERIC_MAX_HEALTH

    override fun onEnable() {

        if (!PaperLib.isPaper()) {
            logger.warning("====================================================")
            logger.warning(" " + this.name + " works better if you use Paper ")
            logger.warning(" as your server software.")
            logger.warning(" ")
            logger.warning(" Paper offers significant performance improvements,")
            logger.warning(" bug fixes, security enhancements and optional")
            logger.warning(" features for server owners to enhance their server.")
            logger.warning(" ")
            logger.warning(" All of your plugins should still work, and the")
            logger.warning(" Paper community will gladly help you fix any issues.")
            logger.warning("")
            logger.warning(" Join the Purpur Community @ https://purpurmc.org/discord")
            logger.warning("====================================================")
            logger.warning("A few features might not work on Spigot so be warned.")

            // getServer().getPluginManager().disablePlugin(this);
        }

        runCatching {
            crazyManager.loadPlugin(plugin)

            fileManager.logInfo(true).setup(this)

            val metricsEnabled = Files.CONFIG.file.getBoolean("Settings.Toggle-Metrics")

            if (Files.CONFIG.file.getString("Settings.Toggle-Metrics") != null) {
                if (metricsEnabled) Metrics(plugin, 4494)
            } else {
                logger.warning("Metrics was automatically enabled.")
                logger.warning("Please add Toggle-Metrics: false to the top of your config.yml")
                logger.warning("https://github.com/Crazy-Crew/Crazy-Crates/blob/main/src/main/resources/config.yml")
                logger.warning("An example if confused is linked above.")

                Metrics(plugin, 4494)
            }

            crazyManager.load()

        }.onFailure {

            logger.severe(it.message)

            it.stackTrace.forEach { e -> logger.severe(e.toString()) }

            pluginEnabled = false

            return
        }

        SupportedPlugins.printHooks()

        CurrencyAPI.loadCurrency()

        val patchHealth = Files.CONFIG.file.getBoolean("Settings.Reset-Players-Max-Health")

        plugin.server.onlinePlayers.forEach {
            crazyManager.loadCEPlayer(it)

            if (patchHealth) it.getAttribute(generic)?.baseValue = it.getAttribute(generic)?.baseValue!!
        }

        plugin.server.scheduler.runTaskTimerAsynchronously(plugin, Runnable {
            crazyManager.cePlayers.forEach { crazyManager.backupCEPlayer(it) }
        }, 5 * 20 * 60, 5 * 20 * 60)

        enable()

        pluginEnabled = true;
    }

    override fun onDisable() {
        if (!pluginEnabled) return

        disable()
    }

    private fun enable() {

        listOf(
            plugin,
            ShopControl(),
            InfoGUIControl(),
            LostBookController(),
            EnchantmentControl(),
            SignControl(),
            DustControl(),
            Tinkerer(),
            AuraListener(),
            ScrollControl(),
            BlackSmith(),
            ArmorListener(),
            ProtectionCrystal(),
            Scrambler(),
            CommandChecker(),
            FireworkDamage(),
            Bows(),
            Axes(),
            Tools(),
            Hoes(),
            Helmets(),
            PickAxes(),
            Boots(),
            Swords(),
            Armor().also { armor = it },
            AllyEnchantments()
        ).onEach {loop ->
            server.pluginManager.registerEvents(loop, plugin)
        }

        if (crazyManager.isGkitzEnabled) {
            logger.info("Gkitz support is now enabled.")
            server.pluginManager.registerEvents(GKitzController(), plugin)
        }

        if (SupportedPlugins.SILKSPAWNERS.isPluginLoaded()) {
            logger.info("Silk Spawners support is now enabled.")
            server.pluginManager.registerEvents(SilkSpawnerSupport(), plugin)
        }

        getCommand("crazyenchantments")?.setExecutor(CECommand())
        getCommand("crazyenchantments")?.tabCompleter = CETab()
        getCommand("tinkerer")?.setExecutor(TinkerCommand())
        getCommand("blacksmith")?.setExecutor(BlackSmithCommand())
        getCommand("gkit")?.setExecutor(GkitzCommand())
        getCommand("gkit")?.tabCompleter = GkitzTab()
    }

    private fun disable() {
        armor?.stop()

        if (crazyManager.allyManager != null) crazyManager.allyManager.forceRemoveAllies()

        server.onlinePlayers.forEach { crazyManager.unloadCEPlayer(it) }
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        crazyManager.loadCEPlayer(player)
        crazyManager.updatePlayerEffects(player)

        val patchHealth = Files.CONFIG.file.getBoolean("Settings.Reset-Players-Max-Health")

        if (patchHealth) player.getAttribute(generic)?.baseValue = player.getAttribute(generic)?.baseValue!!
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        crazyManager.unloadCEPlayer(e.player)
    }
}
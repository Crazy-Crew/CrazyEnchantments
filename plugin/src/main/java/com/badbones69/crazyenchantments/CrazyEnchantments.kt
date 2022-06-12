package com.badbones69.crazyenchantments

import com.badbones69.crazyenchantments.api.CrazyManager
import com.badbones69.crazyenchantments.api.FileManager
import com.badbones69.crazyenchantments.api.FileManager.Files
import com.badbones69.crazyenchantments.api.economy.CurrencyAPI
import com.badbones69.crazyenchantments.commands.*
import com.badbones69.crazyenchantments.controllers.*
import com.badbones69.crazyenchantments.enchantments.*
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin


class CrazyEnchantments : JavaPlugin(), Listener {

    private val manager = CrazyManager.getInstance()
    private val fileManager = FileManager.getInstance()

    private val patchHealth = Files.CONFIG.file.getBoolean("Settings.Reset-Players-Max-Health")
    private var armor: Armor? = null

    // Avoid using @this
    private val plugin = this

    private val generic = Attribute.GENERIC_MAX_HEALTH

    override fun onEnable() {
        fileManager.logInfo(true).setup(plugin)
        manager.load()

        Methods.hasUpdate()

        CurrencyAPI.loadCurrency()

        plugin.server.onlinePlayers.forEach {
            manager.loadCEPlayer(it)
            if (patchHealth) it.getAttribute(generic)?.baseValue = it.getAttribute(generic)?.baseValue!!
        }

        getCommand("crazyenchantments")?.setExecutor(CECommand())
        getCommand("crazyenchantments")?.tabCompleter = CETab()
        getCommand("tinkerer")?.setExecutor(TinkerCommand())
        getCommand("blacksmith")?.setExecutor(BlackSmithCommand())
        getCommand("gkit")?.setExecutor(GkitzCommand())
        getCommand("gkit")?.tabCompleter = GkitzTab()

        registerListener(
            this,
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
            Boots,
            Swords(),
            Armor().also { armor = it },
            AllyEnchantments()
        )

        if (manager.isGkitzEnabled) registerListener(GKitzController())

        // if (PluginSupport.SupportedPlugins.SILKSPAWNERS.isPluginLoaded(plugin))

        plugin.server.scheduler.runTaskTimerAsynchronously(plugin, Runnable {
               manager.cePlayers.forEach { manager.backupCEPlayer(it) }
        }, 5 * 20 * 60, 5 * 20 * 60)
    }

    override fun onDisable() {
        armor?.stop()

        if (manager.allyManager != null) manager.allyManager.forceRemoveAllies()
        server.onlinePlayers.forEach { manager.unloadCEPlayer(it) }
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        manager.loadCEPlayer(player)
        manager.updatePlayerEffects(player)

        if (patchHealth) player.getAttribute(generic)?.baseValue = player.getAttribute(generic)?.baseValue!!
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        manager.unloadCEPlayer(e.player)
    }
}

fun CrazyEnchantments.registerListener(vararg listeners: Listener) = listeners.toList().forEach { server.pluginManager.registerEvents(it, this) }

fun getPlugin() = JavaPlugin.getPlugin(CrazyEnchantments::class.java)
package com.badbones69.crazyenchantments.paper;

import com.badbones69.crazyenchantments.paper.api.FileManager;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.paper.api.support.misc.spawners.SilkSpawnerSupport;
import com.badbones69.crazyenchantments.paper.commands.BlackSmithCommand;
import com.badbones69.crazyenchantments.paper.commands.CECommand;
import com.badbones69.crazyenchantments.paper.commands.CETab;
import com.badbones69.crazyenchantments.paper.commands.GkitzCommand;
import com.badbones69.crazyenchantments.paper.commands.GkitzTab;
import com.badbones69.crazyenchantments.paper.commands.TinkerCommand;
import com.badbones69.crazyenchantments.paper.controllers.*;
import com.badbones69.crazyenchantments.paper.enchantments.AllyEnchantments;
import com.badbones69.crazyenchantments.paper.enchantments.ArmorEnchantments;
import com.badbones69.crazyenchantments.paper.enchantments.AxeEnchantments;
import com.badbones69.crazyenchantments.paper.enchantments.BootEnchantments;
import com.badbones69.crazyenchantments.paper.enchantments.BowEnchantments;
import com.badbones69.crazyenchantments.paper.enchantments.HelmetEnchantments;
import com.badbones69.crazyenchantments.paper.enchantments.HoeEnchantments;
import com.badbones69.crazyenchantments.paper.enchantments.PickaxeEnchantments;
import com.badbones69.crazyenchantments.paper.enchantments.SwordEnchantments;
import com.badbones69.crazyenchantments.paper.enchantments.ToolEnchantments;
import com.badbones69.crazyenchantments.paper.listeners.AuraListener;
import com.badbones69.crazyenchantments.paper.listeners.DustControlListener;
import com.badbones69.crazyenchantments.paper.listeners.FireworkDamageListener;
import com.badbones69.crazyenchantments.paper.listeners.ProtectionCrystalListener;
import com.badbones69.crazyenchantments.paper.listeners.ShopListener;
import com.badbones69.crazyenchantments.paper.listeners.server.WorldSwitchListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyEnchantments extends JavaPlugin implements Listener {

    private static CrazyEnchantments plugin;

    private Starter starter;

    // Plugin Listeners.
    public final PluginManager pluginManager = getServer().getPluginManager();

    private FireworkDamageListener fireworkDamageListener;
    private ShopListener shopListener;
    private ArmorEnchantments armorEnchantments;

    // Menus.
    private Tinkerer tinkerer;
    private BlackSmith blackSmith;
    private GKitzController gKitzController;

    private final BossBarController bossBarController = new BossBarController(this);

    @Override
    public void onEnable() {
        plugin = this;

        starter = new Starter();

        // Create all instances we need.
        starter.run();

        starter.getCurrencyAPI().loadCurrency();

        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration tinker = Files.TINKER.getFile();

        if (!config.contains("Settings.Toggle-Metrics")) {
            config.set("Settings.Toggle-Metrics", false);

            Files.CONFIG.saveFile();
        }

        if (!config.contains("Settings.Refresh-Potion-Effects-On-World-Change")) {
            config.set("Settings.Refresh-Potion-Effects-On-World-Change", false);
            
            Files.CONFIG.saveFile();
        }

        if (!tinker.contains("Settings.Tinker-Version")) {
            tinker.set("Settings.Tinker-Version", 1.0);

            FileManager.Files.TINKER.saveFile();
        }

        if (config.getBoolean("Settings.Toggle-Metrics")) new Metrics(this, 4494);

        pluginManager.registerEvents(this, this);
        pluginManager.registerEvents(blackSmith = new BlackSmith(), this);
        pluginManager.registerEvents(tinkerer = new Tinkerer(), this);
        pluginManager.registerEvents(fireworkDamageListener = new FireworkDamageListener(), this);
        pluginManager.registerEvents(shopListener = new ShopListener(), this);

        enable();
    }

    @Override
    public void onDisable() {
        disable();
    }

    private void enable() {
        // Load what we need to properly enable the plugin.
        starter.getCrazyManager().load();

        pluginManager.registerEvents(new DustControlListener(), this);

        pluginManager.registerEvents(new PickaxeEnchantments(), this);
        pluginManager.registerEvents(new HelmetEnchantments(), this);
        pluginManager.registerEvents(new SwordEnchantments(), this);
        pluginManager.registerEvents(armorEnchantments = new ArmorEnchantments(), this);
        pluginManager.registerEvents(new AllyEnchantments(), this);
        pluginManager.registerEvents(new ToolEnchantments(), this);
        pluginManager.registerEvents(new BootEnchantments(), this);
        pluginManager.registerEvents(new AxeEnchantments(), this);
        pluginManager.registerEvents(new BowEnchantments(), this);
        pluginManager.registerEvents(new HoeEnchantments(), this);

        pluginManager.registerEvents(new ProtectionCrystalListener(), this);
        pluginManager.registerEvents(new FireworkDamageListener(), this);
        pluginManager.registerEvents(new AuraListener(), this);

        pluginManager.registerEvents(new InfoGUIControl(), this);
        pluginManager.registerEvents(new LostBookController(), this);
        pluginManager.registerEvents(new CommandChecker(), this);

        pluginManager.registerEvents(new WorldSwitchListener(), this);

        if (starter.getCrazyManager().isGkitzEnabled()) {
            getLogger().info("G-Kitz support is now enabled.");

            pluginManager.registerEvents(gKitzController = new GKitzController(), this);
        }

        if (SupportedPlugins.SILK_SPAWNERS.isCachedPluginLoaded()) pluginManager.registerEvents(new SilkSpawnerSupport(), this);

        registerCommand(getCommand("crazyenchantments"), new CETab(), new CECommand());

        registerCommand(getCommand("tinkerer"), null, new TinkerCommand());
        registerCommand(getCommand("blacksmith"), null, new BlackSmithCommand());

        registerCommand(getCommand("gkit"), new GkitzTab(), new GkitzCommand());
    }

    private void disable() {
        bossBarController.removeAllBossBars();
        armorEnchantments.stop();

        if (starter.getAllyManager() != null) starter.getAllyManager().forceRemoveAllies();

        getServer().getOnlinePlayers().forEach(starter.getCrazyManager()::unloadCEPlayer);
    }

    private void registerCommand(PluginCommand pluginCommand, TabCompleter tabCompleter, CommandExecutor commandExecutor) {
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commandExecutor);

            if (tabCompleter != null) pluginCommand.setTabCompleter(tabCompleter);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.starter.getCrazyManager().loadCEPlayer(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.starter.getCrazyManager().unloadCEPlayer(event.getPlayer());
    }

    public static CrazyEnchantments getPlugin() {
        return plugin;
    }

    public Starter getStarter() {
        return this.starter;
    }

    // Plugin Listeners.
    public FireworkDamageListener getFireworkDamageListener() {
        return this.fireworkDamageListener;
    }

    public ShopListener getShopListener() {
        return this.shopListener;
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public Tinkerer getTinkerer() {
        return this.tinkerer;
    }

    public BlackSmith getBlackSmith() {
        return this.blackSmith;
    }

    public GKitzController getgKitzController() {
        return this.gKitzController;
    }

    public BossBarController getBossBarController() {
        return bossBarController;
    }
}
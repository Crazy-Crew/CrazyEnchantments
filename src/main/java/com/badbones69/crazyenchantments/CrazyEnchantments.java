package com.badbones69.crazyenchantments;

import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.api.economy.vault.VaultSupport;
import com.badbones69.crazyenchantments.api.support.misc.spawners.SilkSpawnerSupport;
import com.badbones69.crazyenchantments.commands.*;
import com.badbones69.crazyenchantments.controllers.*;
import com.badbones69.crazyenchantments.enchantments.*;
import com.badbones69.crazyenchantments.listeners.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyEnchantments extends JavaPlugin implements Listener {

    private static CrazyEnchantments plugin;

    private boolean isEnabled = false;

    private Starter starter;

    // Plugin Listeners.
    public PluginManager pluginManager = getServer().getPluginManager();

    private FireworkDamageListener fireworkDamageListener;
    private ShopListener shopListener;

    private ArmorEnchantments armorEnchantments;

    // Menus.
    private Tinkerer tinkerer;
    private BlackSmith blackSmith;
    private GKitzController gKitzController;

    @Override
    public void onEnable() {
        try {
            plugin = this;

            starter = new Starter();

            // Create all instances we need.
            starter.run();

            starter.getCurrencyAPI().loadCurrency();

            FileConfiguration config = Files.CONFIG.getFile();

            String metricsPath = config.getString("Settings.Toggle-Metrics");
            boolean metricsEnabled = config.getBoolean("Settings.Toggle-Metrics");

            if (metricsPath == null) {
                config.set("Settings.Toggle-Metrics", false);

                Files.CONFIG.saveFile();
            }

            if (metricsEnabled) new Metrics(this, 4494);

            pluginManager.registerEvents(fireworkDamageListener = new FireworkDamageListener(), this);
            pluginManager.registerEvents(shopListener = new ShopListener(), this);
        } catch (Exception e) {
            e.printStackTrace();

            isEnabled = false;

            return;
        }

        isEnabled = true;

        enable();
    }

    @Override
    public void onDisable() {
        if (!isEnabled) return;

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
        pluginManager.registerEvents(blackSmith = new BlackSmith(), this);
        pluginManager.registerEvents(tinkerer = new Tinkerer(), this);

        if (starter.getCrazyManager().isGkitzEnabled()) {
            getLogger().info("G-Kitz support is now enabled.");

            pluginManager.registerEvents(gKitzController = new GKitzController(), this);
        }

        if (SupportedPlugins.SILK_SPAWNERS.isCachedPluginLoaded()) {
            getLogger().fine("Silk Spawners support is enabled.");
            pluginManager.registerEvents(new SilkSpawnerSupport(), this);
        }

        if (SupportedPlugins.SILK_SPAWNERS_V2.isCachedPluginLoaded()) {
            getLogger().fine("Silk Spawners support by Candc is enabled.");
        }

        getCommand("crazyenchantments").setExecutor(new CECommand());
        getCommand("crazyenchantments").setTabCompleter(new CETab());

        getCommand("tinkerer").setExecutor(new TinkerCommand());
        getCommand("blacksmith").setExecutor(new BlackSmithCommand());

        getCommand("gkit").setExecutor(new GkitzCommand());
        getCommand("gkit").setTabCompleter(new GkitzTab());
    }

    private void disable() {
        armorEnchantments.stop();

        if (starter.getAllyManager() != null) starter.getAllyManager().forceRemoveAllies();

        getServer().getOnlinePlayers().forEach(starter.getCrazyManager()::unloadCEPlayer);
    }

    public static CrazyEnchantments getPlugin() {
        return plugin;
    }

    public Starter getStarter() {
        return starter;
    }

    // Plugin Listeners.
    public FireworkDamageListener getFireworkDamageListener() {
        return fireworkDamageListener;
    }

    public ShopListener getShopListener() {
        return shopListener;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public Tinkerer getTinkerer() {
        return tinkerer;
    }

    public BlackSmith getBlackSmith() {
        return blackSmith;
    }

    public GKitzController getgKitzController() {
        return gKitzController;
    }
}
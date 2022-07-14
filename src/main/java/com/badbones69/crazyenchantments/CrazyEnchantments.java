package com.badbones69.crazyenchantments;

import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.api.multisupport.misc.spawners.SilkSpawnerSupport;
import com.badbones69.crazyenchantments.commands.*;
import com.badbones69.crazyenchantments.controllers.*;
import com.badbones69.crazyenchantments.enchantments.*;
import com.badbones69.crazyenchantments.api.PluginSupport.SupportedPlugins;
import io.papermc.lib.PaperLib;
import org.bstats.bukkit.Metrics;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyEnchantments extends JavaPlugin implements Listener {

    private final CrazyManager crazyManager = CrazyManager.getInstance();

    private final FileManager fileManager = FileManager.getInstance();

    private boolean pluginEnabled = false;

    private Armor armor = null;

    private final Attribute generic = Attribute.GENERIC_MAX_HEALTH;

    @Override
    public void onEnable() {

        if (!PaperLib.isPaper()) {
            getLogger().warning("====================================================");
            getLogger().warning(" " + this.getName() + " works better if you use Paper ");
            getLogger().warning(" as your server software.");
            getLogger().warning(" ");
            getLogger().warning(" Paper offers significant performance improvements,");
            getLogger().warning(" bug fixes, security enhancements and optional");
            getLogger().warning(" features for server owners to enhance their server.");
            getLogger().warning(" ");
            getLogger().warning(" All of your plugins will function the same,");
            getLogger().warning(" as it is a drop in replacement over spigot.")
            getLogger().warning("");
            getLogger().warning(" Join the Purpur Community @ https://purpurmc.org/discord");
            getLogger().warning("====================================================");

            getLogger().warning("A few features might not work on Spigot so be warned.");

            // getServer().getPluginManager().disablePlugin(this);
        }

        try {
            crazyManager.loadPlugin(this);

            fileManager.logInfo(true).setup(this);

            boolean metricsEnabled = Files.CONFIG.getFile().getBoolean("Settings.Toggle-Metrics");
            String metrics = Files.CONFIG.getFile().getString("Settings.Toggle-Metrics");

            if (metrics != null) {
                if (metricsEnabled) new Metrics(this, 4494);
            } else {
                getLogger().warning("Metrics was automatically enabled.");
                getLogger().warning("Please add Toggle-Metrics: false to the top of your config.yml");
                getLogger().warning("https://github.com/Crazy-Crew/Crazy-Crates/blob/main/src/main/resources/config.yml");
                getLogger().warning("An example if confused is linked above.");

                new Metrics(this, 4494);
            }


        } catch (Exception e) {
            getLogger().severe(e.getMessage());

            for (StackTraceElement stack : e.getStackTrace()) {
                getLogger().severe(String.valueOf(stack));
            }

            pluginEnabled = false;

            return;
        }

        SupportedPlugins.Companion.printHooks();
        CurrencyAPI.loadCurrency();

        boolean patchHealth = Files.CONFIG.getFile().getBoolean("Settings.Reset-Players-Max-Health");

        for (Player player : getServer().getOnlinePlayers()) {
            crazyManager.loadCEPlayer(player);

            if (patchHealth) player.getAttribute(generic).setBaseValue(player.getAttribute(generic).getBaseValue());
        }

        getServer().getScheduler().runTaskTimerAsynchronously(this, bukkitTask -> {
            crazyManager.getCEPlayers().forEach(crazyManager::backupCEPlayer);
        }, 5 * 20 * 60, 5 * 20 * 60);


        pluginEnabled = true;

        enable();
    }

    @Override
    public void onDisable() {
        if (pluginEnabled) return;

        disable();
    }

    private void enable() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new EnchantmentControl(), this);
        pluginManager.registerEvents(new SignControl(), this);
        pluginManager.registerEvents(new DustControl(), this);
        pluginManager.registerEvents(new ScrollControl(), this);
        pluginManager.registerEvents(new ShopControl(), this);
        pluginManager.registerEvents(new InfoGUIControl(), this);

        pluginManager.registerEvents(new LostBookController(), this);

        pluginManager.registerEvents(new Bows(), this);
        pluginManager.registerEvents(new Axes(), this);
        pluginManager.registerEvents(new Tools(), this);
        pluginManager.registerEvents(new Hoes(), this);
        pluginManager.registerEvents(new Helmets(), this);
        pluginManager.registerEvents(new PickAxes(), this);
        pluginManager.registerEvents(new Boots(), this);
        pluginManager.registerEvents(new Swords(), this);
        pluginManager.registerEvents(armor = new Armor(), this);

        pluginManager.registerEvents(new AllyEnchantments(), this);

        pluginManager.registerEvents(new Tinkerer(), this);
        pluginManager.registerEvents(new AuraListener(), this);
        pluginManager.registerEvents(new BlackSmith(), this);
        pluginManager.registerEvents(new ArmorListener(), this);
        pluginManager.registerEvents(new ProtectionCrystal(), this);
        pluginManager.registerEvents(new Scrambler(), this);
        pluginManager.registerEvents(new CommandChecker(), this);
        pluginManager.registerEvents(new FireworkDamage(), this);

        if (crazyManager.isGkitzEnabled()) {
            getLogger().info("Gkitz support is now enabled.");
            getServer().getPluginManager().registerEvents(new GKitzController(), this);
        }

        if (SupportedPlugins.SILKSPAWNERS.isPluginLoaded()) {
            getLogger().info("Silk Spawners support is now enabled.");
            getServer().getPluginManager().registerEvents(new SilkSpawnerSupport(), this);
        }

        getCommand("crazyenchantments").setExecutor(new CECommand());
        getCommand("crazyenchantments").setTabCompleter(new CETab());

        getCommand("tinkerer").setExecutor(new TinkerCommand());
        getCommand("blacksmith").setExecutor(new BlackSmithCommand());

        getCommand("gkit").setExecutor(new GkitzCommand());
        getCommand("gkit").setTabCompleter(new GkitzTab());
    }

    private void disable() {
        armor.stop();

        if (crazyManager.getAllyManager() != null) crazyManager.getAllyManager().forceRemoveAllies();

        getServer().getOnlinePlayers().forEach(crazyManager::unloadCEPlayer);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        crazyManager.loadCEPlayer(player);
        crazyManager.updatePlayerEffects(player);

        boolean patchHealth = Files.CONFIG.getFile().getBoolean("Settings.Reset-Players-Max-Health");

        if (patchHealth) player.getAttribute(generic).setBaseValue(player.getAttribute(generic).getBaseValue());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        crazyManager.unloadCEPlayer(event.getPlayer());
    }
}

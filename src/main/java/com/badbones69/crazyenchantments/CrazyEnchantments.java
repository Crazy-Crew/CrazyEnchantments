package com.badbones69.crazyenchantments;

import com.badbones69.crazyenchantments.controllers.settings.EnchantmentSettings;
import com.badbones69.crazyenchantments.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.listeners.DustControlListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyEnchantments extends JavaPlugin implements Listener {

    private static CrazyEnchantments plugin;

    private boolean isEnabled = false;

    private Starter starter;

    // Settings
    private ProtectionCrystalSettings protectionCrystalSettings;

    private EnchantmentSettings enchantmentSettings;

    PluginManager pluginManager = getServer().getPluginManager();

    @Override
    public void onEnable() {
        try {
            plugin = this;

            starter = new Starter();

            // Create all instances we need.
            starter.run();

            // Set up all our files.
            starter.getFileManager().setLog(true).setup();

            protectionCrystalSettings = new ProtectionCrystalSettings();
            enchantmentSettings = new EnchantmentSettings();
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

        //disable();
    }

    // Listeners

    private void enable() {
        // Load what we need to properly enable the plugin.
        starter.getCrazyManager().load();

        pluginManager.registerEvents(new DustControlListener(), this);

        //pluginManager.registerEvents(enchantmentControl = new EnchantmentControl(), this);
        //pluginManager.registerEvents(signControl = new SignControl(), this);
        //pluginManager.registerEvents(scrollControl = new ScrollControl(), this);
        //pluginManager.registerEvents(shopControl = new ShopControl(), this);
        //pluginManager.registerEvents(infoGUIControl = new InfoGUIControl(), this);

        //pluginManager.registerEvents(lostBookController = new LostBookController(), this);

        //pluginManager.registerEvents(bows = new Bows(), this);
        //pluginManager.registerEvents(axes = new Axes(), this);
        //pluginManager.registerEvents(tools = new Tools(), this);
        //pluginManager.registerEvents(hoes = new Hoes(), this);
        //pluginManager.registerEvents(helmets = new Helmets(), this);
        //pluginManager.registerEvents(pickAxes = new PickAxes(), this);
        //pluginManager.registerEvents(boots = new Boots(), this);
        //pluginManager.registerEvents(swords = new Swords(), this);
        //pluginManager.registerEvents(armor = new Armor(), this);

        //pluginManager.registerEvents(allyEnchantments = new AllyEnchantments(), this);

        //pluginManager.registerEvents(tinkerer = new Tinkerer(), this);
        //pluginManager.registerEvents(auraListener = new AuraListener(), this);
        //pluginManager.registerEvents(blackSmith = new BlackSmith(), this);
        //pluginManager.registerEvents(armorListener = new ArmorListener(), this);
        //pluginManager.registerEvents(protectionCrystal = new ProtectionCrystal(), this);
        //pluginManager.registerEvents(scrambler = new Scrambler(), this);
        //pluginManager.registerEvents(commandChecker = new CommandChecker(), this);
        //pluginManager.registerEvents(fireworkDamage = new FireworkDamage(), this);

        //if (starter.getCrazyManager().isGkitzEnabled()) {
        //    getLogger().info("Gkitz support is now enabled.");

        //    getServer().getPluginManager().registerEvents(new GKitzController(), this);
        //}

        //if (SupportedPlugins.SILKSPAWNERS.isPluginLoaded()) {
        //    getLogger().info("Silk Spawners support is now enabled.");

        //    getServer().getPluginManager().registerEvents(new SilkSpawnerSupport(), this);
        //}

        //if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) noCheatPlusSupport = new NoCheatPlusSupport();

        //if (SupportedPlugins.ORAXEN.isPluginLoaded()) oraxenSupport = new OraxenSupport();

        //if (SupportedPlugins.SUPERIORSKYBLOCK.isPluginLoaded()) superiorSkyBlockSupport = new SuperiorSkyBlockSupport();

        //getCommand("crazyenchantments").setExecutor(new CECommand());
        //getCommand("crazyenchantments").setTabCompleter(new CETab());

        //getCommand("tinkerer").setExecutor(new TinkerCommand());
        //getCommand("blacksmith").setExecutor(new BlackSmithCommand());

        //getCommand("gkit").setExecutor(new GkitzCommand());
        //getCommand("gkit").setTabCompleter(new GkitzTab());
    }

    private void disable() {
        //armor.stop();

        //if (starter.getAllyManager() != null) starter.getAllyManager().forceRemoveAllies();

        //getServer().getOnlinePlayers().forEach(starter.getCrazyManager()::unloadCEPlayer);
    }

    public static CrazyEnchantments getPlugin() {
        return plugin;
    }

    public Starter getStarter() {
        return starter;
    }

    // Settings
    public ProtectionCrystalSettings getProtectionCrystalSettings() {
        return protectionCrystalSettings;
    }

    public EnchantmentSettings getEnchantmentSettings() {
        return enchantmentSettings;
    }
}
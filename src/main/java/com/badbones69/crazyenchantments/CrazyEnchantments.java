package com.badbones69.crazyenchantments;

import com.badbones69.crazyenchantments.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.api.economy.vault.VaultSupport;
import com.badbones69.crazyenchantments.api.support.misc.spawners.SilkSpawnerSupport;
import com.badbones69.crazyenchantments.commands.*;
import com.badbones69.crazyenchantments.controllers.GKitzController;
import com.badbones69.crazyenchantments.enchantments.*;
import com.badbones69.crazyenchantments.listeners.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyEnchantments extends JavaPlugin implements Listener {

    private static CrazyEnchantments plugin;

    private boolean isEnabled = false;

    private Starter starter;

    // Plugin Listeners.
    PluginManager pluginManager = getServer().getPluginManager();

    private ProtectionCrystalListener protectionCrystalListener;
    private FireworkDamageListener fireworkDamageListener;
    private ScramblerListener scramblerListener;
    private ScrollListener scrollListener;
    private ArmorListener armorListener;
    private ShopListener shopListener;

    private BootEnchantments bootEnchantments;
    private ArmorEnchantments armorEnchantments;

    @Override
    public void onEnable() {
        try {
            plugin = this;

            starter = new Starter();

            // Create all instances we need.
            starter.run();

            // Load vault support.
            if (SupportedPlugins.VAULT.isPluginLoaded()) {
                starter.setVaultSupport(new VaultSupport());

                starter.getVaultSupport().loadVault();
            } else {
                plugin.getLogger().warning("Vault was not found so support for economies was not enabled.");
            }

            pluginManager.registerEvents(protectionCrystalListener = new ProtectionCrystalListener(), this);
            pluginManager.registerEvents(fireworkDamageListener = new FireworkDamageListener(), this);
            pluginManager.registerEvents(scramblerListener = new ScramblerListener(), this);
            pluginManager.registerEvents(armorListener = new ArmorListener(), this);
            pluginManager.registerEvents(scrollListener = new ScrollListener(), this);
            pluginManager.registerEvents(shopListener = new ShopListener(), this);

            pluginManager.registerEvents(bootEnchantments = new BootEnchantments(), this);
            pluginManager.registerEvents(armorEnchantments = new ArmorEnchantments(), this);
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

        pluginManager.registerEvents(new AllyEnchantments(), this);
        pluginManager.registerEvents(new AxeEnchantments(), this);
        pluginManager.registerEvents(new BowEnchantments(), this);
        pluginManager.registerEvents(new HelmetEnchantments(), this);
        pluginManager.registerEvents(new HoeEnchantments(), this);
        pluginManager.registerEvents(new PickaxeEnchantments(), this);
        pluginManager.registerEvents(new SwordEnchantments(), this);
        pluginManager.registerEvents(new ToolEnchantments(), this);

        pluginManager.registerEvents(new AuraListener(), this);
        pluginManager.registerEvents(new FireworkDamageListener(), this);

        if (starter.getCrazyManager().isGkitzEnabled()) {
            getLogger().info("G-Kitz support is now enabled.");

            pluginManager.registerEvents(new GKitzController(), this);
        }

        if (SupportedPlugins.SILKSPAWNERS.isPluginLoaded()) {
            getLogger().info("SilkSpawners support is now enabled.");

            pluginManager.registerEvents(new SilkSpawnerSupport(), this);
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
    public ProtectionCrystalListener getProtectionCrystalListener() {
        return protectionCrystalListener;
    }

    public FireworkDamageListener getFireworkDamageListener() {
        return fireworkDamageListener;
    }

    public ScrollListener getScrollListener() {
        return scrollListener;
    }

    public ArmorListener getArmorListener() {
        return armorListener;
    }

    public ScramblerListener getScramblerListener() {
        return scramblerListener;
    }

    public ShopListener getShopListener() {
        return shopListener;
    }

    public ArmorEnchantments getArmorEnchantments() {
        return armorEnchantments;
    }
    public BootEnchantments getBootEnchantments() {
        return bootEnchantments;
    }
}
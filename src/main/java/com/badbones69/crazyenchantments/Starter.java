package com.badbones69.crazyenchantments;

import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.api.economy.vault.VaultSupport;
import com.badbones69.crazyenchantments.api.managers.*;
import com.badbones69.crazyenchantments.api.SkullCreator;
import com.badbones69.crazyenchantments.utilities.BowUtils;

public class Starter {

    // Managers
    private FileManager fileManager;

    private CrazyManager crazyManager;

    private PluginSupport pluginSupport;

    private AllyManager allyManager;
    private ArmorEnchantmentManager armorEnchantmentManager;
    private BlackSmithManager blackSmithManager;
    private BowEnchantmentManager bowEnchantmentManager;
    private ShopManager shopManager;
    private WingsManager wingsManager;

    // Misc
    private Methods methods;

    private SkullCreator skullCreator;

    private CurrencyAPI currencyAPI;

    // Plugin Support

    private VaultSupport vaultSupport;

    // Plugin Utils
    private BowUtils bowUtils;

    public void run() {
        // Managers
        fileManager = new FileManager();

        methods = new Methods();

        crazyManager = new CrazyManager();

        pluginSupport = new PluginSupport();

        // Misc

        // Currency
        //vaultSupport = new VaultSupport();

        //currencyAPI = new CurrencyAPI();

        //currencyAPI.loadCurrency();

        //skullCreator = new SkullCreator();

        // Other Managers
        //allyManager = new AllyManager();

        //armorEnchantmentManager = new ArmorEnchantmentManager();

        //blackSmithManager = new BlackSmithManager();
        //blackSmithManager.load();

        //bowEnchantmentManager = new BowEnchantmentManager();

        //shopManager = new ShopManager();
        //wingsManager = new WingsManager();

        // Plugin Utils
        //bowUtils = new BowUtils();
    }

    // Managers
    public FileManager getFileManager() {
        return fileManager;
    }

    public CrazyManager getCrazyManager() {
        return crazyManager;
    }

    public Methods getMethods() {
        return methods;
    }

    public PluginSupport getPluginSupport() {
        return pluginSupport;
    }

    public AllyManager getAllyManager() {
        return allyManager;
    }

    public ArmorEnchantmentManager getArmorEnchantmentManager() {
        return armorEnchantmentManager;
    }

    public BlackSmithManager getBlackSmithManager() {
        return blackSmithManager;
    }

    public BowEnchantmentManager getBowEnchantmentManager() {
        return bowEnchantmentManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public WingsManager getWingsManager() {
        return wingsManager;
    }

    public SkullCreator getSkullCreator() {
        return skullCreator;
    }

    public CurrencyAPI getCurrencyAPI() {
        return currencyAPI;
    }

    // Plugin Support

    public VaultSupport getVaultSupport() {
        return vaultSupport;
    }

    //public OraxenSupport getOraxenSupport() {
    //    return oraxenSupport;
    //}

    //public NoCheatPlusSupport getNoCheatPlusSupport() {
    //    return noCheatPlusSupport;
    //}

    //public SuperiorSkyBlockSupport getSuperiorSkyBlockSupport() {
    //    return superiorSkyBlockSupport;
    //}

    // Plugin Utils
    public BowUtils getBowUtils() {
        return bowUtils;
    }
}
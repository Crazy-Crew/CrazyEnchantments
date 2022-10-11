package com.badbones69.crazyenchantments;

import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.api.economy.vault.VaultSupport;
import com.badbones69.crazyenchantments.api.managers.*;
import com.badbones69.crazyenchantments.api.SkullCreator;
import com.badbones69.crazyenchantments.api.support.claims.SuperiorSkyBlockSupport;
import com.badbones69.crazyenchantments.api.support.misc.OraxenSupport;
import com.badbones69.crazyenchantments.controllers.ProtectionCrystal;
import com.badbones69.crazyenchantments.controllers.Scrambler;
import com.badbones69.crazyenchantments.utilities.BowUtils;

public class Starter {

    // Managers
    private FileManager fileManager;

    private CrazyManager crazyManager;

    private AllyManager allyManager;
    private ArmorEnchantmentManager armorEnchantmentManager;
    private BlackSmithManager blackSmithManager;
    private BowEnchantmentManager bowEnchantmentManager;
    private InfoMenuManager infoMenuManager;
    private ShopManager shopManager;
    private WingsManager wingsManager;

    // Misc
    private Methods methods;

    private SkullCreator skullCreator;

    private CurrencyAPI currencyAPI;

    // Plugin Support
    private OraxenSupport oraxenSupport;

    private SuperiorSkyBlockSupport superiorSkyBlockSupport;

    private VaultSupport vaultSupport;

    private PluginSupport pluginSupport;

    // Plugin Utils
    private BowUtils bowUtils;

    public void run() {
        // Managers
        fileManager = new FileManager();
        crazyManager = new CrazyManager();

        allyManager = new AllyManager();

        armorEnchantmentManager = new ArmorEnchantmentManager();
        blackSmithManager = new BlackSmithManager();
        bowEnchantmentManager = new BowEnchantmentManager();
        infoMenuManager = new InfoMenuManager();
        shopManager = new ShopManager();
        wingsManager = new WingsManager();

        // Misc

        methods = new Methods();

        skullCreator = new SkullCreator();

        currencyAPI = new CurrencyAPI();

        // Plugin Support

        vaultSupport = new VaultSupport();

        pluginSupport = new PluginSupport();

        if (PluginSupport.SupportedPlugins.ORAXEN.isPluginLoaded()) oraxenSupport = new OraxenSupport();

        if (PluginSupport.SupportedPlugins.SUPERIORSKYBLOCK.isPluginLoaded()) superiorSkyBlockSupport = new SuperiorSkyBlockSupport();

        // Plugin Utils
        bowUtils = new BowUtils();
    }

    // Managers
    public FileManager getFileManager() {
        return fileManager;
    }

    public CrazyManager getCrazyManager() {
        return crazyManager;
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

    public InfoMenuManager getInfoMenuManager() {
        return infoMenuManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public WingsManager getWingsManager() {
        return wingsManager;
    }

    // Misc
    public Methods getMethods() {
        return methods;
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

    public PluginSupport getPluginSupport() {
        return pluginSupport;
    }

    public OraxenSupport getOraxenSupport() {
        return oraxenSupport;
    }

    public SuperiorSkyBlockSupport getSuperiorSkyBlockSupport() {
        return superiorSkyBlockSupport;
    }

    // Plugin Utils

    public BowUtils getBowUtils() {
        return bowUtils;
    }
}
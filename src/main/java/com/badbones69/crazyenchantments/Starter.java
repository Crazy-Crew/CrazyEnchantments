package com.badbones69.crazyenchantments;

import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.api.economy.vault.VaultSupport;
import com.badbones69.crazyenchantments.api.managers.*;
import com.badbones69.crazyenchantments.api.SkullCreator;

public class Starter {

    private FileManager fileManager;

    private CrazyManager crazyManager;

    // Managers
    private AllyManager allyManager;
    private ArmorEnchantmentManager armorEnchantmentManager;
    private BlackSmithManager blackSmithManager;
    private BowEnchantmentManager bowEnchantmentManager;
    private InfoMenuManager infoMenuManager;
    private ShopManager shopManager;
    private WingsManager wingsManager;

    private Methods methods;

    private SkullCreator skullCreator;

    private VaultSupport vaultSupport;

    private PluginSupport pluginSupport;

    private CurrencyAPI currencyAPI;

    public void run() {
        fileManager = new FileManager();
        crazyManager = new CrazyManager();

        allyManager = new AllyManager();

        armorEnchantmentManager = new ArmorEnchantmentManager();
        blackSmithManager = new BlackSmithManager();
        bowEnchantmentManager = new BowEnchantmentManager();
        infoMenuManager = new InfoMenuManager();
        shopManager = new ShopManager();
        wingsManager = new WingsManager();

        methods = new Methods();

        skullCreator = new SkullCreator();

        vaultSupport = new VaultSupport();

        pluginSupport = new PluginSupport();

        currencyAPI = new CurrencyAPI();
    }

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

    public Methods getMethods() {
        return methods;
    }

    public SkullCreator getSkullCreator() {
        return skullCreator;
    }

    public VaultSupport getVaultSupport() {
        return vaultSupport;
    }

    public PluginSupport getPluginSupport() {
        return pluginSupport;
    }

    public CurrencyAPI getCurrencyAPI() {
        return currencyAPI;
    }
}
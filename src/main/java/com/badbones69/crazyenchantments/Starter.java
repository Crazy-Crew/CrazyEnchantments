package com.badbones69.crazyenchantments;

import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.api.SkullCreator;
import com.badbones69.crazyenchantments.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.api.economy.vault.VaultSupport;
import com.badbones69.crazyenchantments.api.managers.*;
import com.badbones69.crazyenchantments.api.support.anticheats.NoCheatPlusSupport;
import com.badbones69.crazyenchantments.api.support.anticheats.SpartanSupport;
import com.badbones69.crazyenchantments.api.support.claims.SuperiorSkyBlockSupport;
import com.badbones69.crazyenchantments.api.support.misc.OraxenSupport;
import com.badbones69.crazyenchantments.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.controllers.settings.EnchantmentSettings;
import com.badbones69.crazyenchantments.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.listeners.ArmorListener;
import com.badbones69.crazyenchantments.listeners.ScramblerListener;
import com.badbones69.crazyenchantments.listeners.ScrollListener;
import com.badbones69.crazyenchantments.utilities.BowUtils;
import com.badbones69.crazyenchantments.utilities.WingsUtils;

public class Starter {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private FileManager fileManager;
    private CrazyManager crazyManager;
    private Methods methods;
    private SkullCreator skullCreator;

    // Settings.
    private ProtectionCrystalSettings protectionCrystalSettings;
    private EnchantmentSettings enchantmentSettings;
    private EnchantmentBookSettings enchantmentBookSettings;

    // Plugin Utils.
    private BowUtils bowUtils;
    private WingsUtils wingsUtils;

    // Plugin Support.
    private PluginSupport pluginSupport;
    private VaultSupport vaultSupport;
    private SuperiorSkyBlockSupport superiorSkyBlockSupport;
    private OraxenSupport oraxenSupport;
    private NoCheatPlusSupport noCheatPlusSupport;
    private SpartanSupport spartanSupport;

    // Plugin Managers.
    private ArmorEnchantmentManager armorEnchantmentManager;
    private BowEnchantmentManager bowEnchantmentManager;
    private BlackSmithManager blackSmithManager;

    private InfoMenuManager infoMenuManager;
    private WingsManager wingsManager;
    private AllyManager allyManager;
    private ShopManager shopManager;

    // Economy Management.
    private CurrencyAPI currencyAPI;

    // Listeners.
    private ScramblerListener scramblerListener;
    private ScrollListener scrollListener;
    private ArmorListener armorListener;

    public void run() {
        fileManager = new FileManager();
        methods = new Methods();

        // Set up all our files.
        fileManager.setLog(true).setup();

        // Economy Management.
        currencyAPI = new CurrencyAPI();

        // Settings.
        protectionCrystalSettings = new ProtectionCrystalSettings();
        enchantmentSettings = new EnchantmentSettings();
        enchantmentBookSettings = new EnchantmentBookSettings();

        // Plugin Support.
        pluginSupport = new PluginSupport();

        infoMenuManager = new InfoMenuManager();
        shopManager = new ShopManager();

        // Plugin Managers.
        armorEnchantmentManager = new ArmorEnchantmentManager();
        bowEnchantmentManager = new BowEnchantmentManager();
        blackSmithManager = new BlackSmithManager();
        wingsManager = new WingsManager();
        allyManager = new AllyManager();

        // Plugin Utils.
        bowUtils = new BowUtils();
        wingsUtils = new WingsUtils();

        // Listeners.
        plugin.pluginManager.registerEvents(scramblerListener = new ScramblerListener(), plugin);
        plugin.pluginManager.registerEvents(scrollListener = new ScrollListener(), plugin);
        plugin.pluginManager.registerEvents(armorListener = new ArmorListener(), plugin);

        crazyManager = new CrazyManager();
        skullCreator = new SkullCreator();

        if (SupportedPlugins.SUPERIORSKYBLOCK.isPluginLoaded()) superiorSkyBlockSupport = new SuperiorSkyBlockSupport();

        if (SupportedPlugins.ORAXEN.isPluginLoaded()) oraxenSupport = new OraxenSupport();
        if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) noCheatPlusSupport = new NoCheatPlusSupport();
        if (SupportedPlugins.SPARTAN.isPluginLoaded()) spartanSupport = new SpartanSupport();
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public Methods getMethods() {
        return methods;
    }

    public CrazyManager getCrazyManager() {
        return crazyManager;
    }

    public SkullCreator getSkullCreator() {
        return skullCreator;
    }

    public InfoMenuManager getInfoMenuManager() {
        return infoMenuManager;
    }

    // Settings.
    public ProtectionCrystalSettings getProtectionCrystalSettings() {
        return protectionCrystalSettings;
    }

    public EnchantmentSettings getEnchantmentSettings() {
        return enchantmentSettings;
    }

    public EnchantmentBookSettings getEnchantmentBookSettings() {
        return enchantmentBookSettings;
    }

    // Plugin Support.
    public PluginSupport getPluginSupport() {
        return pluginSupport;
    }

    public VaultSupport getVaultSupport() {
        return vaultSupport;
    }

    public void setVaultSupport(VaultSupport vaultSupport) {
        this.vaultSupport = vaultSupport;
    }

    public SuperiorSkyBlockSupport getSuperiorSkyBlockSupport() {
        return superiorSkyBlockSupport;
    }

    public OraxenSupport getOraxenSupport() {
        return oraxenSupport;
    }

    public NoCheatPlusSupport getNoCheatPlusSupport() {
        return noCheatPlusSupport;
    }

    public SpartanSupport getSpartanSupport() {
        return spartanSupport;
    }

    // Economy Management.
    public CurrencyAPI getCurrencyAPI() {
        return currencyAPI;
    }

    // Plugin Managers.
    public ArmorEnchantmentManager getArmorEnchantmentManager() {
        return armorEnchantmentManager;
    }

    public BowEnchantmentManager getBowEnchantmentManager() {
        return bowEnchantmentManager;
    }

    public BlackSmithManager getBlackSmithManager() {
        return blackSmithManager;
    }

    public WingsManager getWingsManager() {
        return wingsManager;
    }

    public AllyManager getAllyManager() {
        return allyManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    // Listeners.
    public ScramblerListener getScramblerListener() {
        return scramblerListener;
    }

    public ArmorListener getArmorListener() {
        return armorListener;
    }

    public ScrollListener getScrollListener() {
        return scrollListener;
    }

    // Plugin Utils.
    public BowUtils getBowUtils() {
        return bowUtils;
    }

    public WingsUtils getWingsUtils() {
        return wingsUtils;
    }
}
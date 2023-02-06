package com.badbones69.crazyenchantments;

import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.api.SkullCreator;
import com.badbones69.crazyenchantments.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.api.economy.vault.VaultSupport;
import com.badbones69.crazyenchantments.api.managers.AllyManager;
import com.badbones69.crazyenchantments.api.managers.ArmorEnchantmentManager;
import com.badbones69.crazyenchantments.api.managers.BlackSmithManager;
import com.badbones69.crazyenchantments.api.managers.BowEnchantmentManager;
import com.badbones69.crazyenchantments.api.managers.guis.InfoMenuManager;
import com.badbones69.crazyenchantments.api.managers.ShopManager;
import com.badbones69.crazyenchantments.api.managers.WingsManager;
import com.badbones69.crazyenchantments.api.support.anticheats.NoCheatPlusSupport;
import com.badbones69.crazyenchantments.api.support.anticheats.SpartanSupport;
import com.badbones69.crazyenchantments.api.support.claims.SuperiorSkyBlockSupport;
import com.badbones69.crazyenchantments.api.support.misc.OraxenSupport;
import com.badbones69.crazyenchantments.controllers.EnchantmentControl;
import com.badbones69.crazyenchantments.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.controllers.settings.EnchantmentSettings;
import com.badbones69.crazyenchantments.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.listeners.ScramblerListener;
import com.badbones69.crazyenchantments.listeners.ScrollListener;
import com.badbones69.crazyenchantments.utilities.BowUtils;

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

    // Plugin Support.
    private SuperiorSkyBlockSupport superiorSkyBlockSupport;
    private PluginSupport pluginSupport;
    private VaultSupport vaultSupport;
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

    public void run() {
        this.fileManager = new FileManager();

        // Set up all our files.
        this.fileManager.setLog(true).setup();

        // Methods
        this.methods = new Methods();

        // Settings.
        this.protectionCrystalSettings = new ProtectionCrystalSettings();
        this.enchantmentBookSettings = new EnchantmentBookSettings();

        this.infoMenuManager = new InfoMenuManager();

        // Economy Management.
        this.currencyAPI = new CurrencyAPI();

        this.shopManager = new ShopManager();

        // Plugin Managers.
        this.armorEnchantmentManager = new ArmorEnchantmentManager();
        this.bowEnchantmentManager = new BowEnchantmentManager();
        this.blackSmithManager = new BlackSmithManager();
        this.blackSmithManager.load();
        this.wingsManager = new WingsManager();
        this.allyManager = new AllyManager();

        // Listeners.
        this.plugin.pluginManager.registerEvents(this.scramblerListener = new ScramblerListener(), this.plugin);
        this.plugin.pluginManager.registerEvents(this.scrollListener = new ScrollListener(), this.plugin);

        this.skullCreator = new SkullCreator();

        // Plugin Support.
        this.pluginSupport = new PluginSupport();

        this.pluginSupport.initializeWorldGuard();

        if (SupportedPlugins.SUPERIORSKYBLOCK.isPluginLoaded()) this.superiorSkyBlockSupport = new SuperiorSkyBlockSupport();

        if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) this.noCheatPlusSupport = new NoCheatPlusSupport();

        if (SupportedPlugins.ORAXEN.isPluginLoaded()) this.oraxenSupport = new OraxenSupport();
        if (SupportedPlugins.SPARTAN.isPluginLoaded()) this.spartanSupport = new SpartanSupport();

        this.crazyManager = new CrazyManager();

        this.enchantmentSettings = new EnchantmentSettings();

        // Plugin Utils.
        this.bowUtils = new BowUtils();

        this.plugin.pluginManager.registerEvents(new EnchantmentControl(), this.plugin);
    }

    public FileManager getFileManager() {
        return this.fileManager;
    }

    public Methods getMethods() {
        return this.methods;
    }

    public CrazyManager getCrazyManager() {
        return this.crazyManager;
    }

    public SkullCreator getSkullCreator() {
        return this.skullCreator;
    }

    public InfoMenuManager getInfoMenuManager() {
        return this.infoMenuManager;
    }

    // Settings.
    public ProtectionCrystalSettings getProtectionCrystalSettings() {
        return this.protectionCrystalSettings;
    }

    public EnchantmentSettings getEnchantmentSettings() {
        return this.enchantmentSettings;
    }

    public EnchantmentBookSettings getEnchantmentBookSettings() {
        return this.enchantmentBookSettings;
    }

    // Plugin Support.
    public PluginSupport getPluginSupport() {
        return this.pluginSupport;
    }

    public VaultSupport getVaultSupport() {
        return this.vaultSupport;
    }

    public void setVaultSupport(VaultSupport vaultSupport) {
        this.vaultSupport = vaultSupport;

        vaultSupport.loadVault();
    }

    public SuperiorSkyBlockSupport getSuperiorSkyBlockSupport() {
        return this.superiorSkyBlockSupport;
    }

    public OraxenSupport getOraxenSupport() {
        return this.oraxenSupport;
    }

    public NoCheatPlusSupport getNoCheatPlusSupport() {
        return this.noCheatPlusSupport;
    }

    public SpartanSupport getSpartanSupport() {
        return this.spartanSupport;
    }

    // Economy Management.
    public CurrencyAPI getCurrencyAPI() {
        return this.currencyAPI;
    }

    // Plugin Managers.
    public ArmorEnchantmentManager getArmorEnchantmentManager() {
        return this.armorEnchantmentManager;
    }

    public BowEnchantmentManager getBowEnchantmentManager() {
        return this.bowEnchantmentManager;
    }

    public BlackSmithManager getBlackSmithManager() {
        return this.blackSmithManager;
    }

    public WingsManager getWingsManager() {
        return this.wingsManager;
    }

    public AllyManager getAllyManager() {
        return this.allyManager;
    }

    public ShopManager getShopManager() {
        return this.shopManager;
    }

    // Listeners.
    public ScramblerListener getScramblerListener() {
        return this.scramblerListener;
    }

    public ScrollListener getScrollListener() {
        return this.scrollListener;
    }

    // Plugin Utils.
    public BowUtils getBowUtils() {
        return this.bowUtils;
    }
}
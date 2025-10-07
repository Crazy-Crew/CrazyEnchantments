package com.badbones69.crazyenchantments.paper;

import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsManager;
import com.badbones69.crazyenchantments.paper.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.paper.api.economy.vault.VaultSupport;
import com.badbones69.crazyenchantments.paper.api.managers.AllyManager;
import com.badbones69.crazyenchantments.paper.api.managers.ArmorEnchantmentManager;
import com.badbones69.crazyenchantments.paper.api.managers.BowEnchantmentManager;
import com.badbones69.crazyenchantments.paper.api.managers.ShopManager;
import com.badbones69.crazyenchantments.paper.api.managers.WingsManager;
import com.badbones69.crazyenchantments.paper.api.utils.BowUtils;
import com.badbones69.crazyenchantments.paper.controllers.EnchantmentControl;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.paper.listeners.ScramblerListener;
import com.badbones69.crazyenchantments.paper.listeners.ScrollListener;
import com.badbones69.crazyenchantments.paper.listeners.SlotCrystalListener;
import com.badbones69.crazyenchantments.paper.support.PluginSupport;
import com.badbones69.crazyenchantments.paper.support.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.paper.support.SkullCreator;
import com.badbones69.crazyenchantments.paper.support.claims.SuperiorSkyBlockSupport;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Starter {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private CrazyManager crazyManager;
    private Methods methods;
    private SkullCreator skullCreator;

    // Settings.
    private ProtectionCrystalSettings protectionCrystalSettings;
    private EnchantmentBookSettings enchantmentBookSettings;

    // Plugin Utils.
    private BowUtils bowUtils;

    // Plugin Support.
    private SuperiorSkyBlockSupport superiorSkyBlockSupport;
    private PluginSupport pluginSupport;
    private VaultSupport vaultSupport;

    // Plugin Managers.
    private ArmorEnchantmentManager armorEnchantmentManager;
    private BowEnchantmentManager bowEnchantmentManager;
    private WingsManager wingsManager;
    private AllyManager allyManager;
    private ShopManager shopManager;

    // Economy Management.
    private CurrencyAPI currencyAPI;

    // Listeners.
    private ScramblerListener scramblerListener;
    private ScrollListener scrollListener;
    private SlotCrystalListener slotCrystalListener;


    public void run() {
        // Plugin Support.
        this.pluginSupport = new PluginSupport();
        this.pluginSupport.initializeWorldGuard();

        if (SupportedPlugins.SUPERIORSKYBLOCK.isPluginLoaded()) this.superiorSkyBlockSupport = new SuperiorSkyBlockSupport();

        // Methods
        this.methods = new Methods();

        // Settings.
        this.protectionCrystalSettings = new ProtectionCrystalSettings();
        this.enchantmentBookSettings = new EnchantmentBookSettings();

        BlackSmithManager.load();
        KitsManager.load();

        MenuManager.load();

        // Economy Management.
        this.currencyAPI = new CurrencyAPI();

        this.shopManager = new ShopManager();

        // Plugin Managers.
        this.armorEnchantmentManager = new ArmorEnchantmentManager();
        this.bowEnchantmentManager = new BowEnchantmentManager();
        this.wingsManager = new WingsManager();
        this.allyManager = new AllyManager();

        // Listeners.
        this.plugin.pluginManager.registerEvents(this.scramblerListener = new ScramblerListener(), this.plugin);
        this.plugin.pluginManager.registerEvents(this.scrollListener = new ScrollListener(), this.plugin);
        this.plugin.pluginManager.registerEvents(this.slotCrystalListener = new SlotCrystalListener(), this.plugin);

        this.skullCreator = new SkullCreator();

        this.crazyManager = new CrazyManager();

        // Plugin Utils.
        this.bowUtils = new BowUtils();

        this.plugin.pluginManager.registerEvents(new EnchantmentControl(), this.plugin);
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

    // Settings.
    public ProtectionCrystalSettings getProtectionCrystalSettings() {
        return this.protectionCrystalSettings;
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

    public void setVaultSupport(@NotNull final VaultSupport vaultSupport) {
        this.vaultSupport = vaultSupport;

        vaultSupport.loadVault();
    }

    public SuperiorSkyBlockSupport getSuperiorSkyBlockSupport() {
        if (this.superiorSkyBlockSupport == null) this.superiorSkyBlockSupport = new SuperiorSkyBlockSupport();

        return this.superiorSkyBlockSupport;
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

    public SlotCrystalListener getSlotCrystalListener() {
        return this.slotCrystalListener;
    }

    // Plugin Utils.
    public BowUtils getBowUtils() {
        return this.bowUtils;
    }
}
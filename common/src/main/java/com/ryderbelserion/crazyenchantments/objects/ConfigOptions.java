package com.ryderbelserion.crazyenchantments.objects;

import com.ryderbelserion.crazyenchantments.enums.FileKeys;
import com.ryderbelserion.fusion.core.files.types.YamlCustomFile;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.List;

public class ConfigOptions {

    private String prefix;

    private boolean maxAmountOfEnchantsToggle;

    private boolean isRefreshingEffectsOnWorldChange;

    private List<String> enchantmentBookLore;

    private boolean isMetricsEnabled;

    private int lightningSoundRange;

    private boolean mysteryDustFireworkToggle;
    private String mysteryDustFireworkColors;

    private boolean mysteryDustSuccessToggle;
    private boolean mysteryDustDestroyToggle;
    private boolean mysteryDustFailedToggle;

    private boolean loseProtectionOnDeath;

    private boolean transmogAddBlankLines;
    private List<String> transmogLoreOrder;

    private String whiteScrollProtectedName;

    private String protectionCrystalProtected;

    private boolean treefellerFullDurability;

    private boolean veinMinerFullDurability;

    private boolean blastFullDurability;

    private boolean armorUpgradeToggle;

    private boolean armorUpgradeEnchantmentBreak;

    private boolean limitChangeOnFail;

    private boolean rightClickDescription;

    private boolean useUnsafeEnchantments;

    private boolean protectionCrystalChanceToggle;
    private int protectionCrystalSuccessChance;

    private boolean resetPlayersMaxHealth;

    private boolean enchantmentBookGlowing;

    private int blackScrollSuccessMax;
    private int blackScrollSuccessMin;

    private int blackScrollDestroyMax;
    private int blackScrollDestroyMin;

    private String lostBookItem;
    private String lostBookName;
    private List<String> lostBookLore;

    public void init() {
        final YamlCustomFile config = FileKeys.config.getCustomFile();
        final CommentedConfigurationNode node = config.getConfiguration();

        this.prefix = config.getStringValueWithDefault("&8[&aCrazyEnchantments&8]: ", "Settings", "Prefix");

        this.isRefreshingEffectsOnWorldChange = config.getBooleanValueWithDefault(false, "Settings", "Refresh-Potion-Effects-On-World-Change");

        this.lightningSoundRange = config.getIntValueWithDefault(160, "Settings", "EnchantmentOptions", "Lightning-Sound-Range");

        this.isMetricsEnabled = config.getBooleanValueWithDefault(true, "Settings", "Toggle-Metrics");

        this.enchantmentBookLore = config.getStringList("Settings", "EnchantmentBookLore");

        this.mysteryDustSuccessToggle = config.getBooleanValueWithDefault(true, "Settings", "Dust", "MysteryDust", "Dust-Toggle", "Success");
        this.mysteryDustDestroyToggle = config.getBooleanValueWithDefault(true, "Settings", "Dust", "MysteryDust", "Dust-Toggle", "Destroy");
        this.mysteryDustFailedToggle = config.getBooleanValueWithDefault(true, "Settings", "Dust", "MysteryDust", "Dust-Toggle", "Failed");

        this.mysteryDustFireworkToggle = config.getBooleanValueWithDefault(true, "Settings", "Dust", "MysteryDust", "Firework", "Toggle");
        this.mysteryDustFireworkColors = config.getStringValueWithDefault("Black, Gray, Lime", "Settings", "Dust", "MysteryDust", "Firework", "Colors");

        this.loseProtectionOnDeath = config.getBooleanValueWithDefault(true, "Settings", "ProtectionCrystal", "Lose-Protection-On-Death");

        this.transmogAddBlankLines = config.getBooleanValueWithDefault(true, "Settings", "TransmogScroll", "Add-Blank-Lines");

        this.transmogLoreOrder = config.getStringList(List.of("CE_Enchantments", "Protection", "Normal_Lore"), "Settings", "TransmogScroll", "Lore-Order");

        this.whiteScrollProtectedName = config.getStringValueWithDefault("&b&lPROTECTED", "Settings", "WhiteScroll", "ProtectedName");

        this.protectionCrystalProtected = config.getStringValueWithDefault("&6Ancient Protection", "Settings", "ProtectionCrystal", "Protected");

        this.treefellerFullDurability = config.getBooleanValueWithDefault(true, "Settings", "EnchantmentOptions", "TreeFeller-Full-Durability");

        this.blastFullDurability = config.getBooleanValueWithDefault(true, "Settings", "EnchantmentOptions", "Blast-Full-Durability");

        this.veinMinerFullDurability = config.getBooleanValueWithDefault(true, "Settings", "EnchantmentOptions", "VeinMiner-Full-Durability");

        this.rightClickDescription = config.getBooleanValueWithDefault(true, "Settings", "EnchantmentOptions", "Right-Click-Book-Description");

        this.limitChangeOnFail = config.getBooleanValueWithDefault(true, "Settings", "EnchantmentOptions", "Limit", "Change-On-Fail");

        this.armorUpgradeToggle = config.getBooleanValueWithDefault(true, "Settings", "EnchantmentOptions", "Armor-Upgrade", "Toggle");

        this.armorUpgradeEnchantmentBreak = config.getBooleanValueWithDefault(true, "Settings", "EnchantmentOptions", "Armor-Upgrade", "Enchantment-Break");

        this.useUnsafeEnchantments = config.getBooleanValueWithDefault(true, "Settings", "EnchantmentOptions", "UnSafe-Enchantments");

        this.protectionCrystalChanceToggle = config.getBooleanValueWithDefault(false, "Settings", "ProtectionCrystal", "Chance", "Toggle");

        this.protectionCrystalSuccessChance = config.getIntValueWithDefault(100, "Settings", "ProtectionCrystal", "Chance", "Success-Chance");

        this.resetPlayersMaxHealth = config.getBooleanValueWithDefault(true, "Settings", "Reset-Players-Max-Health");

        this.enchantmentBookGlowing = config.getBooleanValueWithDefault(true, "Settings", "Enchantment-Book-Glowing");

        this.blackScrollDestroyMax = config.getIntValueWithDefault(100, "Settings", "BlackScroll", "DestroyChance", "Max");
        this.blackScrollDestroyMin = config.getIntValueWithDefault(15, "Settings", "BlackScroll", "DestroyChance", "Min");

        this.blackScrollSuccessMax = config.getIntValueWithDefault(100, "Settings", "BlackScroll", "SuccessChance", "Max");
        this.blackScrollSuccessMin = config.getIntValueWithDefault(15, "Settings", "BlackScroll", "SuccessChance", "Min");

        this.lostBookItem = config.getStringValueWithDefault("BOOK", "Settings", "LostBook", "Item");
        this.lostBookName = config.getStringValueWithDefault("&8&l&nA Lost %category%&8&l&n Book", "Settings", "LostBook", "Name");
        this.lostBookLore = config.getStringList(List.of(
                "&7This book has been lost for centuries",
                "&7It is said to be an enchantment book from %category%",
                "&7But you must clean it off to find out what kind it is.",
                "&7&l(&6&l!&7&l) &7Right Click to clean off."
        ), "Settings", "LostBook", "Lore");

        this.maxAmountOfEnchantsToggle = config.getBooleanValueWithDefault(true, "Settings", "EnchantmentOptions", "MaxAmountOfEnchantmentsToggle");
    }

    public final boolean isRefreshingEffectsOnWorldChange() {
        return this.isRefreshingEffectsOnWorldChange;
    }

    public final boolean isUseUnsafeEnchantments() {
        return this.useUnsafeEnchantments;
    }

    public final boolean isMysteryDustFireworkToggle() {
        return this.mysteryDustFireworkToggle;
    }

    public final String getMysteryDustFireworkColors() {
        return this.mysteryDustFireworkColors;
    }

    public final List<String> getEnchantmentBookLore() {
        return this.enchantmentBookLore;
    }

    public final String getProtectionCrystalProtected() {
        return this.protectionCrystalProtected;
    }

    public final String getWhiteScrollProtectedName() {
        return this.whiteScrollProtectedName;
    }

    public final boolean isMysteryDustSuccessToggle() {
        return this.mysteryDustSuccessToggle;
    }

    public final boolean isTreefellerFullDurability() {
        return this.treefellerFullDurability;
    }

    public final boolean isMysteryDustDestroyToggle() {
        return this.mysteryDustDestroyToggle;
    }

    public final boolean isMysteryDustFailedToggle() {
        return this.mysteryDustFailedToggle;
    }

    public final boolean isVeinMinerFullDurability() {
        return this.veinMinerFullDurability;
    }

    public final List<String> getTransmogLoreOrder() {
        return this.transmogLoreOrder;
    }

    public final boolean isTransmogAddBlankLines() {
        return this.transmogAddBlankLines;
    }

    public final boolean isLoseProtectionOnDeath() {
        return this.loseProtectionOnDeath;
    }

    public final boolean isBlastFullDurability() {
        return this.blastFullDurability;
    }

    public final int getLightningSoundRange() {
        return this.lightningSoundRange;
    }

    public final boolean isMetricsEnabled() {
        return this.isMetricsEnabled;
    }

    public final boolean isArmorUpgradeEnchantmentBreak() {
        return this.armorUpgradeEnchantmentBreak;
    }

    public final boolean isArmorUpgradeToggle() {
        return this.armorUpgradeToggle;
    }

    public final boolean isLimitChangeOnFail() {
        return this.limitChangeOnFail;
    }

    public final boolean isProtectionCrystalChanceToggle() {
        return this.protectionCrystalChanceToggle;
    }

    public final int getProtectionCrystalSuccessChance() {
        return this.protectionCrystalSuccessChance;
    }

    public final boolean isRightClickDescription() {
        return this.rightClickDescription;
    }

    public final boolean isResetPlayersMaxHealth() {
        return this.resetPlayersMaxHealth;
    }

    public final boolean isEnchantmentBookGlowing() {
        return this.enchantmentBookGlowing;
    }

    public final int getBlackScrollDestroyMax() {
        return this.blackScrollDestroyMax;
    }

    public final int getBlackScrollDestroyMin() {
        return this.blackScrollDestroyMin;
    }

    public final int getBlackScrollSuccessMax() {
        return this.blackScrollSuccessMax;
    }

    public final int getBlackScrollSuccessMin() {
        return this.blackScrollSuccessMin;
    }

    public final String getLostBookItem() {
        return this.lostBookItem;
    }

    public final String getLostBookName() {
        return this.lostBookName;
    }

    public final List<String> getLostBookLore() {
        return this.lostBookLore;
    }

    public final boolean isMaxAmountOfEnchantsToggle() {
        return this.maxAmountOfEnchantsToggle;
    }

    public final String getPrefix() {
        return this.prefix;
    }
}
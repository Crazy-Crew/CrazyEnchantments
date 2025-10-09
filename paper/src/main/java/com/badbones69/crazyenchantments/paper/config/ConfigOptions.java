package com.badbones69.crazyenchantments.paper.config;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.exceptions.CrazyException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class ConfigOptions {

    private boolean refreshPotionEffectsOnWorldChange;
    private boolean resetMaxHealth;
    private boolean toggleMetrics;
    private String prefix;

    private String inventoryName;
    private int inventorySize;

    private ItemBuilder enchantBook;

    private int successOverride;
    private int failureOverride;

    private boolean updateExamplesFolder;

    private int rageMaxLevel;
    private boolean gkitzToggle;
    private boolean useUnsafeEnchantments;
    private boolean breakRageOnDamage;
    private boolean useRageBossBar;

    private double rageIncrement;
    private boolean maxEnchantmentCheck;
    private boolean checkVanillaLimit;

    private boolean dropBlocksBlast;
    private boolean dropBlocksVeinMiner;
    private int defaultLimit;
    private int defaultBaseLimit;
    private boolean useEnchantmentLimiter;
    private boolean useConfigLimits;

    private String suffix;
    private boolean countVanillaEnchantments;
    private boolean useSuffix;
    private boolean blackScrollChanceToggle;
    private int blackScrollChance;

    public void init(@NotNull final YamlConfiguration configuration) {
        if (configuration.contains("settings")) {
            throw new CrazyException("config.yml is not formatted properly!");
        }

        ConfigurationSection section = configuration.getConfigurationSection("Settings");

        if (section == null) {
            throw new CrazyException("config.yml does not have the configuration section needed");
        }

        this.prefix = section.getString("Prefix", "&8[&aCrazyEnchantments&8]: ");

        this.toggleMetrics = section.getBoolean("Toggle-Metrics", false);
        this.resetMaxHealth = section.getBoolean("Reset-Players-Max-Health", true);
        this.refreshPotionEffectsOnWorldChange = section.getBoolean("Refresh-Potion-Effects-On-World-Change", false);

        this.inventoryName = section.getString("InvName", "&4&l&nCrazy Enchanter");
        this.inventorySize = section.getInt("GUISize", 54);

        this.enchantBook = new ItemBuilder().setMaterial(section.getString("Enchantment-Book-Item", "BOOK"))
                .setGlow(section.getBoolean("Enchantment-Book-Glowing", true))
                .setLore(section.getStringList("EnchantmentBookLore"));

        this.successOverride = section.getInt("CESuccessOverride", -1);
        this.failureOverride = section.getInt("CEFailureOverride", -1);

        this.updateExamplesFolder = section.getBoolean("Update-Examples-Folder", true);

        this.useUnsafeEnchantments = section.getBoolean("EnchantmentOptions.UnSafe-Enchantments", true);
        this.maxEnchantmentCheck = section.getBoolean("EnchantmentOptions.MaxAmountOfEnchantmentsToggle", true);
        this.useConfigLimits = section.getBoolean("EnchantmentOptions.Limit.Check-Perms", false);
        this.defaultLimit = section.getInt("EnchantmentOptions.Limit.Default-Limit", 6);
        this.defaultBaseLimit = section.getInt("EnchantmentOptions.Limit.Default-Base-Limit", 6);
        this.useEnchantmentLimiter = section.getBoolean("EnchantmentOptions.Limit.Enable-SlotCrystal", true);
        this.checkVanillaLimit = section.getBoolean("EnchantmentOptions.IncludeVanillaEnchantments", false);
        this.gkitzToggle = section.getBoolean("GKitz.Enabled", true);
        this.rageMaxLevel = section.getInt("EnchantmentOptions.MaxRageLevel", 4);
        this.breakRageOnDamage = section.getBoolean("EnchantmentOptions.Break-Rage-On-Damage", true);
        this.useRageBossBar = section.getBoolean("EnchantmentOptions.Rage-Boss-Bar", false);
        this.rageIncrement = section.getDouble("EnchantmentOptions.Rage-Increase", 0.1);

        this.dropBlocksBlast = section.getBoolean("EnchantmentOptions.Drop-Blocks-For-Blast", true);
        this.dropBlocksVeinMiner = section.getBoolean("EnchantmentOptions.Drop-Blocks-For-VeinMiner", true);

        this.suffix = section.getString("TransmogScroll.Amount-of-Enchantments", " &7[&6&n%amount%&7]");
        this.countVanillaEnchantments = section.getBoolean("TransmogScroll.Count-Vanilla-Enchantments", true);
        this.useSuffix = section.getBoolean("TransmogScroll.Amount-Toggle", true);
        this.blackScrollChance = section.getInt("BlackScroll.Chance", 75);
        this.blackScrollChanceToggle = section.getBoolean("BlackScroll.Chance-Toggle", false);
    }

    public final boolean isCountVanillaEnchantments() {
        return this.countVanillaEnchantments;
    }

    public final boolean isBlackScrollChanceToggle() {
        return this.blackScrollChanceToggle;
    }

    public final int getBlackScrollChance() {
        return this.blackScrollChance;
    }

    public @NotNull final String getSuffix() {
        return this.suffix;
    }

    public final boolean isUseSuffix() {
        return this.useSuffix;
    }

    /**
     * @return If the blast enchantment drops blocks.
     */
    public boolean isDropBlocksBlast() {
        return this.dropBlocksBlast;
    }

    /**
     * @return If the vein-miner enchantment drops blocks.
     */
    public boolean isDropBlocksVeinMiner() {
        return this.dropBlocksVeinMiner;
    }

    /**
     * @return The max rage stack level.
     */
    public int getRageMaxLevel() {
        return this.rageMaxLevel;
    }

    public int getDefaultBaseLimit() {
        return this.defaultBaseLimit;
    }

    public int getDefaultLimit() {
        return this.defaultLimit;
    }

    public boolean isUseEnchantmentLimiter() {
        return this.useEnchantmentLimiter;
    }

    public boolean isUseConfigLimits() {
        return this.useConfigLimits;
    }

    public boolean isBreakRageOnDamage() {
        return this.breakRageOnDamage;
    }

    public boolean isUseRageBossBar() {
        return this.useRageBossBar;
    }

    public boolean isUseUnsafeEnchantments() {
        return this.useUnsafeEnchantments;
    }

    public boolean isMaxEnchantmentCheck() {
        return this.maxEnchantmentCheck;
    }

    public boolean isCheckVanillaLimit() {
        return this.checkVanillaLimit;
    }

    public double getRageIncrement() {
        return this.rageIncrement;
    }

    public boolean isGkitzToggle() {
        return this.gkitzToggle;
    }

    public boolean isRefreshPotionEffectsOnWorldChange() {
        return this.refreshPotionEffectsOnWorldChange;
    }

    public boolean isUpdateExamplesFolder() {
        return this.updateExamplesFolder;
    }

    public boolean isResetMaxHealth() {
        return this.resetMaxHealth;
    }

    public String getInventoryName() {
        return this.inventoryName;
    }

    public boolean isToggleMetrics() {
        return this.toggleMetrics;
    }

    public int getFailureOverride() {
        return this.failureOverride;
    }

    public int getSuccessOverride() {
        return this.successOverride;
    }

    public int getInventorySize() {
        return this.inventorySize;
    }

    public ItemBuilder getEnchantBook() {
        return this.enchantBook;
    }

    public String getPrefix() {
        return this.prefix;
    }
}
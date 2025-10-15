package com.badbones69.crazyenchantments.paper.managers.configs;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.enums.FileKeys;
import com.badbones69.crazyenchantments.paper.managers.configs.types.*;
import com.badbones69.crazyenchantments.paper.managers.configs.types.currency.VaultConfig;
import com.badbones69.crazyenchantments.paper.managers.configs.types.guis.BlackSmithConfig;
import com.badbones69.crazyenchantments.paper.managers.configs.types.guis.TinkerConfig;
import com.badbones69.crazyenchantments.paper.managers.configs.types.items.NavigationConfig;
import com.badbones69.crazyenchantments.paper.managers.configs.types.items.ProtectionCrystalConfig;
import com.badbones69.crazyenchantments.paper.managers.configs.types.items.ScramblerConfig;
import com.badbones69.crazyenchantments.paper.managers.configs.types.items.SlotCrystalConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.crazyenchantments.exceptions.CrazyException;
import java.util.Optional;

public class ConfigManager {

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

    private ProtectionCrystalConfig protectionCrystalConfig;
    private SlotCrystalConfig slotCrystalConfig;
    private NavigationConfig navigationConfig;
    private BlackSmithConfig blackSmithConfig;
    private ScramblerConfig scramblerConfig;
    private TinkerConfig tinkerConfig;
    private KitConfig kitConfig;

    private VaultConfig vaultConfig;

    public void init(@NotNull final YamlConfiguration configuration) {
        Optional.ofNullable(configuration.getConfigurationSection("Settings")).ifPresentOrElse(section -> {
            Optional.ofNullable(section.getConfigurationSection("ProtectionCrystal")).ifPresent(protection -> this.protectionCrystalConfig = new ProtectionCrystalConfig(protection));
            Optional.ofNullable(section.getConfigurationSection("BlackSmith")).ifPresent(blacksmith -> this.blackSmithConfig = new BlackSmithConfig(blacksmith));
            Optional.ofNullable(section.getConfigurationSection("Scrambler")).ifPresent(scrambler -> this.scramblerConfig = new ScramblerConfig(scrambler));
            Optional.ofNullable(section.getConfigurationSection("Slot_Crystal")).ifPresent(slot -> this.slotCrystalConfig = new SlotCrystalConfig(slot));

            this.prefix = section.getString("Prefix", "<dark_gray>[<green>CrazyEnchantments<dark_gray>]: ");

            this.toggleMetrics = section.getBoolean("Toggle-Metrics", false);
            this.resetMaxHealth = section.getBoolean("Reset-Players-Max-Health", true);
            this.refreshPotionEffectsOnWorldChange = section.getBoolean("Refresh-Potion-Effects-On-World-Change", false);

            this.inventoryName = section.getString("InvName", "<dark_red><bold><u>Crazy Enchanter");
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

            this.suffix = section.getString("TransmogScroll.Amount-of-Enchantments", " <gray>[<gold><u>%amount%<gray>]");
            this.countVanillaEnchantments = section.getBoolean("TransmogScroll.Count-Vanilla-Enchantments", true);
            this.useSuffix = section.getBoolean("TransmogScroll.Amount-Toggle", true);
            this.blackScrollChance = section.getInt("BlackScroll.Chance", 75);
            this.blackScrollChanceToggle = section.getBoolean("BlackScroll.Chance-Toggle", false);
        }, () -> {
            throw new CrazyException("config.yml does not have the configuration section needed!");
        });

        final YamlConfiguration enchantmentTypes = FileKeys.enchantment_types.getPaperConfiguration();

        Optional.ofNullable(enchantmentTypes.getConfigurationSection("Info-GUI-Settings.Back-Item")).ifPresentOrElse(back -> this.navigationConfig = new NavigationConfig(back), () -> {
            throw new CrazyException("Enchantment-Types.yml does not have the configuration section needed!");
        });

        final YamlConfiguration kits = FileKeys.gkitz.getPaperConfiguration();

        Optional.ofNullable(kits.getConfigurationSection("Settings")).ifPresentOrElse(kit -> this.kitConfig = new KitConfig(kit), () -> {
            throw new CrazyException("GKitz.yml does not have the configuration section needed!");
        });

        this.tinkerConfig = new TinkerConfig(FileKeys.tinker.getPaperConfiguration());

        final CommentedConfigurationNode currency = FileKeys.currency.getYamlConfiguration();

        Optional.of(currency.node("settings", "vault")).ifPresentOrElse(config -> this.vaultConfig = new VaultConfig(config), () -> {
            throw new CrazyException("currency.yml does not have the configuration node needed!");
        });
    }

    public @NotNull final ProtectionCrystalConfig getProtectionCrystalConfig() {
        return this.protectionCrystalConfig;
    }

    public @NotNull final SlotCrystalConfig getSlotCrystalConfig() {
        return this.slotCrystalConfig;
    }

    public @NotNull final BlackSmithConfig getBlackSmithConfig() {
        return this.blackSmithConfig;
    }

    public @NotNull final NavigationConfig getNavigationConfig() {
        return this.navigationConfig;
    }

    public @NotNull final ScramblerConfig getScramblerConfig() {
        return this.scramblerConfig;
    }

    public @NotNull final TinkerConfig getTinkerConfig() {
        return this.tinkerConfig;
    }

    public @NotNull final VaultConfig getVaultConfig() {
        return this.vaultConfig;
    }

    public @NotNull final KitConfig getKitConfig() {
        return this.kitConfig;
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
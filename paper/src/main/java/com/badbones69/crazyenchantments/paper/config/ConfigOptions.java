package com.badbones69.crazyenchantments.paper.config;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.exceptions.CrazyException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class ConfigOptions {

    private ConfigurationSection section;

    private String prefix;
    private boolean toggleMetrics;
    private boolean playerBackupMessage;
    private boolean resetMaxHealth;
    private boolean refreshPotionEffectsOnWorldChange;

    private String inventoryName;
    private int inventorySize;

    private ItemBuilder enchantBook;

    private int successOverride;
    private int failureOverride;

    public void init(@NotNull final YamlConfiguration configuration) {
        if (configuration.contains("settings")) {
            throw new CrazyException("config.yml is not formatted properly!");
        }

        ConfigurationSection section = configuration.getConfigurationSection("Settings");

        if (section == null) {
            throw new CrazyException("config.yml does not have the configuration section needed");
        }

        this.section = section;

        this.prefix = section.getString("Prefix", "&8[&aCrazyEnchantments&8]: ");

        this.toggleMetrics = section.getBoolean("Toggle-Metrics", false);
        this.playerBackupMessage = section.getBoolean("Player-Info-Backup-Message", true);
        this.resetMaxHealth = section.getBoolean("Reset-Players-Max-Health", true);
        this.refreshPotionEffectsOnWorldChange = section.getBoolean("Refresh-Potion-Effects-On-World-Change", false);

        this.inventoryName = section.getString("InvName", "&4&l&nCrazy Enchanter");
        this.inventorySize = section.getInt("GUISize", 54);

        this.enchantBook = new ItemBuilder().setMaterial(section.getString("Enchantment-Book-Item", "BOOK"))
                .setGlow(section.getBoolean("Enchantment-Book-Glowing", true))
                .setLore(section.getStringList("EnchantmentBookLore"));

        this.successOverride = section.getInt("CESuccessOverride", -1);
        this.failureOverride = section.getInt("CEFailureOverride", -1);
    }

    public boolean isRefreshPotionEffectsOnWorldChange() {
        return this.refreshPotionEffectsOnWorldChange;
    }

    public boolean isPlayerBackupMessage() {
        return this.playerBackupMessage;
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

    public String getPrefix() {
        return this.prefix;
    }

    public ItemBuilder getEnchantBook() {
        return this.enchantBook;
    }
}
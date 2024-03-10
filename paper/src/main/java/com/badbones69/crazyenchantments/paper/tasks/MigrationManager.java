package com.badbones69.crazyenchantments.paper.tasks;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.platform.TinkerConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MigrationManager {

    @NotNull
    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    public static void migrate() {
        File directory = new File(plugin.getDataFolder(), "backups");
        directory.mkdirs();

        // Move the file to the backup folder
        File blockFile = new File(plugin.getDataFolder(), "BlockList.yml");
        if (blockFile.exists()) {
            blockFile.renameTo(new File(directory, "BlockList-v1.yml"));
        }

        // Update Tinker.yml
        copyTinkerer(directory);
    }

    private static void copyTinkerer(File directory) {
        File input = new File(plugin.getDataFolder(), "Tinker.yml");

        // Load the configuration.
        YamlConfiguration inputConfiguration = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(input)).join();

        if (inputConfiguration.get("Settings.Tinker-Version") == null) return;

        // Move to backup folder.
        File file = new File(directory, "Tinker-v2.yml");

        if (!file.exists()) {
            input.renameTo(file);
        }

        // Load the configuration.
        YamlConfiguration configuration = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(file)).join();

        ConfigurationSection section = configuration.getConfigurationSection("Settings");

        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        SettingsManager tinker = SettingsManagerBuilder
                .withYamlFile(new File(plugin.getDataFolder(), "tinker.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(TinkerConfig.class)
                .create();

        if (section != null && section.getString("Currency") != null) {
            tinker.setProperty(TinkerConfig.currency, section.getString("Currency", TinkerConfig.currency.getDefaultValue()));

            tinker.setProperty(TinkerConfig.bottle_item, section.getString("BottleOptions.Item", TinkerConfig.bottle_item.getDefaultValue()));
            tinker.setProperty(TinkerConfig.bottle_name, section.getString("BottleOptions.Name", TinkerConfig.bottle_name.getDefaultValue()));
            tinker.setProperty(TinkerConfig.bottle_lore, section.getStringList("BottleOptions.Lore"));

            tinker.setProperty(TinkerConfig.menu_name, section.getString("GUIName", TinkerConfig.menu_name.getDefaultValue()));

            tinker.setProperty(TinkerConfig.menu_trade_button, section.getString("TradeButton", TinkerConfig.menu_trade_button.getDefaultValue()));

            tinker.setProperty(TinkerConfig.menu_trade_lore, section.getStringList("TradeButton-Lore"));

            tinker.save();
        }

        ConfigurationSection tinkerSection = configuration.getConfigurationSection("Tinker");

        // If the section is not null.
        if (tinkerSection != null) {
            ConfigurationSection vanillaSection = tinkerSection.getConfigurationSection("Vanilla-Enchantments");

            // If section is not null.
            if (vanillaSection != null) {
                List<String> enchantments = new ArrayList<>();

                // Get all values and add to the list.
                vanillaSection.getKeys(false).forEach(line -> {
                    String enchant = vanillaSection.getString(line);

                    if (enchant != null) {
                        enchantments.add(line + ": " + enchant.replaceAll(" ", ""));
                    }
                });

                // Set new property.
                tinker.setProperty(TinkerConfig.enchantments, enchantments);

                // Save the file.
                tinker.save();
            }

            ConfigurationSection crazySection = tinkerSection.getConfigurationSection("Crazy-Enchantments");

            if (crazySection != null) {
                List<String> enchantments = new ArrayList<>();

                crazySection.getKeys(false).forEach(enchant -> {
                    String items = crazySection.getString(enchant + ".Items").replaceAll(" ", "");
                    String book = crazySection.getString(enchant + ".Book").replaceAll(" ", "");

                    String format = enchant + ": Items;" + items + "|Book;" + book;

                    enchantments.add(format);
                });

                // Set the properties.
                tinker.setProperty(TinkerConfig.crazyEnchantments, enchantments);

                // Save the file.
                tinker.save();
            }
        }
    }
}
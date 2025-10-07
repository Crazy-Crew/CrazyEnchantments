package com.badbones69.crazyenchantments.paper.api;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.io.File;

public class MigrateManager {

    @NotNull
    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private static final FusionPaper fusion = plugin.getFusion();

    public static void convert() {
        double tinkerVersion = 1.1;

        File firstFile = new File(plugin.getDataFolder() + "/Tinker.yml");

        File secondFile = new File(plugin.getDataFolder() + "/Tinker-v1.yml");

        YamlConfiguration TINKER = FileKeys.tinker.getConfiguration();

        if (TINKER.getDouble("Settings.Tinker-Version", 1.1) >= tinkerVersion) {
            plugin.getLogger().info("Tinker.yml is up to date.");
            return;
        } else {
            plugin.getLogger().warning("Updating Tinker.yml version.");

            TINKER.set("Settings.Tinker-Version", tinkerVersion);

            FileKeys.tinker.save();
        }

        if (firstFile.renameTo(secondFile)) {
            plugin.getLogger().warning("Renamed " + firstFile.getName() + " to Tinker-v1.yml");

            FileKeys.tinker.save();
        }

        YamlConfiguration secondConfiguration = YamlConfiguration.loadConfiguration(secondFile);

        final ConfigurationSection settings = secondConfiguration.getConfigurationSection("Settings");

        if (settings == null) {
            fusion.log("warn", "The black-smith section cannot be found in config.yml, It's possible the file is badly formatted!");
        } else {
            // Settings
            for (String setting : settings.getKeys(false)) {
                TINKER.set("Settings." + setting, secondConfiguration.get("Settings." + setting));
            }
        }

        final ConfigurationSection enchantments = secondConfiguration.getConfigurationSection("Tinker.Vanilla-Enchantments");

        if (enchantments == null) {
            fusion.log("warn", "The black-smith section cannot be found in config.yml, It's possible the file is badly formatted!");
        } else {
            // Vanilla Enchantments
            for (String enchantment : enchantments.getKeys(false)) {
                int amount = secondConfiguration.getInt("Tinker.Vanilla-Enchantments." + enchantment);

                TINKER.set("Tinker.Vanilla-Enchantments." + enchantment, amount + ", " + 1);
            }
        }

        final ConfigurationSection custom = secondConfiguration.getConfigurationSection("Tinker.Crazy-Enchantments");

        if (custom == null) {
            fusion.log("warn", "The black-smith section cannot be found in config.yml, It's possible the file is badly formatted!");
        } else {
            for (String enchantment : custom.getKeys(false)) {
                final ConfigurationSection type = secondConfiguration.getConfigurationSection("Tinker.Crazy-Enchantments." + enchantment);

                if (type == null) {
                    fusion.log("warn", "The enchantment {} could not be found in the tinker.yml", enchantment);

                    continue;
                }

                for (String itemBook : type.getKeys(false)) {
                    int value = secondConfiguration.getInt("Tinker.Crazy-Enchantments." + enchantment + "." + itemBook);

                    TINKER.set("Tinker.Crazy-Enchantments." + enchantment + "." + itemBook, value + ", " + 1);
                }
            }
        }

        FileKeys.tinker.save();

        plugin.getLogger().warning("Tinker.yml file has been updated.");
    }
}
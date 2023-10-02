package com.badbones69.crazyenchantments.paper.configs;

import java.io.File;
import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.ryderbelserion.cluster.bukkit.utils.LegacyLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConvertTinker {

    private static final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    public static void convert() {
        double tinkerVersion = 1.1;

        File firstFile = new File(plugin.getDataFolder() + "/Tinker.yml");

        File secondFile = new File(plugin.getDataFolder() + "/Tinker-v1.yml");

        FileConfiguration TINKER = Files.TINKER.getFile();

        if (TINKER.getDouble("Settings.Tinker-Version") >= tinkerVersion) {
            LegacyLogger.info("Tinker.yml is up to date.");
            return;
        } else {
            LegacyLogger.warn("Updating Tinker.yml version.");

            TINKER.set("Settings.Tinker-Version", tinkerVersion);
            Files.TINKER.saveFile();
        }

        if (firstFile.renameTo(secondFile)) {
            LegacyLogger.warn("Renamed " + firstFile.getName() + " to Tinker-v1.yml");

            Files.TINKER.saveFile();
        }

        YamlConfiguration secondConfiguration = YamlConfiguration.loadConfiguration(secondFile);

        // Settings
        for (String settings : secondConfiguration.getConfigurationSection("Settings").getKeys(false)) {
            TINKER.set("Settings." + settings, secondConfiguration.get("Settings." + settings));
        }

        // Vanilla Enchantments
        for (String enchantment : secondConfiguration.getConfigurationSection("Tinker.Vanilla-Enchantments").getKeys(false)) {
            int amount = secondConfiguration.getInt("Tinker.Vanilla-Enchantments." + enchantment);

            TINKER.set("Tinker.Vanilla-Enchantments." + enchantment, amount + ", " + 1);
        }

        //Custom Enchantments
        for (String enchantment : secondConfiguration.getConfigurationSection("Tinker.Crazy-Enchantments").getKeys(false)) {
            for(String itemBook : secondConfiguration.getConfigurationSection("Tinker.Crazy-Enchantments." + enchantment).getKeys(false)) {
                int value = secondConfiguration.getInt("Tinker.Crazy-Enchantments." + enchantment + "." + itemBook);

                TINKER.set("Tinker.Crazy-Enchantments." + enchantment + "." + itemBook, value + ", " + 1);
            }
        }

        Files.TINKER.saveFile();

        LegacyLogger.warn("Tinker.yml file has been updated.");
    }
}
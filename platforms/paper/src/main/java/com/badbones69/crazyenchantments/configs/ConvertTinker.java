package com.badbones69.crazyenchantments.configs;

import java.io.File;
import java.io.IOException;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConvertTinker {

    private static final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    public static void convert() {

        double TinkerVersion = 1.1;

        File file = new File(plugin.getDataFolder() + "/Tinker.yml");

        File secondFile = new File(plugin.getDataFolder() + "/Tinker-v1.yml");

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);

        if (!(yamlConfiguration.contains("Settings.Tinker-Version"))) {
            plugin.getLogger().warning("Could not find Tinker-Version, I am assuming configurations have been converted.");
            plugin.getLogger().warning("Make sure to add Tinker-Version: 0 under the Tinker.yml settings section if you haven't done so yet.");
            return;
        }

        if (yamlConfiguration.getDouble("Settings.Tinker-Version") >= TinkerVersion) {
            plugin.getLogger().info("Tinker.yml is up to date.");
            return;
        }

        if (file.renameTo(secondFile)) plugin.getLogger().warning("Renamed " + file.getName() + " to Tinker-v1.yml");

        YamlConfiguration secondConfiguration = YamlConfiguration.loadConfiguration(secondFile);

        //Settings
        for (String settings : secondConfiguration.getConfigurationSection("Settings").getKeys(false)) {
            yamlConfiguration.set("Settings." + settings, secondConfiguration.get("Settings." + settings));
        }
        yamlConfiguration.set("Settings.Tinker-Version", TinkerVersion);

        //Vanilla Enchantments
        for (String enchantment : secondConfiguration.getConfigurationSection("Tinker.Vanilla-Enchantments").getKeys(false)) {
            int amount = secondConfiguration.getInt("Tinker.Vanilla-Enchantments." + enchantment);
            yamlConfiguration.set("Tinker.Vanilla-Enchantments." + enchantment, amount + ", " + amount);
        }

        //Custom Enchantments
        for (String enchantment : secondConfiguration.getConfigurationSection("Tinker.Crazy-Enchantments").getKeys(false)) {
            for(String itemBook : secondConfiguration.getConfigurationSection("Tinker.Crazy-Enchantments." + enchantment).getKeys(false)) {
                int value = secondConfiguration.getInt("Tinker.Crazy-Enchantments." + enchantment + "." + itemBook);
                yamlConfiguration.set("Tinker.Crazy-Enchantments." + enchantment + "." + itemBook, value + ", " + value);
            }
        }

        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.getLogger().warning("Tinker.yml file has been updated.");
    }

}

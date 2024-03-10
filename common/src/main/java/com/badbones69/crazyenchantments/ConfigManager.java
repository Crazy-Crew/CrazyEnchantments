package com.badbones69.crazyenchantments;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazyenchantments.platform.BlockConfig;
import com.badbones69.crazyenchantments.platform.TinkerConfig;
import com.badbones69.crazyenchantments.platform.impl.Config;
import com.badbones69.crazyenchantments.platform.impl.Messages;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private static final Map<String, String> vanilla = new HashMap<>();

    private static SettingsManager config;

    private static SettingsManager messages;

    private static SettingsManager tinker;

    private static SettingsManager blocks;

    public static void load(File dataFolder) {
        // Create config files
        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        config = SettingsManagerBuilder
                .withYamlFile(new File(dataFolder, "config.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(Config.class)
                .create();

        messages = SettingsManagerBuilder
                .withYamlFile(new File(dataFolder, "messages.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(Messages.class)
                .create();

        tinker = SettingsManagerBuilder
                .withYamlFile(new File(dataFolder, "tinker.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(TinkerConfig.class)
                .create();

        // Update enchantment values.
        update();

        blocks = SettingsManagerBuilder
                .withYamlFile(new File(dataFolder, "blocks.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(BlockConfig.class)
                .create();
    }

    public static void reload() {
        config.reload();

        messages.reload();

        tinker.reload();

        blocks.reload();

        // Update enchantment values.
        update();
    }

    public static void disable() {
        config.save();

        messages.save();

        tinker.save();

        blocks.save();
    }

    public static SettingsManager getConfig() {
        return config;
    }

    public static SettingsManager getMessages() {
        return messages;
    }

    public static SettingsManager getTinker() {
        return tinker;
    }

    public static SettingsManager getBlocks() {
        return blocks;
    }

    public static Map<String, String> getVanillaEnchantments() {
        return Collections.unmodifiableMap(vanilla);
    }

    private static void update() {
        // Clear it in case.
        vanilla.clear();

        // Repopulate it
        tinker.getProperty(TinkerConfig.enchantments).forEach(line -> {
            String[] split = line.split(":");

            vanilla.put(split[0], split[1]);
        });
    }
}
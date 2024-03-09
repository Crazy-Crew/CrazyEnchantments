package com.badbones69.crazyenchantments;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazyenchantments.platform.impl.Config;
import com.badbones69.crazyenchantments.platform.impl.Messages;
import java.io.File;

public class ConfigManager {

    private static SettingsManager config;

    private static SettingsManager messages;

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
    }

    public static void reload() {
        config.reload();

        messages.reload();
    }

    public static void disable() {
        config.save();

        messages.save();
    }

    public static SettingsManager getConfig() {
        return config;
    }

    public static SettingsManager getMessages() {
        return messages;
    }
}
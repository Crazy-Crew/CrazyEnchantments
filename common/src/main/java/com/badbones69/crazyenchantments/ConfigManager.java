package com.badbones69.crazyenchantments;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazyenchantments.platform.impl.Config;
import com.badbones69.crazyenchantments.platform.impl.Messages;

import java.io.File;

public class ConfigManager {

    private final SettingsManager config;

    private final SettingsManager messages;

    public ConfigManager(File dataFolder) {
        // Create config files
        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        this.config = SettingsManagerBuilder
                .withYamlFile(new File(dataFolder, "config.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(Config.class)
                .create();

        this.messages = SettingsManagerBuilder
                .withYamlFile(new File(dataFolder, "messages.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(Messages.class)
                .create();
    }

    public void reload() {
        // Reload the config.
        this.config.reload();

        this.messages.reload();
    }

    public void disable() {
        // Save the config.
        this.config.save();

        this.messages.save();
    }

    public SettingsManager getConfig() {
        return this.config;
    }

    public SettingsManager getMessages() {
        return this.messages;
    }
}
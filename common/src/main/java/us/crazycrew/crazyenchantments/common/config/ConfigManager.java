package us.crazycrew.crazyenchantments.common.config;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import us.crazycrew.crazyenchantments.common.config.types.Config;
import us.crazycrew.crazyenchantments.common.config.types.Messages;
import java.io.File;

public class ConfigManager {

    private final File dataFolder;

    public ConfigManager(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    private SettingsManager messages;
    private SettingsManager config;

    public void load() {
        File configFile = new File(this.dataFolder, "config.yml");

        this.config = SettingsManagerBuilder
                .withYamlFile(configFile)
                .useDefaultMigrationService()
                .configurationData(ConfigurationDataBuilder.createConfiguration(Config.class))
                .create();

        createLocale();
    }

    public void reload() {
        // Reload config.yml
        this.config.reload();

        // Reload messages.yml
        this.messages.save();

        createLocale();
    }

    private void createLocale() {
        File localeDir = new File(this.dataFolder, "locale");

        if (!localeDir.exists()) localeDir.mkdirs();

        File messagesFile = new File(localeDir, this.config.getProperty(Config.locale_file) + ".yml");

        this.messages = SettingsManagerBuilder
                .withYamlFile(messagesFile)
                .useDefaultMigrationService()
                .configurationData(Messages.class)
                .create();
    }

    public SettingsManager getConfig() {
        return this.config;
    }

    public SettingsManager getMessages() {
        return this.messages;
    }
}
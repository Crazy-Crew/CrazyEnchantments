package us.crazycrew.crazyenchantments.common;

import us.crazycrew.crazyenchantments.common.api.AbstractPlugin;
import us.crazycrew.crazyenchantments.common.config.ConfigManager;
import java.io.File;

public class CrazyEnchantmentsPlugin extends AbstractPlugin {

    private final ConfigManager configManager;

    public CrazyEnchantmentsPlugin(File dataFolder) {
        this.configManager = new ConfigManager(dataFolder);
    }

    public void enable() {
        this.configManager.load();
    }

    public void disable() {
        this.configManager.reload();
    }

    @Override
    public ConfigManager getConfigManager() {
        return this.configManager;
    }
}
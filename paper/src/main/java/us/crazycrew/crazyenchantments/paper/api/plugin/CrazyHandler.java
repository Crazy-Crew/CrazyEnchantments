package us.crazycrew.crazyenchantments.paper.api.plugin;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.FileManager;
import com.ryderbelserion.cluster.bukkit.BukkitPlugin;
import com.ryderbelserion.cluster.bukkit.utils.LegacyLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenchantments.common.CrazyEnchantmentsPlugin;
import us.crazycrew.crazyenchantments.common.config.ConfigManager;
import us.crazycrew.crazyenchantments.common.config.types.Config;
import us.crazycrew.crazyenchantments.paper.api.MetricsHandler;
import java.io.File;

public class CrazyHandler extends CrazyEnchantmentsPlugin {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private BukkitPlugin bukkitPlugin;
    private MetricsHandler metrics;
    private FileManager fileManager;

    public CrazyHandler(File dataFolder) {
        super(dataFolder);
    }

    public void install() {
        this.bukkitPlugin = new BukkitPlugin(this.plugin);
        this.bukkitPlugin.enable();

        super.enable();

        LegacyLogger.setName(getConfigManager().getConfig().getProperty(Config.console_prefix));

        this.fileManager = new FileManager();
        this.fileManager.setup();

        boolean metrics = getConfigManager().getConfig().getProperty(Config.toggle_metrics);

        this.metrics = new MetricsHandler();
        if (metrics) this.metrics.start();
    }

    public void uninstall() {
        super.disable();

        this.bukkitPlugin.disable();
    }

    @Override
    public @NotNull ConfigManager getConfigManager() {
        return super.getConfigManager();
    }

    public @NotNull FileManager getFileManager() {
        return this.fileManager;
    }

    public @NotNull MetricsHandler getMetrics() {
        return this.metrics;
    }
}
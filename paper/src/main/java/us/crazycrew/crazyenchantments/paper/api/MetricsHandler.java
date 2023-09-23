package us.crazycrew.crazyenchantments.paper.api;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.cluster.bukkit.utils.LegacyLogger;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class MetricsHandler {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private Metrics metrics;

    public void start() {
        if (this.metrics != null) {
            if (this.plugin.isLogging()) LegacyLogger.warn("Metrics is already enabled.");
            return;
        }

        this.metrics = new Metrics(this.plugin, 4494);

        if (this.plugin.isLogging()) LegacyLogger.success("Metrics has been enabled.");
    }

    public void stop() {
        if (this.metrics == null) {
            if (this.plugin.isLogging()) LegacyLogger.warn("Metrics isn't enabled so we do nothing.");
            return;
        }

        this.metrics.shutdown();
        this.metrics = null;

        if (this.plugin.isLogging()) LegacyLogger.success("Metrics has been turned off.");
    }
}
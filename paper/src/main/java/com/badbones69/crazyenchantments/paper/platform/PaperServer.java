package com.badbones69.crazyenchantments.paper.platform;

import com.ryderbelserion.cluster.ClusterFactory;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperServer extends ClusterFactory {

    public PaperServer(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean isPapiEnabled() {
        return false;
    }

    @Override
    public boolean isOraxenEnabled() {
        return false;
    }

    @Override
    public boolean isItemsAdderEnabled() {
        return false;
    }

    @Override
    public boolean isHeadDatabaseEnabled() {
        return false;
    }
}
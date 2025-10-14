package com.badbones69.crazyenchantments.paper.listeners.server;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.support.PluginSupport;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ServerReadyListener implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    // Plugin Support.
    @NotNull
    private final PluginSupport pluginSupport = null;

    @EventHandler(ignoreCancelled = true)
    public void onServerReady(ServerLoadEvent event) {
        this.pluginSupport.updateHooks();
    }
}
package com.badbones69.crazyenchantments.listeners.server;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.PluginSupport;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerReadyListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final PluginSupport pluginSupport = starter.getPluginSupport();

    @EventHandler(ignoreCancelled = true)
    public void onServerReady(ServerLoadEvent event) {
        plugin.getLogger().info("Guten Tag!");

        pluginSupport.updateCachedPluginState();
        pluginSupport.printHooks();
    }
}
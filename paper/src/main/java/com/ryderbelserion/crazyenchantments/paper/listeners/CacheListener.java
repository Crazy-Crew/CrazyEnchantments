package com.ryderbelserion.crazyenchantments.paper.listeners;

import com.ryderbelserion.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.crazyenchantments.paper.api.registry.UserRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CacheListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final UserRegistry userRegistry = this.plugin.getUserRegistry();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.userRegistry.addUser(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.userRegistry.removeUser(event.getPlayer());
    }
}
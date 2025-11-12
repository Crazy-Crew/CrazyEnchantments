package com.ryderbelserion.crazyenchantments.paper.listeners;

import com.ryderbelserion.crazyenchantments.core.registry.UserRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class CacheListener implements Listener {

    private final UserRegistry userRegistry;

    public CacheListener(@NotNull final UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.userRegistry.addUser(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.userRegistry.removeUser(event.getPlayer());
    }
}
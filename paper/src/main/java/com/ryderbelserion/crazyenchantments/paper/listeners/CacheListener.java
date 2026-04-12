package com.ryderbelserion.crazyenchantments.paper.listeners;

import com.ryderbelserion.crazyenchantments.paper.api.CrazyEnchantmentsPaper;
import com.ryderbelserion.crazyenchantments.paper.api.registry.PaperUserRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class CacheListener implements Listener {

    private final PaperUserRegistry registry;

    public CacheListener(@NotNull final CrazyEnchantmentsPaper platform) {
        this.registry = platform.getUserRegistry();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.registry.addUser(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.registry.removeUser(event.getPlayer().getUniqueId());
    }
}
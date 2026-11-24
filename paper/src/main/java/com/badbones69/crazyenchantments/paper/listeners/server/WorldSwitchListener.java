package com.badbones69.crazyenchantments.paper.listeners.server;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.ryderbelserion.common.api.enums.Files;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class WorldSwitchListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyManager crazyManager = this.plugin.getCrazyManager();

    @EventHandler(ignoreCancelled = true)
    public void onWorldSwitch(PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();

        final CommentedConfigurationNode config = Files.CONFIG.getConfiguration();

        final boolean refreshEnabled = config.node("Settings", "Refresh-Potion-Effects-On-World-Change").getBoolean(false);

        if (refreshEnabled) {
            this.crazyManager.updatePlayerEffects(player);
        }
    }
}
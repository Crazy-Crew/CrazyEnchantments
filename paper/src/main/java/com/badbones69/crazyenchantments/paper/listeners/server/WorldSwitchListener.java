package com.badbones69.crazyenchantments.paper.listeners.server;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.keys.FileKeys;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class WorldSwitchListener implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final CrazyManager crazyManager = this.plugin.getStarter().getCrazyManager();

    @EventHandler(ignoreCancelled = true)
    public void onWorldSwitch(PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();

        final CommentedConfigurationNode config = FileKeys.CONFIG.getConfiguration();

        final boolean refreshEnabled = config.node("Settings", "Refresh-Potion-Effects-On-World-Change").getBoolean(false);

        if (refreshEnabled) {
            this.crazyManager.updatePlayerEffects(player);
        }
    }
}
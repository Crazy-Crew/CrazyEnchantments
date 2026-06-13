package com.badbones69.crazyenchantments.paper.support.v2.interfaces;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

public abstract class TerritorySupport<B, S> {

    protected final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    protected final Server server = this.plugin.getServer();

    protected final PluginManager pluginManager = this.server.getPluginManager();

    protected final ServicesManager servicesManager = this.server.getServicesManager();

    public boolean canBreakBlock(@NonNull final Player player, @NonNull final B container) {
        return true;
    }

    public boolean isCombatEnabled(@NonNull final S container) {
        return true;
    }

    public boolean isTerritory(@NonNull final Player player, @NonNull final S container) {
        return true;
    }

    public boolean isTerritory(@NonNull final Player player) {
        return true;
    }

    public boolean isProtected(@NonNull final S container) {
        return false;
    }

    public boolean isFriendly(@NonNull final Player player, @NonNull final Player target) {
        return false;
    }

    public boolean isOwner(@NonNull final Player player) {
        return false;
    }

    public boolean isMember(@NonNull final Player player) {
        return false;
    }

    public boolean isPluginReady() {
        return true;
    }
}
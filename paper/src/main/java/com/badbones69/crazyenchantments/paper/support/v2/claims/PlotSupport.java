package com.badbones69.crazyenchantments.paper.support.v2.claims;

import com.badbones69.crazyenchantments.paper.support.v2.interfaces.TerritorySupport;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public final class PlotSupport extends TerritorySupport<Location, Location> {

    @Override
    public boolean canBreakBlock(@NonNull final Player player, @NonNull final Location container) {
        if (!isPluginReady()) {
            return true;
        }

        return true;
    }

    @Override
    public boolean isCombatEnabled(@NonNull final Location container) {
        if (!isPluginReady()) {
            return true;
        }

        return true;
    }

    @Override
    public boolean isTerritory(@NonNull final Player player, @NonNull final Location container) {
        return isTerritory(player);
    }

    @Override
    public boolean isTerritory(@NonNull final Player player) {
        if (!isPluginReady()) {
            return true;
        }

        final PlotPlayer<Player> plotPlayer = PlotPlayer.from(player);

        final Plot plot = plotPlayer.getCurrentPlot();

        if (plot == null) {
            return false;
        }

        return plot.isAdded(player.getUniqueId());
    }

    @Override
    public boolean isFriendly(@NonNull final Player player, @NonNull final Player target) {
        if (!isPluginReady()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isPluginReady() {
        return this.pluginManager.isPluginEnabled("PlotSquared");
    }
}
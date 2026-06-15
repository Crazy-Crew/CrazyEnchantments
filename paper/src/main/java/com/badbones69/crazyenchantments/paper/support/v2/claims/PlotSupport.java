package com.badbones69.crazyenchantments.paper.support.v2.claims;

import com.badbones69.crazyenchantments.paper.support.v2.enums.PluginType;
import com.badbones69.crazyenchantments.paper.support.v2.interfaces.TerritorySupport;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PlotSupport extends TerritorySupport<Location, Location> {

    @Override
    public PluginType getPluginType() {
        return PluginType.DEFAULT;
    }

    @Override
    public String getPluginName() {
        return "PlotSquared";
    }

    @Override
    public boolean canBreakBlock(final Player player, final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        return true;
    }

    @Override
    public boolean isCombatEnabled(final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        return true;
    }

    @Override
    public boolean isTerritory(final Player player, final Location location) {
        return isTerritory(player);
    }

    @Override
    public boolean isTerritory(final Player player) {
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
    public boolean isFriendly(final Entity player, final Entity target) {
        if (!isPluginReady()) {
            return false;
        }

        return true;
    }
}
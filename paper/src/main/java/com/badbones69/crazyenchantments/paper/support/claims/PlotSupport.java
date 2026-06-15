package com.badbones69.crazyenchantments.paper.support.claims;

import com.badbones69.crazyenchantments.paper.support.enums.PluginType;
import com.badbones69.crazyenchantments.paper.support.interfaces.TerritorySupport;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.implementations.PvpFlag;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import java.util.UUID;

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
    public boolean isProtected(final Location location) {
        if (!isPluginReady()) {
            return false;
        }

        final com.plotsquared.core.location.Location plot = getLocation(location);

        return plot.isPlotArea() || plot.isPlotRoad() || plot.isUnownedPlotArea() || plot.getOwnedPlot() != null;
    }

    @Override
    public boolean canBreakBlock(final Player player, final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        final PlotPlayer<Player> plotPlayer = PlotPlayer.from(player);

        final Plot plot = plotPlayer.getCurrentPlot();

        if (plot == null) {
            return isProtected(location);
        }

        return plot.isAdded(player.getUniqueId());
    }

    @Override
    public boolean canPlaceBlock(final Player player, final Location location) {
        return canBreakBlock(player, location);
    }

    @Override
    public boolean canInteract(final Player player, final BlockState block) {
        return canBreakBlock(player, block.getLocation());
    }

    @Override
    public boolean canInteract(final Player player, final Location location) {
        return canBreakBlock(player, location);
    }

    @Override
    public boolean isCombatEnabled(final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        final Plot plot = Plot.getPlot(getLocation(location));

        if (plot == null) {
            return isProtected(location);
        }

        return plot.getFlag(PvpFlag.class);
    }

    @Override
    public boolean isTerritory(final Player player) {
        if (!isPluginReady()) {
            return true;
        }

        final PlotPlayer<Player> plotPlayer = PlotPlayer.from(player);

        final Plot plot = plotPlayer.getCurrentPlot();

        if (plot == null) {
            return isProtected(player.getLocation());
        }

        return plot.isAdded(player.getUniqueId());
    }

    @Override
    public boolean isTerritory(final Player player, final Location location) {
        return isTerritory(player);
    }

    @Override
    public boolean isMember(final Player player) {
        if (!isPluginReady()) {
            return false;
        }

        final PlotPlayer<Player> plotPlayer = PlotPlayer.from(player);

        final Plot plot = plotPlayer.getCurrentPlot();

        if (plot == null) {
            return false;
        }

        final UUID uuid = player.getUniqueId();

        return plot.getMembers().contains(uuid) || plot.getTrusted().contains(uuid);
    }

    @Override
    public boolean isOwner(final Player player) {
        if (!isPluginReady()) {
            return false;
        }

        final PlotPlayer<Player> plotPlayer = PlotPlayer.from(player);

        final Plot plot = plotPlayer.getCurrentPlot();

        if (plot == null) {
            return false;
        }

        return plot.isOwner(player.getUniqueId());
    }

    private com.plotsquared.core.location.Location getLocation(final Location location) {
        final String world = location.getWorld().getName();
        final int x = (int) location.getX();
        final int y = (int) location.getY();
        final int z = (int) location.getZ();

        return com.plotsquared.core.location.Location.at(world, x, y, z);
    }
}
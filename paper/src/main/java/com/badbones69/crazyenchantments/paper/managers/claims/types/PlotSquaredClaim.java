package com.badbones69.crazyenchantments.paper.managers.claims.types;

import com.badbones69.crazyenchantments.paper.managers.claims.interfaces.IClaim;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlotSquaredClaim implements IClaim {

    private final PlotAPI api;

    public PlotSquaredClaim() {
        this.api = new PlotAPI();
    }

    @Override
    public boolean canBreak(@NotNull final Player player, @NotNull final Block block) {
        final PlotPlayer<Player> plotPlayer = (PlotPlayer<Player>) this.api.wrapPlayer(player.getUniqueId());

        if (plotPlayer == null) {
            return true;
        }

        final Plot plot = plotPlayer.getCurrentPlot();

        if (plot == null) {
            return true;
        }

        return plot.isAdded(player.getUniqueId());
    }

    @Override
    public boolean canPVP(@NotNull final Location location) {
        return false;
    }

    @Override
    public boolean canExplode(@NotNull final Location location) {
        final BlockVector3 vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());

        PlotSquared.get().getPlotAreaManager().getPlotArea(new com.plotsquared.core.location.Location())

        return false;
    }

    @Override
    public boolean inTerritory(@NotNull final Player player, @NotNull final String name) {
        final PlotPlayer<Player> plotPlayer = PlotPlayer.from(player);

        final Plot plot = plotPlayer.getCurrentPlot();

        if (plot == null) {
            return true;
        }

        return plot.isAdded(player.getUniqueId());
    }

    @Override
    public boolean isOwner(@NotNull final Player player) {
        final PlotPlayer<Player> plotPlayer = PlotPlayer.from(player);

        final Plot plot = plotPlayer.getCurrentPlot();

        if (plot == null) {
            return false;
        }

        return plot.isOwner(player.getUniqueId());
    }

    @Override
    public boolean isMember(@NotNull final Player player) {
        final PlotPlayer<Player> plotPlayer = PlotPlayer.from(player);

        final Plot plot = plotPlayer.getCurrentPlot();

        if (plot == null) {
            return true;
        }

        return plot.isAdded(player.getUniqueId());
    }
}
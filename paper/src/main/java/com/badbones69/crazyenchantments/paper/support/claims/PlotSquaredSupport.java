package com.badbones69.crazyenchantments.paper.support.claims;

import com.badbones69.crazyenchantments.paper.support.interfaces.claims.PlotSquaredVersion;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlotSquaredSupport implements PlotSquaredVersion {

    public boolean inTerritory(@NotNull final Player player) {
        PlotPlayer<Player> plotPlayer = PlotPlayer.from(player);
        Plot plot = plotPlayer.getCurrentPlot();

        return plot != null && plot.isAdded(player.getUniqueId());
    }
}
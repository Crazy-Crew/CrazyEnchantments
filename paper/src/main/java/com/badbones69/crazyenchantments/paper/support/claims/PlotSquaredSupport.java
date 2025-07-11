package com.badbones69.crazyenchantments.paper.support.claims;

import com.badbones69.crazyenchantments.paper.support.interfaces.claims.PlotSquaredVersion;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import org.bukkit.entity.Player;

public class PlotSquaredSupport implements PlotSquaredVersion {

    public boolean inTerritory(final Player player) {
        final PlotPlayer<Player> plotPlayer = PlotPlayer.from(player);
        final Plot plot = plotPlayer.getCurrentPlot();

        return plot != null && plot.isAdded(player.getUniqueId());
    }
}
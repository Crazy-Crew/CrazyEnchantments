package com.badbones69.crazyenchantments.api.multisupport.plotsquared;

import com.badbones69.crazyenchantments.api.multisupport.interfaces.plotsquared.PlotSquaredVersion;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import org.bukkit.entity.Player;

public class PlotSquaredSupport implements PlotSquaredVersion {

    public boolean inTerritory(Player player) {
        PlotPlayer<Player> plotPlayer = PlotPlayer.from(player);
        Plot plot = plotPlayer.getCurrentPlot();

        return plot != null && plot.isAdded(plotPlayer.getUUID());
    }
}
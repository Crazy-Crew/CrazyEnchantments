package com.badbones69.crazyenchantments.api.support.claims;

import com.badbones69.crazyenchantments.api.support.interfaces.claims.PlotSquaredVersion;
import org.bukkit.entity.Player;

public class PlotSquaredSupport implements PlotSquaredVersion {

    public boolean inTerritory(Player player) {
        //PlotPlayer<Player> plotPlayer = PlotPlayer.from(player);
        //Plot plot = plotPlayer.getCurrentPlot();

        //return plot != null && plot.isAdded(plotPlayer.getUUID());
        return false;
    }
}
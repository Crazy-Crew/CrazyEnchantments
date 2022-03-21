package me.badbones69.crazyenchantments.multisupport.plotsquared;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import org.bukkit.entity.Player;

public class PlotSquared implements PlotSquaredVersion {
    
    public boolean inTerritory(Player player) {
        PlotPlayer<Player> plotPlayer = PlotPlayer.from(player);
        Plot plot = plotPlayer.getCurrentPlot();
        return plot != null && plot.isAdded(plotPlayer.getUUID());
    }
    
}
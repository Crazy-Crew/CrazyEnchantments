package me.badbones69.crazyenchantments.multisupport.plotsquared;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import org.bukkit.entity.Player;

public class PlotSquared implements PlotSquaredVersion {
    
    public boolean inTerritory(Player player) {
        Plot plot = PlotPlayer.get(player.getName()).getCurrentPlot();
        try {
            return plot.getOwners().contains(player.getUniqueId()) || plot.getMembers().contains(player.getUniqueId());
        } catch (Exception e) {
            return false;
        }
    }
    
}
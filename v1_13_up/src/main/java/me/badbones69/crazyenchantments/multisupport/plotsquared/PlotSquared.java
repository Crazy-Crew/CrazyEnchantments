package me.badbones69.crazyenchantments.multisupport.plotsquared;

import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;
import org.bukkit.entity.Player;

public class PlotSquared implements PlotSquaredVersion {
	
	public boolean inTerritory(Player player) {
		Plot plot = PlotPlayer.get(player.getName()).getCurrentPlot();
		try {
			return plot.getOwners().contains(player.getUniqueId()) || plot.getMembers().contains(player.getUniqueId());
		}catch(Exception e) {
			return false;
		}
	}
	
}
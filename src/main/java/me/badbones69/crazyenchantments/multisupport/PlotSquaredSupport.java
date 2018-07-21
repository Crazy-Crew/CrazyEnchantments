package me.badbones69.crazyenchantments.multisupport;

import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import org.bukkit.entity.Player;

public class PlotSquaredSupport {
	
	public static boolean inTerritory(Player player) {
		Plot plot = PlotPlayer.get(player.getName()).getCurrentPlot();
		return plot.getOwners().contains(player.getUniqueId()) || plot.getMembers().contains(player.getUniqueId());
	}
	
}
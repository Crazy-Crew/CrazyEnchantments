package me.badbones69.crazyenchantments.multisupport;

import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import net.sacredlabyrinth.Phaed.PreciousStones.api.IApi;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PreciousStonesSupport {
	
	private static IApi preciousStones = PreciousStones.API();
	
	//No api to check if the player is in their land.
	
	public static boolean canBreakBlock(Player player, Block block) {
		return preciousStones.canBreak(player, block.getLocation());
	}
	
}
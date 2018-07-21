package me.badbones69.crazyenchantments.multisupport;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class GriefPreventionSupport {
	
	private static GriefPrevention gp = GriefPrevention.instance;
	
	public static Boolean inTerritory(Player player) {
		return gp.dataStore.getClaimAt(player.getLocation(), true, null).getOwnerName().equalsIgnoreCase(player.getName()) ||
		gp.dataStore.getClaimAt(player.getLocation(), true, null).allowAccess(player) == null;
	}
	
	public static boolean isFriendly(Player player, Player other) {
		return gp.dataStore.getClaimAt(player.getLocation(), true, null).allowAccess(other) == null;
	}
	
	public static boolean canBreakBlock(Player player, Block block) {
		return gp.dataStore.getClaimAt(player.getLocation(), true, null).allowEdit(player) == null;
	}
	
}
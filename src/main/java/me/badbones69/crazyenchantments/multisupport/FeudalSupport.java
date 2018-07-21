package me.badbones69.crazyenchantments.multisupport;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import us.forseth11.feudal.core.Feudal;
import us.forseth11.feudal.kingdoms.Kingdom;
import us.forseth11.feudal.kingdoms.Land;

public class FeudalSupport {
	
	public static boolean isFrendly(Player player, Player other) {
		Kingdom p = Feudal.getPlayerKingdom(player.getUniqueId().toString());
		if(Feudal.getPlayerKingdom(other.getUniqueId().toString()) == null) {
			return false;
		}
		Kingdom o = Feudal.getPlayerKingdom(other.getUniqueId().toString());
		return p != null && o != null && isFrendly(p, o);
	}
	
	public static boolean isFrendly(Kingdom p, Kingdom o) {
		return p != null && o != null && !p.isEnemied(o) && (p.isAllied(o) || p == o);
	}
	
	public static boolean inTerritory(Player player) {
		Kingdom p = Feudal.getPlayerKingdom(player.getUniqueId().toString());
		return p != null && p.isOnLand(player.getLocation());
	}
	
	public static boolean canBreakBlock(Player player, Block block) {
		Kingdom p = Feudal.getPlayerKingdom(player.getUniqueId().toString());
		Land land = new Land(block.getLocation());
		Kingdom b = Feudal.getLandKingdom(land);
		return p == b || b == null;
	}
	
}
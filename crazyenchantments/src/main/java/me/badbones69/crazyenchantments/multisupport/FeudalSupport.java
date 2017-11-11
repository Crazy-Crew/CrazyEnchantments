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
		if(p == null || o == null) {
			return false;
		}
		if(isFrendly(p, o)) {
			return true;
		}
		return false;
	}

	public static boolean isFrendly(Kingdom p, Kingdom o) {
		if(p == null || o == null) {
			return false;
		}
		if(p.isEnemied(o)) {
			return false;
		}
		if(p.isAllied(o)) {
			return true;
		}
		if(p == o) {
			return true;
		}
		return false;
	}

	public static boolean inTerritory(Player player) {
		Kingdom p = Feudal.getPlayerKingdom(player.getUniqueId().toString());
		if(p == null) {
			return false;
		}
		if(p.isOnLand(player.getLocation())) {
			return true;
		}
		return false;
	}

	public static boolean canBreakBlock(Player player, Block block) {
		Kingdom p = Feudal.getPlayerKingdom(player.getUniqueId().toString());
		Land land = new Land(block.getLocation());
		Kingdom b = Feudal.getLandKingdom(land);
		if(p == b || b == null) {
			return true;
		}
		return false;
	}

}
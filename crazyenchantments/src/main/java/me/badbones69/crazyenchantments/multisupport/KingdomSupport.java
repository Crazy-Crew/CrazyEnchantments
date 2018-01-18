package me.badbones69.crazyenchantments.multisupport;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.kingdoms.constants.kingdom.Kingdom;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.land.SimpleLocation;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.manager.game.GameManagement;

public class KingdomSupport {

	public static boolean isFriendly(Player player, Player other) {
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(player);
		KingdomPlayer ko = GameManagement.getPlayerManager().getSession(other);
		if(kp == null || ko == null) {
			return false;
		}
		if(kp.getKingdom() == null || ko.getKingdom() == null) {
			return false;
		}
		if(kp.getKingdom() == ko.getKingdom()) {
			return true;
		}
		if(kp.getKingdom() != null) {
			if(kp.getKingdom().isAllianceWith(ko.getKingdom())) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}

	public static boolean inTerritory(Player P) {
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(P);
		SimpleLocation loc = new SimpleLocation(P.getLocation());
		Land land = GameManagement.getLandManager().getOrLoadLand(loc.toSimpleChunk());
		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwner());
		if(kp.getKingdom() != null && kingdom != null) {
			if(kingdom.equals(kp.getKingdom())) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}

	public static boolean canBreakBlock(Player player, Block block) {
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(player);
		SimpleLocation loc = new SimpleLocation(block.getLocation());
		Land land = GameManagement.getLandManager().getOrLoadLand(loc.toSimpleChunk());
		Kingdom kingdomland = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwner());
		if(land.getOwner() == null) {
			return true;
		}
		if(kp.isAdminMode()) {
			return true;
		}

		if(kingdomland.equals(kp.getKingdom())) {
			return true;
		}else {
			return false;
		}
	}
}
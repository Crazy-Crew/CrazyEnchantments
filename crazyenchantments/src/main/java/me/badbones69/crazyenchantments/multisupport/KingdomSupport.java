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
		return kp != null && ko != null && kp.getKingdom() != null && ko.getKingdom() != null && (kp.getKingdom() == ko.getKingdom() || kp.getKingdom() != null && kp.getKingdom().isAllianceWith(ko.getKingdom()));
	}
	
	public static boolean inTerritory(Player P) {
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(P);
		SimpleLocation loc = new SimpleLocation(P.getLocation());
		Land land = GameManagement.getLandManager().getOrLoadLand(loc.toSimpleChunk());
		Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwner());
		return kp.getKingdom() != null && kingdom != null && kingdom.equals(kp.getKingdom());
	}
	
	public static boolean canBreakBlock(Player player, Block block) {
		KingdomPlayer kp = GameManagement.getPlayerManager().getSession(player);
		SimpleLocation loc = new SimpleLocation(block.getLocation());
		Land land = GameManagement.getLandManager().getOrLoadLand(loc.toSimpleChunk());
		Kingdom kingdomland = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwner());
		return land.getOwner() == null || kp.isAdminMode() || kingdomland.equals(kp.getKingdom());
		
	}
}
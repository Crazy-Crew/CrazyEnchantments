package me.badbones69.crazyenchantments.multisupport;

import com.wasteofplastic.acidisland.ASkyBlockAPI;
import org.bukkit.entity.Player;

public class AcidIslandSupport {
	
	private static ASkyBlockAPI api = ASkyBlockAPI.getInstance();
	
	public static boolean inTerritory(Player player) {
		return api.playerIsOnIsland(player);
	}
	
	public static boolean isFriendly(Player player, Player other) {
		return api.inTeam(player.getUniqueId()) && api.inTeam(other.getUniqueId()) && api.getTeamMembers(player.getUniqueId()).contains(other.getUniqueId());
	}
	
}
package me.badbones69.crazyenchantments.multisupport;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import org.bukkit.entity.Player;

public class ASkyBlockSupport {

	private static ASkyBlockAPI api = ASkyBlockAPI.getInstance();

	public static Boolean inTerritory(Player player) {
		return api.playerIsOnIsland(player);
	}

	public static Boolean isFriendly(Player player, Player other) {
		if(api.inTeam(player.getUniqueId()) && api.inTeam(other.getUniqueId())) {
			if(api.getTeamMembers(player.getUniqueId()).contains(other.getUniqueId())) {
				return true;
			}
		}
		return false;
	}

}
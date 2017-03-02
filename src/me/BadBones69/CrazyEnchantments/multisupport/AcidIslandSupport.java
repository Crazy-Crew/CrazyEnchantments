package me.BadBones69.CrazyEnchantments.multisupport;

import org.bukkit.entity.Player;

import com.wasteofplastic.acidisland.ASkyBlockAPI;


public class AcidIslandSupport {
	
	private static ASkyBlockAPI api = ASkyBlockAPI.getInstance();
	
	public static Boolean inTerritory(Player player){
		return api.playerIsOnIsland(player);
	}
	
	public static Boolean isFriendly(Player player, Player other){
		if(api.inTeam(player.getUniqueId()) && api.inTeam(other.getUniqueId())){
			if(api.getTeamMembers(player.getUniqueId()).contains(other.getUniqueId())){
				return true;
			}
		}
		return false;
	}
	
}
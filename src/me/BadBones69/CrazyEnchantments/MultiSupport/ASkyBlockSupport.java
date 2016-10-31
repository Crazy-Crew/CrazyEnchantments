package me.BadBones69.CrazyEnchantments.MultiSupport;

import org.bukkit.entity.Player;

import com.wasteofplastic.askyblock.ASkyBlockAPI;

public class ASkyBlockSupport {
	
	private static ASkyBlockAPI api = ASkyBlockAPI.getInstance();
	
	public static Boolean inTerritory(Player player){
		return api.playerIsOnIsland(player);
	}
	
}
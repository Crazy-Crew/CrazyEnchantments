package me.badbones69.crazyenchantments.multisupport;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.entity.Player;

public class TownySupport {

	public static Boolean inTerritory(Player player) {
		try {
			TownBlock block = WorldCoord.parseWorldCoord(player).getTownBlock();
			Resident playerUser = TownyUniverse.getDataSource().getResident(player.getName());
			if(playerUser.hasTown()) {
				if(playerUser.getTown().hasTownBlock(block)) {
					return true;
				}
			}
		}catch(NotRegisteredException e) {
		}
		return false;
	}

	public static Boolean isFriendly(Player player, Player other) {
		try {
			Resident playerUser = TownyUniverse.getDataSource().getResident(player.getName());
			Resident otherUser = TownyUniverse.getDataSource().getResident(other.getName());
			if(playerUser.getTown().getName().equalsIgnoreCase(otherUser.getTown().getName())) {
				return true;
			}
		}catch(NotRegisteredException e) {
		}
		return false;
	}

}
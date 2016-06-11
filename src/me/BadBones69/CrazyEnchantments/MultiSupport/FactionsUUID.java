package me.BadBones69.CrazyEnchantments.MultiSupport;

import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Relation;

import me.BadBones69.CrazyEnchantments.Api;

public class FactionsUUID {
	public static boolean isFriendly(Player player, Player other){
		Faction p = FPlayers.getInstance().getByPlayer(player).getFaction();
		Faction o = FPlayers.getInstance().getByPlayer(other).getFaction();
		Relation r = FPlayers.getInstance().getByPlayer(player).getRelationTo(FPlayers.getInstance().getByPlayer(other));
		if(Api.removeColor(o.getTag()).equalsIgnoreCase("Wilderness"))return false;
		if(p==o)return true;
		if(!r.isAlly())return false;
		if(r.isAlly())return true;
		return false;
	}
	public static boolean inTerritory(Player P){
		if(FPlayers.getInstance().getByPlayer(P).isInOwnTerritory()){
			return true;
		}
		if(FPlayers.getInstance().getByPlayer(P).isInAllyTerritory()){
			return true;
		}
		return false;
	}
}
package me.BadBones69.CrazyEnchantments.MultiSupport;

import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Relation;

import me.BadBones69.CrazyEnchantments.Api;

public class FactionUUIDSupport {
	public static boolean isFriendly(Player P, Player O){
		Player player = (Player)P;
		Player other = (Player)O;
		Faction p = FPlayers.getInstance().getByPlayer(player).getFaction();
		Faction o = FPlayers.getInstance().getByPlayer(other).getFaction();
		Relation r = FPlayers.getInstance().getByPlayer(player).getRelationTo(FPlayers.getInstance().getByPlayer(other));
		if(Api.removeColor(o.getId()).equalsIgnoreCase("Wilderness"))return false;
		if(p==o)return true;
		if(!r.isAlly())return false;
		if(r.isAlly())return true;
		return false;
	}
}
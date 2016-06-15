package me.BadBones69.CrazyEnchantments.MultiSupport;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;

import me.BadBones69.CrazyEnchantments.Api;

public class FactionsSupport {
	public static boolean isFriendly(Player player, Player other){
		Faction p = MPlayer.get(player).getFaction();
		Faction o = MPlayer.get(other).getFaction();
		Rel r = MPlayer.get(player).getRelationTo(MPlayer.get(other));
		if(Api.removeColor(o.getName()).equalsIgnoreCase("Wilderness")){
			return false;
		}
		if(!r.isFriend())return false;
		if(r.isFriend())return true;
		if(p==o)return true;
		return false;
	}
	public static boolean isFriendly(Faction player, Faction other){
		Rel r = player.getRelationTo(other);
		if(Api.removeColor(other.getName()).equalsIgnoreCase("Wilderness")){
			return false;
		}
		if(!r.isFriend())return false;
		if(r.isFriend())return true;
		if(player==other)return true;
		return false;
	}
	public static boolean inTerritory(Player P){
		MPlayer player = MPlayer.get(P);
		if(Api.removeColor(player.getFaction().getName()).equalsIgnoreCase("Wilderness")){
			return false;
		}
		if(player.isInOwnTerritory()){
			return true;
		}
		return false;
	}
	public static boolean canBreakBlock(Player player, Block block){
		Faction P = MPlayer.get(player).getFaction();
		Faction B = BoardColl.get().getFactionAt(PS.valueOf(block.getLocation()));
		if(Api.removeColor(B.getName()).equalsIgnoreCase("Wilderness")||P==B){
			return true;
		}
		return false;
	}
}
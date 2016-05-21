package me.BadBones69.CrazyEnchantments.MultiSupport;

import org.bukkit.entity.Player;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;

import me.BadBones69.CrazyEnchantments.Api;

public class FactionSupport {
	public static boolean isFriendly(Player P, Player O){
		Player player = (Player)P;
		Player other = (Player)O;
		Faction p = MPlayer.get(player).getFaction();
		Faction o = MPlayer.get(other).getFaction();
		Rel r = MPlayer.get(player).getRelationTo(MPlayer.get(other));
		if(Api.removeColor(o.getName()).equalsIgnoreCase("Wilderness"))return false;
		if(!r.isFriend())return false;
		if(r.isFriend())return true;
		if(p==o)return true;
		return false;
	}
}
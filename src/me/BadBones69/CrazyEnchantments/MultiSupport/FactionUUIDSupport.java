package me.BadBones69.CrazyEnchantments.MultiSupport;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Relation;

public class FactionUUIDSupport {
	public static boolean isFriendly(Entity P, Entity O){
		if(P instanceof Player&&O instanceof Player){
			Player player = (Player)P;
			Player other = (Player)O;
			if(Bukkit.getServer().getPluginManager().getPlugin("Factions")!=null){
				Faction p = FPlayers.getInstance().getByPlayer(player).getFaction();
				Faction o = FPlayers.getInstance().getByPlayer(other).getFaction();
				Relation r = FPlayers.getInstance().getByPlayer(player).getRelationTo(FPlayers.getInstance().getByPlayer(other));
				if(Api.removeColor(o.getId()).equalsIgnoreCase("Wilderness"))return false;
				if(Api.removeColor(p.getId()).equalsIgnoreCase("Wilderness"))return false;
				if(p==o)return true;
				if(!r.isAlly())return false;
				if(r.isAlly())return true;
			}
		}
		return false;
	}
}
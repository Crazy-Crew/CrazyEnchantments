package me.BadBones69.CrazyEnchantments.MultiSupport;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;

public class FactionSupport {
	public static boolean isFriendly(Player player, Player other){
		if(Bukkit.getServer().getPluginManager().getPlugin("Factions")!=null){
			Faction p = MPlayer.get(player).getFaction();
			Faction o = MPlayer.get(other).getFaction();
			Rel r = MPlayer.get(player).getRelationTo(MPlayer.get(other));
			if(Api.removeColor(o.getName()).equalsIgnoreCase("Wilderness"))return false;
			if(Api.removeColor(p.getName()).equalsIgnoreCase("Wilderness"))return false;
			if(!r.isFriend())return false;
			if(r.isFriend())return true;
			if(p==o)return true;
		}
		return false;
	}
	public static boolean isFriendly(Entity P, Entity O){
		if(P instanceof Player&&O instanceof Player){
			Player player = (Player)P;
			Player other = (Player)O;
			if(Bukkit.getServer().getPluginManager().getPlugin("Factions")!=null){
				Faction p = MPlayer.get(player).getFaction();
				Faction o = MPlayer.get(other).getFaction();
				Rel r = MPlayer.get(player).getRelationTo(MPlayer.get(other));
				if(Api.removeColor(o.getName()).equalsIgnoreCase("Wilderness"))return false;
				if(Api.removeColor(p.getName()).equalsIgnoreCase("Wilderness"))return false;
				if(!r.isFriend())return false;
				if(r.isFriend())return true;
				if(p==o)return true;
			}
		}
		return false;
	}
}
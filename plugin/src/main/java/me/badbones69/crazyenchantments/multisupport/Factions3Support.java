package me.badbones69.crazyenchantments.multisupport;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import me.badbones69.crazyenchantments.Methods;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Factions3Support {
	
	public static boolean isFriendly(Player player, Player other) {
		if(MPlayer.get(player) == null || MPlayer.get(player).getFaction() == null) {
			return false;
		}
		Faction fPlayer = MPlayer.get(player).getFaction();
		if(MPlayer.get(other) == null || MPlayer.get(other).getFaction() == null) {
			return false;
		}
		Faction fOther = MPlayer.get(other).getFaction();
		Rel relation = MPlayer.get(player).getRelationTo(MPlayer.get(other));
		return !Methods.removeColor(fOther.getName()).equalsIgnoreCase("Wilderness") && (fPlayer == fOther || relation.isFriend() || relation.isAtLeast(Rel.NEUTRAL));
	}
	
	public static boolean isFriendly(Faction player, Faction other) {
		Rel relation = player.getRelationTo(other);
		return !Methods.removeColor(other.getName()).equalsIgnoreCase("Wilderness") && (relation.isFriend() || player == other);
	}
	
	public static boolean inTerritory(Player P) {
		MPlayer player = MPlayer.get(P);
		return !Methods.removeColor(player.getFaction().getName()).equalsIgnoreCase("Wilderness") && player.isInOwnTerritory();
	}
	
	public static boolean canBreakBlock(Player player, Block block) {
		Faction P = MPlayer.get(player).getFaction();
		Faction B = BoardColl.get().getFactionAt(PS.valueOf(block.getLocation()));
		return Methods.removeColor(B.getName()).equalsIgnoreCase("Wilderness") || P == B;
	}
	
}
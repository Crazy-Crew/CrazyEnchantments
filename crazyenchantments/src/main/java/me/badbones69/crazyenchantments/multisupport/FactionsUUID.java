package me.badbones69.crazyenchantments.multisupport;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Relation;
import me.badbones69.crazyenchantments.Methods;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FactionsUUID {
	
	public static boolean isFriendly(Player player, Player other) {
		Faction p = FPlayers.getInstance().getByPlayer(player).getFaction();
		Faction o = FPlayers.getInstance().getByPlayer(other).getFaction();
		if(o.isPeaceful()) {
			return true;
		}
		if(FPlayers.getInstance().getByPlayer(other) == null || FPlayers.getInstance().getByPlayer(other).getFaction() == null) {
			return false;
		}
		Relation r = FPlayers.getInstance().getByPlayer(player).getRelationTo(FPlayers.getInstance().getByPlayer(other));
		return !Methods.removeColor(o.getTag()).equalsIgnoreCase("Wilderness") && (p == o || r.isAlly());
	}
	
	public static boolean inTerritory(Player P) {
		return !Methods.removeColor(FPlayers.getInstance().getByPlayer(P).getFaction().getTag()).equalsIgnoreCase("Wilderness") && (FPlayers.getInstance().getByPlayer(P).isInOwnTerritory() || FPlayers.getInstance().getByPlayer(P).isInAllyTerritory());
	}
	
	public static boolean canBreakBlock(Player player, Block block) {
		Faction P = FPlayers.getInstance().getByPlayer(player).getFaction();
		FLocation loc = new FLocation(block.getLocation());
		Faction B = Board.getInstance().getFactionAt(loc);
		return Methods.removeColor(B.getTag()).equalsIgnoreCase("Wilderness") || P == B;
	}
	
}
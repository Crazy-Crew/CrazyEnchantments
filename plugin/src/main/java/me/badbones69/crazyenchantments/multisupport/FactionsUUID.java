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
		if(FPlayers.getInstance().getByPlayer(other) == null || FPlayers.getInstance().getByPlayer(other).getFaction() == null) {
			return false;
		}
		Faction fPlayer = FPlayers.getInstance().getByPlayer(player).getFaction();
		Faction fOther = FPlayers.getInstance().getByPlayer(other).getFaction();
		if(fOther.isPeaceful()) {
			return true;
		}
		Relation relation = FPlayers.getInstance().getByPlayer(player).getRelationTo(FPlayers.getInstance().getByPlayer(other));
		return !Methods.removeColor(fOther.getTag()).equalsIgnoreCase("Wilderness") && (fPlayer == fOther || relation.isAlly() || relation.isTruce());
	}
	
	public static boolean inTerritory(Player player) {
		return !Methods.removeColor(FPlayers.getInstance().getByPlayer(player).getFaction().getTag()).equalsIgnoreCase("Wilderness") &&
		(FPlayers.getInstance().getByPlayer(player).isInOwnTerritory() || FPlayers.getInstance().getByPlayer(player).isInAllyTerritory());
	}
	
	public static boolean canBreakBlock(Player player, Block block) {
		Faction P = FPlayers.getInstance().getByPlayer(player).getFaction();
		FLocation loc = new FLocation(block.getLocation());
		Faction B = Board.getInstance().getFactionAt(loc);
		return Methods.removeColor(B.getTag()).equalsIgnoreCase("Wilderness") || P == B;
	}
	
}
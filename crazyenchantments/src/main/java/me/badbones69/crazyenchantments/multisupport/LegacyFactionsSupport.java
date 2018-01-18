package me.badbones69.crazyenchantments.multisupport;

import me.badbones69.crazyenchantments.Methods;
import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class LegacyFactionsSupport {

	public static boolean isFriendly(Player player, Player other) {
		Faction p = FPlayerColl.get(player).getFaction();
		Faction o = FPlayerColl.get(other).getFaction();
		if(o.isPeaceful()) {
			return true;
		}
		if(FPlayerColl.get(other) == null || o == null) {
			return false;
		}
		Relation r = FPlayerColl.get(player).getRelationTo(FPlayerColl.get(other));
		if(Methods.removeColor(o.getTag()).equalsIgnoreCase("Wilderness")) return false;
		if(p == o) return true;
		if(r.isAlly()) return true;
		return false;
	}

	public static boolean inTerritory(Player player) {
		if(Methods.removeColor(FPlayerColl.get(player).getFaction().getTag()).equalsIgnoreCase("Wilderness")) return false;
		if(FPlayerColl.get(player).isInOwnTerritory()) {
			return true;
		}
		if(FPlayerColl.get(player).isInAllyTerritory()) {
			return true;
		}
		return false;
	}

	public static boolean canBreakBlock(Player player, Block block) {
		Faction P = FPlayerColl.get(player).getFaction();
		FLocation loc = new FLocation(block.getLocation());
		Faction B = Board.get().getFactionAt(loc);
		if(Methods.removeColor(B.getTag()).equalsIgnoreCase("Wilderness") || P == B) {
			return true;
		}
		return false;
	}

}
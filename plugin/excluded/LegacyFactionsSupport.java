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
        Faction fPlayer = FPlayerColl.get(player).getFaction();
        Faction fOther = FPlayerColl.get(other).getFaction();
        if (fOther.isPeaceful()) {
            return true;
        }
        if (FPlayerColl.get(other) == null) {
            return false;
        }
        Relation relation = FPlayerColl.get(player).getRelationTo(FPlayerColl.get(other));
        return !Methods.removeColor(fOther.getTag()).equalsIgnoreCase("Wilderness") && (fPlayer == fOther || relation.isAlly() || relation.isTruce());
    }
    
    public static boolean inTerritory(Player player) {
        return !Methods.removeColor(FPlayerColl.get(player).getFaction().getTag()).equalsIgnoreCase("Wilderness") && (FPlayerColl.get(player).isInOwnTerritory() || FPlayerColl.get(player).isInAllyTerritory());
    }
    
    public static boolean canBreakBlock(Player player, Block block) {
        Faction P = FPlayerColl.get(player).getFaction();
        FLocation loc = new FLocation(block.getLocation());
        Faction B = Board.get().getFactionAt(loc);
        return Methods.removeColor(B.getTag()).equalsIgnoreCase("Wilderness") || P == B;
    }
    
}
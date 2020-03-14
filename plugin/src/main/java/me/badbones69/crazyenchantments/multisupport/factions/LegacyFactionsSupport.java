package me.badbones69.crazyenchantments.multisupport.factions;

import me.badbones69.crazyenchantments.Methods;
import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class LegacyFactionsSupport implements FactionPlugin {
    
    public boolean isFriendly(Player player, Player other) {
        FPlayer fPlayer = FPlayerColl.get(player);
        FPlayer fOther = FPlayerColl.get(other);
        if (fPlayer == null || fOther == null) {
            return false;
        }
        if (fOther.getFaction().isPeaceful()) {
            return true;
        }
        return !Methods.removeColor(fOther.getTag()).equalsIgnoreCase(wilderness) && (fPlayer.getFaction() == fOther.getFaction() || fPlayer.getRelationTo(fOther).isAlly() || fPlayer.getRelationTo(fOther).isTruce());
    }
    
    public boolean inTerritory(Player player) {
        return !Methods.removeColor(FPlayerColl.get(player).getFaction().getTag()).equalsIgnoreCase(wilderness) && (FPlayerColl.get(player).isInOwnTerritory() || FPlayerColl.get(player).isInAllyTerritory());
    }
    
    public boolean canBreakBlock(Player player, Block block) {
        Faction fBlock = Board.get().getFactionAt(new FLocation(block.getLocation()));
        return Methods.removeColor(fBlock.getTag()).equalsIgnoreCase(wilderness) || FPlayerColl.get(player).getFaction() == fBlock;
    }
    
}
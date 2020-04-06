package me.badbones69.crazyenchantments.multisupport.factions;

import com.massivecraft.factions.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FactionsUUID implements FactionPlugin {
    
    public boolean isFriendly(Player player, Player other) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        FPlayer fOther = FPlayers.getInstance().getByPlayer(other);
        if (fPlayer == null || fOther == null) {
            return false;
        }
        if (fOther.getFaction().isPeaceful()) {
            return true;
        }
        if (isWilderness(fPlayer) || isWilderness(fOther)) {
            return false;
        }
        return fPlayer.getFaction() == fOther.getFaction() || fPlayer.getRelationTo(fOther).isAlly() || fPlayer.getRelationTo(fOther).isTruce();
    }
    
    public boolean inTerritory(Player player) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        return !isWilderness(fPlayer) && (fPlayer.isInOwnTerritory() || fPlayer.isInAllyTerritory());
    }
    
    public boolean canBreakBlock(Player player, Block block) {
        Faction fBlock = Board.getInstance().getFactionAt(new FLocation(block.getLocation()));
        return isWilderness(fBlock) || FPlayers.getInstance().getByPlayer(player).getFaction() == fBlock;
    }
    
    private boolean isWilderness(FPlayer player) {
        return isWilderness(player.getFaction());
    }
    
    private boolean isWilderness(Faction faction) {
        return faction != null && faction.isWilderness();
    }
    
}
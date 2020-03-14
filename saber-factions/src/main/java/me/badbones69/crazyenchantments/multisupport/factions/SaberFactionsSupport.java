package me.badbones69.crazyenchantments.multisupport.factions;

import com.massivecraft.factions.*;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class SaberFactionsSupport implements FactionPlugin {
    
    public boolean isFriendly(Player player, Player other) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        FPlayer fOther = FPlayers.getInstance().getByPlayer(other);
        if (fPlayer == null || fOther == null) {
            return false;
        }
        if (fOther.getFaction().isPeaceful()) {
            return true;
        }
        return !removeColor(fOther.getTag()).equalsIgnoreCase(wilderness) && (fPlayer.getFaction() == fOther.getFaction() || fPlayer.getRelationTo(fOther).isAlly() || fPlayer.getRelationTo(fOther).isTruce());
    }
    
    public boolean inTerritory(Player player) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        return !removeColor(fPlayer.getFaction().getTag()).equalsIgnoreCase(wilderness) &&
        (fPlayer.isInOwnTerritory() || fPlayer.isInAllyTerritory());
    }
    
    public boolean canBreakBlock(Player player, Block block) {
        Faction fBlock = Board.getInstance().getFactionAt(new FLocation(block.getLocation()));
        return removeColor(fBlock.getTag()).equalsIgnoreCase(wilderness) || FPlayers.getInstance().getByPlayer(player).getFaction() == fBlock;
    }
    
    private String removeColor(String msg) {
        return ChatColor.stripColor(msg);
    }
    
}
package me.badbones69.crazyenchantments.multisupport.factions;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Factions3Support implements FactionPlugin {
    
    public boolean isFriendly(Player player, Player other) {
        MPlayer fPlayer = MPlayer.get(player);
        MPlayer fOther = MPlayer.get(other);
        if (fPlayer == null || fOther == null) {
            return false;
        }
        return !removeColor(fOther.getName()).equalsIgnoreCase(wilderness) && (fPlayer == fOther || fPlayer.getRelationTo(fOther).isFriend() || fPlayer.getRelationTo(fOther).isAtLeast(Rel.NEUTRAL));
    }
    
    public boolean inTerritory(Player player) {
        MPlayer fPlayer = MPlayer.get(player);
        return !removeColor(fPlayer.getFaction().getName()).equalsIgnoreCase(wilderness) && fPlayer.isInOwnTerritory();
    }
    
    public boolean canBreakBlock(Player player, Block block) {
        Faction fBlock = BoardColl.get().getFactionAt(PS.valueOf(block.getLocation()));
        return removeColor(fBlock.getName()).equalsIgnoreCase(wilderness) || MPlayer.get(player).getFaction() == fBlock;
    }
    
    private String removeColor(String msg) {
        return ChatColor.stripColor(msg);
    }
    
}
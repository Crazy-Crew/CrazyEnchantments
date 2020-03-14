package me.badbones69.crazyenchantments.multisupport.factions;

import de.browniecodez.feudal.main.Main;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import us.forseth11.feudal.kingdoms.Kingdom;
import us.forseth11.feudal.kingdoms.Land;

public class FeudalSupport implements FactionPlugin {
    
    public boolean isFriendly(Player player, Player other) {
        Kingdom pKingdom = Main.getPlayerKingdom(player.getUniqueId().toString());
        if (Main.getPlayerKingdom(other.getUniqueId().toString()) == null) {
            return false;
        }
        Kingdom oKingdom = Main.getPlayerKingdom(other.getUniqueId().toString());
        return oKingdom != null && isFriendly(pKingdom, oKingdom);
    }
    
    public boolean isFriendly(Kingdom pKingdom, Kingdom oKingdom) {
        return pKingdom != null && oKingdom != null && !pKingdom.isEnemied(oKingdom) && (pKingdom.isAllied(oKingdom) || pKingdom == oKingdom);
    }
    
    public boolean inTerritory(Player player) {
        Kingdom pKingdom = Main.getPlayerKingdom(player.getUniqueId().toString());
        return pKingdom != null && pKingdom.isOnLand(player.getLocation());
    }
    
    public boolean canBreakBlock(Player player, Block block) {
        Kingdom kBlock = Main.getLandKingdom(new Land(block.getLocation()));
        return Main.getPlayerKingdom(player.getUniqueId().toString()) == kBlock || kBlock == null;
    }
    
}
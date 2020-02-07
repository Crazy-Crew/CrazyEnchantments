package me.badbones69.crazyenchantments.multisupport;

import de.browniecodez.feudal.main.Main;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import us.forseth11.feudal.kingdoms.Kingdom;
import us.forseth11.feudal.kingdoms.Land;

public class FeudalSupport {
    
    public static boolean isFrendly(Player player, Player other) {
        Kingdom p = Main.getPlayerKingdom(player.getUniqueId().toString());
        if (Main.getPlayerKingdom(other.getUniqueId().toString()) == null) {
            return false;
        }
        Kingdom o = Main.getPlayerKingdom(other.getUniqueId().toString());
        return o != null && isFrendly(p, o);
    }
    
    public static boolean isFrendly(Kingdom player, Kingdom other) {
        return player != null && other != null && !player.isEnemied(other) && (player.isAllied(other) || player == other);
    }
    
    public static boolean inTerritory(Player player) {
        Kingdom p = Main.getPlayerKingdom(player.getUniqueId().toString());
        return p != null && p.isOnLand(player.getLocation());
    }
    
    public static boolean canBreakBlock(Player player, Block block) {
        Kingdom kPlayer = Main.getPlayerKingdom(player.getUniqueId().toString());
        Land land = new Land(block.getLocation());
        Kingdom kBlock = Main.getLandKingdom(land);
        return kPlayer == kBlock || kBlock == null;
    }
    
}
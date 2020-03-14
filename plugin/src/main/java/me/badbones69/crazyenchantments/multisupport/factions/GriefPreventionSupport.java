package me.badbones69.crazyenchantments.multisupport.factions;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class GriefPreventionSupport {
    
    private static GriefPrevention gp = GriefPrevention.instance;
    
    public static boolean inTerritory(Player player) {
        Claim claim = gp.dataStore.getClaimAt(player.getLocation(), true, null);
        return claim != null && (claim.getOwnerName().equalsIgnoreCase(player.getName()) || claim.allowAccess(player) == null);
    }
    
    public static boolean isFriendly(Player player, Player other) {
        Claim claim = gp.dataStore.getClaimAt(player.getLocation(), true, null);
        return claim != null && claim.allowAccess(other) == null;
    }
    
    public static boolean canBreakBlock(Player player, Block block) {
        Claim claim = gp.dataStore.getClaimAt(block.getLocation(), true, null);
        return claim == null || claim.allowEdit(player) == null;
    }
    
}

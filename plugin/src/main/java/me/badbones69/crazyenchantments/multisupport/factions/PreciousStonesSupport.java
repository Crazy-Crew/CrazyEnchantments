package me.badbones69.crazyenchantments.multisupport.factions;

import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PreciousStonesSupport {
    
    //No api to check if the player is in their land.
    public static boolean canBreakBlock(Player player, Block block) {
        return PreciousStones.API().canBreak(player, block.getLocation());
    }
    
}
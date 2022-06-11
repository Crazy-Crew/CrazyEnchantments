package me.badbones69.crazyenchantments.multisupport.factions;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface FactionsVersion {

    String wilderness = "Wilderness";

    boolean isFriendly(Player player, Player other);

    boolean inTerritory(Player player);

    boolean canBreakBlock(Player player, Block block);

}
package com.badbones69.crazyenchantments.paper.api.support.interfaces.claims;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface ClaimSupport {

    String wilderness = "Wilderness";

    boolean isFriendly(Player player, Player other);

    boolean inTerritory(Player player);

    boolean canBreakBlock(Player player, Block block);

}
package com.badbones69.crazyenchantments.paper.support.interfaces.claims;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ClaimSupport {

    String wilderness = "Wilderness";

    boolean isFriendly(@NotNull final Player player, @NotNull final Player other);

    boolean inTerritory(@NotNull final Player player);

    boolean canBreakBlock(@NotNull final Player player, @NotNull final Block block);

}
package com.badbones69.crazyenchantments.paper.support.interfaces;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public interface CropManagerVersion {
    
    void fullyGrowPlant(@NotNull final Block block);
    
    boolean isFullyGrown(@NotNull final Block block);
    
    void hydrateSoil(@NotNull final Block soil);
    
}
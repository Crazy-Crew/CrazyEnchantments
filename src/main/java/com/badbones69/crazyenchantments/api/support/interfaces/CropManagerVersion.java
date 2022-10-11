package com.badbones69.crazyenchantments.api.support.interfaces;

import org.bukkit.block.Block;

public interface CropManagerVersion {
    
    void fullyGrowPlant(Block block);
    
    boolean isFullyGrown(Block block);
    
    void hydrateSoil(Block soil);
    
}
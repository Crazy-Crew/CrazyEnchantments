package com.badbones69.crazyenchantments.paper.support;

import com.badbones69.crazyenchantments.paper.support.interfaces.CropManagerVersion;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Farmland;

public class CropManager implements CropManagerVersion {
    
    @Override
    public void fullyGrowPlant(Block block) {
        if (block.getBlockData() instanceof Ageable age) {
            if (age.getAge() < age.getMaximumAge()) {
                age.setAge(age.getMaximumAge());
                block.setBlockData(age);
            }
        }
    }
    
    @Override
    public boolean isFullyGrown(Block block) {
        if (block.getBlockData() instanceof Ageable age) return age.getAge() == age.getMaximumAge();

        return false;
    }
    
    @Override
    public void hydrateSoil(Block soil) {
        Farmland farmland = (Farmland) soil.getBlockData();

        farmland.setMoisture(farmland.getMaximumMoisture());
        soil.setBlockData(farmland);
    }
}
package me.badbones69.crazyenchantments.multisupport;

import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Farmland;

public class NMS_v1_13_Up implements NMSSupport {
    
    @Override
    public void fullyGrowPlant(Block block) {
        if (block.getBlockData() instanceof Ageable) {
            Ageable age = (Ageable) block.getBlockData();
            if (age.getAge() < age.getMaximumAge()) {
                age.setAge(age.getMaximumAge());
                block.setBlockData(age);
            }
        }
    }
    
    @Override
    public boolean isFullyGrown(Block block) {
        if (block.getBlockData() instanceof Ageable) {
            Ageable age = (Ageable) block.getBlockData();
            return age.getAge() == age.getMaximumAge();
        }
        return false;
    }
    
    @Override
    public void hydrateSoil(Block soil) {
        Farmland farmland = (Farmland) soil.getBlockData();
        farmland.setMoisture(farmland.getMaximumMoisture());
        soil.setBlockData(farmland);
    }
}
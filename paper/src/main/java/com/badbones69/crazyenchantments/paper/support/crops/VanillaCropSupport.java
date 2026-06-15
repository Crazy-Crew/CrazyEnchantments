package com.badbones69.crazyenchantments.paper.support.crops;

import com.badbones69.crazyenchantments.paper.support.interfaces.CropSupport;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Farmland;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class VanillaCropSupport extends CropSupport {

    @Override
    public boolean isCropGrown(final BlockState blockState) {
        return blockState.getBlockData() instanceof Ageable age && age.getAge() == age.getMaximumAge();
    }

    @Override
    public void hydrateCrop(final BlockState blockState) {
        if (blockState.getBlockData() instanceof Ageable age) {
            final int maximumAge = age.getMaximumAge();

            if (age.getAge() < maximumAge) {
                age.setAge(maximumAge);

                blockState.setBlockData(age);
            }
        }
    }

    @Override
    public void growCrop(final BlockState blockState) {
        if (blockState.getBlockData() instanceof Farmland farmland) {
            farmland.setMoisture(farmland.getMaximumMoisture());

            blockState.setBlockData(farmland);
        }
    }
}
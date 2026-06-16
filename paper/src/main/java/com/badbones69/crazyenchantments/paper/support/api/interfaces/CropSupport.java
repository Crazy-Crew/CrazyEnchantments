package com.badbones69.crazyenchantments.paper.support.api.interfaces;

import org.bukkit.block.BlockState;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class CropSupport {

    public abstract boolean isCropGrown(final BlockState blockState);

    public abstract void hydrateCrop(final BlockState blockState);

    public abstract void growCrop(final BlockState blockState);

}
package com.ryderbelserion.crazyenchantments.paper.api.objects.types;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class CEBlock {

    private final int distance;
    private final Block block;
    private final String type;

    public CEBlock(@NotNull final Block block, final int distance) {
        this.block = block;
        this.type = block.getType().getKey().asString();
        this.distance = distance;
    }

    private int experience = 0;

    public final CEBlock setExperience(final int experience) {
        this.experience = experience;

        return this;
    }

    public final Block getBlock() {
        return this.block;
    }

    public final int getExperience() {
        return this.experience;
    }

    public final int getDistance() {
        return this.distance;
    }

    public final String getType() {
        return this.type;
    }
}
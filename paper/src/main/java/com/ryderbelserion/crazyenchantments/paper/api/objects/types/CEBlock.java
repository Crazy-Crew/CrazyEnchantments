package com.ryderbelserion.crazyenchantments.paper.api.objects.types;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class CEBlock {

    private final int distance;
    private final Block block;
    private final String type;
    private final UUID uuid;

    public CEBlock(@NotNull final Block block, final int distance) {
        this.block = block;
        this.type = block.getType().getKey().asString();
        this.distance = distance;
        this.uuid = UUID.randomUUID();
    }

    private int experience = 0;

    public final CEBlock setExperience(final int experience) {
        this.experience = experience;

        return this;
    }

    public @NotNull final Block getBlock() {
        return this.block;
    }

    public @NotNull final String getType() {
        return this.type;
    }

    public @NotNull final UUID getUuid() {
        return this.uuid;
    }

    public final int getExperience() {
        return this.experience;
    }

    public final int getDistance() {
        return this.distance;
    }
}
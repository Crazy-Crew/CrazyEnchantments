package com.badbones69.crazyenchantments.paper.api.objects;

import org.jetbrains.annotations.NotNull;

public class DustData {

    private final String name;
    private final int min;
    private final int max;
    private final int chance;

    public DustData(@NotNull final String dustConfigName, final int min, final int max, final int chance) {
        this.name = dustConfigName;
        this.min = min;
        this.max = max;
        this.chance = chance;
    }

    public int getMax() {
        return this.max;
    }

    public int getMin() {
        return this.min;
    }

    public String getConfigName() {
        return this.name;
    }

    public int getChance() {
        return this.chance;
    }
}
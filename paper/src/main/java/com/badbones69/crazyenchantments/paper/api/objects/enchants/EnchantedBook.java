package com.badbones69.crazyenchantments.paper.api.objects.enchants;

import org.jetbrains.annotations.NotNull;

public class EnchantedBook {

    private final String name;
    private int success;
    private int destroy;
    private int level;

    public EnchantedBook(@NotNull final String enchantmentName, final int success, final int destroy, final int level) {
        this.name = enchantmentName;
        this.success = success;
        this.destroy = destroy;
        this.level = level;
    }

    public int getDestroyChance() {
        return this.destroy;
    }

    public void setDestroyChance(final int newChance) {
        this.destroy = newChance;
    }

    public int getSuccessChance() {
        return this.success;
    }

    public void setSuccessChance(final int newChance) {
        this.success = newChance;
    }

    public String getName() {
        return this.name;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(final int newLevel) {
        this.level = newLevel;
    }
}
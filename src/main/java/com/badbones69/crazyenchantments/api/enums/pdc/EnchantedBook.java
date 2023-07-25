package com.badbones69.crazyenchantments.api.enums.pdc;

public class EnchantedBook {

    private final String name;
    private int success;
    private int destroy;
    private int level;

    public EnchantedBook(String enchantmentName, int success, int destroy, int level) {
        this.name = enchantmentName;
        this.success = success;
        this.destroy = destroy;
        this.level = level;
    }

    public int getDestroyChance() {
        return this.destroy;
    }
    public void setDestroyChance(int newChance) {
        this.destroy = newChance;
    }

    public int getSuccessChance() {
        return this.success;
    }
    public void setSuccessChance(int newChance) {
        this.success = newChance;
    }

    public String getName() {
        return this.name;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int newLevel) {
        this.level = newLevel;
    }
}

package com.badbones69.crazyenchantments.api.enums.pdc;

public class EnchantedBook {

    private final String name;
    private final int success;
    private final int destroy;
    private final int level;

    public EnchantedBook(String enchantmentName, int success, int destroy, int level) {
        this.name = enchantmentName;
        this.success = success;
        this.destroy = destroy;
        this.level = level;
    }

    public int getDestroyChance() {
        return destroy;
    }

    public int getSuccessChance() {
        return success;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

}

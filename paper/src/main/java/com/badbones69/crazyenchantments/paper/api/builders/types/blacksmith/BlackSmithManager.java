package com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith;

public class BlackSmithManager {

    private static int bookUpgrade,levelUp,addEnchantment;

    /**
     * @return the amount of enchants to add.
     */
    public static int getAddEnchantment() {
        return addEnchantment;
    }

    /**
     * @return the config value for book upgrades.
     */
    public static int getBookUpgrade() {
        return bookUpgrade;
    }

    /**
     * @return the config value for level up
     */
    public static int getLevelUp() {
        return levelUp;
    }
}
package me.badbones69.crazyenchantments.api.enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Arnah
 * @since Jul 30, 2015
 */
public enum ArmorType {
    
    HELMET(5), CHESTPLATE(6), LEGGINGS(7), BOOTS(8);
    
    private final int slot;
    
    private ArmorType(int slot) {
        this.slot = slot;
    }
    
    /**
     * Attempts to match the ArmorType for the specified ItemStack.
     *
     * @param itemStack The ItemStack to parse the type of.
     * @return The parsed ArmorType. (null if none were found.)
     */
    public static ArmorType matchType(final ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().equals(Material.AIR)) return null;
        String type = itemStack.getType().name();
        if (type.endsWith("_HELMET") || type.endsWith("_SKULL")) return HELMET;
        else if (type.endsWith("_CHESTPLATE")) return CHESTPLATE;
        else if (type.endsWith("_LEGGINGS")) return LEGGINGS;
        else if (type.endsWith("_BOOTS")) return BOOTS;
        else return null;
    }
    
    public int getSlot() {
        return slot;
    }
    
}
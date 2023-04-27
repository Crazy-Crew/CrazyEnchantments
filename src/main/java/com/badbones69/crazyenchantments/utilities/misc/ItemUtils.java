package com.badbones69.crazyenchantments.utilities.misc;

import org.bukkit.inventory.ItemStack;

public class ItemUtils {

    /**
     * Verify the ItemStack has lore. This checks to make sure everything isn't null because recent minecraft updates cause NPEs.
     *
     * @param item Itemstack you are checking.
     * @return True if the item has lore and no null issues.
     */
    public static boolean verifyItemLore(ItemStack item) {
        return item != null && item.lore() != null;
    }
}
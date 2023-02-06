package com.badbones69.crazyenchantments.utilities.misc;

import org.bukkit.inventory.ItemStack;

public class ItemUtils {

    /**
     * Verify the ItemStack has a lore. This checks to make sure everything isn't null because recent minecraft updates cause NPEs.
     *
     * @param item Itemstack you are checking.
     * @return True if the item has a lore and no null issues.
     */
    public static boolean verifyItemLore(ItemStack item) {
        return item != null && item.getItemMeta() != null && item.hasItemMeta() && item.getItemMeta().getLore() != null && item.getItemMeta().hasLore();
    }

}
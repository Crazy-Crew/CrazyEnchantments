package com.badbones69.crazyenchantments.paper.managers.items.interfaces;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface CustomItem {

    ItemStack getItemStack(final int amount);

    default ItemStack getItemStack() {
        return getItemStack(1);
    }

    boolean isItem(@NotNull final ItemStack itemStack);

    void init();

}
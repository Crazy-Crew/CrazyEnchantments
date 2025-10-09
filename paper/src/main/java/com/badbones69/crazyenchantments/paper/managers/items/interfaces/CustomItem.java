package com.badbones69.crazyenchantments.paper.managers.items.interfaces;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface CustomItem {

    void addKey(@NotNull final ItemStack itemStack, @NotNull final NamespacedKey key, @NotNull final String value);

    void addKey(@NotNull final NamespacedKey key, @NotNull final String value);

    void removeKey(@NotNull final ItemStack itemStack, @NotNull final NamespacedKey key);

    ItemStack getItemStack(final int amount);

    default ItemStack getItemStack() {
        return getItemStack(1);
    }

    boolean isItem(@NotNull final ItemStack itemStack);

    boolean hasKey(@NotNull final ItemStack itemStack, @NotNull final NamespacedKey key);

    void init();

}
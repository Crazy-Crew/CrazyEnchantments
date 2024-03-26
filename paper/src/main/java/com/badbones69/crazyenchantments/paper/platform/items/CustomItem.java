package com.badbones69.crazyenchantments.paper.platform.items;

import com.badbones69.crazyenchantments.paper.platform.items.types.ScramblerItem;
import org.bukkit.inventory.ItemStack;

public enum CustomItem {

    scrambler(new ScramblerItem());

    private final AbstractItem item;

    CustomItem(AbstractItem item) {
        this.item = item;
    }

    public boolean isItem() {
        return this.item.isItem();
    }

    public ItemStack getItem(int amount) {
        return this.item.setAmount(amount).getItem();
    }

    public ItemStack getItem() {
        return this.item.getItem();
    }
}
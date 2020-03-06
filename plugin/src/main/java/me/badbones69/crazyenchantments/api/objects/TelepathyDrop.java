package me.badbones69.crazyenchantments.api.objects;

import org.bukkit.inventory.ItemStack;

public class TelepathyDrop {
    
    private ItemStack itemStack;
    private int xp;
    
    public TelepathyDrop(ItemStack itemStack, int xp) {
        this.itemStack = itemStack;
        this.xp = xp;
    }
    
    public ItemStack getItem() {
        return itemStack;
    }
    
    public int getXp() {
        return xp;
    }
    
    public boolean hasXp() {
        return xp > 0;
    }
    
}
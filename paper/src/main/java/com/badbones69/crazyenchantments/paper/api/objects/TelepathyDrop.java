package com.badbones69.crazyenchantments.paper.api.objects;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class TelepathyDrop {
    
    private final ItemStack itemStack;
    private final int xp;
    private final List<Block> sugarCaneBlocks;
    
    public TelepathyDrop(ItemStack itemStack, int xp, List<Block> sugarCaneBlocks) {
        this.itemStack = itemStack;
        this.xp = xp;
        this.sugarCaneBlocks = sugarCaneBlocks;
    }
    
    public ItemStack getItem() {
        return this.itemStack;
    }
    
    public int getXp() {
        return this.xp;
    }
    
    public boolean hasXp() {
        return this.xp > 0;
    }
    
    public List<Block> getSugarCaneBlocks() {
        return this.sugarCaneBlocks;
    }
}
package me.badbones69.crazyenchantments.api.objects;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TelepathyDrop {
    
    private ItemStack itemStack;
    private int xp;
    private List<Block> sugarCaneBlocks;
    
    public TelepathyDrop(ItemStack itemStack, int xp, List<Block> sugarCaneBlocks) {
        this.itemStack = itemStack;
        this.xp = xp;
        this.sugarCaneBlocks = sugarCaneBlocks;
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
    
    public List<Block> getSugarCaneBlocks() {
        return sugarCaneBlocks;
    }
    
}
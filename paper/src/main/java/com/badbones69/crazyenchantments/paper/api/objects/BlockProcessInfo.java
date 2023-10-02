package com.badbones69.crazyenchantments.paper.api.objects;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class BlockProcessInfo {
    
    private final ItemStack item;
    private final Block block;
    private final List<ItemStack> drops;
    private final boolean isSpawner;
    
    public BlockProcessInfo(ItemStack item, Block block) {
        this.item = item;
        this.block = block;
        this.drops = new ArrayList<>(block.getDrops(item));
        this.isSpawner = block.getType() == Material.SPAWNER;
    }
    
    public ItemStack getItem() {
        return this.item;
    }
    
    public Block getBlock() {
        return this.block;
    }
    
    public List<ItemStack> getDrops() {
        return this.drops;
    }
    
    public boolean isSpawner() {
        return this.isSpawner;
    }
}
package me.badbones69.crazyenchantments.api.objects;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class GKitzItem {
    
    private ItemBuilder itemBuilder;
    private HashMap<CEnchantment, Integer> ceEnchantments;
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    
    /**
     * Make an empty gkit item.
     */
    public GKitzItem() {
        this.itemBuilder = new ItemBuilder();
        this.ceEnchantments = new HashMap<>();
    }
    
    /**
     * Make an empty gkit item.
     */
    public GKitzItem(ItemBuilder itemBuilder) {
        this.itemBuilder = itemBuilder;
        this.ceEnchantments = new HashMap<>();
    }
    
    /**
     *
     * @return The ItemBuilder object that is set.
     */
    public ItemBuilder getItemBuilder() {
        return itemBuilder;
    }
    
    /**
     * Set the ItemBuilder for the gkit item.
     * @param itemBuilder The Item you wish the given item to be.
     */
    public void setItemBuilder(ItemBuilder itemBuilder) {
        this.itemBuilder = itemBuilder;
    }
    
    /**
     *
     * @param enchant Crazy Enchantment
     * @param level Level of the enchantment
     */
    public void addCEEnchantment(CEnchantment enchant, int level) {
        ceEnchantments.put(enchant, level);
    }
    
    /**
     *
     * @param enchant Crazy Enchantment
     */
    public void removeCEEnchantment(CEnchantment enchant) {
        ceEnchantments.remove(enchant);
    }
    
    /**
     *
     * @return Returns a fully finished item.
     */
    public ItemStack build() {
        ItemStack item = itemBuilder.build();
        for (CEnchantment enchantment : ceEnchantments.keySet()) {
            ce.addEnchantment(item, enchantment, ceEnchantments.get(enchantment));
        }
        return item;
    }
    
}
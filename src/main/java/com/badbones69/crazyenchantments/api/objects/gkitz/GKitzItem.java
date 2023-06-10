package com.badbones69.crazyenchantments.api.objects.gkitz;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;

public class GKitzItem {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();
    
    private ItemBuilder itemBuilder;
    private final HashMap<CEnchantment, Integer> ceEnchantments;
    
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
     * @param enchant Crazy Enchantment
     * @param level Level of the enchantment
     */
    public void addCEEnchantment(CEnchantment enchant, int level) {
        ceEnchantments.put(enchant, level);
    }
    
    /**
     * @return Returns a fully finished item.
     */
    public ItemStack build() {
        ItemStack item = itemBuilder.build();

        for (Map.Entry<CEnchantment, Integer> enchantment : ceEnchantments.entrySet()) {
            item = crazyManager.addEnchantment(item, enchantment.getKey(), enchantment.getValue());
        }

        return item;
    }
}
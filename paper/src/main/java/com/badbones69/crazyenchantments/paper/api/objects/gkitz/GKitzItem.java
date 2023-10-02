package com.badbones69.crazyenchantments.paper.api.objects.gkitz;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GKitzItem {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CrazyManager crazyManager = this.plugin.getStarter().getCrazyManager();
    
    private final ItemBuilder itemBuilder;
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
        this.ceEnchantments.put(enchant, level);
    }
    
    /**
     * @return Returns a fully finished item.
     */
    public ItemStack build() {
        ItemStack item = this.itemBuilder.build();

        for (Map.Entry<CEnchantment, Integer> enchantment : this.ceEnchantments.entrySet()) {
            item = this.crazyManager.addEnchantment(item, enchantment.getKey(), enchantment.getValue());
        }

        return item;
    }
}
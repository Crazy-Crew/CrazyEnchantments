package com.badbones69.crazyenchantments.paper.api.objects.gkitz;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class GKitzItem {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final CrazyManager crazyManager = null;
    
    private final ItemBuilder itemBuilder;
    private final Map<CEnchantment, Integer> ceEnchantments;
    
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
    public GKitzItem(@NotNull final ItemBuilder itemBuilder) {
        this.itemBuilder = itemBuilder;
        this.ceEnchantments = new HashMap<>();
    }
    
    /**
     * @param enchant Crazy Enchantment
     * @param level Level of the enchantment
     */
    public void addCEEnchantment(@NotNull final CEnchantment enchant, final int level) {
        this.ceEnchantments.put(enchant, level);
    }
    
    /**
     * @return Returns a fully finished item.
     */
    public ItemStack build() {
        final ItemStack item = this.itemBuilder.build();

        for (Map.Entry<CEnchantment, Integer> enchantment : this.ceEnchantments.entrySet()) {
            this.crazyManager.addEnchantment(item, enchantment.getKey(), enchantment.getValue());
        }

        return item;
    }
}
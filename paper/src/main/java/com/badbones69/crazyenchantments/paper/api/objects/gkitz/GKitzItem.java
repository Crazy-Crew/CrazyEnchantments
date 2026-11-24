package com.badbones69.crazyenchantments.paper.api.objects.gkitz;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import net.kyori.adventure.audience.Audience;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;

public class GKitzItem {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyPlatform platform = this.plugin.getPlatform();

    private final EnchantmentBookSettings bookSettings = this.platform.getBookSettings();
    
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
    public ItemStack build(final Audience player) {
        ItemStack item = this.itemBuilder.build(player);

        for (Map.Entry<CEnchantment, Integer> enchantment : this.ceEnchantments.entrySet()) {
            this.bookSettings.addEnchantment(item, enchantment.getKey(), enchantment.getValue());
        }

        return item;
    }
}
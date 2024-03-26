package com.badbones69.crazyenchantments.paper.platform.items.types;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.platform.items.AbstractItem;
import com.badbones69.crazyenchantments.platform.impl.Config;
import com.ryderbelserion.cluster.items.ItemBuilder;
import com.ryderbelserion.cluster.items.ParentBuilder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ScramblerItem extends AbstractItem {

    private final ItemBuilder item;

    public ScramblerItem() {
        this.item = ParentBuilder.of()
                .setMaterial(this.config.getProperty(Config.scrambler_item).toLowerCase())
                .setDisplayName(this.config.getProperty(Config.scrambler_name))
                .setDisplayLore(this.config.getProperty(Config.scrambler_lore))
                .setGlowing(this.config.getProperty(Config.scrambler_glowing));

        this.item.setPlugin(JavaPlugin.getPlugin(CrazyEnchantments.class));
    }

    @Override
    public ScramblerItem setAmount(int amount) {
        this.item.setAmount(amount);

        return this;
    }

    @Override
    public ItemStack getItem() {
        this.item.setBoolean(DataKeys.scrambler.getNamespacedKey(), true);

        return this.item.build();
    }

    @Override
    public boolean isItem() {
        return this.item.hasKey(DataKeys.scrambler.getNamespacedKey());
    }
}
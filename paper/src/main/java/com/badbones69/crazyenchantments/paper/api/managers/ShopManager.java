package com.badbones69.crazyenchantments.paper.api.managers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.ShopOption;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.LostBook;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.ryderbelserion.crazyenchantments.utils.ConfigUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//todo() redo this
public class ShopManager {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private String inventoryName;
    private int inventorySize;
    private boolean enchantmentTableShop;
    private final Map<ItemBuilder, Integer> customizerItems = new HashMap<>();
    private final Map<ItemBuilder, Integer> shopItems = new HashMap<>();
    
    public void load(final CommentedConfigurationNode config) {
        this.customizerItems.clear();
        this.shopItems.clear();

        this.inventoryName = ColorUtils.color(config.node("Settings", "InvName").getString("&4&l&nCrazy Enchanter"));
        this.inventorySize = config.node("Settings", "GUISize").getInt(54);
        this.enchantmentTableShop = config.node("Settings", "EnchantmentOptions", "Right-Click-Enchantment-Table").getBoolean(false);

        for (final String customItemString : ConfigUtils.getStringList(config, "Settings", "GUICustomization")) {
            int slot = 0;

            for (String option : customItemString.split(", ")) {
                if (option.contains("Slot:")) {
                    option = option.replace("Slot:", "");

                    slot = Integer.parseInt(option);

                    break;
                }
            }

            if (slot > this.inventorySize || slot <= 0) continue;

            slot--;

            this.customizerItems.put(ItemBuilder.convertString(customItemString), slot);
        }

        for (final Category category : this.enchantmentBookSettings.getCategories()) {
            if (category.isInGUI()) {
                if (category.getSlot() > this.inventorySize) continue;

                this.shopItems.put(category.getDisplayItem(), category.getSlot());
            }

            final LostBook lostBook = category.getLostBook();

            if (lostBook.isInGUI()) {
                if (lostBook.getSlot() > this.inventorySize) continue;

                this.shopItems.put(lostBook.getDisplayItem(), lostBook.getSlot());
            }
        }

        for (final ShopOption option : ShopOption.values()) {
            if (option.isInGUI()) {
                if (option.getSlot() > this.inventorySize) continue;

                this.shopItems.put(option.getItemBuilder(), option.getSlot());
            }
        }
    }

    public Map<ItemBuilder, Integer> getShopItems() {
        return Collections.unmodifiableMap(this.shopItems);
    }

    public Map<ItemBuilder, Integer> getCustomizerItems() {
        return Collections.unmodifiableMap(this.customizerItems);
    }

    public String getInventoryName() {
        return this.inventoryName;
    }
    
    public int getInventorySize() {
        return this.inventorySize;
    }
    
    public boolean isEnchantmentTableShop() {
        return this.enchantmentTableShop;
    }
}
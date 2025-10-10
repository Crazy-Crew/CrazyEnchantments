package com.badbones69.crazyenchantments.paper.api.managers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.ShopOption;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.LostBook;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.managers.ConfigManager;
import com.badbones69.crazyenchantments.paper.managers.CategoryManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

//todo() redo this
public class ShopManager {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CategoryManager categoryManager = this.plugin.getCategoryManager();

    private final ConfigManager options = this.plugin.getOptions();

    private String inventoryName;
    private int inventorySize;
    private boolean enchantmentTableShop;
    private final Map<ItemBuilder, Integer> customizerItems = new HashMap<>();
    private final Map<ItemBuilder, Integer> shopItems = new HashMap<>();
    
    public void load() {
        this.customizerItems.clear();
        this.shopItems.clear();

        final YamlConfiguration config = FileKeys.config.getYamlConfiguration();

        this.inventoryName = ColorUtils.color(this.options.getInventoryName());
        this.inventorySize = this.options.getInventorySize();
        this.enchantmentTableShop = config.getBoolean("Settings.EnchantmentOptions.Right-Click-Enchantment-Table", false);

        for (final String customItemString : config.getStringList("Settings.GUICustomization")) {
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

        final Collection<Category> categories = this.categoryManager.getCategories().values();

        for (final Category category : categories) {
            if (category.isInGUI()) {
                final int slot = category.getSlot();

                if (slot > this.inventorySize) continue;

                this.shopItems.put(category.getDisplayItem(), slot);
            }

            final LostBook lostBook = category.getLostBook();

            if (lostBook.isInGUI()) {
                final int slot = lostBook.getSlot();

                if (slot > this.inventorySize) continue;

                this.shopItems.put(lostBook.getDisplayItem(), slot);
            }
        }

        final ShopOption[] options = ShopOption.values();

        for (final ShopOption option : options) {
            if (!option.isInGUI()) continue;

            final int slot = option.getSlot();

            if (slot > this.inventorySize) continue;

            this.shopItems.put(option.getItemBuilder(), slot);
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
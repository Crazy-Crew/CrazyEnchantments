package com.badbones69.crazyenchantments.paper.api.managers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.ShopOption;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.LostBook;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.config.ConfigOptions;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//todo() redo this
public class ShopManager {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final ConfigOptions options = this.plugin.getOptions();

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private String inventoryName;
    private int inventorySize;
    private boolean enchantmentTableShop;
    private final Map<ItemBuilder, Integer> customizerItems = new HashMap<>();
    private final Map<ItemBuilder, Integer> shopItems = new HashMap<>();
    
    public void load() {
        this.customizerItems.clear();
        this.shopItems.clear();

        final YamlConfiguration config = FileKeys.config.getConfiguration();

        this.inventoryName = ColorUtils.color(this.options.getInventoryName());
        this.inventorySize = this.options.getInventorySize();
        this.enchantmentTableShop = config.getBoolean("Settings.EnchantmentOptions.Right-Click-Enchantment-Table", false);

        for (String customItemString : config.getStringList("Settings.GUICustomization")) {
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

        for (Category category : this.enchantmentBookSettings.getCategories()) {
            if (category.isInGUI()) {
                if (category.getSlot() > this.inventorySize) continue;

                this.shopItems.put(category.getDisplayItem(), category.getSlot());
            }

            LostBook lostBook = category.getLostBook();

            if (lostBook.isInGUI()) {
                if (lostBook.getSlot() > this.inventorySize) continue;

                this.shopItems.put(lostBook.getDisplayItem(), lostBook.getSlot());
            }
        }

        for (ShopOption option : ShopOption.values()) {
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
package com.badbones69.crazyenchantments.paper.api.managers;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenchantments.ConfigManager;
import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.paper.api.enums.ShopOption;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.LostBook;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.platform.impl.Config;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//todo() redo this
public class ShopManager {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();
    private final Map<ItemBuilder, Integer> customizerItems = new HashMap<>();
    private final Map<ItemBuilder, Integer> shopItems = new HashMap<>();
    private String inventoryName;
    private int inventorySize;
    private boolean enchantmentTableShop;

    public void load() {
        this.customizerItems.clear();
        this.shopItems.clear();
        SettingsManager config = ConfigManager.getConfig();
        this.inventoryName = ColorUtils.color(config.getProperty(Config.inventory_name));
        this.inventorySize = config.getProperty(Config.inventory_size);
        this.enchantmentTableShop = config.getProperty(Config.right_click_enchantment_table);

        for (String customItemString : config.getProperty(Config.gui_customization)) {
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
            if (option.isInGui()) {
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
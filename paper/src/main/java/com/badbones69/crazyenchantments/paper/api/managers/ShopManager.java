package com.badbones69.crazyenchantments.paper.api.managers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.api.enums.ShopOption;
import com.badbones69.crazyenchantments.paper.api.objects.CEOption;
import com.ryderbelserion.common.api.enums.Files;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.LostBook;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//todo() redo this
public class ShopManager {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyPlatform platform = this.plugin.getPlatform();

    private final EnchantmentBookSettings bookSettings = this.platform.getBookSettings();

    private final Map<ItemBuilder, Integer> customizerItems = new HashMap<>();
    private final Map<ItemBuilder, Integer> shopItems = new HashMap<>();
    private boolean enchantmentTableShop;
    private String inventoryName;
    private int inventorySize;
    
    public void load() {
        this.customizerItems.clear();
        this.shopItems.clear();

        final CommentedConfigurationNode config = Files.CONFIG.getConfiguration();

        this.inventoryName = ColorUtils.color(config.node("Settings", "InvName").getString("&4&l&nCrazy Enchanter"));
        this.inventorySize = config.node("Settings", "GUISize").getInt(54);
        this.enchantmentTableShop = config.node("Settings", "EnchantmentOptions", "Right-Click-Enchantment-Table").getBoolean(false);

        for (final String customItemString : StringUtils.getStringList(config.node("Settings", "GUICustomization"))) {
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

        for (Category category : this.bookSettings.getCategories()) {
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

        for (final ShopOption option : ShopOption.values()) {
            final Optional<CEOption> index = this.platform.getOption(option.getPath());

            if (index.isEmpty()) {
                continue;
            }

            final CEOption value = index.get();

            if (!value.inGUI()) {
                continue;
            }

            final int slot = value.slot();

            if (slot > this.inventorySize) {
                continue;
            }

            this.shopItems.put(value.itemBuilder(), slot);
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
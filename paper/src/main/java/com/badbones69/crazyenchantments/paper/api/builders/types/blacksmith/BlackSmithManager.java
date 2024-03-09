package com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenchantments.ConfigManager;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.platform.impl.Config;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BlackSmithManager {

    private static ItemStack exitButton;
    private static ItemStack redGlass,blueGlass,grayGlass;

    private static String inventoryName,itemCost;

    private static Currency currency;

    private static int bookUpgrade,levelUp,addEnchantment;

    private static boolean maxEnchantments;

    /**
     * Initially loads all things we need.
     */
    public static void load() {
        redGlass = new ItemBuilder().setMaterial(Material.RED_STAINED_GLASS_PANE).setName(" ").build();
        grayGlass = new ItemBuilder().setMaterial(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
        blueGlass = new ItemBuilder().setMaterial(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setName(" ").build();

        get();
    }

    /**
     * Refreshes the values that require config options.
     */
    public static void refresh() {
        get();
    }

    /**
     * @return the exit button.
     */
    public static ItemStack getExitButton() {
        return exitButton;
    }

    /**
     * @return the blue glass pane.
     */
    public static ItemStack getBlueGlass() {
        return blueGlass;
    }

    /**
     * @return the gray glass pane.
     */
    public static ItemStack getGrayGlass() {
        return grayGlass;
    }

    /**
     * @return the red glass pane.
     */
    public static ItemStack getRedGlass() {
        return redGlass;
    }

    /**
     * @return the currency option defined in the config.
     */
    public static Currency getCurrency() {
        return currency;
    }

    /**
     * @return the amount of enchants to add.
     */
    public static int getAddEnchantment() {
        return addEnchantment;
    }

    /**
     * @return the config value for book upgrades.
     */
    public static int getBookUpgrade() {
        return bookUpgrade;
    }

    /**
     * @return the config value for level up
     */
    public static int getLevelUp() {
        return levelUp;
    }

    /**
     * @return the name of the inventory.
     */
    public static String getInventoryName() {
        return inventoryName;
    }

    /**
     * Get the cost of the item.
     *
     * @return item cost string
     */
    public static String getItemCost() {
        return itemCost;
    }

    /**
     * Checks if items can only have X amount of enchantments.
     *
     * @return true or false
     */
    public static boolean isMaxEnchantments() {
        return maxEnchantments;
    }

    private static void get() {
        SettingsManager config = ConfigManager.getConfig();

        exitButton = new ItemBuilder()
                .setMaterial(Material.BARRIER)
                .setName(config.getProperty(Config.blacksmith_results_none))
                .setLore(config.getProperty(Config.blacksmith_results_lore))
                .build();

        inventoryName = ColorUtils.color(config.getProperty(Config.blacksmith_gui_name));
        itemCost = config.getProperty(Config.blacksmith_results_found);
        currency = Currency.getCurrency(config.getProperty(Config.blacksmith_transaction_currency));

        bookUpgrade = config.getProperty(Config.blacksmith_transaction_book_upgrade);
        levelUp = config.getProperty(Config.blacksmith_transaction_power_up);
        addEnchantment = config.getProperty(Config.blacksmith_transaction_add_enchantment);

        maxEnchantments = config.getProperty(Config.max_amount_of_enchantments);
    }
}
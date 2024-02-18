package com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith;

import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
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

        get(Files.CONFIG.getFile());
    }

    /**
     * Refreshes the values that require config options.
     */
    public static void refresh() {
        get(Files.CONFIG.getFile());
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

    private static ConfigurationSection getSection(FileConfiguration config) {
        return config.getConfigurationSection("Settings.BlackSmith");
    }

    private static void get(FileConfiguration config) {
        ConfigurationSection section = getSection(config);

        // If section is null, do nothing.
        if (section == null) return;

        exitButton = new ItemBuilder()
                .setMaterial(Material.BARRIER)
                .setName(section.getString("Results.None", "&c&lNo Results."))
                .setLore(section.getStringList("Results.Not-Found-Lore"))
                .build();

        inventoryName = ColorUtils.color(section.getString("GUIName"));
        itemCost = section.getString("Results.Found", "");
        currency = Currency.getCurrency(section.getString("Transaction.Currency", "XP_LEVEL"));

        bookUpgrade = section.getInt("Transaction.Costs.Book-Upgrade", 5);
        levelUp = section.getInt("Transaction.Costs.Power-Up", 5);
        addEnchantment = section.getInt("Transaction.Costs.Add-Enchantment", 3);

        maxEnchantments = config.getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle", true);
    }
}
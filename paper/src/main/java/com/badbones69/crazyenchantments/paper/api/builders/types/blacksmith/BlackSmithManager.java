package com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.ryderbelserion.common.api.enums.Files;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.ryderbelserion.fusion.api.enums.Level;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class BlackSmithManager {

    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private static final CrazyPlatform platform = plugin.getPlatform();

    private static final FusionPaper fusion = platform.getFusion();

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

        get(Files.CONFIG.getConfiguration());
    }

    /**
     * Refreshes the values that require config options.
     */
    public static void refresh() {
        get(Files.CONFIG.getConfiguration());
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

    private static void get(CommentedConfigurationNode config) {
        CommentedConfigurationNode section = config.node("Settings", "BlackSmith");

        // If section is null, do nothing.
        if (section == null) {
            fusion.log(Level.warn, "The black-smith section cannot be found in config.yml, It's possible the file is badly formatted!");

            return;
        }

        exitButton = new ItemBuilder()
                .setMaterial(section.node("Results", "Item", "Type").getString("BARRIER"))
                .setName(section.node("Results", "None").getString("&c&lNo Results."))
                .setItemModel(section.node("Results", "Item", "Model", "Namespace").getString(""),
                        section.node("Results", "Item", "Model", "Key").getString(""))
                .setLore(StringUtils.getStringList(section.node("Results", "Not-Found-Lore")))
                .build();

        inventoryName = ColorUtils.color(section.node("GUIName").getString(""));
        itemCost = section.node("Results", "Found").getString("&c&lCost: &6&l%cost% XP");
        currency = Currency.getCurrency(section.node("Transaction", "Currency").getString("XP_LEVEL"));

        bookUpgrade = section.node("Transaction", "Costs", "Book-Upgrade").getInt(5);
        levelUp = section.node("Transaction", "Costs", "Power-Up").getInt(5);
        addEnchantment = section.node("Transaction", "Costs", "Add-Enchantment").getInt(3);

        maxEnchantments = config.node("Settings", "EnchantmentOptions", "MaxAmountOfEnchantmentsToggle")
                .getBoolean(true);
    }
}
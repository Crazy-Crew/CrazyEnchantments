package com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.ryderbelserion.crazyenchantments.objects.ConfigOptions;
import com.ryderbelserion.crazyenchantments.utils.ConfigUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.List;

public class BlackSmithManager {

    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private static final ConfigOptions options = plugin.getOptions();

    private static ItemStack exitButton;
    private static ItemStack redGlass,blueGlass,grayGlass;

    private static String inventoryName,itemCost;

    private static Currency currency;

    private static int bookUpgrade,levelUp,addEnchantment;

    private static boolean maxEnchantments;

    /**
     * Initially loads all things we need.
     */
    public static void load(final CommentedConfigurationNode node) {
        redGlass = new ItemBuilder().setMaterial(Material.RED_STAINED_GLASS_PANE).setName(" ").build();
        grayGlass = new ItemBuilder().setMaterial(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
        blueGlass = new ItemBuilder().setMaterial(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setName(" ").build();

        get(node);
    }

    /**
     * Refreshes the values that require config options.
     */
    public static void refresh(final CommentedConfigurationNode node) {
        get(node);
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

    private static void get(@NotNull final CommentedConfigurationNode config) {
        final CommentedConfigurationNode child = config.node("Settings", "BlackSmith");

        exitButton = new ItemBuilder().setMaterial(Material.BARRIER)
                .setName(child.node("Results", "None").getString("&c&lNo Results."))
                .setLore(ConfigUtils.getStringList(child, List.of(
                        "&7No results could be found.",
                        "&7Please put in two books of",
                        "&7the same enchantment and level.",
                        "&7Or put in two items to combined",
                        "&7the enchantments on them."
                ) ,"Results", "Not-Found-Lore"))
                .build();

        inventoryName = ColorUtils.color(child.node("GUIName").getString("&8&lThe Black Smith"));
        itemCost = child.node("Results", "Found").getString("&c&lCost: &6&l%cost% XP");
        currency = Currency.getCurrency(child.node("Transaction", "Currency").getString("XP_Level"));

        bookUpgrade = child.node("Transaction", "Costs", "Book-Upgrade", 5).getInt(5);
        levelUp = child.node("Transaction", "Costs", "Power-Up", 5).getInt(5);
        addEnchantment = child.node("Transaction", "Costs", "Add-Enchantment").getInt(3);

        maxEnchantments = options.isMaxAmountOfEnchantsToggle();
    }
}
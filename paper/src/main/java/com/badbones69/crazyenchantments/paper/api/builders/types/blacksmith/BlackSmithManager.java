package com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BlackSmithManager {

    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private static final FusionPaper fusion = plugin.getFusion();

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

        get(FileKeys.config.getYamlConfiguration());
    }

    /**
     * Refreshes the values that require config options.
     */
    public static void refresh() {
        get(FileKeys.config.getYamlConfiguration());
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

    private static ConfigurationSection getSection(@NotNull final FileConfiguration config) {
        return config.getConfigurationSection("Settings.BlackSmith");
    }

    private static void get(@NotNull final FileConfiguration config) {
        final ConfigurationSection section = getSection(config);

        // If section is null, do nothing.
        if (section == null) {
            fusion.log("warn", "The black-smith section cannot be found in config.yml, It's possible the file is badly formatted!");

            return;
        }

        exitButton = new ItemBuilder()
                .setMaterial(Material.BARRIER)
                .setName(section.getString("Results.None", "&c&lNo Results."))
                .setLore(section.getStringList("Results.Not-Found-Lore"))
                .build();

        //inventoryName = ColorUtils.color(section.getString("GUIName", "&8&lThe Black Smith")); //todo() legacy trash
        itemCost = section.getString("Results.Found", "&c&lCost: &6&l%cost% XP");
        currency = Currency.getCurrency(section.getString("Transaction.Currency", "XP_LEVEL"));

        bookUpgrade = section.getInt("Transaction.Costs.Book-Upgrade", 5);
        levelUp = section.getInt("Transaction.Costs.Power-Up", 5);
        addEnchantment = section.getInt("Transaction.Costs.Add-Enchantment", 3);

        maxEnchantments = config.getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle", true);
    }
}
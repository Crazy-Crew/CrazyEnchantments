package com.badbones69.crazyenchantments.controllers.settings;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.FileManager;
import com.badbones69.crazyenchantments.api.economy.Currency;
import com.badbones69.crazyenchantments.api.objects.Category;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.api.objects.LostBook;
import com.google.common.collect.Lists;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class EnchantmentBookSettings {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private ItemBuilder enchantmentBook;

    private final List<Category> categories = Lists.newArrayList();

    /**
     * @return a clone of the ItemBuilder of the enchantment book.
     */
    public ItemBuilder getEnchantmentBook() {
        return enchantmentBook.copy();
    }

    public ItemBuilder getNormalBook() {
        return enchantmentBook;
    }

    /**
     * @return the itemstack of the enchantment book.
     */
    public ItemStack getEnchantmentBookItem() {
        return enchantmentBook.build();
    }

    public void setEnchantmentBook(ItemBuilder enchantmentBook) {
        this.enchantmentBook = enchantmentBook;
    }

    /**
     * Get all the categories that can be used.
     * @return List of all the categories.
     */
    public List<Category> getCategories() {
        return categories;
    }

    public void populateMaps() {
        FileConfiguration config = FileManager.Files.CONFIG.getFile();

        for (String category : config.getConfigurationSection("Categories").getKeys(false)) {
            String path = "Categories." + category;
            LostBook lostBook = new LostBook(
                    config.getInt(path + ".LostBook.Slot"),
                    config.getBoolean(path + ".LostBook.InGUI"),
                    new ItemBuilder()
                            .setMaterial(config.getString(path + ".LostBook.Item"))
                            .setPlayerName(config.getString(path + ".LostBook.Player"))
                            .setName(config.getString(path + ".LostBook.Name"))
                            .setLore(config.getStringList(path + ".LostBook.Lore"))
                            .setGlow(config.getBoolean(path + ".LostBook.Glowing")),
                    config.getInt(path + ".LostBook.Cost"),
                    Currency.getCurrency(config.getString(path + ".LostBook.Currency")),
                    config.getBoolean(path + ".LostBook.FireworkToggle"),
                    getColors(config.getString(path + ".LostBook.FireworkColors")),
                    config.getBoolean(path + ".LostBook.Sound-Toggle"),
                    config.getString(path + ".LostBook.Sound"));
            categories.add(new Category(
                    category,
                    config.getInt(path + ".Slot"),
                    config.getBoolean(path + ".InGUI"),
                    new ItemBuilder()
                            .setMaterial(config.getString(path + ".Item"))
                            .setPlayerName(config.getString(path + ".Player"))
                            .setName(config.getString(path + ".Name"))
                            .setLore(config.getStringList(path + ".Lore"))
                            .setGlow(config.getBoolean(path + ".Glowing")),
                    config.getInt(path + ".Cost"),
                    Currency.getCurrency(config.getString(path + ".Currency")),
                    config.getInt(path + ".Rarity"),
                    lostBook,
                    config.getInt(path + ".EnchOptions.SuccessPercent.Max"),
                    config.getInt(path + ".EnchOptions.SuccessPercent.Min"),
                    config.getInt(path + ".EnchOptions.DestroyPercent.Max"),
                    config.getInt(path + ".EnchOptions.DestroyPercent.Min"),
                    config.getBoolean(path + ".EnchOptions.MaxLvlToggle"),
                    config.getInt(path + ".EnchOptions.LvlRange.Max"),
                    config.getInt(path + ".EnchOptions.LvlRange.Min")));
        }
    }

    /**
     * @param name The name of the category you want.
     * @return The category object.
     */
    public Category getCategory(String name) {
        for (Category category : categories) {
            if (category.getName().equalsIgnoreCase(name)) return category;
        }

        return null;
    }

    /**
     * Get the category of a lostbook from an itemstack.
     * @param item The itemstack you are checking.
     * @return The category it has or null if not found.
     */
    public Category getCategoryFromLostBook(ItemStack item) {
        for (Category category : categories) {
            if (item.isSimilar(category.getLostBook().getLostBook(category).build())) return category;
        }

        return null;
    }

    private List<Color> getColors(String string) {
        List<Color> colors = new ArrayList<>();
        methods.checkString(colors, string, methods);

        return colors;
    }
}
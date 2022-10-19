package com.badbones69.crazyenchantments.controllers.settings;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.FileManager;
import com.badbones69.crazyenchantments.api.economy.Currency;
import com.badbones69.crazyenchantments.api.objects.*;
import com.google.common.collect.Lists;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

public class EnchantmentBookSettings {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final FileManager fileManager = starter.getFileManager();

    private final Methods methods = starter.getMethods();

    private ItemBuilder enchantmentBook;

    private final List<Category> categories = Lists.newArrayList();

    private final List<CEnchantment> registeredEnchantments = Lists.newArrayList();

    /**
     * Get the enchantment from an enchantment book.
     * @param book The book you want the enchantment from.
     * @return The enchantment the book is.
     */
    public CEnchantment getEnchantmentBookEnchantment(ItemStack book) {
        if (book != null && book.getType() == getNormalBook().getMaterial() && book.hasItemMeta() && book.getItemMeta().hasDisplayName()) {
            for (CEnchantment enchantment : getRegisteredEnchantments()) {
                String bookNameCheck = book.getItemMeta().getDisplayName();
                String[] split = bookNameCheck.split(" ");

                if (bookNameCheck.replace(" " + split[split.length - 1], "").equals(enchantment.getBookColor() + enchantment.getCustomName())) return enchantment;
            }
        }

        return null;
    }

    /**
     * @param item Item that you want to check if it has an enchantment.
     * @param enchantment The enchantment you want to check if the item has.
     * @return True if the item has the enchantment / False if it doesn't have the enchantment.
     */
    public boolean hasEnchantment(ItemStack item, CEnchantment enchantment) {
        if (verifyItemLore(item)) {
            ItemMeta meta = item.getItemMeta();
            List<String> itemLore = meta.getLore();

            if (enchantment.isActivated() && itemLore != null) {
                for (String lore : itemLore) {
                    String[] split = lore.split(" ");
                    // Split can generate an empty array in rare case.
                    if (split.length > 0 && lore.replace(" " + split[split.length - 1], "").equals(enchantment.getColor() + enchantment.getCustomName())) return true;
                }
            }
        }

        return false;
    }

    /**
     * This method converts an ItemStack into a CEBook.
     * @param book The ItemStack you are converting.
     * @return If the book is a CEBook it will return the CEBook object and if not it will return null.
     */
    public CEBook getCEBook(ItemStack book) {
        try {
            return new CEBook(getEnchantmentBookEnchantment(book), getBookLevel(book, getEnchantmentBookEnchantment(book)), book.getAmount())
                    .setSuccessRate(getPercent("%success_rate%", book, FileManager.Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 100))
                    .setDestroyRate(getPercent("%destroy_rate%", book, FileManager.Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 0));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int getPercent(String argument, ItemStack item, List<String> originalLore, int defaultValue) {
        String arg = defaultValue + "";

        for (String originalLine : originalLore) {
            originalLine = starter.color(originalLine).toLowerCase();

            if (originalLine.contains(argument.toLowerCase())) {
                String[] b = originalLine.split(argument.toLowerCase());

                for (String itemLine : item.getItemMeta().getLore()) {
                    boolean toggle = false; // Checks to make sure the lore is the same.

                    if (b.length >= 1) {
                        if (itemLine.toLowerCase().startsWith(b[0])) {
                            arg = itemLine.toLowerCase().replace(b[0], "");
                            toggle = true;
                        }
                    }

                    if (b.length >= 2) {
                        if (itemLine.toLowerCase().endsWith(b[1])) {
                            arg = arg.toLowerCase().replace(b[1], "");
                        } else {
                            toggle = false;
                        }
                    }

                    if (toggle) break;
                }

                if (starter.isInt(arg)) break;
            }
        }

        int percent = defaultValue;

        if (starter.isInt(arg)) percent = Integer.parseInt(arg);

        return percent;
    }

    /**
     * Verify the ItemStack has a lore. This checks to make sure everything isn't null because recent minecraft updates cause NPEs.
     *
     * @param item Itemstack you are checking.
     * @return True if the item has a lore and no null issues.
     */
    public boolean verifyItemLore(ItemStack item) {
        return item != null && item.getItemMeta() != null && item.hasItemMeta() && item.getItemMeta().getLore() != null && item.getItemMeta().hasLore();
    }

    /**
     * @param item Item you want to check to see if it has enchantments.
     * @return True if it has enchantments / False if it doesn't have enchantments.
     */
    public boolean hasEnchantments(ItemStack item) {
        for (CEnchantment enchantment : registeredEnchantments) {
            if (hasEnchantment(item, enchantment)) return true;
        }

        return false;
    }

    /**
     * Get a new book that has been scrambled.
     * @param book The old book.
     * @return A new scrambled book.
     */
    public ItemStack getNewScrambledBook(ItemStack book) {
        if (isEnchantmentBook(book)) {
            CEnchantment enchantment = getEnchantmentBookEnchantment(book);
            return new CEBook(enchantment, getBookLevel(book, enchantment), methods.getHighestEnchantmentCategory(enchantment)).buildBook();
        }

        return new ItemStack(Material.AIR);
    }

    /**
     * @param book The book you are getting the level from.
     * @param enchant The enchantment you want the level from.
     * @return The level the enchantment has.
     */
    public int getBookLevel(ItemStack book, CEnchantment enchant) {
        return methods.convertLevelInteger(book.getItemMeta().getDisplayName().replace(enchant.getBookColor() + enchant.getCustomName() + " ", ""));
    }

    /**
     * Check if an itemstack is an enchantment book.
     * @param book The item you are checking.
     * @return True if it is and false if not.
     */
    public boolean isEnchantmentBook(ItemStack book) {
        if (book != null && book.getType() == getNormalBook().getMaterial() && book.hasItemMeta() && book.getItemMeta().hasDisplayName()) {
            for (CEnchantment enchantment : getRegisteredEnchantments()) {
                String bookNameCheck = book.getItemMeta().getDisplayName();
                String[] split = bookNameCheck.split(" ");

                if (bookNameCheck.replace(" " + split[split.length - 1], "").equals(enchantment.getBookColor() + enchantment.getCustomName())) return true;
            }
        }

        return false;
    }

    public List<CEnchantment> getRegisteredEnchantments() {
        return registeredEnchantments;
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
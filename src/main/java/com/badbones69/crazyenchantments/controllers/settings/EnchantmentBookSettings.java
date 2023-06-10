package com.badbones69.crazyenchantments.controllers.settings;

import com.badbones69.crazyenchantments.api.FileManager;
import com.badbones69.crazyenchantments.api.economy.Currency;
import com.badbones69.crazyenchantments.api.objects.CEBook;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.api.objects.Category;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.api.objects.LostBook;
import com.badbones69.crazyenchantments.utilities.misc.ColorUtils;
import com.badbones69.crazyenchantments.utilities.misc.EnchantUtils;
import com.badbones69.crazyenchantments.utilities.misc.ItemUtils;
import com.badbones69.crazyenchantments.utilities.misc.NumberUtils;
import com.google.common.collect.Lists;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jline.utils.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentBookSettings {

    private ItemBuilder enchantmentBook;

    private final List<Category> categories = Lists.newArrayList();

    private final List<CEnchantment> registeredEnchantments = Lists.newArrayList();

    public boolean useUnsafeEnchantments() {
        FileConfiguration config = FileManager.Files.CONFIG.getFile();

        return config.getBoolean("Settings.EnchantmentOptions.UnSafe-Enchantments");
    }

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
        if (ItemUtils.verifyItemLore(item)) {
            ItemMeta meta = item.getItemMeta();
            List<String> itemLore = meta.getLore();

            if (enchantment.isActivated()) {
                for (String line : itemLore) {
                    if (line.equals("") || line.equals(" ")) continue;
                    String[] split = line.split(" ");

                    // Split can generate an empty array in rare case.
                    String stripped = ColorUtils.removeColor(line.replace(" " + split[split.length - 1], ""));

                    if (stripped.equals(enchantment.getCustomName())) return true;
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
            Log.error(e);
        }

        return null;
    }

    public int getPercent(String argument, ItemStack item, List<String> originalLore, int defaultValue) {
        String arg = defaultValue + "";

        for (String originalLine : originalLore) {
            originalLine = ColorUtils.color(originalLine).toLowerCase();

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

                if (NumberUtils.isInt(arg)) break;
            }
        }

        int percent = defaultValue;

        if (NumberUtils.isInt(arg)) percent = Integer.parseInt(arg);

        return percent;
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
            return new CEBook(enchantment, getBookLevel(book, enchantment), EnchantUtils.getHighestEnchantmentCategory(enchantment)).buildBook();
        }

        return new ItemStack(Material.AIR);
    }

    /**
     * @param book The book you are getting the level from.
     * @param enchant The enchantment you want the level from.
     * @return The level the enchantment has.
     */
    public int getBookLevel(ItemStack book, CEnchantment enchant) {
        return NumberUtils.convertLevelInteger(book.getItemMeta().getDisplayName().replace(enchant.getBookColor() + enchant.getCustomName() + " ", ""));
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
     * Note: If the enchantment is not active it will not be added to the Map.
     * @param item Item you want to get the enchantments from.
     * @return A Map of all enchantments and their levels on the item.
     */
    public Map<CEnchantment, Integer> getEnchantments(ItemStack item) {

        if (!ItemUtils.verifyItemLore(item)) return Collections.emptyMap();

        List<String> lore = item.getItemMeta().getLore();
        Map<CEnchantment, Integer> enchantments = null;

        assert lore != null;
        for (String line : lore) {
            int lastSpaceIndex = line.lastIndexOf(' ');

            if (lastSpaceIndex < 1 || lastSpaceIndex + 1 > line.length()) continue;

            String enchantmentName = line.substring(0, lastSpaceIndex);

            for (CEnchantment enchantment : getRegisteredEnchantments()) {
                if (!enchantment.isActivated()) continue;

                String stripped = ColorUtils.removeColor(enchantmentName);

                if (!stripped.equals(enchantment.getCustomName())) continue;

                String levelString = line.substring(lastSpaceIndex + 1);
                int level = NumberUtils.convertLevelInteger(levelString);

                if (level < 1) break;

                if (enchantments == null) enchantments = new HashMap<>();

                enchantments.put(enchantment, level);
                break; // Next line
            }
        }

        if (enchantments == null) enchantments = Collections.emptyMap();

        return enchantments;
    }

    /**
     * Note: If the enchantment is not active it will not be added to the list.
     * @param item Item you want to get the enchantments from.
     * @return A list of enchantments the item has.
     */
    public List<CEnchantment> getEnchantmentsOnItem(ItemStack item) {
        return new ArrayList<>(getEnchantments(item).keySet());
    }

    public int getEnchantmentAmount(ItemStack item, boolean checkVanillaLimit) {
        int amount = getEnchantmentsOnItem(item).size();

        if (checkVanillaLimit) {
            if (item.hasItemMeta()) {
                if (item.getItemMeta().hasEnchants()) amount += item.getItemMeta().getEnchants().size();
            }
        }

        return amount;
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
        ColorUtils.color(colors, string);

        return colors;
    }

    /**
     * This converts an integer into a roman numeral if its between 1-10 otherwise it will just be the number as a string.
     * @param i The integer you want to convert.
     * @return The integer as a roman numeral if between 1-10 otherwise the number as a string.
     */
    public String convertLevelString(int i) {
        return switch (i) {
            case 0, 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> i + "";
        };
    }

    /**
     * @param item Item you are getting the level from.
     * @param enchant The enchantment you want the level from.
     * @return The level the enchantment has.
     */
    public int getLevel(ItemStack item, CEnchantment enchant) {
        int level = NumberUtils.convertLevelInteger(NumberUtils.checkLevels(item, enchant.getCustomName()).replace(enchant.getColor() + enchant.getCustomName() + " ", ""));

        if (!useUnsafeEnchantments() && level > enchant.getMaxLevel()) level = enchant.getMaxLevel();

        return level;
    }

    /**
     * @param item Item you want to remove the enchantment from.
     * @param enchant Enchantment you want removed.
     * @return Item without the enchantment.
     */
    public ItemStack removeEnchantment(ItemStack item, CEnchantment enchant) {
        List<String> newLore = new ArrayList<>();
        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasLore()) {
            List<String> itemLore = meta.getLore();

            if (itemLore != null) {
                for (String lore : itemLore) {
                    if (!lore.contains(enchant.getCustomName())) newLore.add(lore);
                }
            }
        }

        if (meta != null) meta.setLore(newLore);

        item.setItemMeta(meta);
        return item;
    }
}
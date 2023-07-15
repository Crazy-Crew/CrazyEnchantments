package com.badbones69.crazyenchantments.controllers.settings;

import com.badbones69.crazyenchantments.api.FileManager;
import com.badbones69.crazyenchantments.api.economy.Currency;
import com.badbones69.crazyenchantments.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.api.enums.pdc.Enchant;
import com.badbones69.crazyenchantments.api.enums.pdc.EnchantedBook;
import com.badbones69.crazyenchantments.api.objects.*;
import com.badbones69.crazyenchantments.utilities.misc.ColorUtils;
import com.badbones69.crazyenchantments.utilities.misc.EnchantUtils;
import com.badbones69.crazyenchantments.utilities.misc.NumberUtils;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jline.utils.Log;

import java.util.*;

public class EnchantmentBookSettings {

    private ItemBuilder enchantmentBook;

    private final List<Category> categories = Lists.newArrayList();

    private final List<CEnchantment> registeredEnchantments = Lists.newArrayList();

    private final Gson gson = new Gson();

    /**
     *
     * @return True if unsafe enchantments are enabled.
     */
    public boolean useUnsafeEnchantments() {
        FileConfiguration config = FileManager.Files.CONFIG.getFile();

        return config.getBoolean("Settings.EnchantmentOptions.UnSafe-Enchantments");
    }

    /**
     * @param item        Item that you want to check if it has an enchantment.
     * @param enchantment The enchantment you want to check if the item has.
     * @return True if the item has the enchantment / False if it doesn't have the enchantment.
     */
    public boolean hasEnchantment(ItemStack item, CEnchantment enchantment) {

    // PDC Start

        if (item == null || !item.hasItemMeta()) return false;

        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();

        if (!data.has(DataKeys.ENCHANTMENTS.getKey())) return false;

        String itemData = data.get(DataKeys.ENCHANTMENTS.getKey(), PersistentDataType.STRING);
        if (itemData == null) return false;

        return gson.fromJson(itemData, Enchant.class).hasEnchantment(enchantment.getName());

    // PDC End
    }

    /**
     * This method converts an ItemStack into a CEBook.
     * @param book The ItemStack you are converting.
     * @return If the book is a CEBook it will return the CEBook object and if not it will return null.
     */
    public CEBook getCEBook(ItemStack book) {

        if (!book.hasItemMeta() || !book.getItemMeta().getPersistentDataContainer().has(DataKeys.STORED_ENCHANTMENTS.getKey())) return null;

        EnchantedBook data = gson.fromJson(book.getItemMeta().getPersistentDataContainer().get(DataKeys.STORED_ENCHANTMENTS.getKey(), PersistentDataType.STRING), EnchantedBook.class);
       
        CEnchantment enchantment = null;
        for (CEnchantment enchant : getRegisteredEnchantments()) {
            if (enchant.getName().equalsIgnoreCase(data.getName())) {
                enchantment = enchant;
                break;
            }
        }
        return new CEBook(enchantment, data.getLevel(), book.getAmount())
                .setSuccessRate(data.getSuccessChance())
                .setDestroyRate(data.getDestroyChance());
    }

    /**
     * @param item Item you want to check to see if it has enchantments.
     * @return True if it has enchantments / False if it doesn't have enchantments.
     */
    public boolean hasEnchantments(ItemStack item) {

        if (item == null || !item.hasItemMeta()) return false;
        if (!item.getItemMeta().getPersistentDataContainer().has(DataKeys.ENCHANTMENTS.getKey())) return false;

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

        if (!book.hasItemMeta()) return null;

        // PDC Start
        EnchantedBook data = gson.fromJson(book.getItemMeta().getPersistentDataContainer().get(DataKeys.STORED_ENCHANTMENTS.getKey(), PersistentDataType.STRING), EnchantedBook.class);
        // PDC Enc

        CEnchantment enchantment = null;
        int bookLevel = 0;

        for (CEnchantment enchantment1 : getRegisteredEnchantments()) {
            if (!enchantment1.getName().equalsIgnoreCase(data.getName())) continue;
            enchantment = enchantment1;
            bookLevel = data.getLevel();
        }

        if (enchantment == null) return new ItemStack(Material.AIR);

        return new CEBook(enchantment, bookLevel, EnchantUtils.getHighestEnchantmentCategory(enchantment)).buildBook();
    }

    /**
     * Check if an itemstack is an enchantment book.
     * @param book The item you are checking.
     * @return True if it is and false if not.
     */
    public boolean isEnchantmentBook(ItemStack book) {

        if (book == null || book.getItemMeta() == null) return false;
        if (!book.getItemMeta().getPersistentDataContainer().has(DataKeys.STORED_ENCHANTMENTS.getKey())) return false;

        String dataString = book.getItemMeta().getPersistentDataContainer().get(DataKeys.STORED_ENCHANTMENTS.getKey(), PersistentDataType.STRING);
        EnchantedBook data = gson.fromJson(dataString, EnchantedBook.class);

        for (CEnchantment enchantment : getRegisteredEnchantments()) {
            if (enchantment.getName().equalsIgnoreCase(data.getName())) return true;
        }

        return false;
    }

    /**
     *
     * @return A list of all active enchantments.
     */
    public List<CEnchantment> getRegisteredEnchantments() {
        return registeredEnchantments;
    }

    /**
     *
     * @return itemBuilder for an enchanted book.
     */
    public ItemBuilder getNormalBook() {
        return enchantmentBook;
    }

    /**
     * @return the itemstack of the enchantment book.
     */
    public ItemStack getEnchantmentBookItem() {
        return enchantmentBook.build();
    }

    /**
     *
     * @param enchantmentBook
     */
    public void setEnchantmentBook(ItemBuilder enchantmentBook) {
        this.enchantmentBook = enchantmentBook;
    }

    /**
     * Note: If the enchantment is not active it will not be added to the Map.
     * @param item Item you want to get the enchantments from.
     * @return A Map of all enchantments and their levels on the item.
     */
    public Map<CEnchantment, Integer> getEnchantments(ItemStack item) {

        if (item == null || item.getItemMeta() == null) return Collections.emptyMap();

        Map<CEnchantment, Integer> enchantments = new HashMap<>();

    // PDC Start

        String data = item.getItemMeta().getPersistentDataContainer().get(DataKeys.ENCHANTMENTS.getKey(), PersistentDataType.STRING);

        if (data == null) return Collections.emptyMap();

        Enchant enchants = gson.fromJson(data, Enchant.class);

        if (enchants.isEmpty()) return Collections.emptyMap();

        for (CEnchantment enchantment : getRegisteredEnchantments()) {
            if (!enchantment.isActivated()) continue;
            if (enchants.hasEnchantment(enchantment.getName())) enchantments.put(enchantment, enchants.getLevel(enchantment.getName()));
        }
    // PDC End

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

    /**
     *
     * @param item to check.
     * @param includeVanillaEnchantments
     * @return Amount of enchantments on the item.
     */
    public int getEnchantmentAmount(ItemStack item, boolean includeVanillaEnchantments) {
        int amount = getEnchantmentsOnItem(item).size();

        if (includeVanillaEnchantments) {
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

    /**
     * Loads in all config options.
     */
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
    


    private List<Color> getColors(String string) {
        List<Color> colors = new ArrayList<>();
        ColorUtils.color(colors, string);

        return colors;
    }

    /**
     * @param item Item you are getting the level from.
     * @param enchant The enchantment you want the level from.
     * @return The level the enchantment has.
     */
    public int getLevel(ItemStack item, CEnchantment enchant) {

        // PDC Start
        String data = item.getItemMeta().getPersistentDataContainer().get(DataKeys.ENCHANTMENTS.getKey(), PersistentDataType.STRING);

        int level = data == null ? 0 : gson.fromJson(data, Enchant.class).getLevel(enchant.getName());
        // PDC End

        if (!useUnsafeEnchantments() && level > enchant.getMaxLevel()) level = enchant.getMaxLevel();

        return level;
    }

    /**
     * @param item Item you want to remove the enchantment from.
     * @param enchant Enchantment you want removed.
     * @return Item without the enchantment.
     */
    public ItemStack removeEnchantment(ItemStack item, CEnchantment enchant) {

        if (!item.hasItemMeta()) return item;

        ItemMeta meta = item.getItemMeta();
        List<Component> lore = meta.lore();

        if (lore != null) {

            lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "")
                    .contains(enchant.getCustomName().replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "")));
            meta.lore(lore);
        }
    // PDC Start
        Enchant data;

        if (meta.getPersistentDataContainer().has(DataKeys.ENCHANTMENTS.getKey())) {
            data = gson.fromJson(meta.getPersistentDataContainer().get(DataKeys.ENCHANTMENTS.getKey(), PersistentDataType.STRING), Enchant.class);
        } else {
            data = new Enchant(new HashMap<>());
        }

        data.removeEnchantment(enchant.getName());

        if (data.isEmpty()) {
            if (meta.getPersistentDataContainer().has(DataKeys.ENCHANTMENTS.getKey()))
                meta.getPersistentDataContainer().remove(DataKeys.ENCHANTMENTS.getKey());
        } else {
            meta.getPersistentDataContainer().set(DataKeys.ENCHANTMENTS.getKey(), PersistentDataType.STRING, gson.toJson(data));
        }
    // PDC End

        item.setItemMeta(meta);

        return item;
    }
}
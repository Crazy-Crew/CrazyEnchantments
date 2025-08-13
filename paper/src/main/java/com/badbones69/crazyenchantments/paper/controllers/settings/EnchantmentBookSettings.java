package com.badbones69.crazyenchantments.paper.controllers.settings;

import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.Enchant;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.EnchantedBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.LostBook;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.api.utils.EnchantUtils;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        FileConfiguration config = Files.CONFIG.getFile();

        return config.getBoolean("Settings.EnchantmentOptions.UnSafe-Enchantments");
    }

    /**
     * This method converts an ItemStack into a CEBook.
     * @param book The ItemStack you are converting.
     * @return If the book is a CEBook it will return the CEBook object and if not it will return null.
     */
    @Nullable
    public CEBook getCEBook(final ItemStack book) {
        final PersistentDataContainerView view = book.getPersistentDataContainer();

        if (!view.has(DataKeys.stored_enchantments.getNamespacedKey())) return null;

        EnchantedBook data = Methods.getGson().fromJson(view.get(DataKeys.stored_enchantments.getNamespacedKey(), PersistentDataType.STRING), EnchantedBook.class);

        CEnchantment enchantment = null;

        for (final CEnchantment enchant : getRegisteredEnchantments()) {
            if (enchant.getName().equalsIgnoreCase(data.getName())) {
                enchantment = enchant;

                break;
            }
        }

        return new CEBook(enchantment, data.getLevel(), book.getAmount()).setSuccessRate(data.getSuccessChance()).setDestroyRate(data.getDestroyChance());
    }

    /**
     * Get a new book that has been scrambled.
     * @param book The old book.
     * @return A new scrambled book.
     */
    @Nullable
    public ItemStack getNewScrambledBook(final ItemStack book) {
        final PersistentDataContainerView view = book.getPersistentDataContainer();

        final EnchantedBook data = Methods.getGson().fromJson(view.get(DataKeys.stored_enchantments.getNamespacedKey(), PersistentDataType.STRING), EnchantedBook.class);

        CEnchantment enchantment = null;

        int bookLevel = 0;

        for (final CEnchantment enchantment1 : getRegisteredEnchantments()) {
            if (!enchantment1.getName().equalsIgnoreCase(data.getName())) continue;

            enchantment = enchantment1;

            bookLevel = data.getLevel();
        }

        if (enchantment == null) return null;

        return new CEBook(enchantment, bookLevel, EnchantUtils.getHighestEnchantmentCategory(enchantment)).buildBook();
    }

    /**
     * Check if an itemstack is an enchantment book.
     * @param book The item you are checking.
     * @return True if it is and false if not.
     */
    public boolean isEnchantmentBook(final ItemStack book) {
        if (book == null) return false;

        final PersistentDataContainerView view = book.getPersistentDataContainer();

        if (!view.has(DataKeys.stored_enchantments.getNamespacedKey())) return false;

        final String dataString = view.get(DataKeys.stored_enchantments.getNamespacedKey(), PersistentDataType.STRING);
        final EnchantedBook data = Methods.getGson().fromJson(dataString, EnchantedBook.class);

        for (final CEnchantment enchantment : getRegisteredEnchantments()) {
            if (enchantment.getName().equalsIgnoreCase(data.getName())) return true;
        }

        return false;
    }

    /**
     *
     * @return A list of all active enchantments.
     */
    @NotNull
    public List<CEnchantment> getRegisteredEnchantments() {
        return this.registeredEnchantments;
    }

    /**
     *
     * @return itemBuilder for an enchanted book.
     */
    public ItemBuilder getNormalBook() {
        return new ItemBuilder(this.enchantmentBook);
    }

    /**
     * @return the itemstack of the enchantment book.
     */
    @NotNull
    public ItemStack getEnchantmentBookItem() {
        return new ItemBuilder(this.enchantmentBook).build();
    }

    /**
     *
     * @param enchantmentBook The book data to use for the itemBuilder.
     */
    public void setEnchantmentBook(@NotNull ItemBuilder enchantmentBook) {
        this.enchantmentBook = enchantmentBook;
    }

    /**
     * Note: If the enchantment is not active it will not be added to the Map.
     * @param item Item you want to get the enchantments from.
     * @return A Map of all enchantments and their levels on the item.
     */
    @NotNull
    public Map<CEnchantment, Integer> getEnchantments(@Nullable final ItemStack item) {
        if (item == null) return Collections.emptyMap();

        final PersistentDataContainerView view = item.getPersistentDataContainer();

        final Map<CEnchantment, Integer> enchantments = new HashMap<>();

        final String data = view.get(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING);

        if (data == null) return Collections.emptyMap();

        final Enchant enchants = Methods.getGson().fromJson(data, Enchant.class);

        if (enchants.isEmpty()) return Collections.emptyMap();

        for (final CEnchantment enchantment : getRegisteredEnchantments()) {
            if (!enchantment.isActivated()) continue;

            if (enchants.hasEnchantment(enchantment.getName())) enchantments.put(enchantment, enchants.getLevel(enchantment.getName()));
        }

        return enchantments;
    }

    public List<CEnchantment> getEnchantmentsOnItem(final ItemStack item) {
        return new ArrayList<>(getEnchantments(item).keySet());
    }

    /**
     *
     * @param item to check.
     * @return Amount of enchantments on the item.
     */
    public int getEnchantmentAmount(@NotNull ItemStack item, boolean includeVanillaEnchantments) {
        int amount = getEnchantments(item).size();

        if (includeVanillaEnchantments && item.hasData(DataComponentTypes.ENCHANTMENTS)) {
            amount += item.getEnchantments().size();
        }

        return amount;
    }

    /**
     * Get all the categories that can be used.
     * @return List of all the categories.
     */
    @NotNull
    public List<Category> getCategories() {
        return this.categories;
    }

    /**
     * Loads in all config options.
     */
    public void populateMaps() {
        FileConfiguration config = Files.CONFIG.getFile();

        for (String category : config.getConfigurationSection("Categories").getKeys(false)) {
            String path = "Categories." + category;
            LostBook lostBook = new LostBook(
                    config.getInt(path + ".LostBook.Slot"),
                    config.getBoolean(path + ".LostBook.InGUI"),
                    new ItemBuilder()
                            .setMaterial(config.getString(path + ".LostBook.Item", "BOOK"))
                            .setPlayerName(config.getString(path + ".LostBook.Player"))
                            .setName(config.getString(path + ".LostBook.Name", "Error getting name."))
                            .setLore(config.getStringList(path + ".LostBook.Lore"))
                            .setGlow(config.getBoolean(path + ".LostBook.Glowing", true)),
                    config.getInt(path + ".LostBook.Cost"),
                    Currency.getCurrency(config.getString(path + ".LostBook.Currency")),
                    config.getBoolean(path + ".LostBook.FireworkToggle", false),
                    getColors(config.getString(path + ".LostBook.FireworkColors", "Red, White, Blue")),
                    config.getBoolean(path + ".LostBook.Sound-Toggle", false),
                    config.getString(path + ".LostBook.Sound", "BLOCK_ANVIL_PLACE"));

            this.categories.add(new Category(
                    category,
                    config.getInt(path + ".Slot"),
                    config.getBoolean(path + ".InGUI", true),
                    new ItemBuilder()
                            .setMaterial(config.getString(path + ".Item", ColorUtils.getRandomPaneColor().getName()))
                            .setPlayerName(config.getString(path + ".Player"))
                            .setName(config.getString(path + ".Name", "Error getting name."))
                            .setLore(config.getStringList(path + ".Lore"))
                            .setGlow(config.getBoolean(path + ".Glowing", false)),
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
    @Nullable
    public Category getCategory(String name) {
        for (Category category : this.categories) {
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
    public int getLevel(@NotNull final ItemStack item, @NotNull final CEnchantment enchant) {
        final String data = item.getPersistentDataContainer().get(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING);

        int level = data == null ? 0 : Methods.getGson().fromJson(data, Enchant.class).getLevel(enchant.getName());

        if (!useUnsafeEnchantments() && level > enchant.getMaxLevel()) level = enchant.getMaxLevel();

        return level;
    }

    /**
     * @param itemStack Item you want to remove the enchantment from.
     * @param enchant Enchantment you want removed.
     * @return Item without the enchantment.
     */
    public @NotNull ItemStack removeEnchantment(@NotNull final ItemStack itemStack, @NotNull final CEnchantment enchant) {
        final PersistentDataContainerView view = itemStack.getPersistentDataContainer();

        final List<Component> lore = itemStack.lore();

        if (lore != null) {
            lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).contains(ColorUtils.stripStringColour(enchant.getCustomName())));

            itemStack.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(lore).build());
        }

        Enchant data;

        if (view.has(DataKeys.enchantments.getNamespacedKey())) {
            data = Methods.getGson().fromJson(view.get(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING), Enchant.class);
        } else {
            data = new Enchant(new HashMap<>());
        }

        data.removeEnchantment(enchant.getName());

        if (data.isEmpty() && view.has(DataKeys.enchantments.getNamespacedKey())) {
            itemStack.editPersistentDataContainer(container -> {
                container.remove(DataKeys.enchantments.getNamespacedKey());
            });
        } else {
            itemStack.editPersistentDataContainer(container -> {
                container.set(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING, Methods.getGson().toJson(data));
            });
        }

        return itemStack;
    }

    public void removeEnchantments(@NotNull ItemStack itemStack, @NotNull List<CEnchantment> enchants) {
        final PersistentDataContainerView view = itemStack.getPersistentDataContainer();

        final List<Component> lore = itemStack.lore();

        if (lore != null) {
            for (final CEnchantment enchant : enchants) {
                lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).contains(ColorUtils.stripStringColour(enchant.getCustomName())));

                itemStack.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(lore).build());
            }
        }

        Enchant data;

        if (view.has(DataKeys.enchantments.getNamespacedKey())) {
            data = Methods.getGson().fromJson(view.get(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING), Enchant.class);
        } else {
            data = new Enchant(new HashMap<>());
        }

        enchants.forEach(enchant -> data.removeEnchantment(enchant.getName()));

        if (data.isEmpty() && view.has(DataKeys.enchantments.getNamespacedKey())) {
            itemStack.editPersistentDataContainer(container -> {
                container.remove(DataKeys.enchantments.getNamespacedKey());
            });
        } else {
            itemStack.editPersistentDataContainer(container -> {
                container.set(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING, Methods.getGson().toJson(data));
            });
        }
    }
}
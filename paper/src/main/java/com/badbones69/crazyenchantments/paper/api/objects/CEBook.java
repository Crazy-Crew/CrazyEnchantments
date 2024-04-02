package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.EnchantedBook;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.google.gson.Gson;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CEBook {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this.starter.getMethods();

    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private CEnchantment enchantment;
    private int amount;
    private int level;
    private boolean glowing;
    private int destroyRate;
    private int successRate;
    
    /**
     * @param enchantment Enchantment you want.
     */
    public CEBook(CEnchantment enchantment) {
        this(enchantment, 1, 1);
    }
    
    /**
     * @param enchantment Enchantment you want.
     * @param level Tier of the enchantment.
     */
    public CEBook(CEnchantment enchantment, int level) {
        this(enchantment, level, 1);
    }
    
    /**
     * @param enchantment Enchantment you want.
     * @param level Tier of the enchantment.
     * @param amount Amount of books you want.
     */
    public CEBook(CEnchantment enchantment, int level, int amount) {
        this.enchantment = enchantment;
        this.amount = amount;
        this.level = level;

        FileConfiguration config = Files.CONFIG.getFile();

        this.glowing = config.getBoolean("Settings.Enchantment-Book-Glowing", true);
        int successMax = config.getInt("Settings.BlackScroll.SuccessChance.Max", 100);
        int successMin = config.getInt("Settings.BlackScroll.SuccessChance.Min", 15);
        int destroyMax = config.getInt("Settings.BlackScroll.DestroyChance.Max", 100);
        int destroyMin = config.getInt("Settings.BlackScroll.DestroyChance.Min", 15);
        this.destroyRate = this.methods.percentPick(destroyMax, destroyMin);
        this.successRate = this.methods.percentPick(successMax, successMin);
    }
    
    /**
     * @param enchantment Enchantment you want.
     * @param level Tier of the enchantment.
     * @param category The category for the rates.
     */
    public CEBook(CEnchantment enchantment, int level, Category category) {
        this(enchantment, level, 1, category);
    }
    
    /**
     * @param enchantment Enchantment you want.
     * @param level Tier of the enchantment.
     * @param amount Amount of books you want.
     * @param category The category for the rates.
     */
    public CEBook(CEnchantment enchantment, int level, int amount, Category category) {
        this.enchantment = enchantment;
        this.amount = amount;
        this.level = level;
        this.glowing = Files.CONFIG.getFile().getBoolean("Settings.Enchantment-Book-Glowing", true);
        this.destroyRate = this.methods.percentPick(category.getMaxDestroyRate(), category.getMinDestroyRate());
        this.successRate = this.methods.percentPick(category.getMaxSuccessRate(), category.getMinSuccessRate());
    }
    
    /**
     * @param enchantment Enchantment you want.
     * @param level Tier of the enchantment.
     * @param amount Amount of books you want.
     * @param destroyRate The rate of the destroy rate.
     * @param successRate The rate of the success rate.
     */
    public CEBook(CEnchantment enchantment, int level, int amount, int destroyRate, int successRate) {
        this.enchantment = enchantment;
        this.amount = amount;
        this.level = level;
        this.glowing = Files.CONFIG.getFile().getBoolean("Settings.Enchantment-Book-Glowing", true);
        this.destroyRate = destroyRate;
        this.successRate = successRate;
    }
    
    /**
     * Get the CEEnchantment.
     * @return The CEEnchantment.
     */
    public CEnchantment getEnchantment() {
        return this.enchantment;
    }
    
    /**
     * @param enchantment Set the enchantment.
     */
    public CEBook setEnchantment(CEnchantment enchantment) {
        this.enchantment = enchantment;

        return this;
    }
    
    /**
     * If the item will be glowing or not.
     * @return True if glowing and false if not.
     */
    public boolean getGlowing() {
        return this.glowing;
    }
    
    /**
     * @param toggle Toggle on or off the glowing effect.
     */
    public CEBook setGlowing(boolean toggle) {
        this.glowing = toggle;

        return this;
    }
    
    /**
     * Get the amount of the item.
     * @return The amount that it will be as an ItemStack.
     */
    public int getAmount() {
        return this.amount;
    }
    
    /**
     * @param amount Set the amount of books.
     */
    public CEBook setAmount(int amount) {
        this.amount = amount;

        return this;
    }
    
    /**
     * Get the level of the book.
     * @return The level of the book.
     */
    public int getLevel() {
        return this.level;
    }
    
    /**
     * @param level Set the tier of the enchantment.
     */
    public CEBook setLevel(int level) {
        this.level = level;

        return this;
    }
    
    /**
     * Get the destroy rate on the book.
     * @return Destroy rate of the book.
     */
    public int getDestroyRate() {
        return this.destroyRate;
    }
    
    /**
     * @param destroyRate Set the destroy rate on the book.
     */
    public CEBook setDestroyRate(int destroyRate) {
        this.destroyRate = destroyRate;

        return this;
    }
    
    /**
     * Get the success rate on the book.
     * @return The success rate of the book.
     */
    public int getSuccessRate() {
        return this.successRate;
    }
    
    /**
     * @param successRate Set the success rate on the book.
     */
    public CEBook setSuccessRate(int successRate) {
        this.successRate = successRate;

        return this;
    }
    
    /**
     * @return Return the book as an ItemBuilder.
     */
    public ItemBuilder getItemBuilder() {
        String name = this.enchantment.getCustomName() + " " + NumberUtils.convertLevelString(level);
        List<String> lore = new ArrayList<>();

        for (String bookLine : Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore")) {
            if (bookLine.contains("%Description%") || bookLine.contains("%description%")) {
                for (String enchantmentLine : this.enchantment.getInfoDescription()) {
                    lore.add(ColorUtils.color(enchantmentLine));
                }
            } else {
                lore.add(ColorUtils.color(bookLine)
                .replace("%Destroy_Rate%", String.valueOf(this.destroyRate)).replace("%destroy_rate%", String.valueOf(this.destroyRate))
                .replace("%Success_Rate%", String.valueOf(this.successRate)).replace("%success_rate%", String.valueOf(this.successRate)));
            }
        }

        return this.enchantmentBookSettings.getNormalBook().setAmount(this.amount).setName(name).setLore(lore).setGlow(this.glowing);
    }
    
    /**
     * @return Return the book as an ItemStack.
     */
    public ItemStack buildBook() {
        ItemStack item = getItemBuilder().build(); //TODO Directly set data instead of reconstructing the item.
        ItemMeta meta = item.getItemMeta();

        // PDC Start
        Gson gson = new Gson();

        String data = gson.toJson(new EnchantedBook(this.enchantment.getName(), this.successRate, this.destroyRate, this.level), EnchantedBook.class);

        meta.getPersistentDataContainer().set(DataKeys.stored_enchantments.getNamespacedKey(), PersistentDataType.STRING, data);
        // PDC End
        item.setItemMeta(meta);

        return item;
    }

    /**
     *
     * @return True if the success rate was successful.
     */
    public boolean roleSuccess() {
        return methods.randomPicker(this.getSuccessRate(), 100);
    }

    /**
     *
     * @return True if the destroy rate was successful.
     */
    public boolean roleDestroy() {
        return methods.randomPicker(this.getSuccessRate(), 100);
    }

}
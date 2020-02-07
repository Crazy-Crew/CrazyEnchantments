package me.badbones69.crazyenchantments.api.objects;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CEBook {
    
    private CEnchantment enchantment;
    private int amount;
    private int level;
    private boolean glowing;
    private int destroyRate;
    private int successRate;
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    
    /**
     *
     * @param enchantment Enchantment you want.
     */
    public CEBook(CEnchantment enchantment) {
        this(enchantment, 1, 1);
    }
    
    /**
     *
     * @param enchantment Enchantment you want.
     * @param level Tier of the enchantment.
     */
    public CEBook(CEnchantment enchantment, int level) {
        this(enchantment, level, 1);
    }
    
    /**
     *
     * @param enchantment Enchantment you want.
     * @param level Tier of the enchantment.
     * @param amount Amount of books you want.
     */
    public CEBook(CEnchantment enchantment, int level, int amount) {
        this.enchantment = enchantment;
        this.amount = amount;
        this.level = level;
        this.glowing = Files.CONFIG.getFile().getBoolean("Settings.Enchantment-Book-Glowing");
        int successMax = Files.CONFIG.getFile().getInt("Settings.BlackScroll.SuccessChance.Max");
        int successMin = Files.CONFIG.getFile().getInt("Settings.BlackScroll.SuccessChance.Min");
        int destroyMax = Files.CONFIG.getFile().getInt("Settings.BlackScroll.DestroyChance.Max");
        int destoryMin = Files.CONFIG.getFile().getInt("Settings.BlackScroll.DestroyChance.Min");
        this.destroyRate = percentPick(destroyMax, destoryMin);
        this.successRate = percentPick(successMax, successMin);
    }
    
    /**
     *
     * @param enchantment Enchantment you want.
     * @param level Tier of the enchantment.
     * @param category The category for the rates.
     */
    public CEBook(CEnchantment enchantment, int level, Category category) {
        this(enchantment, level, 1, category);
    }
    
    /**
     *
     * @param enchantment Enchantment you want.
     * @param level Tier of the enchantment.
     * @param amount Amount of books you want.
     * @param category The category for the rates.
     */
    public CEBook(CEnchantment enchantment, int level, int amount, Category category) {
        this.enchantment = enchantment;
        this.amount = amount;
        this.level = level;
        this.glowing = Files.CONFIG.getFile().getBoolean("Settings.Enchantment-Book-Glowing");
        this.destroyRate = percentPick(category.getMaxDestroyRate(), category.getMinDestroyRate());
        this.successRate = percentPick(category.getMaxSuccessRate(), category.getMinSuccessRate());
    }
    
    /**
     *
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
        this.glowing = Files.CONFIG.getFile().getBoolean("Settings.Enchantment-Book-Glowing");
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
     * @param successRate Set the success rate on the book.
     */
    public CEBook setSuccessRate(int successRate) {
        this.successRate = successRate;
        return this;
    }
    
    /**
     *
     * @return Return the book as an ItemBuilder.
     */
    public ItemBuilder getItemBuilder() {
        String name = enchantment.getBookColor() + enchantment.getCustomName() + " " + ce.convertLevelString(level);
        List<String> lore = new ArrayList<>();
        for (String bookLine : Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore")) {
            if (bookLine.contains("%Description%") || bookLine.contains("%description%")) {
                for (String enchantmentLine : enchantment.getInfoDescription()) {
                    lore.add(Methods.color(enchantmentLine));
                }
            } else {
                lore.add(Methods.color(bookLine)
                .replace("%Destroy_Rate%", destroyRate + "").replace("%destroy_rate%", destroyRate + "")
                .replace("%Success_Rate%", successRate + "").replace("%success_rate%", successRate + ""));
            }
        }
        return ce.getEnchantmentBook().setAmount(amount).setName(name).setLore(lore).setGlowing(glowing);
    }
    
    /**
     *
     * @return Return the book as an ItemStack.
     */
    public ItemStack buildBook() {
        return getItemBuilder().build();
    }
    
    private int percentPick(int max, int min) {
        return max == min ? max : min + new Random().nextInt(max - min);
    }
    
}
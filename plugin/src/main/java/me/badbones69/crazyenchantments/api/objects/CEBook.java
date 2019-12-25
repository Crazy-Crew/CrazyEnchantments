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
    private int destroy_rate;
    private int success_rate;
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
        int Smax = Files.CONFIG.getFile().getInt("Settings.BlackScroll.SuccessChance.Max");
        int Smin = Files.CONFIG.getFile().getInt("Settings.BlackScroll.SuccessChance.Min");
        int Dmax = Files.CONFIG.getFile().getInt("Settings.BlackScroll.DestroyChance.Max");
        int Dmin = Files.CONFIG.getFile().getInt("Settings.BlackScroll.DestroyChance.Min");
        this.destroy_rate = percentPick(Dmax, Dmin);
        this.success_rate = percentPick(Smax, Smin);
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
        this.destroy_rate = percentPick(category.getMaxDestroyRate(), category.getMinDestroyRate());
        this.success_rate = percentPick(category.getMaxSuccessRate(), category.getMinSuccessRate());
    }
    
    /**
     *
     * @param enchantment Enchantment you want.
     * @param level Tier of the enchantment.
     * @param amount Amount of books you want.
     * @param destroy_rate The rate of the destroy rate.
     * @param success_rate The rate of the success rate.
     */
    public CEBook(CEnchantment enchantment, int level, int amount, int destroy_rate, int success_rate) {
        this.enchantment = enchantment;
        this.amount = amount;
        this.level = level;
        this.glowing = Files.CONFIG.getFile().getBoolean("Settings.Enchantment-Book-Glowing");
        this.destroy_rate = destroy_rate;
        this.success_rate = success_rate;
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
        return this.destroy_rate;
    }
    
    /**
     *
     * @param destroy_rate Set the destroy rate on the book.
     */
    public CEBook setDestroyRate(int destroy_rate) {
        this.destroy_rate = destroy_rate;
        return this;
    }
    
    /**
     * Get the success rate on the book.
     * @return The success rate of the book.
     */
    public int getSuccessRate() {
        return this.success_rate;
    }
    
    /**
     *
     * @param success_rate Set the success rate on the book.
     */
    public CEBook setSuccessRate(int success_rate) {
        this.success_rate = success_rate;
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
                .replaceAll("%Destroy_Rate%", destroy_rate + "").replaceAll("%destroy_rate%", destroy_rate + "")
                .replaceAll("%Success_Rate%", success_rate + "").replaceAll("%success_rate%", success_rate + ""));
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
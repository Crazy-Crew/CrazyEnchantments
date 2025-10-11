package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.EnchantedBook;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.badbones69.crazyenchantments.paper.managers.configs.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CEBook {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CrazyInstance instance = this.plugin.getInstance();

    private final ConfigManager options = this.plugin.getOptions();

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this.starter.getMethods();

    private CEnchantment enchantment;
    private int amount;
    private int level;
    private int destroyRate;
    private int successRate;
    
    /**
     * @param enchantment Enchantment you want.
     */
    public CEBook(@NotNull final CEnchantment enchantment) {
        this(enchantment, 1, 1);
    }
    
    /**
     * @param enchantment Enchantment you want.
     * @param level Tier of the enchantment.
     */
    public CEBook(@NotNull final CEnchantment enchantment, final int level) {
        this(enchantment, level, 1);
    }
    
    /**
     * @param enchantment Enchantment you want.
     * @param level Tier of the enchantment.
     * @param amount Amount of books you want.
     */
    public CEBook(@Nullable final CEnchantment enchantment, final int level, final int amount) {
        this.enchantment = enchantment;
        this.amount = amount;
        this.level = level;

        final YamlConfiguration config = FileKeys.config.getYamlConfiguration();

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
    public CEBook(@NotNull final CEnchantment enchantment, final int level, @NotNull final Category category) {
        this(enchantment, level, 1, category);
    }
    
    /**
     * @param enchantment Enchantment you want.
     * @param level Tier of the enchantment.
     * @param amount Amount of books you want.
     * @param category The category for the rates.
     */
    public CEBook(@NotNull final CEnchantment enchantment, final int level, final int amount, @NotNull final Category category) {
        this.destroyRate = this.methods.percentPick(category.getMaxDestroyRate(), category.getMinDestroyRate());
        this.successRate = this.methods.percentPick(category.getMaxSuccessRate(), category.getMinSuccessRate());
        this.enchantment = enchantment;
        this.amount = amount;
        this.level = level;
    }
    
    /**
     * @param enchantment Enchantment you want.
     * @param level Tier of the enchantment.
     * @param amount Amount of books you want.
     * @param destroyRate The rate of the destroy rate.
     * @param successRate The rate of the success rate.
     */
    public CEBook(@NotNull final CEnchantment enchantment, final int level, final int amount, final int destroyRate, final int successRate) {
        this.enchantment = enchantment;
        this.destroyRate = destroyRate;
        this.successRate = successRate;
        this.amount = amount;
        this.level = level;
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
    public CEBook setEnchantment(@NotNull final CEnchantment enchantment) {
        this.enchantment = enchantment;

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
    public CEBook setAmount(final int amount) {
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
    public CEBook setLevel(final int level) {
        this.level = level;

        return this;
    }
    
    /**
     * Get the destroy rate on the book.
     * @return Destroy rate of the book or the override value if set in config.yml
     */
    public int getDestroyRate() {
        return this.options.getFailureOverride() == -1 ? this.destroyRate : this.options.getFailureOverride();
    }
    
    /**
     * @param destroyRate Set the destroy rate on the book.
     */
    public CEBook setDestroyRate(final int destroyRate) {
        this.destroyRate = destroyRate;

        return this;
    }
    
    /**
     * Get the success rate on the book.
     * @return The success rate of the book or the override value if set in config.yml.
     */
    public int getSuccessRate() {
        return this.options.getSuccessOverride() == -1 ? this.successRate : this.options.getSuccessOverride();
    }
    
    /**
     * @param successRate Set the success rate on the book.
     */
    public CEBook setSuccessRate(final int successRate) {
        this.successRate = successRate;

        return this;
    }
    
    /**
     * @return Return the book as an ItemBuilder.
     */
    public ItemBuilder getItemBuilder() {
        String name = this.enchantment.getCustomName() + " " + NumberUtils.convertLevelString(this.level);

        List<String> lore = new ArrayList<>();

        final YamlConfiguration configuration = FileKeys.config.getYamlConfiguration();

        for (String bookLine : configuration.getStringList("Settings.EnchantmentBookLore")) {
            if (bookLine.isEmpty()) continue;

            if (bookLine.contains("%Description%") || bookLine.contains("%description%")) {
                for (String enchantmentLine : this.enchantment.getInfoDescription()) {
                    //lore.add(ColorUtils.color(enchantmentLine)); //todo() legacy trash
                }
            } else {
                //lore.add(ColorUtils.color(bookLine)
                //.replace("%Destroy_Rate%", String.valueOf(this.destroyRate)).replace("%destroy_rate%", String.valueOf(this.destroyRate)) //todo() legacy trash
                //.replace("%Success_Rate%", String.valueOf(this.successRate)).replace("%success_rate%", String.valueOf(this.successRate))); //todo() legacy trash
            }
        }

        return this.instance.getEnchantmentBookBuilder().setAmount(this.amount).setName(name).setLore(lore);
    }
    
    /**
     * @return Return the book as an ItemStack.
     */
    public ItemStack buildBook() {
        return getItemBuilder().addKey(DataKeys.stored_enchantments.getNamespacedKey(), Methods.getGson()
                .toJson(new EnchantedBook(this.enchantment.getName(), this.successRate, this.destroyRate, this.level), EnchantedBook.class)).build();
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
        return methods.randomPicker(this.getDestroyRate(), 100);
    }

}
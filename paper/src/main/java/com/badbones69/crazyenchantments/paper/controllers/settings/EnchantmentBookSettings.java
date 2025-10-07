package com.badbones69.crazyenchantments.paper.controllers.settings;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.Enchant;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.LostBook;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.google.common.collect.Lists;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EnchantmentBookSettings {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final FusionPaper fusion = this.plugin.getFusion();

    private final List<Category> categories = Lists.newArrayList();

    /**
     *
     * @return True if unsafe enchantments are enabled.
     */
    public boolean useUnsafeEnchantments() {
        final YamlConfiguration config = FileKeys.config.getConfiguration();

        return config.getBoolean("Settings.EnchantmentOptions.UnSafe-Enchantments", true);
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
        final YamlConfiguration config = FileKeys.config.getConfiguration();

        final ConfigurationSection section = config.getConfigurationSection("Categories");

        if (section == null) {
            this.fusion.log("warn", "The categories section cannot be found in config.yml, It's possible the file is badly formatted!");

            return;
        }

        for (String category : section.getKeys(false)) {
            String path = "Categories." + category;

            LostBook lostBook = new LostBook(
                    config.getInt(path + ".LostBook.Slot"),
                    config.getBoolean(path + ".LostBook.InGUI"),
                    new ItemBuilder()
                            .setMaterial(config.getString(path + ".LostBook.Item", "BOOK"))
                            .setPlayerName(config.getString(path + ".LostBook.Player", ""))
                            .setName(config.getString(path + ".LostBook.Name", "Error getting name."))
                            .setLore(config.getStringList(path + ".LostBook.Lore"))
                            .setGlow(config.getBoolean(path + ".LostBook.Glowing", true)),
                    config.getInt(path + ".LostBook.Cost"),
                    Currency.getCurrency(config.getString(path + ".LostBook.Currency", "XP_LEVEL")),
                    config.getBoolean(path + ".LostBook.FireworkToggle", false),
                    getColors(config.getString(path + ".LostBook.FireworkColors", "Red, White, Blue")),
                    config.getBoolean(path + ".LostBook.Sound-Toggle", false),
                    config.getString(path + ".LostBook.Sound", ""));

            this.categories.add(new Category(
                    category,
                    config.getInt(path + ".Slot"),
                    config.getBoolean(path + ".InGUI", true),
                    new ItemBuilder()
                            .setMaterial(config.getString(path + ".Item", ColorUtils.getRandomPaneColor().getName()))
                            .setPlayerName(config.getString(path + ".Player", ""))
                            .setName(config.getString(path + ".Name", "Error getting name."))
                            .setLore(config.getStringList(path + ".Lore"))
                            .setGlow(config.getBoolean(path + ".Glowing", false)),
                    config.getInt(path + ".Cost"),
                    Currency.getCurrency(config.getString(path + ".Currency", "XP_LEVEL")),
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
    public Category getCategory(@NotNull final String name) {
        for (Category category : this.categories) {
            if (category.getName().equalsIgnoreCase(name)) return category;
        }

        return null;
    }

    private List<Color> getColors(@NotNull final String string) {
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
}
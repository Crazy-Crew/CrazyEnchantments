package com.badbones69.crazyenchantments.paper.managers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.managers.currency.enums.Currency;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.LostBook;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CategoryManager {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final FusionPaper fusion = this.plugin.getFusion();

    private final Map<String, Category> categories = new HashMap<>();

    public void init() {
        final ConfigurationSection section = FileKeys.config.getPaperConfiguration().getConfigurationSection("Categories");

        if (section == null) {
            this.fusion.log("warn", "The categories section cannot be found in config.yml, It's possible the file is badly formatted!");

            return;
        }

        this.categories.clear();

        for (final String category : section.getKeys(false)) {
            final ConfigurationSection config = section.getConfigurationSection(category);

            if (config == null) continue;

            final int lostBookSlot = config.getInt("LostBook.Slot", -1);

            if (lostBookSlot <= 0 || lostBookSlot > 54) {
                this.fusion.log("warn", "Lost Book slot for %s must not be less than 1, or greater than 54".formatted(category));

                continue;
            }

            final int slot = config.getInt("Slot", -1);

            if (slot <= 0 || slot > 54) {
                this.fusion.log("warn", "Category slot for %s must not be less than 1, or greater than 54".formatted(category));

                continue;
            }

            final LostBook book = new LostBook(
                    lostBookSlot,
                    config.getBoolean("LostBook.InGUI"),
                    new ItemBuilder()
                            .setMaterial(config.getString("LostBook.Item", "BOOK"))
                            .setPlayerName(config.getString("LostBook.Player", ""))
                            .setName(config.getString("LostBook.Name", "Error getting name."))
                            .setLore(config.getStringList("LostBook.Lore"))
                            .setGlow(config.getBoolean("LostBook.Glowing", true)),
                    config.getInt("LostBook.Cost"),
                    Currency.getCurrency(config.getString("LostBook.Currency", "XP_LEVEL")),
                    config.getBoolean("LostBook.FireworkToggle", false),
                    ColorUtils.getColors(config.getString("LostBook.FireworkColors", "Red, White, Blue")),
                    config.getBoolean("LostBook.Sound-Toggle", false),
                    config.getString("LostBook.Sound", "")
            );

            addCategory(new Category(
                    category,
                    slot,
                    config.getBoolean("InGUI", true),
                    new ItemBuilder()
                            .setMaterial(config.getString("Item", ColorUtils.getRandomPaneColor().getName()))
                            .setPlayerName(config.getString("Player", ""))
                            .setName(config.getString("Name", "Error getting name."))
                            .setLore(config.getStringList("Lore"))
                            .setGlow(config.getBoolean("Glowing", false)),
                    config.getInt("Cost", 20),
                    Currency.getCurrency(config.getString("Currency", "XP_LEVEL")),
                    config.getInt("Rarity", 1),
                    book,
                    config.getInt("EnchOptions.SuccessPercent.Max", 90),
                    config.getInt("EnchOptions.SuccessPercent.Min", 40),
                    config.getInt("EnchOptions.DestroyPercent.Max", 10),
                    config.getInt("EnchOptions.DestroyPercent.Min", 0),
                    config.getBoolean("EnchOptions.MaxLvlToggle", true),
                    config.getInt("EnchOptions.LvlRange.Max", 2),
                    config.getInt("EnchOptions.LvlRange.Min", 1))
            );
        }
    }

    public void addCategory(@NotNull final Category category) {
        this.categories.put(category.getName(), category);
    }

    public @NotNull final Optional<Category> getCategory(@NotNull final String name) {
        return Optional.of(this.categories.get(name));
    }

    public @NotNull final Map<String, Category> getCategories() {
        return Collections.unmodifiableMap(this.categories);
    }
}
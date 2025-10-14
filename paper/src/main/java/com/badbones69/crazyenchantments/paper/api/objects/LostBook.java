package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LostBook {

    private final int slot;
    private final boolean inGUI;
    private final ItemBuilder displayItem;
    private final int cost;
    private final Currency currency;
    private final boolean useFirework;
    private final List<Color> fireworkColors;
    private final boolean useSound;
    private Sound sound;

    public LostBook(final int slot, final boolean inGUI, @NotNull final ItemBuilder displayItem, final int cost, @NotNull final Currency currency,
                    final boolean useFirework, @NotNull final List<Color> fireworkColors, final boolean useSound, @NotNull final String sound) {
        this.slot = slot - 1;
        this.inGUI = inGUI;
        this.displayItem = displayItem;
        this.cost = cost;
        this.currency = currency;
        this.useFirework = !fireworkColors.isEmpty() && useFirework;
        this.fireworkColors = fireworkColors;

        try { // If the sound doesn't exist it will not error.
            this.sound = Sound.valueOf(sound); //todo() deprecated
        } catch (Exception exception) {
            this.sound = Sound.BLOCK_ANVIL_PLACE;
        }

        this.useSound = !sound.isEmpty() && useSound;
    }
    
    public int getSlot() {
        return this.slot;
    }
    
    public boolean isInGUI() {
        return this.inGUI;
    }
    
    public ItemBuilder getDisplayItem() {
        return this.displayItem;
    }
    
    public int getCost() {
        return this.cost;
    }
    
    public Currency getCurrency() {
        return this.currency;
    }

    public boolean useFirework() {
        return this.useFirework;
    }
    
    public List<Color> getFireworkColors() {
        return this.fireworkColors;
    }
    
    public boolean playSound() {
        return this.useSound;
    }
    
    public Sound getSound() {
        return this.sound;
    }
    
    public ItemBuilder getLostBook(@NotNull final Category category) {
        return getLostBook(category, 1);
    }

    public ItemBuilder getLostBook(@NotNull final Category category, final int amount) {
        final YamlConfiguration configuration = FileKeys.config.getYamlConfiguration();

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%Category%", category.getDisplayItem().getName());

        return new ItemBuilder()
                .setMaterial(configuration.getString("Settings.LostBook.Item", "BOOK"))
                .setAmount(amount)
                .setName(configuration.getString("Settings.LostBook.Name", "<dark_gray><bold><u>A Lost %category%<dark_gray><bold><u> Book"))
                .setNamePlaceholders(placeholders)
                .setLore(configuration.getStringList("Settings.LostBook.Lore"))
                .setLorePlaceholders(placeholders)
                .addKey(DataKeys.lost_book.getNamespacedKey(), category.getName());
    }
}
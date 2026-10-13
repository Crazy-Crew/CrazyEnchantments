package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.ryderbelserion.common.api.enums.Files;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.ryderbelserion.fusion.api.FusionProvider;
import com.ryderbelserion.fusion.api.enums.Level;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LostBook {

    private final FusionPaper fusion = (FusionPaper) FusionProvider.api();

    private final int slot;
    private final boolean inGUI;
    private final ItemBuilder displayItem;
    private final int cost;
    private final Currency currency;
    private final boolean useFirework;
    private final List<Color> fireworkColors;
    private final boolean useSound;
    private Sound sound;

    public LostBook(int slot, boolean inGUI, ItemBuilder displayItem, int cost, Currency currency,
    boolean useFirework, List<Color> fireworkColors, boolean useSound, String sound) {
        this.slot = slot - 1;
        this.inGUI = inGUI;
        this.displayItem = displayItem;
        this.cost = cost;
        this.currency = currency;
        this.useFirework = !fireworkColors.isEmpty() && useFirework;
        this.fireworkColors = fireworkColors;

        try { // If the sound doesn't exist it will not error.
            this.sound = Sound.valueOf(sound);
        } catch (final Exception exception) {
            this.fusion.log(Level.warn, "The sound %s is not a valid sound!", sound);

            this.sound = null;
        }

        this.useSound = sound != null && useSound;
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
    
    public ItemBuilder getLostBook(Category category) {
        return getLostBook(category, 1);
    }

    public ItemBuilder getLostBook(Category category, int amount) {
        final CommentedConfigurationNode configuration = Files.CONFIG.getConfiguration();
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%Category%", category.getDisplayItem().getName());

        final CommentedConfigurationNode section = configuration.node("Settings", "LostBook");

        return new ItemBuilder()
                .setMaterial(section.node("Item").getString("BOOK"))
                .setItemModel(section.node("Model", "Namespace").getString(""), section.node("Model", "Key").getString(""))
                .setAmount(amount)
                .setName(section.node("Name").getString("&cError getting LostBook name."))
                .setLore(StringUtils.getStringList(section.node("Lore")))
                .setNamePlaceholders(placeholders)
                .setLorePlaceholders(placeholders)
                .addKey(DataKeys.lost_book.getNamespacedKey(), category.getName());
    }
}
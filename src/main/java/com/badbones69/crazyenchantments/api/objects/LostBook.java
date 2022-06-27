package com.badbones69.crazyenchantments.api.objects;

import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.economy.Currency;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;
import java.util.List;

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
        } catch (Exception e) {
            Bukkit.getLogger().info(("The sound " + sound + " is not a sound found in this minecraft version."));
            this.sound = null;
        }

        this.useSound = sound != null && useSound;
    }
    
    public int getSlot() {
        return slot;
    }
    
    public boolean isInGUI() {
        return inGUI;
    }
    
    public ItemBuilder getDisplayItem() {
        return displayItem;
    }
    
    public int getCost() {
        return cost;
    }
    
    public Currency getCurrency() {
        return currency;
    }
    
    public boolean useFirework() {
        return useFirework;
    }
    
    public List<Color> getFireworkColors() {
        return fireworkColors;
    }
    
    public boolean playSound() {
        return useSound;
    }
    
    public Sound getSound() {
        return sound;
    }
    
    public ItemBuilder getLostBook(Category category) {
        return getLostBook(category, 1);
    }
    
    public ItemBuilder getLostBook(Category category, int amount) {
        FileConfiguration file = Files.CONFIG.getFile();
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%Category%", category.getDisplayItem().getName());
        return new ItemBuilder()
        .setMaterial(file.getString("Settings.LostBook.Item"))
        .setAmount(amount)
        .setName(file.getString("Settings.LostBook.Name"))
        .setNamePlaceholders(placeholders)
        .setLore(file.getStringList("Settings.LostBook.Lore"))
        .setLorePlaceholders(placeholders);
    }
}
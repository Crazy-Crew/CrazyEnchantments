package me.badbones69.crazyenchantments.api.enums;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum Dust {
    
    SUCCESS_DUST("Success-Dust", "SuccessDust", Arrays.asList("s", "success")),
    DESTROY_DUST("Destroy-Dust", "DestroyDust", Arrays.asList("d", "destroy")),
    MYSTERY_DUST("Mystery-Dust", "MysteryDust", Arrays.asList("m", "mystery")),
    FAILED_DUST("Failed-Dust", "FailedDust", Arrays.asList("f", "failed"));
    
    private static HashMap<Dust, ItemBuilder> itemBuilderDust = new HashMap<>();
    private String name;
    private String configName;
    private List<String> knownNames;
    private int max;
    private int min;
    
    private Dust(String name, String configName, List<String> knowNames) {
        this.name = name;
        this.knownNames = knowNames;
        this.configName = configName;
        this.max = Files.CONFIG.getFile().getInt("Settings.Dust." + configName + ".PercentRange.Max", 100);
        this.min = Files.CONFIG.getFile().getInt("Settings.Dust." + configName + ".PercentRange.Min", max);
    }
    
    public static void loadDust() {
        FileConfiguration config = Files.CONFIG.getFile();
        itemBuilderDust.clear();
        for (Dust dust : values()) {
            String path = "Settings.Dust." + dust.getConfigName() + ".";
            Dust.itemBuilderDust.put(dust, new ItemBuilder()
            .setName(config.getString(path + "Name"))
            .setLore(config.getStringList(path + "Lore"))
            .setMaterial(config.getString(path + "Item")));
        }
    }
    
    public static Dust getFromName(String nameString) {
        for (Dust dust : Dust.values()) {
            if (dust.getKnownNames().contains(nameString.toLowerCase())) {
                return dust;
            }
        }
        return null;
    }
    
    public String getName() {
        return name;
    }
    
    public List<String> getKnownNames() {
        return knownNames;
    }
    
    public String getConfigName() {
        return configName;
    }
    
    public ItemStack getDust() {
        return getDust(1);
    }
    
    public ItemStack getDust(int amount) {
        return getDust(Methods.percentPick(max, min), amount);
    }
    
    public ItemStack getDust(int percent, int amount) {
        return itemBuilderDust.get(this)
        .addLorePlaceholder("%Percent%", percent)
        .setAmount(amount).build();
    }
    
}
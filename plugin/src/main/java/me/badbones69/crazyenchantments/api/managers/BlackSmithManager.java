package me.badbones69.crazyenchantments.api.managers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.currencyapi.Currency;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class BlackSmithManager {
    
    private static BlackSmithManager instance = new BlackSmithManager();
    private ItemStack denyBarrier;
    private ItemStack redGlass;
    private ItemStack blueGlass;
    private ItemStack grayGlass;
    private String menuName;
    private String foundString;
    private Currency currency;
    private int bookUpgrade;
    private int levelUp;
    private int addEnchantment;
    private boolean maxEnchantments;
    
    public static BlackSmithManager getInstance() {
        return instance;
    }
    
    public void load() {
        FileConfiguration config = Files.CONFIG.getFile();
        denyBarrier = new ItemBuilder()
        .setMaterial(Material.BARRIER)
        .setName(config.getString("Settings.BlackSmith.Results.None"))
        .setLore(config.getStringList("Settings.BlackSmith.Results.Not-Found-Lore"))
        .build();
        redGlass = new ItemBuilder().setMaterial("RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:14").setName(" ").build();
        grayGlass = new ItemBuilder().setMaterial("GRAY_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:7").setName(" ").build();
        blueGlass = new ItemBuilder().setMaterial("LIGHT_BLUE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:3").build();
        menuName = Methods.color(config.getString("Settings.BlackSmith.GUIName"));
        foundString = config.getString("Settings.BlackSmith.Results.Found");
        currency = Currency.getCurrency(config.getString("Settings.BlackSmith.Transaction.Currency"));
        bookUpgrade = config.getInt("Settings.BlackSmith.Transaction.Costs.Book-Upgrade");
        levelUp = config.getInt("Settings.BlackSmith.Transaction.Costs.Power-Up");
        addEnchantment = config.getInt("Settings.BlackSmith.Transaction.Costs.Add-Enchantment");
        maxEnchantments = config.getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle");
    }
    
    public ItemStack getDenyBarrier() {
        return denyBarrier;
    }
    
    public ItemStack getRedGlass() {
        return redGlass;
    }
    
    public ItemStack getGrayGlass() {
        return grayGlass;
    }
    
    public ItemStack getBlueGlass() {
        return blueGlass;
    }
    
    public String getMenuName() {
        return menuName;
    }
    
    public String getFoundString() {
        return foundString;
    }
    
    public Currency getCurrency() {
        return currency;
    }
    
    public int getBookUpgrade() {
        return bookUpgrade;
    }
    
    public int getLevelUp() {
        return levelUp;
    }
    
    public int getAddEnchantment() {
        return addEnchantment;
    }
    
    public boolean useMaxEnchantments() {
        return maxEnchantments;
    }
    
}
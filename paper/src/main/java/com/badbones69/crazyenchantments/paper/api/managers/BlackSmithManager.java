package com.badbones69.crazyenchantments.paper.api.managers;

import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.paper.utilities.misc.ColorUtils;
import com.ryderbelserion.cluster.bukkit.utils.LegacyUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class BlackSmithManager {

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
    
    public void load() {
        FileConfiguration config = Files.CONFIG.getFile();
        this.denyBarrier = new ItemBuilder()
        .setMaterial(Material.BARRIER)
        .setName(config.getString("Settings.BlackSmith.Results.None", "&c&lNo Results"))
        .setLore(config.getStringList("Settings.BlackSmith.Results.Not-Found-Lore"))
        .build();
        this.redGlass = new ItemBuilder().setMaterial("RED_STAINED_GLASS_PANE").setName(" ").build();
        this.grayGlass = new ItemBuilder().setMaterial("GRAY_STAINED_GLASS_PANE").setName(" ").build();
        this.blueGlass = new ItemBuilder().setMaterial("LIGHT_BLUE_STAINED_GLASS_PANE").build();
        this.menuName = LegacyUtils.color(config.getString("Settings.BlackSmith.GUIName", "&7&lThe &b&lBlack &9&lSmith"));
        this.foundString = config.getString("Settings.BlackSmith.Results.Found", "&c&lCost: &6&l%cost%XP");
        this.currency = Currency.getCurrency(config.getString("Settings.BlackSmith.Transaction.Currency", "XP_Level"));
        this.bookUpgrade = config.getInt("Settings.BlackSmith.Transaction.Costs.Book-Upgrade", 5);
        this.levelUp = config.getInt("Settings.BlackSmith.Transaction.Costs.Power-Up", 5);
        this.addEnchantment = config.getInt("Settings.BlackSmith.Transaction.Costs.Add-Enchantment", 3);
        this.maxEnchantments = config.getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle");
    }
    
    public ItemStack getDenyBarrier() {
        return this.denyBarrier;
    }
    
    public ItemStack getRedGlass() {
        return this.redGlass;
    }
    
    public ItemStack getGrayGlass() {
        return this.grayGlass;
    }
    
    public ItemStack getBlueGlass() {
        return this.blueGlass;
    }
    
    public String getMenuName() {
        return this.menuName;
    }
    
    public String getFoundString() {
        return this.foundString;
    }
    
    public Currency getCurrency() {
        return this.currency;
    }
    
    public int getBookUpgrade() {
        return this.bookUpgrade;
    }
    
    public int getLevelUp() {
        return this.levelUp;
    }
    
    public int getAddEnchantment() {
        return this.addEnchantment;
    }
    
    public boolean useMaxEnchantments() {
        return this.maxEnchantments;
    }
}
package me.badbones69.crazyenchantments.api.enums;

import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.currencyapi.Currency;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public enum ShopOption {
    
    GKITZ("GKitz", "GKitz", "Name", "Lore", false),
    BLACKSMITH("BlackSmith", "BlackSmith", "Name", "Lore", false),
    TINKER("Tinker", "Tinker", "Name", "Lore", false),
    INFO("Info", "Info", "Name", "Lore", false),
    
    PROTECTION_CRYSTAL("ProtectionCrystal", "ProtectionCrystal", "GUIName", "GUILore", true),
    SUCCESS_DUST("SuccessDust", "Dust.SuccessDust", "GUIName", "GUILore", true),
    DESTROY_DUST("DestroyDust", "Dust.DestroyDust", "GUIName", "GUILore", true),
    SCRAMBLER("Scrambler", "Scrambler", "GUIName", "GUILore", true),
    
    BLACK_SCROLL("BlackScroll", "BlackScroll", "GUIName", "Lore", true),
    WHITE_SCROLL("WhiteScroll", "WhiteScroll", "GUIName", "Lore", true),
    TRANSMOG_SCROLL("TransmogScroll", "TransmogScroll", "GUIName", "Lore", true);
    
    private static HashMap<ShopOption, Option> shopOptions = new HashMap<>();
    private String optionPath;
    private String path;
    private String namePath;
    private String lorePath;
    private Option option;
    private boolean buyable;
    
    private ShopOption(String optionPath, String path, String namePath, String lorePath, boolean buyable) {
        this.optionPath = optionPath;
        this.path = path;
        this.namePath = namePath;
        this.lorePath = lorePath;
        this.buyable = buyable;
    }
    
    public static void loadShopOptions() {
        FileConfiguration config = Files.CONFIG.getFile();
        shopOptions.clear();
        for (ShopOption shopOption : values()) {
            String itemPath = "Settings." + shopOption.getPath() + ".";
            String costPath = "Settings.Costs." + shopOption.getOptionPath() + ".";
            try {
                shopOptions.put(shopOption, new Option(new ItemBuilder()
                .setName(config.getString(itemPath + shopOption.getNamePath()))
                .setLore(config.getStringList(itemPath + shopOption.getLorePath()))
                .setMaterial(config.getString(itemPath + "Item"))
                .setGlowing(config.getBoolean(itemPath + "Glowing")),
                config.getInt(itemPath + "Slot", 1) - 1,
                config.getBoolean(itemPath + "InGUI"),
                config.getInt(costPath + "Cost", 100),
                Currency.getCurrency(config.getString(costPath + "Currency", "Vault"))));
            } catch (Exception e) {
                System.out.println("The option " + shopOption.getOptionPath() + " has failed to load.");
                e.printStackTrace();
            }
        }
    }
    
    public ItemStack getItem() {
        return getItemBuilder().build();
    }
    
    public ItemBuilder getItemBuilder() {
        return shopOptions.get(this).getItemBuilder();
    }
    
    public int getSlot() {
        return shopOptions.get(this).getSlot();
    }
    
    public boolean isInGUI() {
        return shopOptions.get(this).isInGUI();
    }
    
    public int getCost() {
        return shopOptions.get(this).getCost();
    }
    
    public Currency getCurrency() {
        return shopOptions.get(this).getCurrency();
    }
    
    private String getOptionPath() {
        return optionPath;
    }
    
    private String getPath() {
        return path;
    }
    
    private String getNamePath() {
        return namePath;
    }
    
    private String getLorePath() {
        return lorePath;
    }
    
    public boolean isBuyable() {
        return buyable;
    }
    
    private static class Option {
        
        private ItemBuilder itemBuilder;
        private int slot;
        private boolean inGUI;
        private int cost;
        private Currency currency;
        
        public Option(ItemBuilder itemBuilder, int slot, boolean inGUI, int cost, Currency currency) {
            this.itemBuilder = itemBuilder;
            this.slot = slot;
            this.inGUI = inGUI;
            this.cost = cost;
            this.currency = currency;
        }
        
        public ItemBuilder getItemBuilder() {
            return itemBuilder;
        }
        
        public int getSlot() {
            return slot;
        }
        
        public boolean isInGUI() {
            return inGUI;
        }
        
        public int getCost() {
            return cost;
        }
        
        public Currency getCurrency() {
            return currency;
        }
        
    }
    
}
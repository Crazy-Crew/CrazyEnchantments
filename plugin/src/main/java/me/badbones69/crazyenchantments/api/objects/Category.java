package me.badbones69.crazyenchantments.api.objects;

import me.badbones69.crazyenchantments.api.currencyapi.Currency;

import java.util.ArrayList;
import java.util.List;

public class Category {
    
    private String name;
    private int slot;
    private boolean inGUI;
    private ItemBuilder displayItem;
    private int cost;
    private Currency currency;
    private int rarity;
    private LostBook lostBook;
    private int maxSuccessRate;
    private int minSuccessRate;
    private int maxDestroyRate;
    private int minDestroyRate;
    private boolean useMaxLevel;
    private int maxLevel;
    private int minLevel;
    private List<CEnchantment> enchantmentList;
    private List<CEnchantment> enabledEnchantments;
    
    public Category(String name, int slot, boolean inGUI, ItemBuilder displayItem, int cost, Currency currency, int rarity, LostBook lostBook,
    int maxSuccessRate, int minSuccessRate, int maxDestroyRate, int minDestroyRate, boolean useMaxLevel, int maxLevel, int minLevel) {
        this.name = name;
        this.slot = slot - 1;
        this.inGUI = inGUI;
        this.displayItem = displayItem;
        this.cost = cost;
        this.currency = currency;
        this.rarity = rarity;
        this.lostBook = lostBook;
        this.maxSuccessRate = maxSuccessRate;
        this.minSuccessRate = minSuccessRate;
        this.maxDestroyRate = maxDestroyRate;
        this.minDestroyRate = minDestroyRate;
        this.useMaxLevel = useMaxLevel;
        this.maxLevel = maxLevel;
        this.minLevel = minLevel;
        this.enchantmentList = new ArrayList<>();
        this.enabledEnchantments = new ArrayList<>();
    }
    
    public String getName() {
        return name;
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
    
    public int getRarity() {
        return rarity;
    }
    
    public LostBook getLostBook() {
        return lostBook;
    }
    
    public int getMaxSuccessRate() {
        return maxSuccessRate;
    }
    
    public int getMinSuccessRate() {
        return minSuccessRate;
    }
    
    public int getMaxDestroyRate() {
        return maxDestroyRate;
    }
    
    public int getMinDestroyRate() {
        return minDestroyRate;
    }
    
    public boolean useMaxLevel() {
        return useMaxLevel;
    }
    
    public int getMaxLevel() {
        return maxLevel;
    }
    
    public int getMinLevel() {
        return minLevel;
    }
    
    public List<CEnchantment> getEnabledEnchantments() {
        return enabledEnchantments;
    }
    
    public List<CEnchantment> getEnchantmentList() {
        return enchantmentList;
    }
    
    public void addEnchantment(CEnchantment enchantment) {
        enchantmentList.add(enchantment);
        if (enchantment.isActivated()) {
            enabledEnchantments.add(enchantment);
        }
    }
    
    public void removeEnchantment(CEnchantment enchantment) {
        enchantmentList.remove(enchantment);
        enabledEnchantments.remove(enchantment);
    }
    
}
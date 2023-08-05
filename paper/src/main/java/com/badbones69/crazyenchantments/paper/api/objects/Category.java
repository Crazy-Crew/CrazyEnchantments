package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import java.util.ArrayList;
import java.util.List;

public class Category {
    
    private final String name;
    private final int slot;
    private final boolean inGUI;
    private final ItemBuilder displayItem;
    private final int cost;
    private final Currency currency;
    private final int rarity;
    private final LostBook lostBook;
    private final int maxSuccessRate;
    private final int minSuccessRate;
    private final int maxDestroyRate;
    private final int minDestroyRate;
    private final boolean useMaxLevel;
    private final int maxLevel;
    private final int minLevel;
    private final List<CEnchantment> enchantmentList;
    private final List<CEnchantment> enabledEnchantments;
    
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

        if (enchantment.isActivated()) enabledEnchantments.add(enchantment);
    }
    
    public void removeEnchantment(CEnchantment enchantment) {
        enchantmentList.remove(enchantment);
        enabledEnchantments.remove(enchantment);
    }
}
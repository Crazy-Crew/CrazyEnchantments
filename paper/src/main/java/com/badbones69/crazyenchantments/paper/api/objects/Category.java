package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import org.jetbrains.annotations.NotNull;
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
    
    public Category(@NotNull final String name, final int slot, final boolean inGUI, @NotNull final ItemBuilder displayItem,
                    final int cost, @NotNull final Currency currency, final int rarity,
                    @NotNull final LostBook lostBook, final int maxSuccessRate, final int minSuccessRate,
                    final int maxDestroyRate, final int minDestroyRate, final boolean useMaxLevel, final int maxLevel, final int minLevel) {
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
        return this.name;
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
    
    public int getRarity() {
        return this.rarity;
    }
    
    public LostBook getLostBook() {
        return this.lostBook;
    }
    
    public int getMaxSuccessRate() {
        return this.maxSuccessRate;
    }
    
    public int getMinSuccessRate() {
        return this.minSuccessRate;
    }
    
    public int getMaxDestroyRate() {
        return this.maxDestroyRate;
    }
    
    public int getMinDestroyRate() {
        return this.minDestroyRate;
    }
    
    public boolean useMaxLevel() {
        return this.useMaxLevel;
    }
    
    public int getMaxLevel() {
        return this.maxLevel;
    }
    
    public int getMinLevel() {
        return this.minLevel;
    }
    
    public List<CEnchantment> getEnabledEnchantments() {
        return this.enabledEnchantments;
    }
    
    public List<CEnchantment> getEnchantmentList() {
        return this.enchantmentList;
    }
    
    public void addEnchantment(@NotNull final CEnchantment enchantment) {
        this.enchantmentList.add(enchantment);

        if (enchantment.isActivated()) this.enabledEnchantments.add(enchantment);
    }
    
    public void removeEnchantment(@NotNull final CEnchantment enchantment) {
        this.enchantmentList.remove(enchantment);
        this.enabledEnchantments.remove(enchantment);
    }
}
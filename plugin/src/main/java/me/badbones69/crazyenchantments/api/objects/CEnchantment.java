package me.badbones69.crazyenchantments.api.objects;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.events.RegisteredCEnchantmentEvent;
import me.badbones69.crazyenchantments.api.events.UnregisterCEnchantmentEvent;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CEnchantment {
    
    private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
    
    private String name;
    private String customName;
    private boolean activated;
    private String color;
    private String bookColor;
    private int maxLevel;
    private String infoName;
    private int chance;
    private int chanceIncrease;
    private List<String> infoDescription;
    private List<Category> categories;
    private EnchantmentType enchantmentType;
    private CEnchantment instance;
    
    public CEnchantment(String name) {
        this.instance = this;
        this.name = name;
        this.customName = name;
        this.activated = true;
        this.color = "&7";
        this.bookColor = "&b&l";
        this.maxLevel = 3;
        this.infoName = "&7" + name;
        this.chance = 0;
        this.chanceIncrease = 0;
        this.infoDescription = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.enchantmentType = null;
    }
    
    public static CEnchantment getCEnchantmentFromName(String enchantment) {
        return ce.getEnchantmentFromName(enchantment);
    }
    
    public String getName() {
        return name;
    }
    
    public CEnchantment setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getCustomName() {
        return customName;
    }
    
    public CEnchantment setCustomName(String customName) {
        this.customName = customName;
        return this;
    }
    
    public boolean isActivated() {
        return activated;
    }
    
    public CEnchantment setActivated(boolean activated) {
        this.activated = activated;
        return this;
    }
    
    public String getColor() {
        return color;
    }
    
    public CEnchantment setColor(String color) {
        this.color = Methods.color(color);
        return this;
    }
    
    public String getBookColor() {
        return bookColor;
    }
    
    public CEnchantment setBookColor(String bookColor) {
        if (bookColor.startsWith("&f")) {
            bookColor = bookColor.substring(2);
        }
        this.bookColor = Methods.color(bookColor);
        return this;
    }
    
    public int getMaxLevel() {
        return maxLevel;
    }
    
    public CEnchantment setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }
    
    public String getInfoName() {
        return infoName;
    }
    
    public CEnchantment setInfoName(String infoName) {
        this.infoName = Methods.color(infoName);
        return this;
    }
    
    public int getChance() {
        return chance;
    }
    
    public CEnchantment setChance(int chance) {
        this.chance = chance;
        return this;
    }
    
    public int getChanceIncrease() {
        return chanceIncrease;
    }
    
    public CEnchantment setChanceIncrease(int chanceIncrease) {
        this.chanceIncrease = chanceIncrease;
        return this;
    }
    
    public boolean hasChanceSystem() {
        return chance > 0;
    }
    
    public boolean chanceSuccesful(int enchantmentLevel) {
        int newChance = chance + (chanceIncrease * (enchantmentLevel - 1));
        int pickedChance = new Random().nextInt(100) + 1;
        return newChance >= 100 || newChance <= 0 || pickedChance <= chance;
    }
    
    public List<String> getInfoDescription() {
        return infoDescription;
    }
    
    public CEnchantment setInfoDescription(List<String> infoDescription) {
        List<String> info = new ArrayList<>();
        for (String i : infoDescription) {
            info.add(Methods.color(i));
        }
        this.infoDescription = info;
        return this;
    }
    
    public CEnchantment addCategory(Category category) {
        if (category != null) {
            this.categories.add(category);
        }
        return this;
    }
    
    public List<Category> getCategories() {
        return categories;
    }
    
    public CEnchantment setCategories(List<String> categories) {
        for (String categoryString : categories) {
            Category category = ce.getCategory(categoryString);
            if (category != null) {
                this.categories.add(category);
            }
        }
        return this;
    }
    
    public EnchantmentType getEnchantmentType() {
        return enchantmentType;
    }
    
    public boolean canEnchantItem(ItemStack item) {
        return enchantmentType != null && enchantmentType.canEnchantItem(item);
    }
    
    public CEnchantment setEnchantmentType(EnchantmentType enchantmentType) {
        this.enchantmentType = enchantmentType;
        return this;
    }
    
    public void registerEnchantment() {
        RegisteredCEnchantmentEvent event = new RegisteredCEnchantmentEvent(instance);
        Bukkit.getPluginManager().callEvent(event);
        ce.registerEnchantment(instance);
        if (enchantmentType != null) {
            enchantmentType.addEnchantment(instance);
        }
        for (Category category : categories) {
            category.addEnchantment(instance);
        }
    }
    
    public void unregisterEnchantment() {
        UnregisterCEnchantmentEvent event = new UnregisterCEnchantmentEvent(instance);
        Bukkit.getPluginManager().callEvent(event);
        ce.unregisterEnchantment(instance);
        if (enchantmentType != null) {
            enchantmentType.removeEnchantment(instance);
        }
        for (Category category : categories) {
            category.removeEnchantment(instance);
        }
    }
    
    /**
     * @deprecated use {@link CEnchantment#getLevel(ItemStack)}.
     */
    @Deprecated
    public int getPower(ItemStack item) {
        return getLevel(item);
    }
    
    public int getLevel(ItemStack item) {
        int level = 0;
        if (Methods.verifyItemLore(item)) {
            for (String lore : item.getItemMeta().getLore()) {
                if (lore.contains(customName)) {
                    level = ce.convertLevelInteger(lore.replace(color + customName + " ", ""));
                    break;
                }
            }
        }
        if (!ce.useUnsafeEnchantments() && level > maxLevel) {
            level = maxLevel;
        }
        return level;
    }
    
}
package com.badbones69.crazyenchantments.api.objects;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.events.RegisteredCEnchantmentEvent;
import com.badbones69.crazyenchantments.api.events.UnregisterCEnchantmentEvent;
import com.badbones69.crazyenchantments.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.controllers.settings.EnchantmentBookSettings;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CEnchantment {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

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
    private final List<Category> categories;
    private EnchantmentType enchantmentType;
    private final CEnchantment instance;

    public CEnchantment(String name) {
        this.instance = this;
        this.name = name;
        this.customName = name;
        this.activated = true;
        this.color = methods.color("&7");
        this.bookColor = methods.color("&b&l");
        this.maxLevel = 3;
        this.infoName = methods.color("&7" + name);
        this.chance = 0;
        this.chanceIncrease = 0;
        this.infoDescription = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.enchantmentType = null;
    }

    public CEnchantment getCEnchantmentFromName(String enchantment) {
        return crazyManager.getEnchantmentFromName(enchantment);
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
        this.color = methods.color(color);
        return this;
    }

    public String getBookColor() {
        return bookColor;
    }

    public CEnchantment setBookColor(String bookColor) {

        if (bookColor.startsWith("&f")) bookColor = bookColor.substring(2);

        this.bookColor = methods.color(bookColor);

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
        this.infoName = methods.color(infoName);

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

    public boolean chanceSuccessful(int enchantmentLevel) {
        int newChance = chance + (chanceIncrease * (enchantmentLevel - 1));
        int pickedChance = new Random().nextInt(100) + 1;

        return newChance >= 100 || newChance <= 0 || pickedChance <= chance;
    }

    public List<String> getInfoDescription() {
        return infoDescription;
    }

    public CEnchantment setInfoDescription(List<String> infoDescription) {
        List<String> info = new ArrayList<>();

        infoDescription.forEach(lore -> info.add(methods.color(lore)));

        this.infoDescription = info;

        return this;
    }

    public CEnchantment addCategory(Category category) {
        if (category != null) this.categories.add(category);

        return this;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public CEnchantment setCategories(List<String> categories) {

        for (String categoryString : categories) {
            Category category = enchantmentBookSettings.getCategory(categoryString);

            if (category != null) this.categories.add(category);
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
        plugin.getServer().getPluginManager().callEvent(event);
        crazyManager.registerEnchantment(instance);

        if (enchantmentType != null) enchantmentType.addEnchantment(instance);

        categories.forEach(category -> category.addEnchantment(instance));
    }

    public void unregisterEnchantment() {
        UnregisterCEnchantmentEvent event = new UnregisterCEnchantmentEvent(instance);
        plugin.getServer().getPluginManager().callEvent(event);
        crazyManager.unregisterEnchantment(instance);

        if (enchantmentType != null) enchantmentType.removeEnchantment(instance);

        categories.forEach(category -> category.addEnchantment(instance));
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

        if (methods.verifyItemLore(item)) {
            for (String lore : Objects.requireNonNull(item.getItemMeta().getLore())) {
                if (lore.contains(customName)) {
                    level = crazyManager.convertLevelInteger(lore.replace(color + customName + " ", ""));
                    break;
                }
            }
        }

        if (!crazyManager.useUnsafeEnchantments() && level > maxLevel) level = maxLevel;

        return level;
    }
}
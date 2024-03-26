package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.events.RegisteredCEnchantmentEvent;
import com.badbones69.crazyenchantments.paper.api.events.UnregisterCEnchantmentEvent;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CEnchantment {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull Methods methods = this.starter.getMethods();

    private final @NotNull CrazyManager crazyManager = this.starter.getCrazyManager();

    private final @NotNull EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private final List<Category> categories;
    private final CEnchantment instance;
    private String name;
    private String customName;
    private boolean activated;
    private int maxLevel;
    private String infoName;
    private int chance;
    private int chanceIncrease;
    private List<String> infoDescription;
    private EnchantmentType enchantmentType;
    private Sound sound;

    public CEnchantment(String name) {
        this.instance = this;
        this.name = name;
        this.customName = name;
        this.activated = true;
        this.maxLevel = 3;
        //this.infoName = ColorUtils.color("&7" + name);
        this.chance = 0;
        this.chanceIncrease = 0;
        this.infoDescription = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.enchantmentType = null;
        this.sound = Sound.ENTITY_PLAYER_LEVELUP;
    }

    @NotNull
    public Sound getSound() {
        return this.sound;
    }

    public CEnchantment setSound(String soundString) {
        if (soundString == null || soundString.isBlank()) {
            this.sound = Sound.ENTITY_PLAYER_LEVELUP;
            return this;
        }

        try {
            this.sound = Sound.valueOf(soundString);
        } catch (IllegalArgumentException e) {
            //plugin.getLogger().warning(name + " has an invalid sound set.");
            this.sound = Sound.ENTITY_PLAYER_LEVELUP;
        }

        return this;
    }

    public String getName() {
        return this.name;
    }

    public CEnchantment setName(String name) {
        this.name = name;

        return this;
    }

    public String getCustomName() {
        return this.customName;
    }

    public CEnchantment setCustomName(String customName) {
        this.customName = customName;

        return this;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public CEnchantment setActivated(boolean activated) {
        this.activated = activated;

        return this;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public CEnchantment setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;

        return this;
    }

    public String getInfoName() {
        return this.infoName;
    }

    public CEnchantment setInfoName(String infoName) {
        //this.infoName = ColorUtils.color(infoName);

        return this;
    }

    public int getChance() {
        return this.chance;
    }

    public CEnchantment setChance(int chance) {
        this.chance = chance;

        return this;
    }

    public int getChanceIncrease() {
        return this.chanceIncrease;
    }

    public CEnchantment setChanceIncrease(int chanceIncrease) {
        this.chanceIncrease = chanceIncrease;

        return this;
    }

    public boolean hasChanceSystem() {
        return this.chance > 0;
    }

    public boolean chanceSuccessful(int enchantmentLevel) {
        return this.chanceSuccessful(enchantmentLevel, 1.0);
    }

    public boolean chanceSuccessful(int enchantmentLevel, double multiplier) {
        int newChance = this.chance + (this.chanceIncrease * (enchantmentLevel - 1));
        int pickedChance = this.methods.getRandomNumber(0, 100);

        newChance = (int) (newChance * multiplier);

        return newChance >= 100 || newChance <= 0 || pickedChance <= newChance;
    }

    public List<String> getInfoDescription() {
        return this.infoDescription;
    }

    public CEnchantment setInfoDescription(List<String> infoDescription) {
        List<String> info = new ArrayList<>();

        //infoDescription.forEach(lore -> info.add(ColorUtils.color(lore)));

        this.infoDescription = info;

        return this;
    }

    public CEnchantment addCategory(Category category) {
        if (category != null) this.categories.add(category);

        return this;
    }

    public List<Category> getCategories() {
        return this.categories;
    }

    public CEnchantment setCategories(List<String> categories) {

        for (String categoryString : categories) {
            Category category = this.enchantmentBookSettings.getCategory(categoryString);

            if (category != null) this.categories.add(category);
        }

        return this;
    }

    public EnchantmentType getEnchantmentType() {
        return this.enchantmentType;
    }

    public CEnchantment setEnchantmentType(EnchantmentType enchantmentType) {
        this.enchantmentType = enchantmentType;

        return this;
    }

    public boolean canEnchantItem(ItemStack item) {
        return this.enchantmentType != null && this.enchantmentType.canEnchantItem(item);
    }

    public void registerEnchantment() {
        RegisteredCEnchantmentEvent event = new RegisteredCEnchantmentEvent(this.instance);
        this.plugin.getServer().getPluginManager().callEvent(event);
        this.crazyManager.registerEnchantment(this.instance);

        if (this.enchantmentType != null) this.enchantmentType.addEnchantment(this.instance);

        this.categories.forEach(category -> category.addEnchantment(this.instance));
    }

    public void unregisterEnchantment() {
        UnregisterCEnchantmentEvent event = new UnregisterCEnchantmentEvent(this.instance);
        this.plugin.getServer().getPluginManager().callEvent(event);
        this.crazyManager.unregisterEnchantment(this.instance);

        if (this.enchantmentType != null) this.enchantmentType.removeEnchantment(this.instance);

        this.categories.forEach(category -> category.addEnchantment(this.instance));
    }
}
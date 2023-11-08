package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.events.RegisteredCEnchantmentEvent;
import com.badbones69.crazyenchantments.paper.api.events.UnregisterCEnchantmentEvent;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.utilities.misc.ColorUtils;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CEnchantment {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    private String name;
    private String customName;
    private boolean activated;
    private int maxLevel;
    private String infoName;
    private int chance;
    private int chanceIncrease;
    private List<String> infoDescription;
    private final List<Category> categories;
    private EnchantmentType enchantmentType;
    private final CEnchantment instance;
    private Sound sound;

    public CEnchantment(String name) {
        this.instance = this;
        this.name = name;
        this.customName = name;
        this.activated = true;
        this.maxLevel = 3;
        this.infoName = ColorUtils.color("&7" + name);
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
        this.infoName = ColorUtils.color(infoName);

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
        int pickedChance = methods.getRandomNumber (0, 100);

        return newChance >= 100 || newChance <= 0 || pickedChance <= chance;
    }

    public List<String> getInfoDescription() {
        return infoDescription;
    }

    public CEnchantment setInfoDescription(List<String> infoDescription) {
        List<String> info = new ArrayList<>();

        infoDescription.forEach(lore -> info.add(ColorUtils.color(lore)));

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
}
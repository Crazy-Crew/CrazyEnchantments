package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CEItem {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull CrazyManager crazyManager = this.starter.getCrazyManager();

    private final @NotNull EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private final ItemStack item;
    private final List<Enchantment> vanillaEnchantmentRemove;
    private final List<CEnchantment> cEnchantmentRemove;
    private final Map<Enchantment, Integer> vanillaEnchantments;
    private final Map<CEnchantment, Integer> cEnchantments;

    public CEItem(ItemStack item) {
        this.item = item;
        // Has to make a new map as .getEnchantments is a ImmutableMap.
        this.vanillaEnchantments = new HashMap<>(item.getEnchantments());
        EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();
        this.cEnchantments = enchantmentBookSettings.getEnchantments(item);
        this.vanillaEnchantmentRemove = new ArrayList<>();
        this.cEnchantmentRemove = new ArrayList<>();
    }

    public ItemStack getItem() {
        return this.item;
    }

    public boolean hasVanillaEnchantment(Enchantment enchantment) {
        return this.vanillaEnchantments.containsKey(enchantment);
    }

    public int getVanillaEnchantmentLevel(Enchantment enchantment) {
        return this.vanillaEnchantments.getOrDefault(enchantment, 0);
    }

    public Map<Enchantment, Integer> getVanillaEnchantments() {
        return this.vanillaEnchantments;
    }

    public void setVanillaEnchantment(Enchantment enchantment, int level) {
        this.vanillaEnchantments.put(enchantment, level);
    }

    public void removeVanillaEnchantment(Enchantment enchantment) {
        this.vanillaEnchantmentRemove.add(enchantment);
    }

    public boolean hasCEnchantment(CEnchantment enchantment) {
        return this.cEnchantments.containsKey(enchantment);
    }

    public int getCEnchantmentLevel(CEnchantment enchantment) {
        return this.cEnchantments.getOrDefault(enchantment, 0);
    }

    public Map<CEnchantment, Integer> getCEnchantments() {
        return this.cEnchantments;
    }

    public void setCEnchantment(CEnchantment enchantment, int level) {
        this.cEnchantments.put(enchantment, level);
    }

    public void removeCEnchantment(CEnchantment enchantment) {
        this.cEnchantmentRemove.add(enchantment);
    }

    public ItemStack build() {
        this.vanillaEnchantmentRemove.forEach(this.item::removeEnchantment);
        this.vanillaEnchantments.keySet().forEach(enchantment -> this.item.addUnsafeEnchantment(enchantment, this.vanillaEnchantments.get(enchantment)));
        this.cEnchantmentRemove.forEach(enchantment -> this.enchantmentBookSettings.removeEnchantment(this.item, enchantment));
        this.crazyManager.addEnchantments(this.item, this.cEnchantments);

        return this.item;
    }
}
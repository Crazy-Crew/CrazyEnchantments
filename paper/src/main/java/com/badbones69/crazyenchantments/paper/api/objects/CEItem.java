package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CEItem {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CrazyInstance instance = this.plugin.getInstance();

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    private final ItemStack item;
    private final List<Enchantment> vanillaEnchantmentRemove;
    private final List<CEnchantment> cEnchantmentRemove;
    private final Map<Enchantment, Integer> vanillaEnchantments;
    private final Map<CEnchantment, Integer> cEnchantments;
    
    public CEItem(@NotNull final ItemStack item) {
        this.item = item;
        this.vanillaEnchantments = new HashMap<>(item.getEnchantments());
        this.cEnchantments = this.instance.getEnchantments(item);
        this.vanillaEnchantmentRemove = new ArrayList<>();
        this.cEnchantmentRemove = new ArrayList<>();
    }
    
    public ItemStack getItem() {
        return this.item;
    }
    
    public boolean hasVanillaEnchantment(@NotNull final Enchantment enchantment) {
        return this.vanillaEnchantments.containsKey(enchantment);
    }
    
    public int getVanillaEnchantmentLevel(@NotNull final Enchantment enchantment) {
        return this.vanillaEnchantments.getOrDefault(enchantment, 0);
    }
    
    public Map<Enchantment, Integer> getVanillaEnchantments() {
        return this.vanillaEnchantments;
    }
    
    public void addVanillaEnchantment(@NotNull final Enchantment enchantment, final int level) {
        this.vanillaEnchantments.put(enchantment, level);
    }
    
    public void removeVanillaEnchantment(@NotNull final Enchantment enchantment) {
        this.vanillaEnchantmentRemove.add(enchantment);
    }
    
    public boolean hasCEnchantment(@NotNull final CEnchantment enchantment) {
        return this.cEnchantments.containsKey(enchantment);
    }
    
    public int getCEnchantmentLevel(@NotNull final CEnchantment enchantment) {
        return this.cEnchantments.getOrDefault(enchantment, 0);
    }
    
    public Map<CEnchantment, Integer> getCEnchantments() {
        return this.cEnchantments;
    }
    
    public void addCEnchantment(@NotNull final CEnchantment enchantment, final int level) {
        this.cEnchantments.put(enchantment, level);
    }
    
    public void removeCEnchantment(@NotNull final CEnchantment enchantment) {
        this.cEnchantmentRemove.add(enchantment);
    }

    public boolean canAddEnchantment(@NotNull final Player player) {
        return this.crazyManager.canAddEnchantment(player, this.cEnchantments.size(), this.vanillaEnchantments.size());
    }

    public ItemStack build() {
        this.vanillaEnchantmentRemove.forEach(this.item::removeEnchantment);

        this.vanillaEnchantments.keySet().forEach(enchantment -> this.item.addUnsafeEnchantment(enchantment, this.vanillaEnchantments.get(enchantment)));

        this.cEnchantmentRemove.forEach(enchantment -> this.instance.removeEnchantment(this.item, enchantment));

        this.instance.addEnchantments(this.item, this.cEnchantments);

        return this.item;
    }
}
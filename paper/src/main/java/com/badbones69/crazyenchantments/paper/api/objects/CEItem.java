package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CEItem {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyPlatform platform = this.plugin.getPlatform();

    private final EnchantmentBookSettings bookSettings = this.platform.getBookSettings();

    private final CrazyManager crazyManager = this.plugin.getCrazyManager();

    private final Map<Enchantment, Integer> vanillaEnchantments;
    private final List<Enchantment> vanillaEnchantmentRemove;
    private final Map<CEnchantment, Integer> cEnchantments;
    private final List<CEnchantment> cEnchantmentRemove;
    private final ItemStack item;
    
    public CEItem(ItemStack item) {
        this.item = item;
        this.vanillaEnchantments = new HashMap<>(item.getEnchantments());
        this.cEnchantments = this.bookSettings.getEnchantments(item);
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
    
    public void addVanillaEnchantment(Enchantment enchantment, int level) {
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
    
    public void addCEnchantment(CEnchantment enchantment, int level) {
        this.cEnchantments.put(enchantment, level);
    }
    
    public void removeCEnchantment(CEnchantment enchantment) {
        this.cEnchantmentRemove.add(enchantment);
    }

    public boolean canAddEnchantment(Player player) {
        return this.crazyManager.canAddEnchantment(player, this.cEnchantments.size(), this.vanillaEnchantments.size());
    }

    public ItemStack build() {
        this.vanillaEnchantmentRemove.forEach(this.item::removeEnchantment);
        this.vanillaEnchantments.keySet().forEach(enchantment -> this.item.addUnsafeEnchantment(enchantment, this.vanillaEnchantments.get(enchantment)));
        this.cEnchantmentRemove.forEach(enchantment -> this.bookSettings.removeEnchantment(this.item, enchantment));
        this.bookSettings.addEnchantments(this.item, this.cEnchantments);

        return this.item;
    }
}
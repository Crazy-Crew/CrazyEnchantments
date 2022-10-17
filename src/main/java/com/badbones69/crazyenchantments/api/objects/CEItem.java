package com.badbones69.crazyenchantments.api.objects;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.api.CrazyManager;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CEItem {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();
    
    private final ItemStack item;
    private final List<Enchantment> vanillaEnchantmentRemove;
    private final List<CEnchantment> cEnchantmentRemove;
    private final Map<Enchantment, Integer> vanillaEnchantments;
    private final Map<CEnchantment, Integer> cEnchantments;
    
    public CEItem(ItemStack item) {
        this.item = item;
        // Has to make a new map as .getEnchantments is a ImmutableMap.
        vanillaEnchantments = new HashMap<>(item.getEnchantments());
        cEnchantments = crazyManager.getEnchantments(item);
        vanillaEnchantmentRemove = new ArrayList<>();
        cEnchantmentRemove = new ArrayList<>();
    }
    
    public ItemStack getItem() {
        return item;
    }
    
    public boolean hasVanillaEnchantment(Enchantment enchantment) {
        return vanillaEnchantments.containsKey(enchantment);
    }
    
    public int getVanillaEnchantmentLevel(Enchantment enchantment) {
        return vanillaEnchantments.getOrDefault(enchantment, 0);
    }
    
    public Map<Enchantment, Integer> getVanillaEnchantments() {
        return vanillaEnchantments;
    }
    
    public void setVanillaEnchantment(Enchantment enchantment, int level) {
        vanillaEnchantments.put(enchantment, level);
    }
    
    public void removeVanillaEnchantment(Enchantment enchantment) {
        vanillaEnchantmentRemove.add(enchantment);
    }
    
    public boolean hasCEnchantment(CEnchantment enchantment) {
        return cEnchantments.containsKey(enchantment);
    }
    
    public int getCEnchantmentLevel(CEnchantment enchantment) {
        return cEnchantments.getOrDefault(enchantment, 0);
    }
    
    public Map<CEnchantment, Integer> getCEnchantments() {
        return cEnchantments;
    }
    
    public void setCEnchantment(CEnchantment enchantment, int level) {
        cEnchantments.put(enchantment, level);
    }
    
    public void removeCEnchantment(CEnchantment enchantment) {
        cEnchantmentRemove.add(enchantment);
    }
    
    public ItemStack build() {
        vanillaEnchantmentRemove.forEach(enchantment -> item.removeEnchantment(enchantment));
        vanillaEnchantments.keySet().forEach(enchantment -> item.addUnsafeEnchantment(enchantment, vanillaEnchantments.get(enchantment)));
        cEnchantmentRemove.forEach(enchantment -> crazyManager.removeEnchantment(item, enchantment));
        crazyManager.addEnchantments(item, cEnchantments);
        return item;
    }
}
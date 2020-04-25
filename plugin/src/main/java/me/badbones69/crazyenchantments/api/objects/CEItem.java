package me.badbones69.crazyenchantments.api.objects;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CEItem {
    
    private ItemStack item;
    private List<Enchantment> vanillaEnchantmentRemove;
    private List<CEnchantment> cEnchantmentRemove;
    private Map<Enchantment, Integer> vanillaEnchantments;
    private Map<CEnchantment, Integer> cEnchantments;
    private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
    
    public CEItem(ItemStack item) {
        this.item = item;
        //Has to make a new map as .getEnchantments is a ImmutableMap.
        vanillaEnchantments = new HashMap<>(item.getEnchantments());
        cEnchantments = ce.getEnchantments(item);
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
    
    public void setVanillaEnchantment(Enchantment enchantmnet, int level) {
        vanillaEnchantments.put(enchantmnet, level);
    }
    
    public void removeVanillaEnchantmnet(Enchantment enchantmnet) {
        vanillaEnchantmentRemove.add(enchantmnet);
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
        cEnchantmentRemove.forEach(enchantment -> ce.removeEnchantment(item, enchantment));
        ce.addEnchantments(item, cEnchantments);
        return item;
    }
    
}
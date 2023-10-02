package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CEItem {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    private final ItemStack item;
    private final List<Enchantment> vanillaEnchantmentRemove;
    private final List<CEnchantment> cEnchantmentRemove;
    private final Map<Enchantment, Integer> vanillaEnchantments;
    private final Map<CEnchantment, Integer> cEnchantments;
    
    public CEItem(ItemStack item) {
        this.item = item;
        // Has to make a new map as .getEnchantments is a ImmutableMap.
        vanillaEnchantments = new HashMap<>(item.getEnchantments());
        EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();
        cEnchantments = enchantmentBookSettings.getEnchantments(item);
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
        vanillaEnchantmentRemove.forEach(item::removeEnchantment);
        vanillaEnchantments.keySet().forEach(enchantment -> item.addUnsafeEnchantment(enchantment, vanillaEnchantments.get(enchantment)));
        cEnchantmentRemove.forEach(enchantment -> enchantmentBookSettings.removeEnchantment(item, enchantment));
        crazyManager.addEnchantments(item, cEnchantments);

        return item;
    }
}
package me.badbones69.crazyenchantments.api.objects;

import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class BlackSmithCompare {
    
    private Map<Enchantment, Integer> newVanillaEnchantments = new HashMap<>();
    private Map<CEnchantment, Integer> newCEnchantments = new HashMap<>();
    
    public BlackSmithCompare(CEItem mainCE, CEItem subCE) {
        for (Entry<Enchantment, Integer> entry : subCE.getVanillaEnchantments().entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (!mainCE.hasVanillaEnchantment(enchantment)) {
                newVanillaEnchantments.put(enchantment, entry.getValue());
            }
        }
        for (Entry<CEnchantment, Integer> entry : subCE.getCEnchantments().entrySet()) {
            CEnchantment enchantment = entry.getKey();
            if (!mainCE.hasCEnchantment(enchantment)) {
                newCEnchantments.put(enchantment, entry.getValue());
            }
        }
    }
    
    public Map<Enchantment, Integer> getNewVanillaEnchantments() {
        return newVanillaEnchantments;
    }
    
    public Map<CEnchantment, Integer> getNewCEnchantments() {
        return newCEnchantments;
    }
    
}
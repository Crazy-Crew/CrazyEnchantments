package com.badbones69.crazyenchantments.paper.api.objects;

import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class BlackSmithCompare {
    
    private final Map<Enchantment, Integer> newVanillaEnchantments = new HashMap<>();
    private final Map<CEnchantment, Integer> newCEnchantments = new HashMap<>();
    
    public BlackSmithCompare(@NotNull final CEItem mainCE, @NotNull final CEItem subCE) {
        for (Entry<Enchantment, Integer> entry : subCE.getVanillaEnchantments().entrySet()) {
            Enchantment enchantment = entry.getKey();

            if (!mainCE.hasVanillaEnchantment(enchantment)) this.newVanillaEnchantments.put(enchantment, entry.getValue());
        }

        for (Entry<CEnchantment, Integer> entry : subCE.getCEnchantments().entrySet()) {
            CEnchantment enchantment = entry.getKey();

            if (!mainCE.hasCEnchantment(enchantment)) this.newCEnchantments.put(enchantment, entry.getValue());
        }
    }
    
    public Map<Enchantment, Integer> getNewVanillaEnchantments() {
        return this.newVanillaEnchantments;
    }
    
    public Map<CEnchantment, Integer> getNewCEnchantments() {
        return this.newCEnchantments;
    }
}
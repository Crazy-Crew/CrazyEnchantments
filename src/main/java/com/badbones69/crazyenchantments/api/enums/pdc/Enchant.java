package com.badbones69.crazyenchantments.api.enums.pdc;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Enchant {

    private final HashMap<String, Integer> enchants;

    public Enchant(HashMap<String, Integer> enchants) {
        this.enchants = enchants == null ? new HashMap<>() : enchants;
    }

    /**
     *
     * @param enchantment
     * @return true if the item has the specified enchantment.
     */
    public boolean hasEnchantment(String enchantment) { return this.enchants.containsKey(enchantment); }

    /**
     *
     * @return Hashmap of all enchantments and their corresponding levels.
     */
    public HashMap<String, Integer> getFullEnchantments() { return this.enchants; }

    /**
     *
     * @return Set of enchantments that are on the item.
     */
    public Set<String> getEnchantments() { return this.enchants.keySet(); }

    public Integer getLevel(String enchantment) { return this.enchants.get(enchantment); }

    /**
     *
     * @param enchantment
     */
    public void addEnchantments(Map<String, Integer> enchantment) { this.enchants.putAll(enchantment); }

    /**
     *
     * @param enchantment
     * @param level
     */
    public void addEnchantment(String enchantment, Integer level) { this.enchants.put(enchantment, level); }

    /**
     *
     * @param enchantment
     */
    public void removeEnchantment(String enchantment) { this.enchants.remove(enchantment); }

    /**
     *
     * @return true if there are no enchantments on the item.
     */
    public boolean isEmpty() { return enchants.isEmpty(); }

}

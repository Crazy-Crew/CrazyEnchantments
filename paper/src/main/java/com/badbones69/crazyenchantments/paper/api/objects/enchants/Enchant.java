package com.badbones69.crazyenchantments.paper.api.objects.enchants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Enchant {

    private final Map<String, Integer> enchants;

    public Enchant(@Nullable final Map<String, Integer> enchants) {
        this.enchants = enchants == null ? new HashMap<>() : enchants;
    }

    /**
     *
     * @param enchantment The enchantment you want to check for.
     * @return true if the item has the specified enchantment.
     */
    public boolean hasEnchantment(@NotNull final String enchantment) {
        return this.enchants.containsKey(enchantment);
    }

    /**
     *
     * @return Hashmap of all enchantments and their corresponding levels.
     */
    public Map<String, Integer> getFullEnchantments() {
        return this.enchants;
    }

    /**
     *
     * @return Set of enchantments that are on the item.
     */
    public Set<String> getEnchantments() {
        return this.enchants.keySet();
    }

    /**
     *
     * @param enchantment The enchantment want the level for.
     * @return The level or null if the enchantment is not on the item.
     */
    public int getLevel(@NotNull final String enchantment) {
        return this.enchants.get(enchantment);
    }

    /**
     *
     * @param enchantment The enchantment you want to add.
     */
    public void addEnchantments(@NotNull final Map<String, Integer> enchantment) {
        this.enchants.putAll(enchantment);
    }

    /**
     *
     * @param enchantment The enchantment you want to add.
     * @param level The level of the enchantment.
     */
    public void addEnchantment(@NotNull final String enchantment, final int level) {
        this.enchants.put(enchantment, level);
    }

    /**
     *
     * @param enchantment The enchantment you want to remove.
     */
    public void removeEnchantment(@NotNull final String enchantment) {
        this.enchants.remove(enchantment);
    }

    /**
     *
     * @return true if there are no enchantments on the item.
     */
    public boolean isEmpty() {
        return this.enchants.isEmpty();
    }
}
package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public record EnchantedArrow(Arrow arrow, ItemStack bow, Map<CEnchantment, Integer> enchantments) {

    public Entity getShooter() {
        return (Entity) this.arrow.getShooter();
    }

    public int getLevel(CEnchantments enchantment) {
        return this.enchantments.get(enchantment.getEnchantment());
    }

    public boolean hasEnchantment(CEnchantment enchantment) {
        return this.enchantments.containsKey(enchantment);
    }

    public boolean hasEnchantment(CEnchantments enchantment) {
        return hasEnchantment(enchantment.getEnchantment());
    }

    public Map<CEnchantment, Integer> getEnchantments() {
        return this.enchantments;
    }

    public Arrow getArrow() {
        return this.arrow;
    }
}
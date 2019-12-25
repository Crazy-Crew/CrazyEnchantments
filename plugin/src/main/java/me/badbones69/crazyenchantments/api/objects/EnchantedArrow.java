package me.badbones69.crazyenchantments.api.objects;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EnchantedArrow {
    
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    
    private Arrow arrow;
    private ItemStack bow;
    private Entity shooter;
    private List<CEnchantment> enchantments;
    
    public EnchantedArrow(Arrow arrow, Entity shooter, ItemStack bow, List<CEnchantment> enchantments) {
        this.bow = bow;
        this.arrow = arrow;
        this.shooter = shooter;
        this.enchantments = enchantments;
    }
    
    public Arrow getArrow() {
        return arrow;
    }
    
    public ItemStack getBow() {
        return bow;
    }
    
    public Entity getShooter() {
        return shooter;
    }
    
    public int getLevel(CEnchantments enchantment) {
        return ce.getLevel(bow, enchantment);
    }
    
    public int getLevel(CEnchantment enchantment) {
        return ce.getLevel(bow, enchantment);
    }
    
    public List<CEnchantment> getEnchantments() {
        return enchantments;
    }
    
    public boolean hasEnchantment(CEnchantment enchantment) {
        return enchantments.contains(enchantment);
    }
    
    public boolean hasEnchantment(CEnchantments enchantment) {
        return enchantments.contains(enchantment.getEnchantment());
    }
    
}
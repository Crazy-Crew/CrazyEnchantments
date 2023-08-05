package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class EnchantedArrow {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();
    
    private final Arrow arrow;
    private final ItemStack bow;
    private final Entity shooter;
    private final List<CEnchantment> enchantments;
    
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
        return crazyManager.getLevel(bow, enchantment);
    }
    
    public int getLevel(CEnchantment enchantment) {
        return enchantmentBookSettings.getLevel(bow, enchantment);
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
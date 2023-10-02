package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class EnchantedArrow {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Starter starter = this.plugin.getStarter();

    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();
    
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
        return this.arrow;
    }
    
    public ItemStack getBow() {
        return this.bow;
    }
    
    public Entity getShooter() {
        return this.shooter;
    }
    
    public int getLevel(CEnchantments enchantment) {
        return this.crazyManager.getLevel(this.bow, enchantment);
    }
    
    public int getLevel(CEnchantment enchantment) {
        return this.enchantmentBookSettings.getLevel(this.bow, enchantment);
    }
    
    public List<CEnchantment> getEnchantments() {
        return this.enchantments;
    }
    
    public boolean hasEnchantment(CEnchantment enchantment) {
        return this.enchantments.contains(enchantment);
    }
    
    public boolean hasEnchantment(CEnchantments enchantment) {
        return this.enchantments.contains(enchantment.getEnchantment());
    }
}
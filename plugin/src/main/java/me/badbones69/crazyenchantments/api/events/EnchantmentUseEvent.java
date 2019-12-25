package me.badbones69.crazyenchantments.api.events;

import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class EnchantmentUseEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private CEnchantment enchantment;
    private boolean cancel;
    private ItemStack item;
    
    public EnchantmentUseEvent(Player player, CEnchantment enchantment, ItemStack item) {
        this.player = player;
        this.enchantment = enchantment;
        cancel = false;
        this.item = item;
    }
    
    public EnchantmentUseEvent(Player player, CEnchantments enchantments, ItemStack item) {
        this.player = player;
        enchantment = enchantments.getEnchantment();
        cancel = false;
        this.item = item;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    /**
     *
     * @return The player that uses the enchantment.
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     *
     * @return The enchantment that is used.
     */
    public CEnchantment getEnchantment() {
        return enchantment;
    }
    
    /**
     *
     * @return The item that uses the enchantment.
     */
    public ItemStack getItem() {
        return item;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    @Override
    public boolean isCancelled() {
        return cancel;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
}
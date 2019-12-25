package me.badbones69.crazyenchantments.api.events;

import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegisteredCEnchantmentEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private CEnchantment enchantment;
    
    public RegisteredCEnchantmentEvent(CEnchantment enchantment) {
        this.enchantment = enchantment;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    /**
     * @return The enchantment being registered.
     */
    public CEnchantment getEnchantment() {
        return enchantment;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
}
package com.badbones69.crazyenchantments.api.events;

import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UnregisterCEnchantmentEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private final CEnchantment enchantment;
    
    public UnregisterCEnchantmentEvent(CEnchantment enchantment) {
        this.enchantment = enchantment;
    }
    
    /**
     * @return The enchantment being unregistered.
     */
    public CEnchantment getEnchantment() {
        return enchantment;
    }

    /**
     * Gets a list of handlers handling this event.
     *
     * @return A list of handlers handling this event.
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
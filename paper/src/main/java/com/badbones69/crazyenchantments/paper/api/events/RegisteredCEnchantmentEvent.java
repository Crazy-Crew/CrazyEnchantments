package com.badbones69.crazyenchantments.paper.api.events;

import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegisteredCEnchantmentEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private final CEnchantment enchantment;
    
    public RegisteredCEnchantmentEvent(CEnchantment enchantment) {
        this.enchantment = enchantment;
    }
    
    /**
     * @return The enchantment being registered.
     */
    public CEnchantment getEnchantment() {
        return this.enchantment;
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
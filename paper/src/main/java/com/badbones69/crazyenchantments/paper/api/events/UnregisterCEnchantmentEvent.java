package com.badbones69.crazyenchantments.paper.api.events;

import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
package com.badbones69.crazyenchantments.paper.api.events;

import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EnchantmentUseEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final CEnchantment enchantment;
    private boolean cancel;
    private final ItemStack item;
    
    public EnchantmentUseEvent(@NotNull final Player player, @NotNull final CEnchantment enchantment, @NotNull final ItemStack item) {
        this.player = player;
        this.enchantment = enchantment;
        this.cancel = false;
        this.item = item;
    }
    
    public EnchantmentUseEvent(@NotNull final Player player, @NotNull final CEnchantments enchantments, @NotNull final ItemStack item) {
        this.player = player;
        this.enchantment = enchantments.getEnchantment();
        this.cancel = false;
        this.item = item;
    }
    
    /**
     * @return The player that uses the enchantment.
     */
    public Player getPlayer() {
        return this.player;
    }
    
    /**
     * @return The enchantment that is used.
     */
    public CEnchantment getEnchantment() {
        return this.enchantment;
    }
    
    /**
     * @return The item that uses the enchantment.
     */
    public ItemStack getItem() {
        return this.item;
    }

    /**
     *
     * @return if the event is cancelled.
     */
    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    /**
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(final boolean cancel) {
        this.cancel = cancel;
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
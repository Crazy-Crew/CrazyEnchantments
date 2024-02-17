package com.badbones69.crazyenchantments.paper.api.events;

import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PreBookApplyEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final int level;
    private final boolean destroyed;
    private boolean cancelled;
    private final boolean successful;
    private final int successChance;
    private final int destroyChance;
    private final boolean creativeSuccess;
    private final ItemStack enchantedItem;
    private final CEnchantment enchantment;
    private final CEBook ceBook;
    
    public PreBookApplyEvent(Player player, ItemStack enchantedItem, CEBook ceBook, boolean creativeSuccess, boolean successful, boolean destroyed) {
        this.level = ceBook.getLevel();
        this.player = player;
        this.enchantment = ceBook.getEnchantment();
        this.enchantedItem = enchantedItem;
        this.ceBook = ceBook;
        this.creativeSuccess = creativeSuccess;
        this.successful = successful;
        this.successChance = ceBook.getSuccessRate();
        this.destroyed = destroyed;
        this.destroyChance = ceBook.getDestroyRate();
        this.cancelled = false;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public ItemStack getEnchantedItem() {
        return this.enchantedItem;
    }
    
    public CEnchantment getEnchantment() {
        return this.enchantment;
    }
    
    public CEBook getCEBook() {
        return this.ceBook;
    }
    
    public boolean getDestroyed() {
        return this.destroyed;
    }
    
    public boolean getSuccessful() {
        return this.successful;
    }
    
    public int getSuccessChance() {
        return this.successChance;
    }
    
    public int getDestroyChance() {
        return this.destroyChance;
    }
    
    public boolean getCreativeSuccess() {
        return this.creativeSuccess;
    }
    
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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
package me.badbones69.crazyenchantments.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class HellForgedUseEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private ItemStack item;
    private boolean cancel;
    
    public HellForgedUseEvent(Player player, ItemStack item) {
        this.player = player;
        this.item = item;
        cancel = false;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    /**
     *
     * @return The player using the enchantment.
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     *
     * @return The item with the enchantment.
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
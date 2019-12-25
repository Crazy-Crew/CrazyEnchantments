package me.badbones69.crazyenchantments.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class AngelUseEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private ItemStack item;
    private boolean cancel;
    
    public AngelUseEvent(Player player, ItemStack item) {
        this.player = player;
        this.item = item;
        this.cancel = false;
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
     * @return The item the enchantment is on.
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
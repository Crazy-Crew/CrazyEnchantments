package me.badbones69.crazyenchantments.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class DisarmerUseEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Player damager;
    private ItemStack item;
    private boolean cancel;
    
    public DisarmerUseEvent(Player player, Player damager, ItemStack item) {
        this.player = player;
        this.damager = damager;
        this.item = item;
        cancel = false;
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
     * @return The player that is attacking the player.
     */
    public Player getDamager() {
        return damager;
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
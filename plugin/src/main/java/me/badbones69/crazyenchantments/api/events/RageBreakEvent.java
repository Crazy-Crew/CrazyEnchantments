package me.badbones69.crazyenchantments.api.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class RageBreakEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Entity damager;
    private ItemStack weapon;
    private boolean cancel;
    
    public RageBreakEvent(Player player, Entity damager, ItemStack weapon) {
        this.player = player;
        this.damager = damager;
        this.weapon = weapon;
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
     * @return The entity that is attacking the player.
     */
    public Entity getDamager() {
        return damager;
    }
    
    /**
     *
     * @return The item that uses the enchantment.
     */
    public ItemStack getItem() {
        return weapon;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    /**
     *
     * @return True if the event is canceled and false if not.
     */
    @Override
    public boolean isCancelled() {
        return cancel;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
}
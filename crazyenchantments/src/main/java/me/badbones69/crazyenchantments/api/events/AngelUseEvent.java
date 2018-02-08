package me.badbones69.crazyenchantments.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class AngelUseEvent extends Event implements Cancellable {
	
	private Player player;
	private ItemStack item;
	private Boolean cancel;
	
	public AngelUseEvent(Player player, ItemStack item) {
		this.player = player;
		this.item = item;
		this.cancel = false;
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
	
	private static final HandlerList handlers = new HandlerList();
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
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
package me.BadBones69.CrazyEnchantments.API.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class HellForgedUseEvent extends Event implements Cancellable{
	Player Player;
	ItemStack It;
	Boolean Cancel;

	public HellForgedUseEvent(Player player, ItemStack item) {
		Player=player;
		It=item;
		Cancel=false;
	}
	
	/**
	 * 
	 * @return The player using the enchantment.
	 */
	public Player getPlayer(){
		return Player;
	}
	
	/**
	 * 
	 * @return The item with the enchantment.
	 */
	public ItemStack getItem(){
		return It;
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
		return Cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		Cancel=cancel;
	}
}
package me.badbones69.crazyenchantments.api.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class RageBreakEvent extends Event implements Cancellable {
	
	private Player Player;
	private Entity Damager;
	private ItemStack It;
	private Boolean Cancel;
	
	public RageBreakEvent(Player player, Entity damager, ItemStack item) {
		Player = player;
		Damager = damager;
		It = item;
		Cancel = false;
	}
	
	/**
	 * 
	 * @return The player that uses the enchantment.
	 */
	public Player getPlayer() {
		return Player;
	}
	
	/**
	 * 
	 * @return The entity that is attacking the player.
	 */
	public Entity getDamager() {
		return Damager;
	}
	
	/**
	 * 
	 * @return The item that uses the enchantment.
	 */
	public ItemStack getItem() {
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
		Cancel = cancel;
	}
	
}
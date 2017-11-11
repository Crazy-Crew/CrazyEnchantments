package me.badbones69.crazyenchantments.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class DisarmerUseEvent extends Event implements Cancellable {

	private Player Player;
	private Player Damager;
	private ItemStack It;
	private Boolean Cancel;

	public DisarmerUseEvent(Player player, Player damager, ItemStack item) {
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
	 * @return The player that is attacking the player.
	 */
	public Player getDamager() {
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
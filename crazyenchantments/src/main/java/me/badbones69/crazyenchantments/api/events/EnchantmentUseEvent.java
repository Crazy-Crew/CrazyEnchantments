package me.badbones69.crazyenchantments.api.events;

import me.badbones69.crazyenchantments.api.CEnchantments;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class EnchantmentUseEvent extends Event implements Cancellable {

	private Player Player;
	private CEnchantments Enchant;
	private Boolean Cancel;
	private ItemStack Item;

	public EnchantmentUseEvent(Player player, CEnchantments enchant, ItemStack item) {
		Player = player;
		Enchant = enchant;
		Cancel = false;
		Item = item;
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
	 * @return The enchantment that is used.
	 */
	public CEnchantments getEnchantment() {
		return Enchant;
	}

	/**
	 *
	 * @return The item that uses the enchantment.
	 */
	public ItemStack getItem() {
		return Item;
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
package me.BadBones69.CrazyEnchantments.API.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import me.BadBones69.CrazyEnchantments.API.CEnchantments;

public class EnchantmentUseEvent extends Event implements Cancellable{
	Player Player;
	CEnchantments Enchant;
	Boolean Cancel;
	ItemStack Item;

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
	public Player getPlayer(){
		return Player;
	}
	
	/**
	 * 
	 * @return The enchantment that is used.
	 */
	public CEnchantments getEnchantment(){
		return Enchant;
	}
	
	/**
	 * 
	 * @return The item that uses the enchantment.
	 */
	public ItemStack getItem(){
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
		Cancel=cancel;
	}
}
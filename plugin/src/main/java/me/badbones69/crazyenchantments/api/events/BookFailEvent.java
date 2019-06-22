package me.badbones69.crazyenchantments.api.events;

import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class BookFailEvent extends Event implements Cancellable {
	
	private Player player;
	private Integer level;
	private Boolean cancelled;
	private ItemStack enchantedItem;
	private CEnchantment enchantment;
	private ItemStack enchantmentBook;
	private static final HandlerList handlers = new HandlerList();
	
	public BookFailEvent(Player player, CEnchantment enchantment, Integer level, ItemStack enchantedItem, ItemStack enchantmentBook) {
		this.level = level;
		this.player = player;
		this.enchantment = enchantment;
		this.enchantedItem = enchantedItem;
		this.enchantmentBook = enchantmentBook;
		this.cancelled = false;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Integer getLevel() {
		return level;
	}
	
	public ItemStack getEnchantedItem() {
		return enchantedItem;
	}
	
	public CEnchantment getEnchantment() {
		return enchantment;
	}
	
	public ItemStack getEnchantmentBook() {
		return enchantmentBook;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
package me.badbones69.crazyenchantments.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.badbones69.crazyenchantments.api.CEBook;
import me.badbones69.crazyenchantments.api.CEPlayer;
import me.badbones69.crazyenchantments.api.currencyapi.Currency;

public class BuyBookEvent extends Event{
	
	private int price;
	private CEBook book;
	private CEPlayer player;
	private Currency currency;
	private static final HandlerList handlers = new HandlerList();
	
	/**
	 * Called when a book is being bought with the signs and gui.
	 * @param player Player buying the book.
	 * @param currency Currency being used.
	 * @param price Price of the book.
	 * @param book CEBook being bought.
	 * @param customBook CustomEBook being bought.
	 */
	public BuyBookEvent(CEPlayer player, Currency currency, int price, CEBook book){
		this.book = book;
		this.price = price;
		this.player = player;
		this.currency = currency;
	}
	
	/**
	 * Get the player that buys the book.
	 * @return The player that bought the book.
	 */
	public CEPlayer getPlayer(){
		return this.player;
	}
	
	/**
	 * Get the currency being used.
	 * @return The currency being used.
	 */
	public Currency getCurrency(){
		return this.currency;
	}
	
	/**
	 * Get the price they bought the book at.
	 * @return The price they payed.
	 */
	public int getPrice(){
		return this.price;
	}
	
	/**
	 * The CEBook that is being bought.
	 * @return The CEBook that is being bought. This maybe null if they bought a CustomEBook instead.
	 */
	public CEBook getBook(){
		return this.book;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
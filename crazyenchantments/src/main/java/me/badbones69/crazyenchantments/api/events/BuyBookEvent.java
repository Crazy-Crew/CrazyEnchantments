package me.badbones69.crazyenchantments.api.events;

import me.badbones69.crazyenchantments.api.CEBook;
import me.badbones69.crazyenchantments.api.CEPlayer;
import me.badbones69.crazyenchantments.api.CustomEBook;
import me.badbones69.crazyenchantments.api.currencyapi.Currency;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BuyBookEvent extends Event {

	private int price;
	private CEBook book;
	private CEPlayer player;
	private Currency currency;
	private CustomEBook customBook;
	private static final HandlerList handlers = new HandlerList();

	/**
	 * Called when a book is being bought with the signs and gui.
	 * @param player Player buying the book.
	 * @param currency Currency being used.
	 * @param price Price of the book.
	 * @param book CEBook being bought.
	 * @param customBook CustomEBook being bought.
	 */
	public BuyBookEvent(CEPlayer player, Currency currency, int price, CEBook book, CustomEBook customBook) {
		this.book = book;
		this.price = price;
		this.player = player;
		this.currency = currency;
		this.customBook = customBook;
	}

	/**
	 * Get the player that buys the book.
	 * @return The player that bought the book.
	 */
	public CEPlayer getPlayer() {
		return this.player;
	}

	/**
	 * Get the currency being used.
	 * @return The currency being used.
	 */
	public Currency getCurrency() {
		return this.currency;
	}

	/**
	 * Get the price they bought the book at.
	 * @return The price they payed.
	 */
	public int getPrice() {
		return this.price;
	}

	/**
	 * The CEBook that is being bought.
	 * @return The CEBook that is being bought. This maybe null if they bought a CustomEBook instead.
	 */
	public CEBook getBook() {
		return this.book;
	}

	/**
	 * The CustomEBook the player bought.
	 * @return The CuustomEBook being bought. This maybe null if the player bought a CEBook.
	 */
	public CustomEBook getCustomBook() {
		return this.customBook;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
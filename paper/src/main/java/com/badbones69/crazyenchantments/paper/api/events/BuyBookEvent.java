package com.badbones69.crazyenchantments.paper.api.events;

import com.badbones69.crazyenchantments.paper.managers.currency.enums.Currency;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BuyBookEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private final int price;
    private final CEBook book;
    private final CEPlayer player;
    private final Currency currency;
    
    /**
     * Called when a book is being bought with the signs and gui.
     * @param player Player buying the book.
     * @param currency Currency being used.
     * @param price Price of the book.
     * @param book CEBook being bought.
     */
    public BuyBookEvent(@NotNull final CEPlayer player, @NotNull final Currency currency, final int price, @NotNull final CEBook book) {
        this.book = book;
        this.price = price;
        this.player = player;
        this.currency = currency;
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
     * @return The price they paid.
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
     * Gets a list of handlers handling this event.
     *
     * @return A list of handlers handling this event.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
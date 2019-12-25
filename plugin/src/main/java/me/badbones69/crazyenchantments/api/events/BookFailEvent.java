package me.badbones69.crazyenchantments.api.events;

import me.badbones69.crazyenchantments.api.objects.CEBook;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class BookFailEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private int level;
    private boolean cancelled;
    private ItemStack enchantedItem;
    private CEnchantment enchantment;
    private CEBook ceBook;
    
    public BookFailEvent(Player player, ItemStack enchantedItem, CEBook ceBook) {
        this.level = ceBook.getLevel();
        this.player = player;
        this.enchantment = ceBook.getEnchantment();
        this.enchantedItem = enchantedItem;
        this.ceBook = ceBook;
        this.cancelled = false;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public int getLevel() {
        return level;
    }
    
    public ItemStack getEnchantedItem() {
        return enchantedItem;
    }
    
    public CEnchantment getEnchantment() {
        return enchantment;
    }
    
    public CEBook getCEBook() {
        return ceBook;
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
    
}
package me.badbones69.crazyenchantments.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.badbones69.crazyenchantments.api.CEnchantments;

public class AuraActiveEvent extends Event{
	
	private Player Player;
	private Player Other;
	private CEnchantments Enchant;
	private Integer Power;
	
	/**
	 * This event fires when a player walks near another player with an aura enchantment on.
	 * @param player Player that has the aura enchantment on.
	 * @param other The player that will be effected by it.
	 * @param enchantment The aura enchantment that is activated.
	 * @param power The power of the aura enchantment.
	 */
	public AuraActiveEvent(Player player, Player other, CEnchantments enchantment, Integer power){
		Player = player;
		Other = other;
		Enchant = enchantment;
		Power = power;
	}
	
	/**
	 * Gets the player that is being effected by the aura enchantment.
	 * @return The player with the enchantment.
	 */
	public Player getPlayer(){
		return Player;
	}
	
	/**
	 * The player being effected by the aura enchantment.
	 * @return The player that is being targeted.
	 */
	public Player getOther(){
		return Other;
	}
	
	/**
	 * The aura enchantment that is on the player.
	 * @return The aura enchantment being used.
	 */
	public CEnchantments getEnchantment(){
		return Enchant;
	}
	
	/**
	 * The power of the aura enchantment.
	 * @return The power of the aura enchantment that is being used.
	 */
	public Integer getPower(){
		return Power;
	}
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
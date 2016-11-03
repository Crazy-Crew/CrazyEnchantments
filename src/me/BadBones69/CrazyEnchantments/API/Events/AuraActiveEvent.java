package me.BadBones69.CrazyEnchantments.API.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.BadBones69.CrazyEnchantments.API.CEnchantments;

public class AuraActiveEvent extends Event{
	
	Player Player;
	Player Other;
	CEnchantments Enchant;
	Integer Power;
	
	public AuraActiveEvent(Player player, Player other, CEnchantments enchantment, Integer power){
		Player = player;
		Other = other;
		Enchant = enchantment;
		Power = power;
	}
	
	public Player getPlayer(){
		return Player;
	}
	
	public Player getOther(){
		return Other;
	}
	
	public CEnchantments getEnchantment(){
		return Enchant;
	}
	
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
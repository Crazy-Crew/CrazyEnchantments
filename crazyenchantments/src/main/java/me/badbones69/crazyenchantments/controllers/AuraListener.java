package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.AuraActiveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class AuraListener implements Listener {
	
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		if(!e.isCancelled()) {
			if((e.getFrom().getBlockX() != e.getTo().getBlockX()) || (e.getFrom().getBlockY() != e.getTo().getBlockY()) || (e.getFrom().getBlockZ() != e.getTo().getBlockZ())) {
				ArrayList<Player> players = getNearByPlayers(e.getPlayer(), 3);
				for(ItemStack item : e.getPlayer().getEquipment().getArmorContents()) {
					Player player = e.getPlayer();
					if(ce.hasEnchantments(item)) {
						for(CEnchantments enchant : getAuraEnchantments()) {
							if(ce.hasEnchantment(item, ce.getEnchantmentFromName(enchant.getName()))) {
								int power = ce.getPower(item, ce.getEnchantmentFromName(enchant.getName()));
								if(players.size() > 0) {
									for(Player other : players) {
										Bukkit.getPluginManager().callEvent(new AuraActiveEvent(player, other, enchant, power));
									}
								}
							}
						}
					}
				}
				for(Player other : players) {
					for(ItemStack item : other.getEquipment().getArmorContents()) {// The other players moving.
						if(ce.hasEnchantments(item)) {
							for(CEnchantments enchant : getAuraEnchantments()) {
								if(ce.hasEnchantment(item, ce.getEnchantmentFromName(enchant.getName()))) {
									Bukkit.getPluginManager().callEvent(new AuraActiveEvent(other, e.getPlayer(), enchant,
									ce.getPower(item, ce.getEnchantmentFromName(enchant.getName()))));
								}
							}
						}
					}
				}
			}
		}
	}
	
	private ArrayList<CEnchantments> getAuraEnchantments() {
		ArrayList<CEnchantments> enchants = new ArrayList<>();
		enchants.add(CEnchantments.BLIZZARD);
		enchants.add(CEnchantments.ACIDRAIN);
		enchants.add(CEnchantments.SANDSTORM);
		enchants.add(CEnchantments.RADIANT);
		enchants.add(CEnchantments.INTIMIDATE);
		return enchants;
	}
	
	private ArrayList<Player> getNearByPlayers(Player player, int radius) {
		ArrayList<Player> players = new ArrayList<>();
		for(Entity en : player.getNearbyEntities(radius, radius, radius)) {
			if(en instanceof Player) {
				if(en != player) {
					players.add((Player) en);
				}
			}
		}
		return players;
	}
	
}
package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.AuraActiveEvent;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuraListener implements Listener {
	
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	private static List<CEnchantments> auraEnchantments = Arrays.asList(
	CEnchantments.BLIZZARD,
	CEnchantments.ACIDRAIN,
	CEnchantments.SANDSTORM,
	CEnchantments.RADIANT,
	CEnchantments.INTIMIDATE);
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		if(!e.isCancelled()) {
			if((e.getFrom().getBlockX() != e.getTo().getBlockX()) || (e.getFrom().getBlockY() != e.getTo().getBlockY()) || (e.getFrom().getBlockZ() != e.getTo().getBlockZ())) {
				Player player = e.getPlayer();
				ArrayList<Player> players = getNearByPlayers(player, 3);
				for(ItemStack item : player.getEquipment().getArmorContents()) {//The player that moves.
					List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
					if(!enchantments.isEmpty()) {
						for(CEnchantments enchantment : auraEnchantments) {
							if(enchantments.contains(enchantment.getEnchantment())) {
								for(Player other : players) {
									Bukkit.getPluginManager().callEvent(new AuraActiveEvent(player, other, enchantment, ce.getLevel(item, enchantment.getEnchantment())));
								}
							}
						}
					}
				}
				for(Player other : players) {
					for(ItemStack item : other.getEquipment().getArmorContents()) {// The other players moving.
						List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
						if(!enchantments.isEmpty()) {
							for(CEnchantments enchantment : auraEnchantments) {
								if(enchantments.contains(enchantment.getEnchantment())) {
									Bukkit.getPluginManager().callEvent(new AuraActiveEvent(other, player, enchantment, ce.getLevel(item, enchantment.getEnchantment())));
								}
							}
						}
					}
				}
			}
		}
	}
	
	private ArrayList<Player> getNearByPlayers(Player player, int radius) {
		ArrayList<Player> players = new ArrayList<>();
		for(Entity entity : player.getNearbyEntities(radius, radius, radius)) {
			if(entity instanceof Player) {
				if(entity != player) {
					players.add((Player) entity);
				}
			}
		}
		return players;
	}
	
}
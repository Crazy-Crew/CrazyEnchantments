package me.badbones69.crazyenchantments.api.events;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.api.CEnchantments;
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

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		if(!e.isCancelled()) {
			if((e.getFrom().getBlockX() != e.getTo().getBlockX()) || (e.getFrom().getBlockY() != e.getTo().getBlockY()) || (e.getFrom().getBlockZ() != e.getTo().getBlockZ())) {
				for(ItemStack item : e.getPlayer().getEquipment().getArmorContents()) {
					Player player = e.getPlayer();
					ArrayList<Player> players = getNearByPlayers(player, 3);
					if(Main.CE.hasEnchantments(item)) {
						for(CEnchantments enchant : getAuraEnchantments()) {
							if(Main.CE.hasEnchantment(item, enchant)) {
								int power = Main.CE.getPower(item, enchant);
								if(players.size() > 0) {
									for(Player other : players) {
										Bukkit.getPluginManager().callEvent(new AuraActiveEvent(player, other, enchant, power));
									}
								}
							}
						}
					}
				}
				for(Player player : getNearByPlayers(e.getPlayer(), 3)) {
					for(ItemStack item : player.getEquipment().getArmorContents()) {
						if(Main.CE.hasEnchantments(item)) {
							for(CEnchantments enchant : getAuraEnchantments()) {
								if(Main.CE.hasEnchantment(item, enchant)) {
									int power = Main.CE.getPower(item, enchant);
									Bukkit.getPluginManager().callEvent(new AuraActiveEvent(player, e.getPlayer(), enchant, power));
								}
							}
						}
					}
				}
			}
		}
	}

	private ArrayList<CEnchantments> getAuraEnchantments() {
		ArrayList<CEnchantments> enchants = new ArrayList<CEnchantments>();
		enchants.add(CEnchantments.BLIZZARD);
		enchants.add(CEnchantments.ACIDRAIN);
		enchants.add(CEnchantments.SANDSTORM);
		enchants.add(CEnchantments.RADIANT);
		enchants.add(CEnchantments.INTIMIDATE);
		return enchants;
	}

	private ArrayList<Player> getNearByPlayers(Player player, int radius) {
		ArrayList<Player> players = new ArrayList<Player>();
		for(Entity en : player.getNearbyEntities(radius, radius, radius)) {
			if(en instanceof Player) {
				if((Player) en != player) {
					players.add((Player) en);
				}
			}
		}
		return players;
	}

}
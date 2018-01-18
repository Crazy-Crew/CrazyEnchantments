package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.api.CEnchantments;
import me.badbones69.crazyenchantments.api.events.ArmorEquipEvent;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.multisupport.Support;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class Helmets implements Listener {

	private int time = Integer.MAX_VALUE;

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEquip(ArmorEquipEvent e) {
		Player player = e.getPlayer();
		ItemStack NewItem = e.getNewArmorPiece();
		ItemStack OldItem = e.getOldArmorPiece();
		if(Main.CE.hasEnchantments(NewItem)) {
			if(Main.CE.hasEnchantment(NewItem, CEnchantments.GLOWING)) {
				if(CEnchantments.GLOWING.isEnabled()) {
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.GLOWING, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, time, Main.CE.getPower(NewItem, CEnchantments.GLOWING) - 1));
					}
				}
			}
			if(Main.CE.hasEnchantment(NewItem, CEnchantments.MERMAID)) {
				if(CEnchantments.MERMAID.isEnabled()) {
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.MERMAID, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, time, Main.CE.getPower(NewItem, CEnchantments.MERMAID) - 1));
					}
				}
			}
		}
		if(Main.CE.hasEnchantments(OldItem)) {
			if(Main.CE.hasEnchantment(OldItem, CEnchantments.GLOWING)) {
				if(CEnchantments.GLOWING.isEnabled()) {
					player.removePotionEffect(PotionEffectType.NIGHT_VISION);
				}
			}
			if(Main.CE.hasEnchantment(OldItem, CEnchantments.MERMAID)) {
				if(CEnchantments.MERMAID.isEnabled()) {
					player.removePotionEffect(PotionEffectType.WATER_BREATHING);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMovment(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		for(ItemStack armor : player.getEquipment().getArmorContents()) {
			if(Main.CE.hasEnchantments(armor)) {
				if(Main.CE.hasEnchantment(armor, CEnchantments.COMMANDER)) {
					if(CEnchantments.COMMANDER.isEnabled()) {
						int radius = 4 + Main.CE.getPower(armor, CEnchantments.COMMANDER);
						ArrayList<Player> players = new ArrayList<Player>();
						for(Entity en : player.getNearbyEntities(radius, radius, radius)) {
							if(en instanceof Player) {
								Player o = (Player) en;
								if(Support.isFriendly(player, o)) {
									players.add(o);
								}
							}
						}
						if(players.size() > 0) {
							EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.COMMANDER, armor);
							Bukkit.getPluginManager().callEvent(event);
							if(!event.isCancelled()) {
								for(Player P : players) {
									P.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 3 * 20, 1));
								}
							}
						}
					}
				}
			}
		}
	}

}

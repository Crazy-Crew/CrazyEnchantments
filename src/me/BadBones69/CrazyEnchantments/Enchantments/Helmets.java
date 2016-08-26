package me.BadBones69.CrazyEnchantments.Enchantments;

import java.util.ArrayList;

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

import ca.thederpygolems.armorequip.ArmorEquipEvent;
import me.BadBones69.CrazyEnchantments.Api;
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.CrazyEnchantments;
import me.BadBones69.CrazyEnchantments.API.Events.EnchantmentUseEvent;

public class Helmets implements Listener{
	CrazyEnchantments CE = CrazyEnchantments.getInstance();
	int time = 99999999*20;
	@EventHandler(priority = EventPriority.MONITOR)
 	public void onEquip(ArmorEquipEvent e){
		Player player = e.getPlayer();
		ItemStack NewItem = e.getNewArmorPiece();
		ItemStack OldItem = e.getOldArmorPiece();
		if(CE.hasEnchantments(NewItem)){
			if(CE.hasEnchantment(NewItem, CEnchantments.GLOWING)){
				if(CEnchantments.GLOWING.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.GLOWING, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, time, CE.getPower(NewItem, CEnchantments.GLOWING)-1));
					}
				}
			}
			if(CE.hasEnchantment(NewItem, CEnchantments.MERMAID)){
				if(CEnchantments.MERMAID.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.MERMAID, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, time, CE.getPower(NewItem, CEnchantments.MERMAID)-1));
					}
				}
			}
		}
		if(CE.hasEnchantments(OldItem)){
			if(CE.hasEnchantment(OldItem, CEnchantments.GLOWING)){
				if(CEnchantments.GLOWING.isEnabled()){
					player.removePotionEffect(PotionEffectType.NIGHT_VISION);
				}
			}
			if(CE.hasEnchantment(OldItem, CEnchantments.MERMAID)){
				if(CEnchantments.MERMAID.isEnabled()){
					player.removePotionEffect(PotionEffectType.WATER_BREATHING);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onMovment(PlayerMoveEvent e){
		Player player = e.getPlayer();
		for(ItemStack armor : player.getEquipment().getArmorContents()){
			if(CE.hasEnchantments(armor)){
				if(CE.hasEnchantment(armor, CEnchantments.COMMANDER)){
					if(CEnchantments.COMMANDER.isEnabled()){
						if(Api.hasFactions()){
							int radius = 4+CE.getPower(armor, CEnchantments.COMMANDER);
							ArrayList<Player> players = new ArrayList<Player>();
							for(Entity en : player.getNearbyEntities(radius, radius, radius)){
								if(en instanceof Player){
									Player o = (Player) en;
									if(Api.isFriendly(player, o)){
										players.add(o);
									}
								}
							}
							if(players.size()>0){
								EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.COMMANDER, armor);
								Bukkit.getPluginManager().callEvent(event);
								if(!event.isCancelled()){
									for(Player P : players){
										P.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 3*20, 1));
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
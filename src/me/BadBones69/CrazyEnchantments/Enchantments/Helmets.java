package me.BadBones69.CrazyEnchantments.Enchantments;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ca.thederpygolems.armorequip.ArmorEquipEvent;
import me.BadBones69.CrazyEnchantments.Api;

public class Helmets implements Listener{
	int time = 99999999*20;
	@EventHandler
 	public void onEquip(ArmorEquipEvent e){
		Player player = e.getPlayer();
		ItemStack NewItem = e.getNewArmorPiece();
		ItemStack OldItem = e.getOldArmorPiece();
		if(e.getNewArmorPiece() != null && e.getNewArmorPiece().hasItemMeta() && e.getNewArmorPiece().getType() != Material.AIR){
			if(NewItem.getItemMeta().hasLore()){
				for(String lore : NewItem.getItemMeta().getLore()){
					if(lore.contains(Api.getEnchName("Glowing"))){
						if(Api.isEnchantmentEnabled("Glowing")){
							player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, time, Api.getPower(lore, Api.getEnchName("Glowing"))-1));
						}
					}
					if(lore.contains(Api.getEnchName("Mermaid"))){
						if(Api.isEnchantmentEnabled("Mermaid")){
							player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, time, Api.getPower(lore, Api.getEnchName("Mermaid"))-1));
						}
					}
				}
			}
		}
		if(e.getOldArmorPiece() != null && e.getOldArmorPiece().hasItemMeta() && e.getOldArmorPiece().getType() != Material.AIR){
			if(OldItem.getItemMeta().hasLore()){
				for(String lore : OldItem.getItemMeta().getLore()){
					if(lore.contains(Api.getEnchName("Glowing"))){
						if(Api.isEnchantmentEnabled("Glowing")){
							player.removePotionEffect(PotionEffectType.NIGHT_VISION);
						}
					}
					if(lore.contains(Api.getEnchName("Mermaid"))){
						if(Api.isEnchantmentEnabled("Mermaid")){
							player.removePotionEffect(PotionEffectType.WATER_BREATHING);
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onMovment(PlayerMoveEvent e){
		Player player = e.getPlayer();
		for(ItemStack i : player.getEquipment().getArmorContents()){
			if(i!=null){
				if(i.hasItemMeta()){
					if(i.getItemMeta().hasLore()){
						for(String lore : i.getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Commander"))){
								if(Api.isEnchantmentEnabled("Commander")){
									if(Api.hasFactions()){
										int radius = 4+Api.getPower(lore, Api.getEnchName("Commander"));
										for(Entity en : player.getNearbyEntities(radius, radius, radius)){
											if(en instanceof Player){
												Player o = (Player) en;
												if(Api.isFriendly(player, o)){
													o.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 3*20, 1));
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
		}
	}
}
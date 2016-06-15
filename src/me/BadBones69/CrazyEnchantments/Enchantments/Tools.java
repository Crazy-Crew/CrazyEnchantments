package me.BadBones69.CrazyEnchantments.Enchantments;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.BadBones69.CrazyEnchantments.Api;

public class Tools implements Listener{
	private HashMap<Player, HashMap<String, Boolean>> effect = new HashMap<Player, HashMap<String, Boolean>>();
	int time = 99999999*20;
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		Player player = e.getPlayer();
		ItemStack item = Api.getItemInHand(player);
		HashMap<String, Boolean> Trigger = new HashMap<String, Boolean>();
		Trigger.put("Haste", false);
		Trigger.put("Oxygenate", false);
		if(item!=null){
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasLore()){
					for(String lore : item.getItemMeta().getLore()){
						if(lore.contains(Api.getEnchName("Haste"))){
							if(Api.isEnchantmentEnabled("Haste")){
								int power = Api.getPower(lore, Api.getEnchName("Haste"));
								Trigger.put("Haste", true);
								player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, time, power-1));
							}
						}
						if(lore.contains(Api.getEnchName("Oxygenate"))){
							if(Api.isEnchantmentEnabled("Oxygenate")){
								Trigger.put("Oxygenate", true);
								player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, time, 5));
							}
						}
					}
				}
			}
		}
		effect.put(player, Trigger);
		if(effect.containsKey(player)){
			if(!effect.get(player).get("Haste")){
				player.removePotionEffect(PotionEffectType.FAST_DIGGING);
			}
			if(!effect.get(player).get("Oxygenate")){
				player.removePotionEffect(PotionEffectType.WATER_BREATHING);
			}
		}
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		if(!Api.allowsBreak(e.getPlayer()))return;
		Block block = e.getBlock();
		Player player = e.getPlayer();
		if(!Api.canBreakBlock(player, block))return;
		if(player.getGameMode()!=GameMode.CREATIVE){
			if(Api.getItemInHand(player)!=null){
				ItemStack item = Api.getItemInHand(player);
				if(item.hasItemMeta()){
					if(item.getItemMeta().hasLore()){
						for(String lore : item.getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Telepathy"))){
								if(Api.isEnchantmentEnabled("Telepathy")){
									Boolean T = false;
									if(item.getItemMeta().hasEnchants()){
										if(item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)){
											if(Bukkit.getServer().getPluginManager().getPlugin("SilkSpawners")!=null){
												if(block.getType()==Material.MOB_SPAWNER){
													T=true;
												}
											}
										}
									}
									if(!T){
										e.setCancelled(true);
										for(ItemStack i : block.getDrops()){
											if(!Api.isInvFull(player)){
												player.getInventory().addItem(i);
											}else{
												block.getWorld().dropItemNaturally(block.getLocation(), i);
											}
										}
										block.setType(Material.AIR);
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
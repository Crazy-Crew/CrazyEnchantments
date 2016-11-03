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
import me.BadBones69.CrazyEnchantments.Main;
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.Events.EnchantmentUseEvent;

public class Tools implements Listener{

	private HashMap<Player, HashMap<String, Boolean>> effect = new HashMap<Player, HashMap<String, Boolean>>();
	private HashMap<Player, HashMap<String, Boolean>> hadEnchant = new HashMap<Player, HashMap<String, Boolean>>();
	int time = 99999999*20;
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		Player player = e.getPlayer();
		ItemStack item = Api.getItemInHand(player);
		HashMap<String, Boolean> Trigger = new HashMap<String, Boolean>();
		Trigger.put("Haste", false);
		Trigger.put("Oxygenate", false);
		Boolean Haste = false;
		Boolean Ox = false;
		if(Main.CE.hasEnchantments(item)){
			if(Main.CE.hasEnchantment(item, CEnchantments.HASTE)){
				if(CEnchantments.HASTE.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.HASTE, item);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						int power = Main.CE.getPower(item, CEnchantments.HASTE);
						Trigger.put("Haste", true);
						player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, time, power-1));
						Haste=true;
						hadEnchant.put(player, Trigger);
					}
				}
			}
			if(Main.CE.hasEnchantment(item, CEnchantments.OXYGENATE)){
				if(CEnchantments.OXYGENATE.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.OXYGENATE, item);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						Trigger.put("Oxygenate", true);
						player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, time, 5));
						Ox=true;
						hadEnchant.put(player, Trigger);
					}
				}
			}
		}
		if(!Ox){
			Trigger.put("Oxygenate", false);
		}
		if(!Haste){
			Trigger.put("Haste", false);
		}
		effect.put(player, Trigger);
		if(effect.containsKey(player)&&hadEnchant.containsKey(player)){
			if(!effect.get(player).get("Haste")&&hadEnchant.get(player).get("Haste")){
				player.removePotionEffect(PotionEffectType.FAST_DIGGING);
				Trigger.put("Haste", false);
				hadEnchant.put(player, Trigger);
			}
			if(!effect.get(player).get("Oxygenate")&&hadEnchant.get(player).get("Oxygenate")){
				player.removePotionEffect(PotionEffectType.WATER_BREATHING);
				Trigger.put("Oxygenate", false);
				hadEnchant.put(player, Trigger);
			}
			effect.remove(player);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		if(!Api.allowsBreak(e.getPlayer().getLocation()))return;
		Block block = e.getBlock();
		Player player = e.getPlayer();
		if(!Api.canBreakBlock(player, block))return;
		if(player.getGameMode()!=GameMode.CREATIVE){
			ItemStack item = Api.getItemInHand(player);
			if(Main.CE.hasEnchantments(item)){
				if(Main.CE.hasEnchantment(item, CEnchantments.TELEPATHY)){
					if(CEnchantments.TELEPATHY.isEnabled()){
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
							EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.TELEPATHY, item);
							Bukkit.getPluginManager().callEvent(event);
							if(!event.isCancelled()){
								e.setCancelled(true);
								for(ItemStack i : block.getDrops()){
									if(!Api.isInvFull(player)){
										player.getInventory().addItem(i);
									}else{
										block.getWorld().dropItemNaturally(block.getLocation(), i);
									}
								}
								block.setType(Material.AIR);
								int dur = item.getDurability()+1;
								item.setDurability((short)dur);
							}
						}
					}
				}
			}
		}
	}
	
}
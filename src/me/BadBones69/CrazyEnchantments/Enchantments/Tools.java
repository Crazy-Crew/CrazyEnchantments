package me.BadBones69.CrazyEnchantments.Enchantments;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
	private HashMap<Player, Boolean> effect = new HashMap<Player, Boolean>();
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		Player player = e.getPlayer();
		if(Api.getItemInHand(player)!=null){
			ItemStack item = Api.getItemInHand(player);
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasLore()){
					for(String lore : item.getItemMeta().getLore()){
						if(lore.contains(Api.getEnchName("Haste"))){
							if(Api.isEnchantmentEnabled("Haste")){
								int power = Api.getPower(lore, Api.getEnchName("Haste"));
								effect.put(player, true);
								player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 55555*20, power-1));
							}
						}
						if(lore.contains(Api.getEnchName("Oxygenate"))){
							if(Api.isEnchantmentEnabled("Oxygenate")){
								effect.put(player, true);
								player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 55555*20, 5));
							}
						}
					}
				}
			}
		}
		if(effect.containsKey(player)){
			if(effect.get(player)){
				effect.put(player, false);
				player.removePotionEffect(PotionEffectType.FAST_DIGGING);
				player.removePotionEffect(PotionEffectType.WATER_BREATHING);
				return;
			}
		}
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		if(!Api.allowsBreak(e.getPlayer()))return;
		Block block = e.getBlock();
		Player player = e.getPlayer();
		if(player.getGameMode()!=GameMode.CREATIVE){
			if(Api.getItemInHand(player)!=null){
				ItemStack item = Api.getItemInHand(player);
				if(item.hasItemMeta()){
					if(item.getItemMeta().hasLore()){
						for(String lore : item.getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Telepathy"))){
								if(Api.isEnchantmentEnabled("Telepathy")){
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
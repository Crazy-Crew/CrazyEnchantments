package me.BadBones69.CrazyEnchantments.Enchantments.Tools;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class Telepathy implements Listener{
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		if(!Api.isEnchantmentEnabled("Telepathy"))return;
		Block block = e.getBlock();
		Player player = e.getPlayer();
		if(player.getGameMode()!=GameMode.CREATIVE){
			if(Api.getItemInHand(player)!=null){
				ItemStack item = Api.getItemInHand(player);
				if(item.hasItemMeta()){
					if(item.getItemMeta().hasLore()){
						for(String lore : item.getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Telepathy"))){
								e.setCancelled(true);
								for(ItemStack i : block.getDrops()){
									if(!Api.isInvFull(player)){
										player.getInventory().addItem(i);
									}else{
										block.getWorld().dropItemNaturally(block.getLocation(), i);
									}
								}
								block.setType(Material.AIR);
								return;
							}
						}
					}
				}
			}
		}
	}
}
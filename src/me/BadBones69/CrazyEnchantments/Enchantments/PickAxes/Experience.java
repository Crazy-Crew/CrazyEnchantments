package me.BadBones69.CrazyEnchantments.Enchantments.PickAxes;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class Experience implements Listener{
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		if(!Api.isEnchantmentEnabled("Experience"))return;
		if(!Api.allowsBreak(e.getPlayer()))return;
		Player player = e.getPlayer();
		if(player.getGameMode()!=GameMode.CREATIVE){
			if(Api.getItemInHand(player)!=null){
				ItemStack item = Api.getItemInHand(player);
				if(item.hasItemMeta()){
					if(item.getItemMeta().hasLore()){
						for(String lore : item.getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Experience"))){
								int power = Api.getPower(lore, Api.getEnchName("Experience"));
								if(Api.randomPicker(3)){
									e.setExpToDrop(e.getExpToDrop()+(power));
									return;
								}
							}
						}
					}
				}
			}
		}
	}
}
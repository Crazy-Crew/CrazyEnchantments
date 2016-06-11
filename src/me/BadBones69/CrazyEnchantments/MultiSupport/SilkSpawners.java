package me.BadBones69.CrazyEnchantments.MultiSupport;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerBreakEvent;
import de.dustplanet.util.SilkUtil;
import me.BadBones69.CrazyEnchantments.Api;

public class SilkSpawners implements Listener{
	SilkUtil su = SilkUtil.hookIntoSilkSpanwers();
	@EventHandler
	public void onBreak(SilkSpawnersSpawnerBreakEvent e){
		Player player = e.getPlayer();
		if(!Api.allowsBreak(player))return;
		Block block = e.getBlock();
		if(player!=null){
			if(block!=null){
				if(player.getGameMode()!=GameMode.CREATIVE){
					if(Api.getItemInHand(player)!=null){
						ItemStack item = Api.getItemInHand(player);
						if(item.hasItemMeta()){
							if(item.getItemMeta().hasLore()){
								for(String lore : item.getItemMeta().getLore()){
									if(lore.contains(Api.getEnchName("Telepathy"))){
										if(Api.isEnchantmentEnabled("Telepathy")){
											e.setCancelled(true);
											ItemStack it = su.newSpawnerItem(e.getEntityID(), su.getCustomSpawnerName(su.getCreatureName(e.getEntityID())), 1, false);
											if(!Api.isInvFull(player)){
												player.getInventory().addItem(it);
											}else{
												block.getWorld().dropItemNaturally(block.getLocation(), it);
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
}
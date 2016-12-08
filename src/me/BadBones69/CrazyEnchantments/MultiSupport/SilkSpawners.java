package me.BadBones69.CrazyEnchantments.MultiSupport;

import org.bukkit.Bukkit;
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
import me.BadBones69.CrazyEnchantments.Main;
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.Events.EnchantmentUseEvent;

public class SilkSpawners implements Listener{
	
	@EventHandler
	public void onBreak(SilkSpawnersSpawnerBreakEvent e){
		Player player = e.getPlayer();
		if(!Support.allowsBreak(player.getLocation()))return;
		Block block = e.getBlock();
		if(!Support.canBreakBlock(player, block))return;
		if(player!=null){
			if(block!=null){
				if(player.getGameMode()!=GameMode.CREATIVE){
					ItemStack item = Api.getItemInHand(player);
					if(Main.CE.hasEnchantments(item)){
						if(Main.CE.hasEnchantment(item, CEnchantments.TELEPATHY)){
							if(CEnchantments.TELEPATHY.isEnabled()){
								EnchantmentUseEvent useEnchant = new EnchantmentUseEvent(player, CEnchantments.TELEPATHY, item);
								Bukkit.getPluginManager().callEvent(useEnchant);
								if(useEnchant.isCancelled()){
									return;
								}
								e.setCancelled(true);
								SilkUtil su = SilkUtil.hookIntoSilkSpanwers();
								ItemStack it = su.newSpawnerItem(e.getEntityID(), su.getCustomSpawnerName(su.getCreatureName(e.getEntityID())), 1, false);
								if(!Api.isInvFull(player)){
									player.getInventory().addItem(it);
								}else{
									block.getWorld().dropItemNaturally(block.getLocation(), it);
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
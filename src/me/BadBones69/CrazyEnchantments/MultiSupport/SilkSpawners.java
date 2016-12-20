package me.BadBones69.CrazyEnchantments.multisupport;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerBreakEvent;
import de.dustplanet.util.SilkUtil;
import me.BadBones69.CrazyEnchantments.Methods;
import me.BadBones69.CrazyEnchantments.Main;
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.Events.EnchantmentUseEvent;

public class SilkSpawners implements Listener{
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBreak(SilkSpawnersSpawnerBreakEvent e){
		if(e.isCancelled())return;
		Player player = e.getPlayer();
		Block block = e.getBlock();
		if(player != null){
			if(block != null){
				if(player.getGameMode() != GameMode.CREATIVE){
					ItemStack item = Methods.getItemInHand(player);
					if(Main.CE.hasEnchantments(item)){
						if(Main.CE.hasEnchantment(item, CEnchantments.TELEPATHY)){
							if(CEnchantments.TELEPATHY.isEnabled()){
								EnchantmentUseEvent useEnchant = new EnchantmentUseEvent(player, CEnchantments.TELEPATHY, item);
								Bukkit.getPluginManager().callEvent(useEnchant);
								if(useEnchant.isCancelled()){
									return;
								}
								SilkUtil su = SilkUtil.hookIntoSilkSpanwers();
								ItemStack it = su.newSpawnerItem(e.getEntityID(), su.getCustomSpawnerName(su.getCreatureName(e.getEntityID())), 1, false);
								if(!Methods.isInvFull(player)){
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
package me.badbones69.crazyenchantments.multisupport;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.songoda.epicspawners.EpicSpawners;
import com.songoda.epicspawners.Spawners.SpawnerDropEvent;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CEnchantments;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;

public class EpicSpawnersSupport implements Listener{
	
	@EventHandler
	public void onSpawnerDrop(SpawnerDropEvent e){
		if(!e.isCancelled()){
			Player player = e.getPlayer();
			Block block = e.getLocation().getBlock();
			if(player != null){
				if(block != null){
					if(player.getGameMode() != GameMode.CREATIVE){
						ItemStack item = Methods.getItemInHand(player);
						if(Main.CE.hasEnchantments(item)){
							if(Main.CE.hasEnchantment(item, Main.CE.getEnchantmentFromName(CEnchantments.TELEPATHY.getName()))){
								if(CEnchantments.TELEPATHY.isEnabled()){
									EnchantmentUseEvent useEnchant = new EnchantmentUseEvent(player, CEnchantments.TELEPATHY.getEnchantment(), item);
									Bukkit.getPluginManager().callEvent(useEnchant);
									if(useEnchant.isCancelled()){
										return;
									}
									ItemStack it = getSpawner(block);
									if(!Methods.isInvFull(player)){
										player.getInventory().addItem(it);
									}else{
										block.getWorld().dropItemNaturally(block.getLocation(), it);
									}
									block.setType(Material.AIR);
									e.setCancelled(true);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static ItemStack getSpawner(Block block){
		if(block.getState() instanceof CreatureSpawner){
			return EpicSpawners.pl().getApi().newSpawnerItem(((CreatureSpawner)block.getState()).getSpawnedType(), 1);
		}
		return null;
	}
	
}
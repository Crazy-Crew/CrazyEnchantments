package me.badbones69.crazyenchantments.multisupport;

import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.inventory.ItemStack;

import com.songoda.epicspawners.EpicSpawners;

public class EpicSpawnersSupport {
	
	public static ItemStack getSpawner(Block block) {
		if(block.getState() instanceof CreatureSpawner) {
			return EpicSpawners.pl().getApi().newSpawnerItem(((CreatureSpawner) block.getState()).getSpawnedType(), 1);
		}
		return null;
	}
	
}
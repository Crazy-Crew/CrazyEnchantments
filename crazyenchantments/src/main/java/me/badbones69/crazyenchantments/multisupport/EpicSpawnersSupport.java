package me.badbones69.crazyenchantments.multisupport;

import com.songoda.epicspawners.EpicSpawners;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.inventory.ItemStack;

public class EpicSpawnersSupport {

	public static ItemStack getSpawner(Block block) {
		if(block.getState() instanceof CreatureSpawner) {
			return EpicSpawners.pl().getApi().newSpawnerItem(((CreatureSpawner) block.getState()).getSpawnedType(), 1);
		}
		return null;
	}

}
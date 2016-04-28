package me.BadBones69.CrazyEnchantments.Enchantments.PickAxes;

import java.util.ArrayList;
import java.util.HashMap;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class AutoSmelt implements Listener{
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		if(!Api.isEnchantmentEnabled("AutoSmelt"))return;
		Block block = e.getBlock();
		Player player = e.getPlayer();
		if(player.getGameMode()!=GameMode.CREATIVE){
			if(getOres().containsKey(block.getType())){
				if(Api.getItemInHand(player)!=null){
					ItemStack item = Api.getItemInHand(player);
					if(item.hasItemMeta()){
						if(item.getItemMeta().hasLore()){
							for(String lore : item.getItemMeta().getLore()){
								if(lore.contains(Api.getEnchName("AutoSmelt"))){
									int power = Api.getPower(lore, Api.getEnchName("AutoSmelt"));
									if(Api.randomPicker(7)){
										e.setCancelled(true);
										ArrayList<ItemStack> items = (ArrayList<ItemStack>) block.getDrops();
										items.add(new ItemStack(getOres().get(block.getType()), power));
										for(ItemStack i : items){
											block.getWorld().dropItemNaturally(block.getLocation(), i);
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
	}
	HashMap<Material, Material> getOres(){
		HashMap<Material, Material> ores = new HashMap<Material, Material>();
		ores.put(Material.COAL_ORE, Material.COAL);
		ores.put(Material.IRON_ORE, Material.IRON_INGOT);
		ores.put(Material.GOLD_ORE, Material.GOLD_INGOT);
		ores.put(Material.DIAMOND_ORE, Material.DIAMOND);
		ores.put(Material.EMERALD_ORE, Material.EMERALD);
		ores.put(Material.REDSTONE_ORE, Material.REDSTONE);
		ores.put(Material.LAPIS_ORE, new ItemStack(Material.INK_SACK,1,(short)4).getType());
		return ores;
	}
}
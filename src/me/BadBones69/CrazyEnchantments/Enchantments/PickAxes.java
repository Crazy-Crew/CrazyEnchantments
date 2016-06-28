package me.BadBones69.CrazyEnchantments.Enchantments;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.BadBones69.CrazyEnchantments.Api;
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.CrazyEnchantments;
import me.BadBones69.CrazyEnchantments.API.Events.EnchantmentUseEvent;

public class PickAxes implements Listener{
	CrazyEnchantments CE = CrazyEnchantments.getInstance();
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		if(!Api.allowsBreak(e.getPlayer()))return;
		Block block = e.getBlock();
		Player player = e.getPlayer();
		if(!Api.canBreakBlock(player, block))return;
		if(player.getGameMode()!=GameMode.CREATIVE){
			if(getOres().containsKey(block.getType())){
				ItemStack item = Api.getItemInHand(player);
				if(CE.hasEnchantments(item)){
					if(CE.hasEnchantment(item, CEnchantments.AUTOSMELT)){
						if(CEnchantments.AUTOSMELT.isEnabled()){
							if(Api.randomPicker(2)){
								EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.AUTOSMELT, item);
								Bukkit.getPluginManager().callEvent(event);
								if(!event.isCancelled()){
									e.setCancelled(true);
									int drop = 0;
									drop+=CE.getPower(item, CEnchantments.AUTOSMELT);
									if(item.getItemMeta().hasEnchants()){
										if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)){
											drop+=item.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
										}
									}
									block.getWorld().dropItem(block.getLocation(), new ItemStack(getOres().get(block.getType()), drop));
									ExperienceOrb orb = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);
									orb.setExperience(Api.percentPick(7, 3));
									block.setType(Material.AIR);
									int dur = item.getDurability()+1;
									item.setDurability((short)dur);
								}
							}
						}
					}
					if(CE.hasEnchantment(item, CEnchantments.EXPERIENCE)){
						if(CEnchantments.EXPERIENCE.isEnabled()){
							int power = CE.getPower(item, CEnchantments.EXPERIENCE);
							if(Api.randomPicker(2)){
								EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.EXPERIENCE, item);
								Bukkit.getPluginManager().callEvent(event);
								if(!event.isCancelled()){
									e.setExpToDrop(e.getExpToDrop()+(power+2));
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
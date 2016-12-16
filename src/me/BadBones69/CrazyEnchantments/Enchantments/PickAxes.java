package me.BadBones69.CrazyEnchantments.Enchantments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.BadBones69.CrazyEnchantments.Main;
import me.BadBones69.CrazyEnchantments.Methods;
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.Events.EnchantmentUseEvent;
import me.BadBones69.CrazyEnchantments.MultiSupport.Support;

public class PickAxes implements Listener{
	
	HashMap<Player, HashMap<Block, BlockFace>> blocks = new HashMap<Player, HashMap<Block, BlockFace>>();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockClick(PlayerInteractEvent e){
		if(e.isCancelled())return;
		Player player = e.getPlayer();
		if(e.getAction() == Action.LEFT_CLICK_BLOCK){
			ItemStack item = Methods.getItemInHand(player);
			Block block = e.getClickedBlock();
			if(Main.CE.hasEnchantments(item)){
				if(Main.CE.hasEnchantment(item, CEnchantments.BLAST)){
					if(CEnchantments.BLAST.isEnabled()){
						HashMap<Block, BlockFace> blockFace = new HashMap<Block, BlockFace>();
						blockFace.put(block, e.getBlockFace());
						blocks.put(player, blockFace);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent e){
		if(e.isCancelled())return;
		Block block = e.getBlock();
		Player player = e.getPlayer();
		ItemStack item = Methods.getItemInHand(player);
		if(Main.CE.hasEnchantments(item)){
			if(player.getGameMode() != GameMode.CREATIVE){
				if(Main.CE.hasEnchantment(item, CEnchantments.AUTOSMELT)){
					if(CEnchantments.AUTOSMELT.isEnabled()){
						if(getOres().containsKey(block.getType())){
							if(Methods.randomPicker(2)){
								EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.AUTOSMELT, item);
								Bukkit.getPluginManager().callEvent(event);
								if(!event.isCancelled()){
									int drop = 0;
									drop += Main.CE.getPower(item, CEnchantments.AUTOSMELT);
									if(item.getItemMeta().hasEnchants()){
										if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)){
											drop+=item.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
										}
									}
									block.getWorld().dropItem(block.getLocation(), new ItemStack(getOres().get(block.getType()), drop));
									ExperienceOrb orb = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);
									orb.setExperience(Methods.percentPick(7, 3));
									block.setType(Material.AIR);
								}
							}
						}
					}
				}
				if(Main.CE.hasEnchantment(item, CEnchantments.FURNACE)){
					if(CEnchantments.FURNACE.isEnabled()){
						if(getOres().containsKey(block.getType())){
							EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.FURNACE, item);
							Bukkit.getPluginManager().callEvent(event);
							if(!event.isCancelled()){
								int drop = 1;
								if(item.getItemMeta().hasEnchants()){
									if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)){
										drop+=item.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
									}
								}
								if(block.getType() == Material.REDSTONE_ORE || block.getType() == Material.COAL_ORE){
									drop+=Methods.percentPick(4, 1);
								}
								block.getWorld().dropItem(block.getLocation(), new ItemStack(getOres().get(block.getType()), drop));
								ExperienceOrb orb = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);
								orb.setExperience(Methods.percentPick(7, 3));
								block.setType(Material.AIR);
							}
						}
					}
				}
				if(Main.CE.hasEnchantment(item, CEnchantments.EXPERIENCE)){
					if(CEnchantments.EXPERIENCE.isEnabled()){
						if(getOres().containsKey(block.getType())){
						int power = Main.CE.getPower(item, CEnchantments.EXPERIENCE);
							if(Methods.randomPicker(2)){
								EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.EXPERIENCE, item);
								Bukkit.getPluginManager().callEvent(event);
								if(!event.isCancelled()){
									e.setExpToDrop(e.getExpToDrop()+(power+2));
								}
							}
						}
					}
				}
			}
			if(Main.CE.hasEnchantment(item, CEnchantments.BLAST)){
				if(CEnchantments.BLAST.isEnabled()){
					if(blocks.containsKey(player)){
						if(blocks.get(player).containsKey(block)){
							for(Block b : getBlocks(block.getLocation(), blocks.get(player).get(block), (Main.CE.getPower(item, CEnchantments.BLAST)-1))){
								if(Main.CE.getBlockList().contains(b.getType())){
									//This stops players from breaking blocks that mite be in protected areas.
									if(Support.canBreakBlock(player, b)&&Support.allowsBreak(b.getLocation())){
										if(player.getGameMode() == GameMode.CREATIVE){
											b.setType(Material.AIR);
										}else{
											b.breakNaturally();
										}
									}
								}
							}
							blocks.remove(player);
						}
					}
				}
			}
		}
	}
	
	private List<Block> getBlocks(Location loc, BlockFace blockFace, Integer depth){
		Location loc2 = loc.clone();
		switch(blockFace){
		case SOUTH:
			loc.add(-1, 1, -depth);
			loc2.add(1, -1, 0);
			break;
		case WEST:
			loc.add(depth, 1, -1);
			loc2.add(0, -1, 1);
			break;
		case EAST:
			loc.add(-depth, 1, 1);
			loc2.add(0, -1, -1);
			break;
		case NORTH:
			loc.add(1, 1, depth);
			loc2.add(-1, -1, 0);
			break;
		case UP:
			loc.add(-1, -depth, -1);
			loc2.add(1, 0, 1);
			break;
		case DOWN:
			loc.add(1, depth, 1);
			loc2.add(-1, 0, -1);
			break;
		default:
			break;
		}
		List<Block> blocks = new ArrayList<Block>();
		int topBlockX = (loc.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc.getBlockX());
		int bottomBlockX = (loc.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc.getBlockX());
		int topBlockY = (loc.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc.getBlockY());
		int bottomBlockY = (loc.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc.getBlockY());
		int topBlockZ = (loc.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc.getBlockZ());
		int bottomBlockZ = (loc.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc.getBlockZ());
		for(int x = bottomBlockX; x <= topBlockX; x++){
			for(int z = bottomBlockZ; z <= topBlockZ; z++){
				for(int y = bottomBlockY; y <= topBlockY; y++){
					Block block = loc.getWorld().getBlockAt(x, y, z);
					blocks.add(block);
				}
			}
		}
		return blocks;
	}
	
	private HashMap<Material, Material> getOres(){
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
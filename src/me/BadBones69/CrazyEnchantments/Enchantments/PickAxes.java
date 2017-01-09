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
import me.BadBones69.CrazyEnchantments.multisupport.Support;

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
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent e){
		if(e.isCancelled())return;
		Block block = e.getBlock();
		Player player = e.getPlayer();
		ItemStack item = Methods.getItemInHand(player);
		if(Main.CE.hasEnchantments(item)){
			if(player.getGameMode() != GameMode.CREATIVE){
				if(Main.CE.hasEnchantment(item, CEnchantments.AUTOSMELT)  && 
						!(Main.CE.hasEnchantment(item, CEnchantments.BLAST) || Main.CE.hasEnchantment(item, CEnchantments.FURNACE) ||
						Main.CE.hasEnchantment(item, CEnchantments.TELEPATHY))){
					if(CEnchantments.AUTOSMELT.isEnabled()){
						if(getOres().containsKey(block.getType())){
							if(Methods.randomPicker(2)){
								EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.AUTOSMELT, item);
								Bukkit.getPluginManager().callEvent(event);
								if(!event.isCancelled()){
									int drop = 0;
									drop += Main.CE.getPower(item, CEnchantments.AUTOSMELT);
									if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)){
										if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)){
											drop += item.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
										}
									}
									block.getWorld().dropItem(block.getLocation(), new ItemStack(getOres().get(block.getType()), drop));
									if(Main.CE.hasEnchantment(item, CEnchantments.EXPERIENCE)){
										if(Methods.randomPicker(2)){
											int power = Main.CE.getPower(item, CEnchantments.EXPERIENCE);
											if(getOres().containsKey(block.getType())){
												ExperienceOrb orb = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);
												orb.setExperience(Methods.percentPick(7, 3) * power);
											}
										}
									}
									block.setType(Material.AIR);
									Methods.removeDurability(item, player);
								}
							}
						}
					}
				}
				if(Main.CE.hasEnchantment(item, CEnchantments.FURNACE ) && 
						!(Main.CE.hasEnchantment(item, CEnchantments.BLAST) || Main.CE.hasEnchantment(item, CEnchantments.TELEPATHY))){
					if(CEnchantments.FURNACE.isEnabled()){
						if(getOres().containsKey(block.getType())){
							EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.FURNACE, item);
							Bukkit.getPluginManager().callEvent(event);
							if(!event.isCancelled()){
								int drop = 1;
								if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)){
									if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)){
										drop += item.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
									}
								}
								if(block.getType() == Material.REDSTONE_ORE || block.getType() == Material.COAL_ORE){
									drop+=Methods.percentPick(4, 1);
								}
								block.getWorld().dropItem(block.getLocation(), new ItemStack(getOres().get(block.getType()), drop));
								if(Main.CE.hasEnchantment(item, CEnchantments.EXPERIENCE)){
									if(Methods.randomPicker(2)){
										int power = Main.CE.getPower(item, CEnchantments.EXPERIENCE);
										if(getOres().containsKey(block.getType())){
											ExperienceOrb orb = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);
											orb.setExperience(Methods.percentPick(7, 3) * power);
										}
									}
								}
								block.setType(Material.AIR);
								Methods.removeDurability(item, player);
							}
						}
					}
				}
				if(Main.CE.hasEnchantment(item, CEnchantments.EXPERIENCE) && 
						!(Main.CE.hasEnchantment(item, CEnchantments.BLAST) || Main.CE.hasEnchantment(item, CEnchantments.TELEPATHY))){
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
							HashMap<ItemStack, Integer> drops = new HashMap<ItemStack, Integer>();
							int xp = 0;
							Boolean fortune = false;
							List<Block> B = getBlocks(block.getLocation(), blocks.get(player).get(block), (Main.CE.getPower(item, CEnchantments.BLAST)-1));
							for(Block b : B){
								if(Main.CE.getBlockList().contains(b.getType())){
									//This stops players from breaking blocks that mite be in protected areas.
									if(Support.canBreakBlock(player, b) && Support.allowsBreak(b.getLocation())){
										if(player.getGameMode() == GameMode.CREATIVE){
											b.setType(Material.AIR);
										}else{
											boolean toggle = true; //True means its air and false means it breaks normaly.
											if(Main.CE.hasEnchantment(item, CEnchantments.TELEPATHY)){
												for(ItemStack drop : b.getDrops()){
													if(Main.CE.hasEnchantment(item, CEnchantments.FURNACE) && getOres().containsKey(b.getType())){
														drop.setType(getOres().get(b.getType()));
														if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)){
															if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)){
																drop.setAmount(1 + item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
																fortune = true;
															}
														}
													}else if(Main.CE.hasEnchantment(item, CEnchantments.AUTOSMELT) && getOres().containsKey(b.getType())){
														if(Methods.randomPicker(2)){
															drop.setType(getOres().get(b.getType()));
															drop.setAmount(1 + Main.CE.getPower(item, CEnchantments.AUTOSMELT));
															if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)){
																if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)){
																	drop.setAmount(drop.getAmount() + item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
																	fortune = true;
																}
															}
														}
													}else{
														if(getItems().contains(b.getType())){
															if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)){
																if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)){
																	drop.setAmount(1 + item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
																	fortune = true;
																}
															}
														}
													}
													if(item.getItemMeta().hasEnchants()){
														if(item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)){
															drop = new ItemStack(b.getType(), 1, b.getData());
														}
													}
													int amount = drop.getAmount();
													if(drops.containsKey(drop)){
														drops.put(drop, drops.get(drop) + amount);
													}else{
														drops.put(drop, amount);
													}
												}
											}else{
												if(Main.CE.hasEnchantment(item, CEnchantments.FURNACE) && getOres().containsKey(b.getType())){
													for(ItemStack drop : b.getDrops()){
														if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)){
															if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)){
																drop.setAmount(1 + item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
																fortune = true;
															}
														}
														b.getWorld().dropItem(b.getLocation(), new ItemStack(getOres().get(b.getType()), drop.getAmount()));
													}
												}else if(Main.CE.hasEnchantment(item, CEnchantments.AUTOSMELT) && getOres().containsKey(b.getType())){
													for(ItemStack drop : b.getDrops()){
														if(Methods.randomPicker(2)){
															drop.setType(getOres().get(b.getType()));
															drop.setAmount(Main.CE.getPower(item, CEnchantments.AUTOSMELT));
															if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)){
																if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)){
																	drop.setAmount(drop.getAmount() + item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
																	fortune = true;
																}
															}
														}
														b.getWorld().dropItem(b.getLocation(), drop);
													}
												}else{
													toggle = false;
												}
											}
											if(Main.CE.hasEnchantment(item, CEnchantments.EXPERIENCE)){
												if(Methods.randomPicker(2)){
													int power = Main.CE.getPower(item, CEnchantments.EXPERIENCE);
													if(getOres().containsKey(b.getType())){
														xp += Methods.percentPick(7, 3) * power;
													}
												}
											}
											if(!fortune){
												for(ItemStack drop : b.getDrops()){
													if(getItems().contains(b.getType())){
														if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)){
															if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)){
																drop.setAmount(1 + item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
															}
														}
													}
												}
											}
											if(toggle){
												b.setType(Material.AIR);
											}else{
												b.breakNaturally();
											}
											Methods.removeDurability(item, player);
										}
									}
								}
							}
							for(ItemStack i : drops.keySet()){
								if(i.getType() == Material.INK_SACK){
									i.setType((new ItemStack(Material.INK_SACK, 1, (short) 4)).getType());
								}
								i.setAmount(drops.get(i));
								if(Methods.isInvFull(player)){
									player.getWorld().dropItem(player.getLocation(), i);
								}else{
									player.getInventory().addItem(i);
								}
							}
							if(xp > 0){
								ExperienceOrb orb = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);
								orb.setExperience(xp);
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
	
	
	private ArrayList<Material> getItems(){
		ArrayList<Material> items = new ArrayList<Material>();
		items.add(Material.COAL_ORE);
		items.add(Material.DIAMOND_ORE);
		items.add(Material.EMERALD_ORE);
		items.add(Material.REDSTONE_ORE);
		items.add(Material.LAPIS_ORE);
		items.add(Material.LONG_GRASS);
		items.add(Material.NETHER_WARTS);
		items.add(Material.GLOWSTONE);
		items.add(Material.GRAVEL);
		items.add(Material.LEAVES);
		return items;
	}
	

}
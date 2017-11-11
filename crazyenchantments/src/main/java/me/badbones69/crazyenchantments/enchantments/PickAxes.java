package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CEnchantments;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.multisupport.AACSupport;
import me.badbones69.crazyenchantments.multisupport.NoCheatPlusSupport;
import me.badbones69.crazyenchantments.multisupport.SpartanSupport;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PickAxes implements Listener {

	private HashMap<Player, HashMap<Block, BlockFace>> blocks = new HashMap<Player, HashMap<Block, BlockFace>>();

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockClick(PlayerInteractEvent e) {
		if(e.isCancelled()) return;
		Player player = e.getPlayer();
		if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
			ItemStack item = Methods.getItemInHand(player);
			Block block = e.getClickedBlock();
			if(Main.CE.hasEnchantments(item)) {
				if(Main.CE.hasEnchantment(item, CEnchantments.BLAST)) {
					if(CEnchantments.BLAST.isEnabled()) {
						HashMap<Block, BlockFace> blockFace = new HashMap<Block, BlockFace>();
						blockFace.put(block, e.getBlockFace());
						blocks.put(player, blockFace);
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlastBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		Block block = e.getBlock();
		ItemStack item = Methods.getItemInHand(player);
		if(blocks.containsKey(player)) {
			if(Main.CE.hasEnchantment(item, CEnchantments.BLAST)) {
				if(CEnchantments.BLAST.isEnabled()) {
					if(blocks.get(player).containsKey(block)) {
						BlockFace face = blocks.get(player).get(block);
						blocks.remove(player);
						HashMap<ItemStack, Integer> drops = new HashMap<ItemStack, Integer>();
						int xp = 0;
						if(SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) {
							NoCheatPlusSupport.exemptPlayer(player);
						}
						if(SupportedPlugins.SPARTAN.isPluginLoaded()) {
							SpartanSupport.cancelNucker(player);
							SpartanSupport.cancelNoSwing(player);
							SpartanSupport.cancelBlockReach(player);
						}
						if(SupportedPlugins.AAC.isPluginLoaded()) {
							AACSupport.exemptPlayer(player);
						}
						Boolean damage = true;
						if(Main.settings.getConfig().contains("Settings.EnchantmentOptions.Blast-Full-Durability")) {
							damage = Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.Blast-Full-Durability");
						}
						for(Block b : getBlocks(block.getLocation(), face, (Main.CE.getPower(item, CEnchantments.BLAST) - 1))) {
							if(Main.CE.getBlockList().contains(b.getType())) {
								BlockBreakEvent event = new BlockBreakEvent(b, player);
								Bukkit.getPluginManager().callEvent(event);
								if(!event.isCancelled()) { //This stops players from breaking blocks that might be in protected areas.
									if(player.getGameMode() == GameMode.CREATIVE) {
										b.setType(Material.AIR);
									}else {
										boolean toggle = true; //True means its air and false means it breaks normaly.
										if(Main.CE.hasEnchantment(item, CEnchantments.TELEPATHY)) {
											for(ItemStack drop : b.getDrops()) {
												if(Main.CE.hasEnchantment(item, CEnchantments.FURNACE) && getOres().containsKey(b.getType())) {
													drop = getOres().get(b.getType());
													if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
														if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
															drop.setAmount(Methods.getRandomNumber(1 + item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)));
														}
													}
												}else if(Main.CE.hasEnchantment(item, CEnchantments.AUTOSMELT) && getOres().containsKey(b.getType())) {
													if(Methods.randomPicker(2)) {
														drop = getOres().get(b.getType());
														drop.setAmount(1 + Main.CE.getPower(item, CEnchantments.AUTOSMELT));
														if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
															if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
																drop.setAmount(drop.getAmount() + Methods.getRandomNumber(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)));
															}
														}
													}
												}else {
													if(getItems().contains(b.getType())) {
														if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
															if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
																drop.setAmount(Methods.getRandomNumber(1 + item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)));
															}
														}
													}
												}
												if(item.getItemMeta().hasEnchants()) {
													if(item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
														if(b.getType() == Material.GLOWING_REDSTONE_ORE) {
															drop = new ItemStack(Material.REDSTONE_ORE, 1, b.getData());
														}else {
															drop = new ItemStack(b.getType(), 1, b.getData());
														}
													}
												}
												int amount = drop.getAmount();
												if(drops.containsKey(drop)) {
													drops.put(drop, drops.get(drop) + amount);
												}else {
													drops.put(drop, amount);
												}
												if(drop.getType() == Material.GLOWING_REDSTONE_ORE || drop.getType() == Material.REDSTONE_ORE || drop.getType() == Material.LAPIS_ORE || drop.getType() == Material.GLOWSTONE) {
													break;
												}
											}
										}else {
											Boolean fortune = false;
											if(Main.CE.hasEnchantment(item, CEnchantments.FURNACE) && getOres().containsKey(b.getType())) {
												for(ItemStack drop : b.getDrops()) {
													drop = getOres().get(b.getType());
													if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
														if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
															drop.setAmount(1 + item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
															fortune = true;
														}
													}
													b.getWorld().dropItem(b.getLocation(), getOres(drop.getAmount()).get(b.getType()));
												}
											}else if(Main.CE.hasEnchantment(item, CEnchantments.AUTOSMELT) && getOres().containsKey(b.getType())) {
												for(ItemStack drop : b.getDrops()) {
													if(Methods.randomPicker(2)) {
														drop = getOres().get(b.getType());
														drop.setAmount(Main.CE.getPower(item, CEnchantments.AUTOSMELT));
														if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
															if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
																drop.setAmount(drop.getAmount() + Methods.getRandomNumber(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)));
																fortune = true;
															}
														}
													}
													b.getWorld().dropItem(b.getLocation(), drop);
												}
											}else {
												if(!fortune) {
													for(ItemStack drop : b.getDrops()) {
														if(getItems().contains(b.getType())) {
															if(item.getItemMeta().hasEnchants()) {
																if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
																	if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
																		drop.setAmount(drop.getAmount() + Methods.getRandomNumber(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)));
																		toggle = false;
																	}
																}
															}
														}
														if(item.getItemMeta().hasEnchants()) {
															if(item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
																if(b.getType() == Material.GLOWING_REDSTONE_ORE) {
																	drop = new ItemStack(Material.REDSTONE_ORE, 1, b.getData());
																}else {
																	drop = new ItemStack(b.getType(), 1, b.getData());
																}
															}
														}
														b.getWorld().dropItem(b.getLocation(), drop);
														toggle = true;
														if(drop.getType() == Material.GLOWING_REDSTONE_ORE || drop.getType() == Material.REDSTONE_ORE || drop.getType() == Material.LAPIS_ORE || drop.getType() == Material.GLOWSTONE) {
															break;
														}
													}
												}
											}
										}
										if(Main.CE.hasEnchantment(item, CEnchantments.EXPERIENCE)) {
											if(Methods.randomPicker(2)) {
												int power = Main.CE.getPower(item, CEnchantments.EXPERIENCE);
												if(getOres().containsKey(b.getType())) {
													xp += Methods.percentPick(7, 3) * power;
												}
											}
										}
										if(toggle) {
											b.setType(Material.AIR);
										}else {
											b.breakNaturally();
										}
										if(damage) {
											Methods.removeDurability(item, player);
										}
									}
								}
							}
						}
						if(!damage) {
							Methods.removeDurability(item, player);
						}
						if(SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) {
							NoCheatPlusSupport.unexemptPlayer(player);
						}
						if(SupportedPlugins.AAC.isPluginLoaded()) {
							AACSupport.unexemptPlayer(player);
						}
						for(ItemStack i : drops.keySet()) {
							if(i.getType() == Material.INK_SACK) {
								i.setType((new ItemStack(Material.INK_SACK, 1, (short) 4)).getType());
							}
							i.setAmount(drops.get(i));
							if(Methods.isInvFull(player)) {
								player.getWorld().dropItem(player.getLocation(), i);
							}else {
								player.getInventory().addItem(i);
							}
						}
						if(xp > 0) {
							ExperienceOrb orb = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);
							orb.setExperience(xp);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent e) {
		if(e.isCancelled()) return;
		Block block = e.getBlock();
		Player player = e.getPlayer();
		ItemStack item = Methods.getItemInHand(player);
		if(Main.CE.hasEnchantments(item)) {
			if(player.getGameMode() != GameMode.CREATIVE) {
				if(Main.CE.hasEnchantment(item, CEnchantments.AUTOSMELT) && !(Main.CE.hasEnchantment(item, CEnchantments.BLAST) || Main.CE.hasEnchantment(item, CEnchantments.FURNACE) || Main.CE.hasEnchantment(item, CEnchantments.TELEPATHY))) {
					if(CEnchantments.AUTOSMELT.isEnabled()) {
						if(getOres().containsKey(block.getType())) {
							if(Methods.randomPicker(2)) {
								EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.AUTOSMELT, item);
								Bukkit.getPluginManager().callEvent(event);
								if(!event.isCancelled()) {
									int drop = 0;
									drop += Main.CE.getPower(item, CEnchantments.AUTOSMELT);
									if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
										if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
											drop += Methods.getRandomNumber(item.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS));
										}
									}
									ItemStack i = getOres(drop).get(block.getType());
									block.getWorld().dropItem(block.getLocation().add(.5, 0, .5), i);
									if(Main.CE.hasEnchantment(item, CEnchantments.EXPERIENCE)) {
										if(Methods.randomPicker(2)) {
											int power = Main.CE.getPower(item, CEnchantments.EXPERIENCE);
											if(getOres().containsKey(block.getType())) {
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
				if(Main.CE.hasEnchantment(item, CEnchantments.FURNACE) && !(Main.CE.hasEnchantment(item, CEnchantments.BLAST) || Main.CE.hasEnchantment(item, CEnchantments.TELEPATHY))) {
					if(CEnchantments.FURNACE.isEnabled()) {
						if(getOres().containsKey(block.getType())) {
							EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.FURNACE, item);
							Bukkit.getPluginManager().callEvent(event);
							if(!event.isCancelled()) {
								int drop = 1;
								if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
									if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
										drop += Methods.getRandomNumber(item.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS));
									}
								}
								if(block.getType() == Material.REDSTONE_ORE || block.getType() == Material.COAL_ORE || block.getType() == Material.LAPIS_ORE) {
									drop += Methods.percentPick(4, 1);
								}
								ItemStack i = getOres(drop).get(block.getType());
								block.getWorld().dropItem(block.getLocation().add(.5, 0, .5), i);
								if(Main.CE.hasEnchantment(item, CEnchantments.EXPERIENCE)) {
									if(Methods.randomPicker(2)) {
										int power = Main.CE.getPower(item, CEnchantments.EXPERIENCE);
										if(getOres().containsKey(block.getType())) {
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
				if(Main.CE.hasEnchantment(item, CEnchantments.EXPERIENCE) && !(Main.CE.hasEnchantment(item, CEnchantments.BLAST) || Main.CE.hasEnchantment(item, CEnchantments.TELEPATHY))) {
					if(CEnchantments.EXPERIENCE.isEnabled()) {
						if(!hasSilkTouch(item)) {
							if(getOres().containsKey(block.getType())) {
								int power = Main.CE.getPower(item, CEnchantments.EXPERIENCE);
								if(Methods.randomPicker(2)) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.EXPERIENCE, item);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										e.setExpToDrop(e.getExpToDrop() + (power + 2));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private Boolean hasSilkTouch(ItemStack item) {
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
				return true;
			}
		}
		return false;
	}

	private List<Block> getBlocks(Location loc, BlockFace blockFace, Integer depth) {
		Location loc2 = loc.clone();
		switch(blockFace) {
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
		for(int x = bottomBlockX; x <= topBlockX; x++) {
			for(int z = bottomBlockZ; z <= topBlockZ; z++) {
				for(int y = bottomBlockY; y <= topBlockY; y++) {
					Block block = loc.getWorld().getBlockAt(x, y, z);
					blocks.add(block);
				}
			}
		}
		return blocks;
	}

	private HashMap<Material, ItemStack> getOres() {
		HashMap<Material, ItemStack> ores = new HashMap<Material, ItemStack>();
		ores.put(Material.COAL_ORE, new ItemStack(Material.COAL));
		ores.put(Material.QUARTZ_ORE, new ItemStack(Material.QUARTZ));
		ores.put(Material.IRON_ORE, new ItemStack(Material.IRON_INGOT));
		ores.put(Material.GOLD_ORE, new ItemStack(Material.GOLD_INGOT));
		ores.put(Material.DIAMOND_ORE, new ItemStack(Material.DIAMOND));
		ores.put(Material.EMERALD_ORE, new ItemStack(Material.EMERALD));
		ores.put(Material.REDSTONE_ORE, new ItemStack(Material.REDSTONE));
		ores.put(Material.GLOWING_REDSTONE_ORE, new ItemStack(Material.REDSTONE));
		ores.put(Material.LAPIS_ORE, new ItemStack(Material.INK_SACK, 1, (short) 4));
		return ores;
	}

	private HashMap<Material, ItemStack> getOres(int amount) {
		HashMap<Material, ItemStack> ores = new HashMap<Material, ItemStack>();
		ores.put(Material.COAL_ORE, new ItemStack(Material.COAL, amount));
		ores.put(Material.QUARTZ_ORE, new ItemStack(Material.QUARTZ, amount));
		ores.put(Material.IRON_ORE, new ItemStack(Material.IRON_INGOT, amount));
		ores.put(Material.GOLD_ORE, new ItemStack(Material.GOLD_INGOT, amount));
		ores.put(Material.DIAMOND_ORE, new ItemStack(Material.DIAMOND, amount));
		ores.put(Material.EMERALD_ORE, new ItemStack(Material.EMERALD, amount));
		ores.put(Material.REDSTONE_ORE, new ItemStack(Material.REDSTONE, amount));
		ores.put(Material.GLOWING_REDSTONE_ORE, new ItemStack(Material.REDSTONE, amount));
		ores.put(Material.LAPIS_ORE, new ItemStack(Material.INK_SACK, amount, (short) 4));
		return ores;
	}

	private ArrayList<Material> getItems() {
		ArrayList<Material> items = new ArrayList<Material>();
		items.add(Material.COAL_ORE);
		items.add(Material.QUARTZ_ORE);
		items.add(Material.DIAMOND_ORE);
		items.add(Material.EMERALD_ORE);
		items.add(Material.REDSTONE_ORE);
		items.add(Material.GLOWING_REDSTONE_ORE);
		items.add(Material.LAPIS_ORE);
		items.add(Material.LONG_GRASS);
		items.add(Material.NETHER_WARTS);
		items.add(Material.GLOWSTONE);
		items.add(Material.GRAVEL);
		items.add(Material.LEAVES);
		return items;
	}

}
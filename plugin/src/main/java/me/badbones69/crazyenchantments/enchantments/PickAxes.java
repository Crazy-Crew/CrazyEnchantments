package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.BlastUseEvent;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.multisupport.AACSupport;
import me.badbones69.crazyenchantments.multisupport.NoCheatPlusSupport;
import me.badbones69.crazyenchantments.multisupport.SpartanSupport;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import me.badbones69.crazyenchantments.multisupport.Version;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PickAxes implements Listener {
	
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	private HashMap<Player, HashMap<Block, BlockFace>> blocks = new HashMap<>();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockClick(PlayerInteractEvent e) {
		if(e.isCancelled()) return;
		Player player = e.getPlayer();
		if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
			ItemStack item = Methods.getItemInHand(player);
			Block block = e.getClickedBlock();
			if(ce.hasEnchantment(item, CEnchantments.BLAST)) {
				if(CEnchantments.BLAST.isActivated()) {
					HashMap<Block, BlockFace> blockFace = new HashMap<>();
					blockFace.put(block, e.getBlockFace());
					blocks.put(player, blockFace);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlastBreak(BlockBreakEvent e) {
		if(e.isCancelled() || ce.getSkippedBreakEvents().contains(e)) return;
		Player player = e.getPlayer();
		Block block = e.getBlock();
		ItemStack item = Methods.getItemInHand(player);
		if(blocks.containsKey(player)) {
			List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
			if(enchantments.contains(CEnchantments.BLAST.getEnchantment())) {
				if(CEnchantments.BLAST.isActivated()) {
					if(blocks.get(player).containsKey(block)) {
						e.setCancelled(true);
						BlockFace face = blocks.get(player).get(block);
						blocks.remove(player);
						List<Block> blockList = getBlocks(block.getLocation(), face, (ce.getLevel(item, CEnchantments.BLAST) - 1));
						BlastUseEvent blastUseEvent = new BlastUseEvent(player, blockList);
						Bukkit.getPluginManager().callEvent(blastUseEvent);
						if(!blastUseEvent.isCancelled()) {
							Location originalBlockLocation = block.getLocation();
							List<Block> finalBlockList = new ArrayList<>();
							for(Block b : blockList) {
								if(b.getType() != Material.AIR) {
									if(ce.getBlockList().contains(b.getType()) || b.getLocation().equals(originalBlockLocation)) {
										BlockBreakEvent event = new BlockBreakEvent(b, player);
										ce.addBreakEvent(event);
										Bukkit.getPluginManager().callEvent(event);
										if(!event.isCancelled()) { //This stops players from breaking blocks that might be in protected areas.
											finalBlockList.add(b);
											ce.removeBreakEvent(event);
										}
									}
								}
							}
							new BukkitRunnable() { // Run async to help offload some lag.
								@Override
								public void run() {
									HashMap<ItemStack, Integer> drops = new HashMap<>();
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
									int xp = 0;
									boolean damage = Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Blast-Full-Durability");
									boolean hasTelepathy = enchantments.contains(CEnchantments.TELEPATHY.getEnchantment());
									boolean hasFurnace = enchantments.contains(CEnchantments.FURNACE.getEnchantment());
									boolean hasLootingBonusBlocks = item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS);
									boolean hasAutoSmelt = enchantments.contains(CEnchantments.AUTOSMELT.getEnchantment());
									boolean hasSilkTouch = item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
									boolean hasExperience = enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment());
									for(Block block : finalBlockList) {
										if(player.getGameMode() == GameMode.CREATIVE) { //If the user is in creative mode.
											new BukkitRunnable() {
												@Override
												public void run() {
													block.setType(Material.AIR);
												}
											}.runTask(ce.getPlugin());
										}else { //If the user is in survival mode.
											if(block.getLocation().equals(originalBlockLocation)) {
												//This is to check if the original block the player broke was in the block list.
												//If it is not then it should be broken and dropped on the ground.
												if(!ce.getBlockList().contains(block.getType())) {
													new BukkitRunnable() {
														@Override
														public void run() {
															block.breakNaturally();
														}
													}.runTask(ce.getPlugin());
													continue;
												}
											}
											boolean toggle = true; //True means its air and false means it breaks normally.
											if(hasTelepathy) {
												for(ItemStack drop : block.getDrops()) {
													if(hasFurnace && isOre(block.getType())) {
														drop = getOreDrop(block.getType());
														if(hasLootingBonusBlocks) {
															if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
																drop.setAmount(Methods.getRandomNumber(1 + item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)));
															}
														}
													}else if(hasAutoSmelt && isOre(block.getType())) {
														if(CEnchantments.AUTOSMELT.chanceSuccessful(item)) {
															drop = getOreDrop(block.getType());
															drop.setAmount(1 + ce.getLevel(item, CEnchantments.AUTOSMELT));
															if(hasLootingBonusBlocks) {
																if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
																	drop.setAmount(drop.getAmount() + Methods.getRandomNumber(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)));
																}
															}
														}
													}else {
														if(getItems().contains(block.getType())) {
															if(hasLootingBonusBlocks) {
																if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
																	drop.setAmount(Methods.getRandomNumber(1 + item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)));
																}
															}
														}
													}
													if(item.getItemMeta().hasEnchants()) {
														if(hasSilkTouch) {
															if(block.getType() == Material.REDSTONE_ORE) {
																drop = new ItemStack(Material.REDSTONE_ORE, 1, block.getData());
															}else {
																drop = new ItemStack(block.getType(), 1, block.getData());
															}
														}
													}
													int amount = drop.getAmount();
													if(drops.containsKey(drop)) {
														drops.put(drop, drops.get(drop) + amount);
													}else {
														drops.put(drop, amount);
													}
													if(drop.getType() == Material.REDSTONE_ORE || drop.getType() == Material.REDSTONE_ORE || drop.getType() == Material.LAPIS_ORE || drop.getType() == Material.GLOWSTONE) {
														break;
													}
												}
											}else {
												boolean fortune = false;
												if(hasFurnace && isOre(block.getType())) {
													for(ItemStack drop : block.getDrops()) {
														drop = getOreDrop(block.getType());
														if(hasLootingBonusBlocks) {
															if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
																drop.setAmount(1 + item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
															}
														}
														ItemStack finalDrop = drop;
														new BukkitRunnable() {
															@Override
															public void run() {
																try {
																	block.getWorld().dropItem(block.getLocation(), getOreDrop(block.getType(), finalDrop.getAmount()));
																}catch(IllegalArgumentException ignore) {
																}
															}
														}.runTask(ce.getPlugin());
													}
												}else if(hasAutoSmelt && isOre(block.getType())) {
													for(ItemStack drop : block.getDrops()) {
														if(CEnchantments.AUTOSMELT.chanceSuccessful(item)) {
															drop = getOreDrop(block.getType());
															drop.setAmount(ce.getLevel(item, CEnchantments.AUTOSMELT));
															if(hasLootingBonusBlocks) {
																if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
																	drop.setAmount(drop.getAmount() + Methods.getRandomNumber(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)));
																}
															}
														}
														ItemStack finalDrop = drop;
														new BukkitRunnable() {
															@Override
															public void run() {
																try {
																	block.getWorld().dropItem(block.getLocation(), finalDrop);
																}catch(IllegalArgumentException ignore) {
																}
															}
														}.runTask(ce.getPlugin());
													}
												}else {
													for(ItemStack drop : block.getDrops()) {
														if(getItems().contains(block.getType())) {
															if(hasLootingBonusBlocks) {
																if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
																	drop.setAmount(drop.getAmount() + Methods.getRandomNumber(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)));
																}
															}
														}
														if(hasSilkTouch) {
															if(block.getType() == Material.REDSTONE_ORE) {
																drop = new ItemStack(Material.REDSTONE_ORE, 1, block.getData());
															}else {
																drop = new ItemStack(block.getType(), 1, block.getData());
															}
														}
														ItemStack finalDrop = drop;
														if(finalDrop.getType() != Material.AIR) {
															new BukkitRunnable() {
																@Override
																public void run() {
																	try {
																		block.getWorld().dropItem(block.getLocation(), finalDrop);
																	}catch(IllegalArgumentException ignore) {
																	}
																}
															}.runTask(ce.getPlugin());
														}
														if(drop.getType() == Material.REDSTONE_ORE || drop.getType() == Material.REDSTONE_ORE || drop.getType() == Material.LAPIS_ORE || drop.getType() == Material.GLOWSTONE) {
															break;
														}
													}
												}
											}
											if(hasExperience) {
												if(CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
													int power = ce.getLevel(item, CEnchantments.EXPERIENCE);
													if(isOre(block.getType())) {
														xp += Methods.percentPick(7, 3) * power;
													}
												}
											}
											new BukkitRunnable() {
												@Override
												public void run() {
													block.setType(Material.AIR);
												}
											}.runTask(ce.getPlugin());
											if(damage) {
												Methods.removeDurability(item, player);
											}
										}
										if(isOre(block.getType())) {
											xp += Methods.percentPick(7, 3);
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
										i.setAmount(drops.get(i));
										if(Methods.isInventoryFull(player)) {
											new BukkitRunnable() {
												@Override
												public void run() {
													try {
														player.getWorld().dropItem(player.getLocation(), i);
													}catch(IllegalArgumentException ignore) {
													}
												}
											}.runTask(ce.getPlugin());
										}else {
											player.getInventory().addItem(i);
										}
									}
									if(player.getGameMode() != GameMode.CREATIVE) {
										if(xp > 0) {
											int finalXp = xp;
											new BukkitRunnable() {
												@Override
												public void run() {
													ExperienceOrb orb = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);
													orb.setExperience(finalXp);
												}
											}.runTask(ce.getPlugin());
										}
									}
								}
							}.runTaskAsynchronously(ce.getPlugin());
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent e) {
		if(e.isCancelled() || ce.getSkippedBreakEvents().contains(e)) return;
		Block block = e.getBlock();
		Player player = e.getPlayer();
		ItemStack item = Methods.getItemInHand(player);
		List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
		if(player.getGameMode() != GameMode.CREATIVE) {
			if(enchantments.contains(CEnchantments.AUTOSMELT.getEnchantment()) && !(enchantments.contains(CEnchantments.BLAST.getEnchantment()) || enchantments.contains(CEnchantments.FURNACE.getEnchantment()) || enchantments.contains(CEnchantments.TELEPATHY.getEnchantment()))) {
				if(CEnchantments.AUTOSMELT.isActivated()) {
					if(isOre(block.getType())) {
						if(CEnchantments.AUTOSMELT.chanceSuccessful(item)) {
							EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.AUTOSMELT, item);
							Bukkit.getPluginManager().callEvent(event);
							if(!event.isCancelled()) {
								int dropAmount = 0;
								dropAmount += ce.getLevel(item, CEnchantments.AUTOSMELT);
								if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
									if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
										dropAmount += Methods.getRandomNumber(item.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS));
									}
								}
								try {
									block.getWorld().dropItem(block.getLocation().add(.5, 0, .5), getOreDrop(block.getType(), dropAmount));
								}catch(IllegalArgumentException ignore) {
								}
								if(enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment())) {
									if(CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
										int power = ce.getLevel(item, CEnchantments.EXPERIENCE);
										if(isOre(block.getType())) {
											ExperienceOrb orb = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);
											orb.setExperience(Methods.percentPick(7, 3) * power);
										}
									}
								}
								if(Version.getCurrentVersion().isNewer(Version.v1_11_R1)) {
									e.setDropItems(false);
								}else {
									block.setType(Material.AIR);
								}
								Methods.removeDurability(item, player);
							}
						}
					}
				}
			}
			if(enchantments.contains(CEnchantments.FURNACE.getEnchantment()) && !(enchantments.contains(CEnchantments.BLAST.getEnchantment()) || enchantments.contains(CEnchantments.TELEPATHY.getEnchantment()))) {
				if(CEnchantments.FURNACE.isActivated()) {
					if(isOre(block.getType())) {
						EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.FURNACE, item);
						Bukkit.getPluginManager().callEvent(event);
						if(!event.isCancelled()) {
							int dropAmount = 1;
							if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
								if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
									dropAmount += Methods.getRandomNumber(item.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS));
								}
							}
							if(block.getType() == Material.REDSTONE_ORE || block.getType() == Material.COAL_ORE || block.getType() == Material.LAPIS_ORE) {
								dropAmount += Methods.percentPick(4, 1);
							}
							try {
								block.getWorld().dropItem(block.getLocation().add(.5, 0, .5), getOreDrop(block.getType(), dropAmount));
							}catch(IllegalArgumentException ignore) {
							}
							if(enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment())) {
								if(CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
									int power = ce.getLevel(item, CEnchantments.EXPERIENCE);
									if(isOre(block.getType())) {
										ExperienceOrb orb = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);
										orb.setExperience(Methods.percentPick(7, 3) * power);
									}
								}
							}
							if(Version.getCurrentVersion().isNewer(Version.v1_11_R1)) {
								e.setDropItems(false);
							}else {
								block.setType(Material.AIR);
							}
							Methods.removeDurability(item, player);
						}
					}
				}
			}
			if(enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment()) && !(enchantments.contains(CEnchantments.BLAST.getEnchantment()) || enchantments.contains(CEnchantments.TELEPATHY.getEnchantment()))) {
				if(CEnchantments.EXPERIENCE.isActivated()) {
					if(!hasSilkTouch(item)) {
						if(isOre(block.getType())) {
							int power = ce.getLevel(item, CEnchantments.EXPERIENCE);
							if(CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
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
	
	private boolean hasSilkTouch(ItemStack item) {
		return item.hasItemMeta() && item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
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
		List<Block> blocks = new ArrayList<>();
		int topBlockX = (Math.max(loc.getBlockX(), loc2.getBlockX()));
		int bottomBlockX = (Math.min(loc.getBlockX(), loc2.getBlockX()));
		int topBlockY = (Math.max(loc.getBlockY(), loc2.getBlockY()));
		int bottomBlockY = (Math.min(loc.getBlockY(), loc2.getBlockY()));
		int topBlockZ = (Math.max(loc.getBlockZ(), loc2.getBlockZ()));
		int bottomBlockZ = (Math.min(loc.getBlockZ(), loc2.getBlockZ()));
		for(int x = bottomBlockX; x <= topBlockX; x++) {
			for(int z = bottomBlockZ; z <= topBlockZ; z++) {
				for(int y = bottomBlockY; y <= topBlockY; y++) {
					blocks.add(loc.getWorld().getBlockAt(x, y, z));
				}
			}
		}
		return blocks;
	}
	
	private boolean isOre(Material material) {
		if(material == ce.getMaterial("NETHER_QUARTZ_ORE", "QUARTZ_ORE")) {
			return true;
		}
		switch(material) {
			case COAL_ORE:
			case IRON_ORE:
			case GOLD_ORE:
			case DIAMOND_ORE:
			case EMERALD_ORE:
			case LAPIS_ORE:
			case REDSTONE_ORE:
				return true;
			default:
				return false;
		}
	}
	
	private ItemStack getOreDrop(Material material) {
		return getOreDrop(material, 1);
	}
	
	private ItemStack getOreDrop(Material material, Integer amount) {
		ItemBuilder dropItem = new ItemBuilder().setAmount(amount);
		if(material == ce.getMaterial("NETHER_QUARTZ_ORE", "QUARTZ_ORE")) {
			dropItem.setMaterial(Material.QUARTZ);
		}else {
			switch(material) {
				case COAL_ORE:
					dropItem.setMaterial(Material.COAL);
					break;
				case IRON_ORE:
					dropItem.setMaterial(Material.IRON_INGOT);
					break;
				case GOLD_ORE:
					dropItem.setMaterial(Material.GOLD_INGOT);
					break;
				case DIAMOND_ORE:
					dropItem.setMaterial(Material.DIAMOND);
					break;
				case EMERALD_ORE:
					dropItem.setMaterial(Material.EMERALD);
					break;
				case LAPIS_ORE:
					dropItem.setMaterial("LAPIS_LAZULI", "INK_SACK:4");
					break;
				case REDSTONE_ORE:
					dropItem.setMaterial(Material.REDSTONE);
					break;
				default:
					dropItem.setMaterial(Material.AIR);
					break;
			}
		}
		return dropItem.build();
	}
	
	private List<Material> getItems() {
		List<Material> items = new ArrayList<>();
		items.add(Material.COAL_ORE);
		items.add(Material.QUARTZ);
		items.add(Material.DIAMOND_ORE);
		items.add(Material.EMERALD_ORE);
		items.add(Material.REDSTONE_ORE);
		items.add(Material.REDSTONE_ORE);
		items.add(Material.LAPIS_ORE);
		items.add(ce.getMaterial("TALL_GRASS", "LONG_GRASS"));
		items.add(ce.getMaterial("NETHER_WART", "NETHER_WARTS"));
		items.add(Material.GLOWSTONE);
		items.add(Material.GRAVEL);
		return items;
	}
	
}
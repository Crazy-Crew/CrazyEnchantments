package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.api.objects.FileManager.Files;
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
			ItemStack item = player.getInventory().getItemInMainHand();
			Block block = e.getClickedBlock();
			if(ce.hasEnchantments(item)) {
				if(ce.hasEnchantment(item, CEnchantments.BLAST)) {
					if(CEnchantments.BLAST.isActivated()) {
						HashMap<Block, BlockFace> blockFace = new HashMap<>();
						blockFace.put(block, e.getBlockFace());
						blocks.put(player, blockFace);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlastBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		Block block = e.getBlock();
		ItemStack item = player.getInventory().getItemInMainHand();
		if(blocks.containsKey(player)) {
			if(ce.hasEnchantment(item, CEnchantments.BLAST)) {
				if(CEnchantments.BLAST.isActivated()) {
					if(blocks.get(player).containsKey(block)) {
						e.setCancelled(true);
						new BukkitRunnable() {// Run async to help offload some lag.
							@Override public void run() {
								BlockFace face = blocks.get(player).get(block);
								blocks.remove(player);
								HashMap<ItemStack, Integer> drops = new HashMap<>();
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
								boolean damage = true;
								if(Files.CONFIG.getFile().contains("Settings.EnchantmentOptions.Blast-Full-Durability")) {
									damage = Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Blast-Full-Durability");
								}
								boolean hasTelepathy = ce.hasEnchantment(item, CEnchantments.TELEPATHY);
								boolean hasFurnace = ce.hasEnchantment(item, CEnchantments.FURNACE);
								boolean hasLootingBonusBlocks = item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS);
								boolean hasAutoSmelt = ce.hasEnchantment(item, CEnchantments.AUTOSMELT);
								boolean hasSilkTouch = item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
								boolean hasExperience = ce.hasEnchantment(item, CEnchantments.EXPERIENCE);
								for(Block block : getBlocks(block.getLocation(), face, (ce.getPower(item, CEnchantments.BLAST) - 1))) {
									if(ce.getBlockList().contains(block.getType())) {
										BlockBreakEvent event = new BlockBreakEvent(block, player);
										Bukkit.getPluginManager().callEvent(event);
										if(!event.isCancelled()) { //This stops players from breaking blocks that might be in protected areas.
											if(player.getGameMode() == GameMode.CREATIVE) {
												new BukkitRunnable() {
													@Override public void run() {
														block.setType(Material.AIR);
													}
												}.runTask(ce.getPlugin());
											}else {
												boolean toggle = true; //True means its air and false means it breaks normaly.
												if(hasTelepathy) {
													for(ItemStack drop : block.getDrops()) {
														if(hasFurnace && getOres().containsKey(block.getType())) {
															drop = getOres().get(block.getType());
															if(hasLootingBonusBlocks) {
																if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
																	drop.setAmount(Methods.getRandomNumber(1 + item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)));
																}
															}
														}else if(hasAutoSmelt && getOres().containsKey(block.getType())) {
															if(CEnchantments.AUTOSMELT.chanceSuccessful(item)) {
																drop = getOres().get(block.getType());
																drop.setAmount(1 + ce.getPower(item, CEnchantments.AUTOSMELT));
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
													Boolean fortune = false;
													if(hasFurnace && getOres().containsKey(block.getType())) {
														for(ItemStack drop : block.getDrops()) {
															drop = getOres().get(block.getType());
															if(hasLootingBonusBlocks) {
																if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
																	drop.setAmount(1 + item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
																}
															}
															ItemStack finalDrop = drop;
															new BukkitRunnable() {
																@Override public void run() {
																	block.getWorld().dropItem(block.getLocation(), getOres(finalDrop.getAmount()).get(block.getType()));
																}
															}.runTask(ce.getPlugin());
														}
													}else if(hasAutoSmelt && getOres().containsKey(block.getType())) {
														for(ItemStack drop : block.getDrops()) {
															if(CEnchantments.AUTOSMELT.chanceSuccessful(item)) {
																drop = getOres().get(block.getType());
																drop.setAmount(ce.getPower(item, CEnchantments.AUTOSMELT));
																if(hasLootingBonusBlocks) {
																	if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
																		drop.setAmount(drop.getAmount() + Methods.getRandomNumber(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)));
																	}
																}
															}
															ItemStack finalDrop = drop;
															new BukkitRunnable() {
																@Override public void run() {
																	block.getWorld().dropItem(block.getLocation(), finalDrop);
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
															new BukkitRunnable() {
																@Override public void run() {
																	block.getWorld().dropItem(block.getLocation(), finalDrop);
																}
															}.runTask(ce.getPlugin());
															if(drop.getType() == Material.REDSTONE_ORE || drop.getType() == Material.REDSTONE_ORE || drop.getType() == Material.LAPIS_ORE || drop.getType() == Material.GLOWSTONE) {
																break;
															}
														}
													}
												}
												if(hasExperience) {
													if(CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
														int power = ce.getPower(item, CEnchantments.EXPERIENCE);
														if(getOres().containsKey(block.getType())) {
															xp += Methods.percentPick(7, 3) * power;
														}
													}
												}
												new BukkitRunnable() {
													@Override public void run() {
														block.setType(Material.AIR);
													}
												}.runTask(ce.getPlugin());
												if(damage) {
													Methods.removeDurability(item, player);
												}
											}
											if(getOres().containsKey(block.getType())) {
												xp += Methods.percentPick(7, 3);
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
									i.setAmount(drops.get(i));
									if(Methods.isInvFull(player)) {
										new BukkitRunnable() {
											@Override public void run() {
												player.getWorld().dropItem(player.getLocation(), i);
											}
										}.runTask(ce.getPlugin());
									}else {
										player.getInventory().addItem(i);
									}
								}
								if(xp > 0) {
									int finalXp = xp;
									new BukkitRunnable() {
										@Override public void run() {
											ExperienceOrb orb = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);
											orb.setExperience(finalXp);
										}
									}.runTask(ce.getPlugin());
								}
							}
						}.runTaskAsynchronously(ce.getPlugin());
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
		ItemStack item = player.getInventory().getItemInMainHand();
		if(ce.hasEnchantments(item)) {
			if(player.getGameMode() != GameMode.CREATIVE) {
				if(ce.hasEnchantment(item, CEnchantments.AUTOSMELT) && !(ce.hasEnchantment(item, CEnchantments.BLAST) || ce.hasEnchantment(item, CEnchantments.FURNACE) || ce.hasEnchantment(item, CEnchantments.TELEPATHY))) {
					if(CEnchantments.AUTOSMELT.isActivated()) {
						if(getOres().containsKey(block.getType())) {
							if(CEnchantments.AUTOSMELT.chanceSuccessful(item)) {
								EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.AUTOSMELT, item);
								Bukkit.getPluginManager().callEvent(event);
								if(!event.isCancelled()) {
									int drop = 0;
									drop += ce.getPower(item, CEnchantments.AUTOSMELT);
									if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
										if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
											drop += Methods.getRandomNumber(item.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS));
										}
									}
									ItemStack i = getOres(drop).get(block.getType());
									block.getWorld().dropItem(block.getLocation().add(.5, 0, .5), i);
									if(ce.hasEnchantment(item, CEnchantments.EXPERIENCE)) {
										if(CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
											int power = ce.getPower(item, CEnchantments.EXPERIENCE);
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
				if(ce.hasEnchantment(item, CEnchantments.FURNACE) && !(ce.hasEnchantment(item, CEnchantments.BLAST) || ce.hasEnchantment(item, CEnchantments.TELEPATHY))) {
					if(CEnchantments.FURNACE.isActivated()) {
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
								if(ce.hasEnchantment(item, CEnchantments.EXPERIENCE)) {
									if(CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
										int power = ce.getPower(item, CEnchantments.EXPERIENCE);
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
				if(ce.hasEnchantment(item, CEnchantments.EXPERIENCE) && !(ce.hasEnchantment(item, CEnchantments.BLAST) || ce.hasEnchantment(item, CEnchantments.TELEPATHY))) {
					if(CEnchantments.EXPERIENCE.isActivated()) {
						if(!hasSilkTouch(item)) {
							if(getOres().containsKey(block.getType())) {
								int power = ce.getPower(item, CEnchantments.EXPERIENCE);
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
	}
	
	private Boolean hasSilkTouch(ItemStack item) {
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
		HashMap<Material, ItemStack> ores = new HashMap<>();
		ores.put(Material.COAL_ORE, new ItemStack(Material.COAL));
		ores.put(Material.QUARTZ, new ItemStack(Material.QUARTZ));
		ores.put(Material.IRON_ORE, new ItemStack(Material.IRON_INGOT));
		ores.put(Material.GOLD_ORE, new ItemStack(Material.GOLD_INGOT));
		ores.put(Material.DIAMOND_ORE, new ItemStack(Material.DIAMOND));
		ores.put(Material.EMERALD_ORE, new ItemStack(Material.EMERALD));
		ores.put(Material.REDSTONE_ORE, new ItemStack(Material.REDSTONE));
		ores.put(Material.LAPIS_ORE, new ItemStack(Material.LAPIS_LAZULI));
		return ores;
	}
	
	private HashMap<Material, ItemStack> getOres(int amount) {
		HashMap<Material, ItemStack> ores = new HashMap<>();
		ores.put(Material.COAL_ORE, new ItemStack(Material.COAL, amount));
		ores.put(Material.QUARTZ, new ItemStack(Material.QUARTZ, amount));
		ores.put(Material.IRON_ORE, new ItemStack(Material.IRON_INGOT, amount));
		ores.put(Material.GOLD_ORE, new ItemStack(Material.GOLD_INGOT, amount));
		ores.put(Material.DIAMOND_ORE, new ItemStack(Material.DIAMOND, amount));
		ores.put(Material.EMERALD_ORE, new ItemStack(Material.EMERALD, amount));
		ores.put(Material.REDSTONE_ORE, new ItemStack(Material.REDSTONE, amount));
		ores.put(Material.LAPIS_ORE, new ItemStack(Material.LAPIS_LAZULI, amount));
		return ores;
	}
	
	private ArrayList<Material> getItems() {
		ArrayList<Material> items = new ArrayList<>();
		items.add(Material.COAL_ORE);
		items.add(Material.QUARTZ);
		items.add(Material.DIAMOND_ORE);
		items.add(Material.EMERALD_ORE);
		items.add(Material.REDSTONE_ORE);
		items.add(Material.REDSTONE_ORE);
		items.add(Material.LAPIS_ORE);
		items.add(Material.TALL_GRASS);
		items.add(Material.NETHER_WART);
		items.add(Material.GLOWSTONE);
		items.add(Material.GRAVEL);
		return items;
	}
	
}

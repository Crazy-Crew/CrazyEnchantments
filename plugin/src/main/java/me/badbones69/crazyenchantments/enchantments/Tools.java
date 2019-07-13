package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Tools implements Listener {
	
	private Random random = new Random();
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		updateEffects(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerClick(PlayerInteractEvent e) {
		updateEffects(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		Player player = e.getPlayer();
		if(e.isCancelled() || ce.getSkippedBreakEvents().contains(e)
		|| block.getType() == Material.AIR
		|| block.getType().toString().toLowerCase().contains("shulker_box")
		|| block.getType().toString().toLowerCase().contains("chest")) {
			return;
		}
		updateEffects(player);
		if(player.getGameMode() != GameMode.CREATIVE) {
			ItemStack item = Methods.getItemInHand(player);
			List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
			if(enchantments.contains(CEnchantments.TELEPATHY.getEnchantment()) && !enchantments.contains(CEnchantments.BLAST.getEnchantment())) {
				if(CEnchantments.TELEPATHY.isActivated()) {
					if(enchantments.contains(CEnchantments.HARVESTER.getEnchantment())) {
						if(Hoes.getHarvesterCrops().contains(block.getType())) {
							return;//This checks if the player is breaking a crop with harvester one. The harvester enchantment will control what happens with telepathy here.
						}
					}
					if(item.getItemMeta().hasEnchants()) {
						if(item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
							if(block.getType() == (ce.useNewMaterial() ? Material.matchMaterial("SPAWNER") : Material.matchMaterial("MOB_SPAWNER"))) {
								return;
							}
						}
					}
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.TELEPATHY, item);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()) {
						HashMap<ItemStack, Integer> drops = new HashMap<>();
						for(ItemStack drop : block.getDrops()) {
							if(enchantments.contains(CEnchantments.FURNACE.getEnchantment()) && isOre(block.getType())) {
								drop = getOreDrop(block.getType());
								if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
									if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
										drop.setAmount(1 + item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
									}
								}
							}else if(enchantments.contains(CEnchantments.AUTOSMELT.getEnchantment()) && isOre(block.getType())) {
								if(CEnchantments.AUTOSMELT.chanceSuccessful(item)) {
									drop = getOreDrop(block.getType());
									drop.setAmount(1 + ce.getLevel(item, CEnchantments.AUTOSMELT));
									if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
										if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
											drop.setAmount(drop.getAmount() + item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
										}
									}
								}
							}else {
								if(getItems().contains(block.getType())) {
									if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
										if(Methods.randomPicker(3)) {
											drop.setAmount(2);
										}
									}
								}
								if(item.getItemMeta().hasEnchants()) {
									if(!item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
										if(getXPOres().contains(block.getType())) {
											if(!enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment())) {
												ExperienceOrb orb = block.getWorld().spawn(block.getLocation().add(.5, .5, .5), ExperienceOrb.class);
												orb.setExperience(Methods.percentPick(7, 3));
											}
										}
									}
								}else {
									if(getXPOres().contains(block.getType())) {
										if(!enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment())) {
											ExperienceOrb orb = block.getWorld().spawn(block.getLocation().add(.5, .5, .5), ExperienceOrb.class);
											orb.setExperience(Methods.percentPick(7, 3));
										}
									}
								}
							}
							if(enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment())) {
								if(CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
									int power = ce.getLevel(item, CEnchantments.EXPERIENCE);
									if(isOre(block.getType())) {
										ExperienceOrb orb = block.getWorld().spawn(block.getLocation().add(.5, .5, .5), ExperienceOrb.class);
										orb.setExperience(Methods.percentPick(7, 3) * power);
									}
								}
							}
							if(block.getType() == Material.SUGAR_CANE) {
								drop.setAmount(0);
								Location loc = block.getLocation();
								for(; loc.getBlock().getType() == Material.SUGAR_CANE; loc.add(0, 1, 0)) ;
								loc.subtract(0, 1, 0);
								for(; loc.getBlock().getType() == Material.SUGAR_CANE; loc.subtract(0, 1, 0)) {
									drop.setAmount(drop.getAmount() + 1);
									loc.getBlock().setType(Material.AIR);
								}
							}
							int amount = drop.getAmount();
							if(drops.containsKey(drop)) {
								drops.put(drop, drops.get(drop) + amount);
							}else {
								drops.put(drop, amount);
							}
						}
						if(item.getItemMeta().hasEnchants()) {
							if(item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
								drops.clear();
								if(block.getType() == Material.REDSTONE_ORE) {
									drops.put(new ItemStack(Material.REDSTONE_ORE, 1, block.getData()), 1);
								}else if(block.getType() == Material.ANVIL) {
									byte data = block.getData();
									if(data == 4) {
										data = 1;
									}else if(data == 8) {
										data = 2;
									}
									drops.put(new ItemStack(block.getType(), 1, data), 1);
								}else {
									drops.put(new ItemStack(block.getType(), 1, block.getData()), 1);
								}
							}
						}
						if(block.getType() == Material.COCOA) {
							drops.put(new ItemBuilder().setMaterial("COCOA_BEANS", "INK_SACK:3").build(),
							ce.getNMSSupport().isFullyGrown(block) ? random.nextInt(2) + 2 : 1);
						}
						for(ItemStack droppedItem : drops.keySet()) {
							if(!ce.useNewMaterial()) {
								if(droppedItem.getType() == Material.matchMaterial("INK_SACK") && droppedItem.getDurability() != 3) {//Changes ink sacks to lapis if on 1.12.2-
									droppedItem.setDurability((short) 4);
								}
							}
							if(droppedItem.getType() == Material.WHEAT || droppedItem.getType() == Material.matchMaterial("BEETROOT_SEEDS")) {
								droppedItem.setAmount(random.nextInt(3));//Wheat and BeetRoots drops 0-3 seeds.
							}else if(droppedItem.getType() == ce.getMaterial("POTATO", "POTATO_ITEM") ||
							droppedItem.getType() == ce.getMaterial("CARROT", "CARROT_ITEM")) {
								droppedItem.setAmount(random.nextInt(4) + 1);//Carrots and Potatoes drop 1-4 of them self's.
							}else {
								droppedItem.setAmount(drops.get(droppedItem));
							}
							if(Methods.isInvFull(player)) {
								player.getWorld().dropItem(player.getLocation(), droppedItem);
							}else {
								player.getInventory().addItem(droppedItem);
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
	
	private void updateEffects(Player player) {
		ItemStack item = Methods.getItemInHand(player);
		if(ce.hasEnchantments(item)) {
			int time = 5 * 20;
			List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
			if(enchantments.contains(CEnchantments.HASTE)) {
				if(CEnchantments.HASTE.isActivated()) {
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.HASTE, item);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()) {
						int power = ce.getLevel(item, CEnchantments.HASTE);
						player.removePotionEffect(PotionEffectType.FAST_DIGGING);
						player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, time, power - 1));
					}
				}
			}
			if(enchantments.contains(CEnchantments.OXYGENATE)) {
				if(CEnchantments.OXYGENATE.isActivated()) {
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.OXYGENATE, item);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()) {
						player.removePotionEffect(PotionEffectType.WATER_BREATHING);
						player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, time, 5));
					}
				}
			}
		}
	}
	
	private ArrayList<Material> getXPOres() {
		ArrayList<Material> ores = new ArrayList<>();
		ores.add(Material.COAL_ORE);
		ores.add(Material.QUARTZ);
		ores.add(Material.DIAMOND_ORE);
		ores.add(Material.EMERALD_ORE);
		ores.add(Material.REDSTONE_ORE);
		ores.add(Material.LAPIS_ORE);
		return ores;
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
		ItemBuilder dropItem = new ItemBuilder();
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
	
	private ArrayList<Material> getItems() {
		ArrayList<Material> items = new ArrayList<>();
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
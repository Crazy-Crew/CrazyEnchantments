package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
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

public class Tools implements Listener {
	
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		updateEffects(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerClick(PlayerInteractEvent e) {
		updateEffects(e.getPlayer());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		Player player = e.getPlayer();
		if(e.isCancelled() || block.getType() == Material.AIR
		|| block.getType().toString().toLowerCase().contains("shulker_box")
		|| block.getType().toString().toLowerCase().contains("chest")) {
			return;
		}
		updateEffects(player);
		if(player.getGameMode() != GameMode.CREATIVE) {
			ItemStack item = Methods.getItemInHand(player);
			if(ce.hasEnchantments(item)) {
				if(ce.hasEnchantment(item, CEnchantments.TELEPATHY) && !ce.hasEnchantment(item, CEnchantments.BLAST)) {
					if(CEnchantments.TELEPATHY.isActivated()) {
						if(item.getItemMeta().hasEnchants()) {
							if(item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
								if(block.getType() == Material.MOB_SPAWNER) {
									return;
								}
							}
						}
						EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.TELEPATHY, item);
						Bukkit.getPluginManager().callEvent(event);
						if(!event.isCancelled()) {
							HashMap<ItemStack, Integer> drops = new HashMap<>();
							for(ItemStack drop : block.getDrops()) {
								if(ce.hasEnchantment(item, CEnchantments.FURNACE) && getOres().containsKey(block.getType())) {
									drop.setType(getOres().get(block.getType()));
									if(item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
										if(Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
											drop.setAmount(1 + item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
										}
									}
								}else if(ce.hasEnchantment(item, CEnchantments.AUTOSMELT) && getOres().containsKey(block.getType())) {
									if(CEnchantments.AUTOSMELT.chanceSuccessful(item)) {
										drop.setType(getOres().get(block.getType()));
										drop.setAmount(1 + ce.getPower(item, CEnchantments.AUTOSMELT));
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
												if(!ce.hasEnchantment(item, CEnchantments.EXPERIENCE)) {
													ExperienceOrb orb = block.getWorld().spawn(block.getLocation().add(.5, .5, .5), ExperienceOrb.class);
													orb.setExperience(Methods.percentPick(7, 3));
												}
											}
										}
									}else {
										if(getXPOres().contains(block.getType())) {
											if(!ce.hasEnchantment(item, CEnchantments.EXPERIENCE)) {
												ExperienceOrb orb = block.getWorld().spawn(block.getLocation().add(.5, .5, .5), ExperienceOrb.class);
												orb.setExperience(Methods.percentPick(7, 3));
											}
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.EXPERIENCE)) {
									if(CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
										int power = ce.getPower(item, CEnchantments.EXPERIENCE);
										if(getOres().containsKey(block.getType())) {
											ExperienceOrb orb = block.getWorld().spawn(block.getLocation().add(.5, .5, .5), ExperienceOrb.class);
											orb.setExperience(Methods.percentPick(7, 3) * power);
										}
									}
								}
								if(block.getType() == Material.SUGAR_CANE_BLOCK) {
									drop.setAmount(0);
									Location loc = block.getLocation();
									for(; loc.getBlock().getType() == Material.SUGAR_CANE_BLOCK; loc.add(0, 1, 0)) ;
									loc.subtract(0, 1, 0);
									for(; loc.getBlock().getType() == Material.SUGAR_CANE_BLOCK; loc.subtract(0, 1, 0)) {
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
									if(block.getType() == Material.GLOWING_REDSTONE_ORE) {
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
							block.setType(Material.AIR);
							Methods.removeDurability(item, player);
						}
					}
				}
			}
		}
	}
	
	private void updateEffects(Player player) {
		ItemStack item = Methods.getItemInHand(player);
		if(ce.hasEnchantments(item)) {
			int time = 5 * 20;
			if(ce.hasEnchantment(item, CEnchantments.HASTE)) {
				if(CEnchantments.HASTE.isActivated()) {
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.HASTE, item);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()) {
						int power = ce.getPower(item, CEnchantments.HASTE);
						player.removePotionEffect(PotionEffectType.FAST_DIGGING);
						player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, time, power - 1));
					}
				}
			}
			if(ce.hasEnchantment(item, CEnchantments.OXYGENATE)) {
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
		ores.add(Material.QUARTZ_ORE);
		ores.add(Material.DIAMOND_ORE);
		ores.add(Material.EMERALD_ORE);
		ores.add(Material.REDSTONE_ORE);
		ores.add(Material.GLOWING_REDSTONE_ORE);
		ores.add(Material.LAPIS_ORE);
		return ores;
	}
	
	private HashMap<Material, Material> getOres() {
		HashMap<Material, Material> ores = new HashMap<>();
		ores.put(Material.COAL_ORE, Material.COAL);
		ores.put(Material.QUARTZ_ORE, Material.QUARTZ);
		ores.put(Material.IRON_ORE, Material.IRON_INGOT);
		ores.put(Material.GOLD_ORE, Material.GOLD_INGOT);
		ores.put(Material.DIAMOND_ORE, Material.DIAMOND);
		ores.put(Material.EMERALD_ORE, Material.EMERALD);
		ores.put(Material.REDSTONE_ORE, Material.REDSTONE);
		ores.put(Material.GLOWING_REDSTONE_ORE, Material.REDSTONE);
		ores.put(Material.LAPIS_ORE, new ItemStack(Material.INK_SACK, 1, (short) 4).getType());
		return ores;
	}
	
	private ArrayList<Material> getItems() {
		ArrayList<Material> items = new ArrayList<>();
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
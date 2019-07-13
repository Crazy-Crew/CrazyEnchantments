package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.multisupport.Version;
import me.badbones69.crazyenchantments.multisupport.particles.ParticleEffect;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Hoes implements Listener {
	
	private List<Material> seedlings;
	private List<Material> crops;
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	private HashMap<UUID, HashMap<Block, BlockFace>> blocks = new HashMap<>();
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if(Version.getCurrentVersion().isNewer(Version.v1_8_R3)) {
			if(e.getHand() != EquipmentSlot.HAND) {
				return;
			}
		}
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = Methods.getItemInHand(player);
			Block block = e.getClickedBlock();
			List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
			if(getSeedlings().contains(block.getType())) {
				if(enchantments.contains(CEnchantments.GREENTHUMB.getEnchantment())) {//Crop is not fully grown
					if(!ce.getNMSSupport().isFullyGrown(block)) {
						if(CEnchantments.GREENTHUMB.chanceSuccessful(item) || player.getGameMode() == GameMode.CREATIVE) {
							ce.getNMSSupport().fullyGrowPlant(block);
							if(Version.getCurrentVersion().isNewer(Version.v1_8_R3)) {
								block.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, block.getLocation().add(.5, .5, .5), 20, .25, .25, .25);
							}else {
								ParticleEffect.VILLAGER_HAPPY.display(.25F, .25F, .25F, 0, 20, block.getLocation().add(.5, .5, .5), 20);
							}
						}
						if(player.getGameMode() != GameMode.CREATIVE) {//Take durability from players not in Creative
							Methods.removeDurability(item, player);
						}
					}
				}
			}
			if(block.getType() == ce.getMaterial("GRASS_BLOCK", "GRASS") || block.getType() == Material.DIRT) {
				if(enchantments.contains(CEnchantments.TILLER.getEnchantment())) {
					for(Block soil : getSoil(player, block)) {
						soil.setType(ce.getMaterial("FARMLAND", "SOIL"));
						for(Block water : getAreaBlocks(soil, 4)) {
							if(water.getType() == Material.WATER || water.getType() == ce.getMaterial("WATER", "STATIONARY_WATER")) {
								ce.getNMSSupport().hydrateSoil(soil);
								break;
							}
						}
						//Take durability from the hoe for each block set to a soil.
						if(player.getGameMode() != GameMode.CREATIVE) {//Take durability from players not in Creative
							Methods.removeDurability(item, player);
						}
					}
				}
			}
		}else if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
			ItemStack item = Methods.getItemInHand(player);
			Block block = e.getClickedBlock();
			if(ce.hasEnchantment(item, CEnchantments.HARVESTER)) {
				if(CEnchantments.HARVESTER.isActivated()) {
					HashMap<Block, BlockFace> blockFace = new HashMap<>();
					blockFace.put(block, e.getBlockFace());
					blocks.put(player.getUniqueId(), blockFace);
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(!e.isCancelled() && !ce.getSkippedBreakEvents().contains(e)) {
			Player player = e.getPlayer();
			Block block = e.getBlock();
			if(getCrops().contains(block.getType())) {
				ItemStack item = Methods.getItemInHand(player);
				List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
				if(!enchantments.isEmpty()) {
					if(enchantments.contains(CEnchantments.HARVESTER.getEnchantment())) {
						if(blocks.containsKey(player.getUniqueId())) {
							BlockFace blockFace = blocks.get(player.getUniqueId()).get(block);
							blocks.remove(player.getUniqueId());
							if(ce.getNMSSupport().isFullyGrown(block)) {
								for(Block crop : getAreaCrops(player, block, blockFace)) {
									crop.breakNaturally();
								}
							}
						}
					}
				}
			}
		}
	}
	
	private List<Material> getSeedlings() {
		if(seedlings == null) {
			seedlings = new ArrayList<>();
			if(Version.getCurrentVersion().isNewer(Version.v1_8_R3) && Version.getCurrentVersion().isOlder(Version.v1_13_R2)) {
				seedlings.add(Material.matchMaterial("BEETROOT_BLOCK"));
			}
			if(Version.getCurrentVersion().isNewer(Version.v1_12_R1)) {
				seedlings.addAll(Arrays.asList(Material.WHEAT,
				Material.matchMaterial("CARROTS"),
				Material.MELON_STEM,
				Material.PUMPKIN_STEM,
				Material.COCOA,
				Material.matchMaterial("BEETROOTS"),
				Material.matchMaterial("POTATOES"),
				Material.matchMaterial("NETHER_WART")));
			}else {
				seedlings.addAll(Arrays.asList(Material.matchMaterial("CROPS"),
				Material.matchMaterial("CARROT"),
				Material.MELON_STEM,
				Material.PUMPKIN_STEM,
				Material.COCOA,
				Material.matchMaterial("POTATO"),
				Material.matchMaterial("NETHER_WARTS")));
			}
		}
		return seedlings;
	}
	
	private List<Material> getCrops() {
		if(crops == null) {
			crops = new ArrayList<>();
			if(Version.getCurrentVersion().isNewer(Version.v1_8_R3) && Version.getCurrentVersion().isOlder(Version.v1_13_R2)) {
				crops.add(Material.matchMaterial("BEETROOT_BLOCK"));
			}
			if(Version.getCurrentVersion().isNewer(Version.v1_12_R1)) {
				crops.addAll(Arrays.asList(Material.WHEAT,
				Material.matchMaterial("CARROTS"),
				Material.COCOA,
				Material.matchMaterial("BEETROOTS"),
				Material.matchMaterial("POTATOES"),
				Material.matchMaterial("NETHER_WART")));
			}else {
				crops.addAll(Arrays.asList(Material.matchMaterial("CROPS"),
				Material.matchMaterial("CARROT"),
				Material.COCOA,
				Material.matchMaterial("POTATO"),
				Material.matchMaterial("NETHER_WARTS")));
			}
		}
		return crops;
	}
	
	private List<Block> getAreaCrops(Player player, Block block, BlockFace blockFace) {
		List<Block> blocks = new ArrayList<>();
		for(Block crop : getAreaBlocks(block, blockFace, 0, 1)) {//Radius of 1 is 3x3
			if(getCrops().contains(crop.getType())) {
				if(ce.getNMSSupport().isFullyGrown(crop)) {
					BlockBreakEvent event = new BlockBreakEvent(crop, player);
					ce.addBreakEvent(event);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()) { //This stops players from breaking blocks that might be in protected areas.
						blocks.add(crop);
						ce.removeBreakEvent(event);
					}
				}
			}
		}
		return blocks;
	}
	
	private List<Block> getSoil(Player player, Block block) {
		Location location = block.getLocation();
		List<Block> soilBlocks = new ArrayList<>();
		for(Block soil : getAreaBlocks(block)) {
			if(soil.getType() == ce.getMaterial("GRASS_BLOCK", "GRASS") || soil.getType() == Material.DIRT) {
				BlockBreakEvent event = new BlockBreakEvent(soil, player);
				ce.addBreakEvent(event);
				Bukkit.getPluginManager().callEvent(event);
				if(!event.isCancelled()) { //This stops players from breaking blocks that might be in protected areas.
					soilBlocks.add(soil);
					ce.removeBreakEvent(event);
				}
			}
		}
		return soilBlocks;
	}
	
	//	private List<Block> getAreaBlocks(Block block) {
	//		Location location = block.getLocation();
	//		return new ArrayList<>(Arrays.asList(
	//		location.clone().add(1, 0, 1).getBlock(),//Top Left
	//		location.clone().add(1, 0, 0).getBlock(),//Top Middle
	//		location.clone().add(1, 0, -1).getBlock(),//Top Right
	//		location.clone().add(0, 0, 1).getBlock(),//Center Left
	//		block,//Center Middle
	//		location.clone().add(0, 0, -1).getBlock(),//Center Right
	//		location.clone().add(-1, 0, 1).getBlock(),//Bottom Left
	//		location.clone().add(-1, 0, 0).getBlock(),//Bottom Middle
	//		location.clone().add(-1, 0, -1).getBlock()//Bottom Right
	//		));
	//	}
	
	private List<Block> getAreaBlocks(Block block) {
		return getAreaBlocks(block, BlockFace.UP, 0, 1);//Radius of 1 is 3x3
	}
	
	private List<Block> getAreaBlocks(Block block, int radius) {
		return getAreaBlocks(block, BlockFace.UP, 0, radius);
	}
	
	private List<Block> getAreaBlocks(Block block, BlockFace blockFace, int depth, int radius) {
		Location loc = block.getLocation();
		Location loc2 = block.getLocation();
		switch(blockFace) {
			case SOUTH:
				loc.add(-radius, radius, -depth);
				loc2.add(radius, -radius, 0);
				break;
			case WEST:
				loc.add(depth, radius, -radius);
				loc2.add(0, -radius, radius);
				break;
			case EAST:
				loc.add(-depth, radius, radius);
				loc2.add(0, -radius, -radius);
				break;
			case NORTH:
				loc.add(radius, radius, depth);
				loc2.add(-radius, -radius, 0);
				break;
			case UP:
				loc.add(-radius, -depth, -radius);
				loc2.add(radius, 0, radius);
				break;
			case DOWN:
				loc.add(radius, depth, radius);
				loc2.add(-radius, 0, -radius);
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
					blocks.add(loc.getWorld().getBlockAt(x, y, z));
				}
			}
		}
		return blocks;
	}
	
}
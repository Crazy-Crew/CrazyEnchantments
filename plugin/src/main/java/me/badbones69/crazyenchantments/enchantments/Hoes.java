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
			if(getSeedlings().contains(block.getType())) {
				if(ce.hasEnchantments(item)) {
					if(ce.hasEnchantment(item, CEnchantments.GREENTHUMB)) {//Crop is not fully grown
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
		if(!e.isCancelled()) {
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
								for(Block crop : getCrops(player, block, blockFace)) {
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
	
	private List<Block> getCrops(Player player, Block block, BlockFace blockFace) {
		Location loc = block.getLocation();
		Location loc2 = block.getLocation();
		int depth = 1;
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
					Block crop = loc.getWorld().getBlockAt(x, y, z);
					if(getCrops().contains(crop.getType())) {
						if(ce.getNMSSupport().isFullyGrown(crop)) {
							BlockBreakEvent event = new BlockBreakEvent(crop, player);
							Bukkit.getPluginManager().callEvent(event);
							if(!event.isCancelled()) { //This stops players from breaking blocks that might be in protected areas.
								blocks.add(crop);
							}
						}
					}
				}
			}
		}
		return blocks;
	}
	
}
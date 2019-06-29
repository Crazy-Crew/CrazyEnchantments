package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.multisupport.Version;
import me.badbones69.crazyenchantments.multisupport.particles.ParticleEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hoes implements Listener {
	
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	private List<Material> materials;
	
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
							if(CEnchantments.GREENTHUMB.chanceSuccessful(item)) {
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
		}
	}
	
	private List<Material> getSeedlings() {
		if(materials == null) {
			materials = new ArrayList<>();
			if(Version.getCurrentVersion().isNewer(Version.v1_8_R3) && Version.getCurrentVersion().isOlder(Version.v1_13_R2)) {
				materials.add(Material.matchMaterial("BEETROOT_BLOCK"));
			}
			if(Version.getCurrentVersion().isNewer(Version.v1_12_R1)) {
				materials.addAll(Arrays.asList(Material.WHEAT,
				Material.matchMaterial("CARROTS"),
				Material.MELON_STEM,
				Material.PUMPKIN_STEM,
				Material.COCOA,
				Material.matchMaterial("BEETROOTS"),
				Material.matchMaterial("POTATOES"),
				Material.matchMaterial("NETHER_WART")));
			}else {
				materials.addAll(Arrays.asList(Material.matchMaterial("CROPS"),
				Material.matchMaterial("CARROT"),
				Material.MELON_STEM,
				Material.PUMPKIN_STEM,
				Material.COCOA,
				Material.matchMaterial("POTATO"),
				Material.matchMaterial("NETHER_WARTS")));
			}
		}
		return materials;
	}
	
}
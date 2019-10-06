package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ProtectionCrystal implements Listener {
	
	private HashMap<UUID, List<ItemStack>> playersItems = new HashMap<>();
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	private static ItemBuilder crystal;
	private static String protectionString;
	
	public static void loadProtectionCrystal() {
		FileConfiguration config = Files.CONFIG.getFile();
		crystal = new ItemBuilder()
		.setMaterial(config.getString("Settings.ProtectionCrystal.Item"))
		.setName(config.getString("Settings.ProtectionCrystal.Name"))
		.setLore(config.getStringList("Settings.ProtectionCrystal.Lore"))
		.setGlowing(config.getBoolean("Settings.ProtectionCrystal.Glowing"));
		protectionString = Methods.color(Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected"));
	}
	
	public static ItemStack getCrystals() {
		return getCrystals(1);
	}
	
	public static ItemStack getCrystals(int amount) {
		return crystal.clone().setAmount(amount).build();
	}
	
	public static boolean isProtected(ItemStack item) {
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasLore()) {
				for(String lore : item.getItemMeta().getLore()) {
					if(lore.contains(protectionString)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Use the ProtectionCrystal.isProtectionSuccessful(Player) method instead.
	 */
	@Deprecated
	public static boolean isSuccessfull(Player player) {
		return isProtectionSuccessful(player);
	}
	
	public static boolean isProtectionSuccessful(Player player) {
		if(player.hasPermission("crazyenchantments.bypass.protectioncrystal")) {
			return true;
		}
		FileConfiguration config = Files.CONFIG.getFile();
		if(config.getBoolean("Settings.ProtectionCrystal.Chance.Toggle")) {
			return Methods.randomPicker(config.getInt("Settings.ProtectionCrystal.Chance.Success-Chance", 100), 100);
		}
		return true;
	}
	
	public static ItemStack removeProtection(ItemStack item) {
		ItemMeta itemMeta = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<>(itemMeta.getLore());
		lore.remove(protectionString);
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
		return item;
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		FileConfiguration config = Files.CONFIG.getFile();
		Player player = (Player) e.getWhoClicked();
		if(e.getInventory() != null) {
			ItemStack crystal = e.getCursor() != null ? e.getCursor() : new ItemStack(Material.AIR);// The Crystal.
			ItemStack item = e.getCurrentItem() != null ? e.getCurrentItem() : new ItemStack(Material.AIR);// The item your adding the protection to.
			if(item.getType() != Material.AIR && crystal.getType() != Material.AIR) {
				//The item getting protected is not stacked.
				if(item.getAmount() == 1) {
					//Making sure they are not dropping crystals on top of other crystals.
					if(!getCrystals().isSimilar(item) && crystal.isSimilar(getCrystals())) {
						//The item does not have protection on it.
						if(!isProtected(item)) {
							//The crystal is not stacked.
							if(crystal.getAmount() > 1) {
								player.sendMessage(Messages.NEED_TO_UNSTACK_ITEM.getMessage());
								return;
							}
							e.setCancelled(true);
							player.setItemOnCursor(Methods.removeItem(crystal));
							e.setCurrentItem(Methods.addLore(item, protectionString));
							player.updateInventory();
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		List<ItemStack> savedItems = new ArrayList<>();
		List<ItemStack> droppedItems = new ArrayList<>();
		for(ItemStack item : e.getDrops()) {
			if(item != null) {
				if(isProtected(item)) {
					if(isProtectionSuccessful(player)) {
						savedItems.add(item);
						continue;
					}
				}
				droppedItems.add(item);
			}
		}
		e.getDrops().clear();
		e.getDrops().addAll(droppedItems);
		playersItems.put(player.getUniqueId(), savedItems);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		if(playersItems.containsKey(player.getUniqueId())) {
			//If the config does not have the option then it will lose the protection by default.
			if(Files.CONFIG.getFile().getBoolean("Settings.ProtectionCrystal.Lose-Protection-On-Death", true)) {
				for(ItemStack item : playersItems.get(player.getUniqueId())) {
					player.getInventory().addItem(removeProtection(item));
				}
			}else {
				for(ItemStack item : playersItems.get(player.getUniqueId())) {
					player.getInventory().addItem(item);
				}
			}
			playersItems.remove(player.getUniqueId());
		}
	}
	
	@EventHandler
	public void onCrystalClick(PlayerInteractEvent e) {
		ItemStack item = Methods.getItemInHand(e.getPlayer());
		if(item != null) {
			if(item.isSimilar(getCrystals())) {
				e.setCancelled(true);
			}
		}
	}
	
}
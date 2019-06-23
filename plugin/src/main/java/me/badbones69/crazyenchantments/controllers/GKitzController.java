package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.objects.CEPlayer;
import me.badbones69.crazyenchantments.api.objects.Cooldown;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.objects.GKitz;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GKitzController implements Listener {
	
	private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	public static void openGUI(Player player) {
		FileConfiguration gkitz = Files.GKITZ.getFile();
		Inventory inv = Bukkit.createInventory(null, gkitz.getInt("Settings.GUI-Size"), Methods.color(gkitz.getString("Settings.Inventory-Name")));
		if(gkitz.contains("Settings.GUI-Customization")) {
			for(String custom : gkitz.getStringList("Settings.GUI-Customization")) {
				String name = "";
				String item = "1";
				int slot = 0;
				ArrayList<String> lore = new ArrayList<>();
				String[] b = custom.split(", ");
				for(String i : b) {
					if(i.contains("Item:")) {
						i = i.replace("Item:", "");
						item = i;
					}
					if(i.contains("Name:")) {
						i = i.replace("Name:", "");
						name = i;
					}
					if(i.contains("Slot:")) {
						i = i.replace("Slot:", "");
						slot = Integer.parseInt(i);
					}
					if(i.contains("Lore:")) {
						i = i.replace("Lore:", "");
						String[] d = i.split(",");
						lore.addAll(Arrays.asList(d));
					}
				}
				slot--;
				inv.setItem(slot, new ItemBuilder().setMaterial(item).setName(name).setLore(lore).build());
			}
		}
		CEPlayer p = ce.getCEPlayer(player);
		for(GKitz kit : ce.getGKitz()) {
			ItemStack displayItem = kit.getDisplayItem().clone();
			ItemMeta m = displayItem.getItemMeta();
			List<String> lore = new ArrayList<>();
			if(displayItem.hasItemMeta()) {
				if(displayItem.getItemMeta().hasLore()) {
					for(String l : displayItem.getItemMeta().getLore()) {
						if(p.canUseGKit(kit)) {
							lore.add(new Cooldown(kit, Calendar.getInstance()).getCooldownLeft(l));
						}else {
							if(p.hasGkitPermission(kit)) {
								lore.add(p.getCooldown(kit).getCooldownLeft(l));
							}else {
								lore.add(new Cooldown(kit, Calendar.getInstance()).getCooldownLeft(l));
							}
						}
					}
				}
			}
			m.setLore(lore);
			displayItem.setItemMeta(m);
			inv.setItem(kit.getSlot() - 1, displayItem);
		}
		player.openInventory(inv);
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if(inv != null) {
			FileConfiguration gkitz = Files.GKITZ.getFile();
			FileConfiguration msg = Files.MESSAGES.getFile();
			Player player = (Player) e.getWhoClicked();
			CEPlayer p = ce.getCEPlayer(player);
			ItemStack item = e.getCurrentItem();
			for(GKitz kit : ce.getGKitz()) {
				if(e.getView().getTitle().equals(Methods.color(kit.getDisplayItem().getItemMeta().getDisplayName()))) {
					e.setCancelled(true);
					if(e.getRawSlot() < inv.getSize()) {
						if(item != null) {
							if(item.hasItemMeta()) {
								if(item.getItemMeta().hasDisplayName()) {
									String name = item.getItemMeta().getDisplayName();
									if(name.equals(Methods.color(msg.getString("Messages.InfoGUI.Categories-Info.Back.Right")))) {
										openGUI(player);
									}
								}
							}
						}
					}
				}
			}
			if(e.getView().getTitle().equals(Methods.color(gkitz.getString("Settings.Inventory-Name")))) {
				e.setCancelled(true);
				if(e.getRawSlot() < inv.getSize()) {
					if(item != null) {
						if(item.hasItemMeta()) {
							if(item.getItemMeta().hasDisplayName()) {
								for(GKitz kit : ce.getGKitz()) {
									String name = kit.getDisplayItem().getItemMeta().getDisplayName();
									if(item.getItemMeta().getDisplayName().equalsIgnoreCase(name)) {
										if(e.getAction() == InventoryAction.PICKUP_HALF) {
											ArrayList<ItemStack> items = kit.getPreviewItems();
											int slots = 9;
											for(int size = items.size(); size >= 9; size -= 9) {
												slots += 9;
											}
											Inventory in = Bukkit.createInventory(null, slots, name);
											for(ItemStack it : items) {
												in.addItem(it);
											}
											in.setItem(slots - 1, new ItemBuilder().setMaterial(Material.PRISMARINE_CRYSTALS).setName(msg.getString("Messages.InfoGUI.Categories-Info.Back.Right")).build());
											player.openInventory(in);
										}else {
											HashMap<String, String> placeholders = new HashMap<>();
											placeholders.put("%kit%", kit.getName());
											if(p.hasGkitPermission(kit)) {
												if(p.canUseGKit(kit)) {
													p.giveGKit(kit);
													p.addCooldown(kit);
													player.sendMessage(Messages.RECEIVED_GKIT.getMessage(placeholders));
													return;
												}else {
													player.sendMessage(Methods.getPrefix() + p.getCooldown(kit).getCooldownLeft(Messages.STILL_IN_COOLDOWN.getMessage(placeholders)));
													return;
												}
											}else {
												player.sendMessage(Messages.NO_GKIT_PERMISSION.getMessage(placeholders));
												return;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
}

package me.badbones69.crazyenchantments.controlers;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CEPlayer;
import me.badbones69.crazyenchantments.api.Cooldown;
import me.badbones69.crazyenchantments.api.GKitz;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.multisupport.Version;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GKitzControler implements Listener {

	public static void openGUI(Player player) {
		FileConfiguration gkitz = Main.settings.getGKitz();
		Inventory inv = Bukkit.createInventory(null, gkitz.getInt("Settings.GUI-Size"), Methods.color(gkitz.getString("Settings.Inventory-Name")));
		if(gkitz.contains("Settings.GUI-Customization")) {
			for(String custom : gkitz.getStringList("Settings.GUI-Customization")) {
				String name = "";
				String item = "1";
				int slot = 0;
				ArrayList<String> lore = new ArrayList<String>();
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
						for(String l : d) {
							lore.add(l);
						}
					}
				}
				slot--;
				inv.setItem(slot, new ItemBuilder().setMaterial(item).setName(name).setLore(lore).build());
			}
		}
		CEPlayer p = Main.CE.getCEPlayer(player);
		for(GKitz kit : Main.CE.getGKitz()) {
			ItemStack displayItem = kit.getDisplayItem().clone();
			ItemMeta m = displayItem.getItemMeta();
			List<String> lore = new ArrayList<String>();
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
			FileConfiguration gkitz = Main.settings.getGKitz();
			FileConfiguration msg = Main.settings.getMessages();
			Player player = (Player) e.getWhoClicked();
			CEPlayer p = Main.CE.getCEPlayer(player);
			ItemStack item = e.getCurrentItem();
			for(GKitz kit : Main.CE.getGKitz()) {
				if(inv.getName().equals(Methods.color(kit.getDisplayItem().getItemMeta().getDisplayName()))) {
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
			if(inv.getName().equals(Methods.color(gkitz.getString("Settings.Inventory-Name")))) {
				e.setCancelled(true);
				if(e.getRawSlot() < inv.getSize()) {
					if(item != null) {
						if(item.hasItemMeta()) {
							if(item.getItemMeta().hasDisplayName()) {
								for(GKitz kit : Main.CE.getGKitz()) {
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
											if(Version.getCurrentVersion().getVersionInteger() < 181) {
												in.setItem(slots - 1, new ItemBuilder().setMaterial(Material.FEATHER).setName(msg.getString("Messages.InfoGUI.Categories-Info.Back.Right")).build());
											}else {
												in.setItem(slots - 1, new ItemBuilder().setMaterial(Material.PRISMARINE_CRYSTALS).setName(msg.getString("Messages.InfoGUI.Categories-Info.Back.Right")).build());
											}
											player.openInventory(in);
										}else {
											if(p.hasGkitPermission(kit)) {
												if(p.canUseGKit(kit)) {
													p.giveGKit(kit);
													p.addCooldown(kit);
													player.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Received-GKit").replaceAll("%Kit%", name).replaceAll("%kit%", name)));
													return;
												}else {
													player.sendMessage(Methods.getPrefix() + p.getCooldown(kit).getCooldownLeft(msg.getString("Messages.Still-In-Cooldown")).replaceAll("%Kit%", name).replaceAll("%kit%", name));
													return;
												}
											}else {
												player.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.No-GKit-Permission").replaceAll("%Kit%", kit.getName()).replaceAll("%kit%", kit.getName())));
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
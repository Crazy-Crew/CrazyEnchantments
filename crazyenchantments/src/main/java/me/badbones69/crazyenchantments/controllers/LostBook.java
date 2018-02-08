package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.objects.FileManager.Files;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LostBook implements Listener {
	
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBookClean(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if(e.getItem() != null) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				ItemStack item = Methods.getItemInHand(player);
				if(item.hasItemMeta()) {
					if(item.getItemMeta().hasDisplayName()) {
						boolean toggle = false;
						String category = null;
						List<String> categories = ce.getCategories();
						for(String cat : categories) {
							String name = Files.CONFIG.getFile().getString("Categories." + cat + ".Name");
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Files.CONFIG.getFile().getString("Settings.LostBook.Name").replaceAll("%Category%", name).replaceAll("%category%", name)))) {
								category = cat;
								toggle = true;
							}
						}
						if(item.getItemMeta().getDisplayName().equals(Methods.color(Files.CONFIG.getFile().getString("Settings.LostBook.Name")))) {
							for(String cat : categories) {
								if(Methods.color(Files.CONFIG.getFile().getString("Categories." + cat + ".Name")).equalsIgnoreCase(EnchantmentControl.getCategory(item))) {
									category = cat;
									toggle = true;
								}
							}
						}
						if(toggle) {
							e.setCancelled(true);
							if(Methods.isInvFull(player)) {
								player.sendMessage(Methods.getPrefix() + Methods.color(Files.MESSAGES.getFile().getString("Messages.Inventory-Full")));
								return;
							}
							Methods.removeItem(item, player);
							ItemStack book = EnchantmentControl.pick(category);
							player.getInventory().addItem(book);
							player.updateInventory();
							player.sendMessage(Methods.getPrefix() + Methods.color(Files.MESSAGES.getFile().getString("Messages.Clean-Lost-Book")
							.replaceAll("%Found%", book.getItemMeta().getDisplayName()).replaceAll("%found%", book.getItemMeta().getDisplayName())));
							if(Files.CONFIG.getFile().contains("Categories." + category + ".LostBook.FireworkToggle")) {
								if(Files.CONFIG.getFile().contains("Categories." + category + ".LostBook.FireworkColors")) {
									if(Files.CONFIG.getFile().getBoolean("Categories." + category + ".LostBook.FireworkToggle")) {
										ArrayList<Color> colors = new ArrayList<>();
										String Cs = Files.CONFIG.getFile().getString("Categories." + category + ".LostBook.FireworkColors");
										if(Cs.contains(", ")) {
											for(String color : Cs.split(", ")) {
												Color c = Methods.getColor(color);
												if(c != null) {
													colors.add(c);
												}
											}
										}else {
											Color c = Methods.getColor(Cs);
											if(c != null) {
												colors.add(c);
											}
										}
										Methods.fireWork(player.getLocation().add(0, 1, 0), colors);
									}
								}
							}
							Sound sound = getSound(category);
							if(sound != null) {
								player.playSound(player.getLocation(), sound, 1, 1);
							}
						}
					}
				}
			}
		}
	}
	
	public static ItemStack getLostBook(String cat, int amount) {
		String id = Files.CONFIG.getFile().getString("Settings.LostBook.Item");
		String name = Files.CONFIG.getFile().getString("Settings.LostBook.Name");
		List<String> lore = new ArrayList<>();
		String tn = Files.CONFIG.getFile().getString("Categories." + cat + ".Name");
		name = name.replaceAll("%Category%", tn).replaceAll("%category%", tn);
		for(String l : Files.CONFIG.getFile().getStringList("Settings.LostBook.Lore")) {
			lore.add(l.replaceAll("%Category%", tn).replaceAll("%category%", tn));
		}
		return new ItemBuilder().setMaterial(id).setAmount(amount).setName(name).setLore(lore).build();
	}
	
	private Sound getSound(String category) {
		FileConfiguration config = Files.CONFIG.getFile();
		if(config.contains("Categories." + category + ".LostBook.Sound-Toggle") && config.contains("Categories." + category + ".LostBook.Sound")) {
			if(config.getBoolean("Categories." + category + ".LostBook.Sound-Toggle")) {
				return Sound.valueOf(config.getString("Categories." + category + ".LostBook.Sound"));
			}
		}
		return null;
	}
}
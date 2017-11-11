package me.badbones69.crazyenchantments.controlers;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
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
import java.util.Set;

public class LostBook implements Listener {

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
						Set<String> categories = Main.settings.getConfig().getConfigurationSection("Categories").getKeys(false);
						for(String C : categories) {
							String name = Main.settings.getConfig().getString("Categories." + C + ".Name");
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.LostBook.Name").replaceAll("%Category%", name).replaceAll("%category%", name)))) {
								category = C;
								toggle = true;
							}
						}
						if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.LostBook.Name")))) {
							for(String C : categories) {
								if(Methods.color(Main.settings.getConfig().getString("Categories." + C + ".Name")).equalsIgnoreCase(EnchantmentControl.getCategory(item))) {
									category = C;
									toggle = true;
								}
							}
						}
						if(toggle) {
							e.setCancelled(true);
							if(Methods.isInvFull(player)) {
								player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Inventory-Full")));
								return;
							}
							Methods.removeItem(item, player);
							ItemStack book = EnchantmentControl.pick(category);
							player.getInventory().addItem(book);
							player.updateInventory();
							player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Clean-Lost-Book").replaceAll("%Found%", book.getItemMeta().getDisplayName()).replaceAll("%found%", book.getItemMeta().getDisplayName())));
							if(Main.settings.getConfig().contains("Categories." + category + ".LostBook.FireworkToggle")) {
								if(Main.settings.getConfig().contains("Categories." + category + ".LostBook.FireworkColors")) {
									if(Main.settings.getConfig().getBoolean("Categories." + category + ".LostBook.FireworkToggle")) {
										ArrayList<Color> colors = new ArrayList<Color>();
										String Cs = Main.settings.getConfig().getString("Categories." + category + ".LostBook.FireworkColors");
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
							return;
						}
					}
				}
			}
		}
	}

	public static ItemStack getLostBook(String category, int amount) {
		String id = Main.settings.getConfig().getString("Settings.LostBook.Item");
		String name = Main.settings.getConfig().getString("Settings.LostBook.Name");
		List<String> lore = new ArrayList<String>();
		String tn = Main.settings.getConfig().getString("Categories." + category + ".Name");
		name = name.replaceAll("%Category%", tn).replaceAll("%category%", tn);
		for(String l : Main.settings.getConfig().getStringList("Settings.LostBook.Lore")) {
			lore.add(l.replaceAll("%Category%", tn).replaceAll("%category%", tn));
		}
		//		return Methods.makeItem(id, amount, name, lore);
		return new ItemBuilder().setMaterial(id).setAmount(amount).setName(name).setLore(lore).build();
	}

	private Sound getSound(String category) {
		FileConfiguration config = Main.settings.getConfig();
		if(config.contains("Categories." + category + ".LostBook.Sound-Toggle") && config.contains("Categories." + category + ".LostBook.Sound")) {
			if(config.getBoolean("Categories." + category + ".LostBook.Sound-Toggle")) {
				Sound sound = Sound.valueOf(config.getString("Categories." + category + ".LostBook.Sound"));
				if(sound != null) {
					return sound;
				}
			}
		}
		return null;
	}

}
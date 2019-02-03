package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.Dust;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.FileManager.Files;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DustControl implements Listener {
	
	private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		Player player = (Player) e.getWhoClicked();
		if(inv != null) {
			if(e.getCurrentItem() != null) {
				if(e.getCursor() != null) {
					ItemStack book = e.getCurrentItem();
					ItemStack dust = e.getCursor();
					if(book.getAmount() == 1) {
						if(book.hasItemMeta() && dust.hasItemMeta()) {
							if(book.getItemMeta().hasLore() && dust.getItemMeta().hasLore()) {
								if(book.getItemMeta().hasDisplayName() && dust.getItemMeta().hasDisplayName()) {
									if(book.getType() == ce.getEnchantmentBookItem().getType()) {
										boolean toggle = false;
										String name = book.getItemMeta().getDisplayName();
										for(CEnchantment en : ce.getRegisteredEnchantments()) {
											if(name.contains(Methods.color(en.getBookColor() + en.getCustomName()))) {
												toggle = true;
											}
										}
										if(!toggle) {
											return;
										}
										if(dust.getItemMeta().getDisplayName().equals(Methods.color(Files.CONFIG.getFile().getString("Settings.Dust.SuccessDust.Name")))) {
											if(dust.getType() == new ItemBuilder().setMaterial(Files.CONFIG.getFile().getString("Settings.Dust.SuccessDust.Item")).getMaterial()) {
												int per = getPercent(Dust.SUCCESS_DUST, dust);
												if(Methods.hasArgument("%Success_Rate%", Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"))) {
													int total = Methods.getPercent("%Success_Rate%", book, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"));
													if(total >= 100) return;
													if(player.getGameMode() == GameMode.CREATIVE && dust.getAmount() > 1) {
														player.sendMessage(Methods.getPrefix() + Methods.color("&cPlease unstack the dust for them to work."));
														return;
													}
													per += total;
													if(per < 0) per = 0;
													if(per > 100) per = 100;
													e.setCancelled(true);
													setLore(book, per, "Success");
													player.setItemOnCursor(Methods.removeItem(dust));
													player.updateInventory();
												}
												return;
											}
										}
										if(dust.getItemMeta().getDisplayName().equals(Methods.color(Files.CONFIG.getFile().getString("Settings.Dust.DestroyDust.Name")))) {
											if(dust.getType() == new ItemBuilder().setMaterial(Files.CONFIG.getFile().getString("Settings.Dust.DestroyDust.Item")).getMaterial()) {
												int per = getPercent(Dust.DESTROY_DUST, dust);
												if(Methods.hasArgument("%Destroy_Rate%", Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"))) {
													int total = Methods.getPercent("%Destroy_Rate%", book, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"));
													if(total <= 0) return;
													if(player.getGameMode() == GameMode.CREATIVE && dust.getAmount() > 1) {
														player.sendMessage(Methods.getPrefix() + Methods.color("&cPlease unstack the dust for them to work."));
														return;
													}
													per = total - per;
													if(per < 0) per = 0;
													if(per > 100) per = 100;
													e.setCancelled(true);
													setLore(book, per, "Destroy");
													player.setItemOnCursor(Methods.removeItem(dust));
													player.updateInventory();
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
	
	@EventHandler
	public void openDust(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		FileConfiguration config = Files.CONFIG.getFile();
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = player.getInventory().getItemInMainHand();
			if(item != null) {
				if(hasPercent(Dust.SUCCESS_DUST, item)) {
					if(Methods.isSimilar(item, Dust.SUCCESS_DUST.getDust(getPercent(Dust.SUCCESS_DUST, item), 1))) {
						e.setCancelled(true);
					}
				}else if(hasPercent(Dust.DESTROY_DUST, item)) {
					if(Methods.isSimilar(item, Dust.DESTROY_DUST.getDust(getPercent(Dust.DESTROY_DUST, item), 1))) {
						e.setCancelled(true);
					}
				}else if(hasPercent(Dust.MYSTERY_DUST, item)) {
					if(Methods.isSimilar(item, Dust.MYSTERY_DUST.getDust(getPercent(Dust.MYSTERY_DUST, item), 1))) {
						e.setCancelled(true);
						Methods.setItemInHand(player, Methods.removeItem(item));
						player.getInventory().addItem(pickDust().getDust(Methods.percentPick(getPercent(Dust.MYSTERY_DUST, item) + 1, 1)));
						player.updateInventory();
						player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1, 1);
						if(config.contains("Settings.Dust.MysteryDust.Firework.Toggle")) {
							if(config.contains("Settings.Dust.MysteryDust.Firework.Colors")) {
								if(config.getBoolean("Settings.Dust.MysteryDust.Firework.Toggle")) {
									ArrayList<Color> colors = new ArrayList<>();
									String Cs = config.getString("Settings.Dust.MysteryDust.Firework.Colors");
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
					}
				}
			}
		}
	}
	
	private static void setLore(ItemStack item, int percent, String rate) {
		ItemMeta m = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<>();
		CEnchantment enchantment = null;
		for(CEnchantment en : ce.getRegisteredEnchantments()) {
			String ench = en.getCustomName();
			if(item.getItemMeta().getDisplayName().contains(ench)) {
				enchantment = en;
			}
		}
		for(String l : Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore")) {
			boolean line = true;
			if(l.contains("%Description%") || l.contains("%description%")) {
				if(enchantment != null) {
					for(String L : enchantment.getInfoDescription()) {
						lore.add(Methods.color(L));
					}
				}
				line = false;
			}
			if(rate.equalsIgnoreCase("Success")) {
				l = l.replaceAll("%Success_Rate%", percent + "").replaceAll("%success_rate%", percent + "")
				.replaceAll("%Destroy_Rate%", Methods.getPercent("%Destroy_Rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore")) + "")
				.replaceAll("%destroy_rate%", Methods.getPercent("%destroy_rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore")) + "");
			}else {
				l = l.replaceAll("%Destroy_Rate%", percent + "").replaceAll("%destroy_rate%", percent + "")
				.replaceAll("%Success_Rate%", Methods.getPercent("%Success_Rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore")) + "")
				.replaceAll("%success_rate%", Methods.getPercent("%success_rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore")) + "");
			}
			if(line) {
				lore.add(Methods.color(l));
			}
		}
		m.setLore(lore);
		item.setItemMeta(m);
	}
	
	private Dust pickDust() {
		Random r = new Random();
		List<Dust> dusts = new ArrayList<>();
		if(Files.CONFIG.getFile().getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Success")) {
			dusts.add(Dust.SUCCESS_DUST);
		}
		if(Files.CONFIG.getFile().getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Destroy")) {
			dusts.add(Dust.DESTROY_DUST);
		}
		if(Files.CONFIG.getFile().getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Failed")) {
			dusts.add(Dust.FAILED_DUST);
		}
		return dusts.get(r.nextInt(dusts.size()));
	}
	
	public static Boolean hasPercent(Dust dust, ItemStack item) {
		String arg = "";
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasLore()) {
				List<String> lore = item.getItemMeta().getLore();
				List<String> L = Files.CONFIG.getFile().getStringList("Settings.Dust." + dust.getConfigName() + ".Lore");
				int i = 0;
				if(lore != null && L != null) {
					if(lore.size() == L.size()) {
						for(String l : L) {
							l = Methods.color(l);
							String lo = lore.get(i);
							if(l.contains("%Percent%")) {
								String[] b = l.split("%Percent%");
								if(b.length >= 1) {
									arg = lo.replace(b[0], "");
								}
								if(b.length >= 2) {
									arg = arg.replace(b[1], "");
								}
								break;
							}
							if(l.contains("%percent%")) {
								String[] b = l.split("%percent%");
								if(b.length >= 1) arg = lo.replace(b[0], "");
								if(b.length >= 2) arg = arg.replace(b[1], "");
								break;
							}
							i++;
						}
					}
				}
			}
		}
		return Methods.isInt(arg);
	}
	
	public static Integer getPercent(Dust dust, ItemStack item) {
		String arg = "";
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasLore()) {
				List<String> lore = item.getItemMeta().getLore();
				List<String> L = Files.CONFIG.getFile().getStringList("Settings.Dust." + dust.getConfigName() + ".Lore");
				int i = 0;
				if(lore != null && L != null) {
					if(lore.size() == L.size()) {
						for(String l : L) {
							l = Methods.color(l);
							String lo = lore.get(i);
							if(l.contains("%Percent%")) {
								String[] b = l.split("%Percent%");
								if(b.length >= 1) {
									arg = lo.replace(b[0], "");
								}
								if(b.length >= 2) {
									arg = arg.replace(b[1], "");
								}
								break;
							}
							if(l.contains("%percent%")) {
								String[] b = l.split("%percent%");
								if(b.length >= 1) arg = lo.replace(b[0], "");
								if(b.length >= 2) arg = arg.replace(b[1], "");
								break;
							}
							i++;
						}
					}
				}
			}
		}
		if(Methods.isInt(arg)) {
			return Integer.parseInt(arg);
		}else {
			return 0;
		}
	}
	
}
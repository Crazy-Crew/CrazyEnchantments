package me.badbones69.crazyenchantments.controlers;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CEnchantments;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.multisupport.Version;
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
									if(book.getType() == Main.CE.getEnchantmentBookItem().getType()) {
										Boolean toggle = false;
										String name = book.getItemMeta().getDisplayName();
										for(CEnchantments en : Main.CE.getEnchantments()) {
											if(name.contains(Methods.color(en.getBookColor() + en.getCustomName()))) {
												toggle = true;
											}
										}
										for(String en : Main.CustomE.getEnchantments()) {
											if(name.contains(Methods.color(Main.CustomE.getBookColor(en) + Main.CustomE.getCustomName(en)))) {
												toggle = true;
											}
										}
										if(!toggle) {
											return;
										}
										if(dust.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.Dust.SuccessDust.Name")))) {
											if(dust.getType() == new ItemBuilder().setMaterial(Main.settings.getConfig().getString("Settings.Dust.SuccessDust.Item")).getMaterial()) {
												int per = getPercent("SuccessDust", dust);
												if(Methods.hasArgument("%Success_Rate%", Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore"))) {
													int total = Methods.getPercent("%Success_Rate%", book, Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore"));
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
										if(dust.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.Dust.DestroyDust.Name")))) {
											if(dust.getType() == new ItemBuilder().setMaterial(Main.settings.getConfig().getString("Settings.Dust.DestroyDust.Item")).getMaterial()) {
												int per = getPercent("DestroyDust", dust);
												if(Methods.hasArgument("%Destroy_Rate%", Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore"))) {
													int total = Methods.getPercent("%Destroy_Rate%", book, Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore"));
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

	@EventHandler
	public void openDust(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		FileConfiguration config = Main.settings.getConfig();
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(Methods.getItemInHand(player) != null) {
				ItemStack item = Methods.getItemInHand(player);
				if(hasPercent("SuccessDust", item)) {
					if(Methods.isSimilar(item, getDust("SuccessDust", 1, getPercent("SuccessDust", item)))) {
						e.setCancelled(true);
					}
				}else if(hasPercent("DestroyDust", item)) {
					if(Methods.isSimilar(item, getDust("DestroyDust", 1, getPercent("DestroyDust", item)))) {
						e.setCancelled(true);
					}
				}else if(hasPercent("MysteryDust", item)) {
					if(Methods.isSimilar(item, getDust("MysteryDust", 1, getPercent("MysteryDust", item)))) {
						e.setCancelled(true);
						Methods.setItemInHand(player, Methods.removeItem(item));
						player.getInventory().addItem(getDust(pickDust(), 1, Methods.percentPick(getPercent("MysteryDust", item) + 1, 1)));
						player.updateInventory();
						try {
							if(Version.getCurrentVersion().getVersionInteger() >= 191) {
								player.playSound(player.getLocation(), Sound.valueOf("BLOCK_LAVA_POP"), 1, 1);
							}else {
								player.playSound(player.getLocation(), Sound.valueOf("LAVA_POP"), 1, 1);
							}
						}catch(Exception ex) {
						}
						if(config.contains("Settings.Dust.MysteryDust.Firework.Toggle")) {
							if(config.contains("Settings.Dust.MysteryDust.Firework.Colors")) {
								if(config.getBoolean("Settings.Dust.MysteryDust.Firework.Toggle")) {
									ArrayList<Color> colors = new ArrayList<Color>();
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
		ArrayList<String> lore = new ArrayList<String>();
		CEnchantments enchantment = null;
		for(CEnchantments en : Main.CE.getEnchantments()) {
			String ench = en.getCustomName();
			if(item.getItemMeta().getDisplayName().contains(ench)) {
				enchantment = en;
			}
		}
		String cEnchantment = "";
		for(String en : Main.CustomE.getEnchantments()) {
			String ench = Main.CustomE.getCustomName(en);
			if(item.getItemMeta().getDisplayName().contains(ench)) {
				cEnchantment = en;
			}
		}
		for(String l : Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore")) {
			Boolean line = true;
			if(l.contains("%Description%") || l.contains("%description%")) {
				if(enchantment != null) {
					for(String L : enchantment.getDiscription()) {
						lore.add(Methods.color(L));
					}
				}else {
					for(String L : Main.CustomE.getDiscription(cEnchantment)) {
						lore.add(Methods.color(L));
					}
				}
				line = false;
			}
			if(rate.equalsIgnoreCase("Success")) {
				l = l.replaceAll("%Success_Rate%", percent + "").replaceAll("%success_rate%", percent + "").replaceAll("%Destroy_Rate%", Methods.getPercent("%Destroy_Rate%", item, Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore")) + "").replaceAll("%destroy_rate%", Methods.getPercent("%destroy_rate%", item, Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore")) + "");
			}else {
				l = l.replaceAll("%Destroy_Rate%", percent + "").replaceAll("%destroy_rate%", percent + "").replaceAll("%Success_Rate%", Methods.getPercent("%Success_Rate%", item, Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore")) + "").replaceAll("%success_rate%", Methods.getPercent("%success_rate%", item, Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore")) + "");
			}
			if(line) {
				lore.add(Methods.color(l));
			}
		}
		m.setLore(lore);
		item.setItemMeta(m);
	}

	public static ItemStack getDust(String Dust, int amount) {
		String id = Main.settings.getConfig().getString("Settings.Dust." + Dust + ".Item");
		String name = Main.settings.getConfig().getString("Settings.Dust." + Dust + ".Name");
		List<String> lore = new ArrayList<String>();
		int max = Main.settings.getConfig().getInt("Settings.Dust." + Dust + ".PercentRange.Max");
		int min = Main.settings.getConfig().getInt("Settings.Dust." + Dust + ".PercentRange.Min");
		int percent = Methods.percentPick(max, min);
		for(String l : Main.settings.getConfig().getStringList("Settings.Dust." + Dust + ".Lore")) {
			lore.add(l.replaceAll("%Percent%", percent + "").replaceAll("%percent%", percent + ""));
		}
		return new ItemBuilder().setMaterial(id).setAmount(amount).setName(name).setLore(lore).build();
	}

	public static ItemStack getMysteryDust(int amount) {
		String id = Main.settings.getConfig().getString("Settings.Dust.MysteryDust.Item");
		String name = Main.settings.getConfig().getString("Settings.Dust.MysteryDust.Name");
		List<String> lore = new ArrayList<String>();
		int max = Main.settings.getConfig().getInt("Settings.Dust.MysteryDust.PercentRange.Max");
		for(String l : Main.settings.getConfig().getStringList("Settings.Dust.MysteryDust.Lore")) {
			lore.add(l.replaceAll("%Percent%", max + "").replaceAll("%percent%", max + ""));
		}
		return new ItemBuilder().setMaterial(id).setAmount(amount).setName(name).setLore(lore).build();
	}

	public static ItemStack getDust(String Dust, int amount, int percent) {
		String id = Main.settings.getConfig().getString("Settings.Dust." + Dust + ".Item");
		String name = Main.settings.getConfig().getString("Settings.Dust." + Dust + ".Name");
		List<String> lore = new ArrayList<String>();
		for(String l : Main.settings.getConfig().getStringList("Settings.Dust." + Dust + ".Lore")) {
			lore.add(l.replaceAll("%Percent%", percent + "").replaceAll("%percent%", percent + ""));
		}
		return new ItemBuilder().setMaterial(id).setAmount(amount).setName(name).setLore(lore).build();
	}

	private String pickDust() {
		Random r = new Random();
		List<String> dusts = new ArrayList<String>();
		if(Main.settings.getConfig().getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Success")) {
			dusts.add("SuccessDust");
		}
		if(Main.settings.getConfig().getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Destroy")) {
			dusts.add("DestroyDust");
		}
		if(Main.settings.getConfig().getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Failed")) {
			dusts.add("FailedDust");
		}
		return dusts.get(r.nextInt(dusts.size()));
	}

	public static Boolean hasPercent(String dust, ItemStack item) {
		String arg = "";
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasLore()) {
				List<String> lore = item.getItemMeta().getLore();
				List<String> L = Main.settings.getConfig().getStringList("Settings.Dust." + dust + ".Lore");
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

	public static Integer getPercent(String dust, ItemStack item) {
		String arg = "";
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasLore()) {
				List<String> lore = item.getItemMeta().getLore();
				List<String> L = Main.settings.getConfig().getStringList("Settings.Dust." + dust + ".Lore");
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
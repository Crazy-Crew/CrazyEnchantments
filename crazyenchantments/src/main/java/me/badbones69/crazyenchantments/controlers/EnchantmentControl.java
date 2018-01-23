package me.badbones69.crazyenchantments.controlers;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CEnchantments;
import me.badbones69.crazyenchantments.api.EnchantmentType;
import me.badbones69.crazyenchantments.api.events.ArmorEquipEvent;
import me.badbones69.crazyenchantments.api.events.ArmorEquipEvent.EquipMethod;
import me.badbones69.crazyenchantments.api.events.ArmorType;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class EnchantmentControl implements Listener {

	private static HashMap<String, String> enchants = new HashMap<>();

	public static String getRandomEnchantment(String cat) {
		Random number = new Random();
		List<String> enchantments = new ArrayList<String>();
		for(CEnchantments en : Main.CE.getEnchantments()) {
			for(String C : Main.settings.getEnchs().getStringList("Enchantments." + en.getName() + ".Categories")) {
				if(cat.equalsIgnoreCase(C)) {
					if(en.isEnabled()) {
						String power = powerPicker(en.getName(), cat);
						enchants.put(en.getName(), en.getBookColor() + en.getCustomName() + " " + power);
						enchantments.add(en.getName());
					}
				}
			}
		}
		if(Main.settings.getCustomEnchs().contains("Enchantments")) {
			for(String en : Main.CustomE.getEnchantments()) {
				for(String C : Main.settings.getCustomEnchs().getStringList("Enchantments." + en + ".Categories")) {
					if(cat.equalsIgnoreCase(C)) {
						if(Main.CustomE.isEnabled(en)) {
							String power = powerPicker(en, cat);
							enchants.put(en, Main.CustomE.getBookColor(en) + Main.CustomE.getCustomName(en) + " " + power);
							enchantments.add(en);
						}
					}
				}
			}
		}
		try {
			return enchantments.get(number.nextInt(enchantments.size()));
		}catch(Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, Methods.color("&c[Crazy Enchantments]>> The category " + cat + " has no enchantments." + " &7Please add enchantments to the category in the Enchantments.yml. If you do not wish to have the category feel free to delete it from the Config.yml."));
			return enchantments.get(number.nextInt(enchantments.size()));
		}
	}

	@EventHandler
	public void addEnchantment(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		Player player = (Player) e.getWhoClicked();
		if(inv != null) {
			if(e.getCursor() != null && e.getCurrentItem() != null) {
				ItemStack book = e.getCursor();
				ItemStack item = e.getCurrentItem();
				if(book.hasItemMeta()) {
					if(book.getItemMeta().hasDisplayName()) {
						if(book.getType() != Main.CE.getEnchantmentBookItem().getType()) {
							return;
						}
						Boolean t = false;
						for(CEnchantments en : Main.CE.getEnchantments()) {
							if(book.getItemMeta().getDisplayName().startsWith(en.getBookColor() + en.getCustomName())) {
								t = true;
							}
						}
						for(String en : Main.CustomE.getEnchantments()) {
							if(book.getItemMeta().getDisplayName().startsWith(Main.CustomE.getBookColor(en) + Main.CustomE.getCustomName(en))) {
								t = true;
							}
						}
						if(!t) {
							return;
						}
						String name = book.getItemMeta().getDisplayName();
						CEnchantments en = CEnchantments.GLOWING;
						String enchant = "Glowing";
						Boolean custom = true;
						EnchantmentType type = EnchantmentType.ALL;
						for(CEnchantments ench : Main.CE.getEnchantments()) {
							if(name.contains(Methods.color(ench.getBookColor() + ench.getCustomName()))) {
								en = ench;
								enchant = ench.getCustomName();
								type = ench.getType();
								custom = false;
							}
						}
						for(String ench : Main.CustomE.getEnchantments()) {
							if(name.contains(Methods.color(Main.CustomE.getBookColor(ench) + Main.CustomE.getCustomName(ench)))) {
								enchant = ench;
								type = Main.CustomE.getType(ench);
								custom = true;
							}
						}
						if(type.getItems().contains(item.getType())) {
							if(book.getAmount() == 1 && item.getAmount() == 1) {
								boolean success = successChance(book);
								boolean destroy = destroyChance(book);
								Boolean toggle = false;
								Boolean lowerLvl = false;
								if(custom) {
									if(Main.CustomE.hasEnchantment(item, enchant)) {
										toggle = true;
										if(Main.CustomE.getPower(item, enchant) < Main.CustomE.getBookPower(book, enchant)) {
											lowerLvl = true;
										}
									}
								}else {
									if(Main.CE.hasEnchantment(item, en)) {
										toggle = true;
										if(Main.CE.getPower(item, en) < Main.CE.getBookPower(book, en)) {
											lowerLvl = true;
										}
									}
								}
								if(toggle) {
									if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.Armor-Upgrade.Toggle")) {
										if(lowerLvl) {
											e.setCancelled(true);
											if(success || player.getGameMode() == GameMode.CREATIVE) {
												String l = "0";
												if(custom) {
													e.setCurrentItem(Main.CustomE.addEnchantment(item, enchant, Main.CustomE.getBookPower(book, enchant)));
													l = Main.CustomE.getBookPower(book, enchant) + "";
												}else {
													ItemStack newItem = Main.CE.addEnchantment(item, en, Main.CE.getBookPower(book, en));
													if(e.getInventory().getType() == InventoryType.CRAFTING) {
														if(e.getRawSlot() >= 5 && e.getRawSlot() <= 8) {
															ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.DRAG, ArmorType.matchType(item), item, newItem);
															Bukkit.getPluginManager().callEvent(event);
														}
													}
													e.setCurrentItem(newItem);
													l = Main.CE.getBookPower(book, en) + "";
												}
												player.setItemOnCursor(new ItemStack(Material.AIR));
												player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Enchantment-Upgrade.Success").replaceAll("%Enchantment%", enchant).replaceAll("%enchantment%", enchant).replaceAll("%Level%", l).replaceAll("%level%", l)));
												try {
													if(Version.getCurrentVersion().getVersionInteger() >= 191) {
														player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
													}else {
														player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
													}
												}catch(Exception ex) {
												}
												return;
											}else if(destroy) {
												if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.Armor-Upgrade.Enchantment-Break")) {
													if(Methods.isProtected(item)) {
														e.setCurrentItem(Methods.removeProtected(item));
														player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Item-Was-Protected")));
													}else {
														if(custom) {
															Main.CustomE.removeEnchantment(item, enchant);
														}else {
															ItemStack newItem = Main.CE.removeEnchantment(item, en);
															if(e.getInventory().getType() == InventoryType.CRAFTING) {
																if(e.getRawSlot() >= 5 && e.getRawSlot() <= 8) {
																	ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.DRAG, ArmorType.matchType(item), item, newItem);
																	Bukkit.getPluginManager().callEvent(event);
																}
															}
															e.setCurrentItem(newItem);
														}
														player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Enchantment-Upgrade.Destroyed")));
													}
												}else {
													if(Methods.isProtected(item)) {
														e.setCurrentItem(Methods.removeProtected(item));
														player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Item-Was-Protected")));
													}else {
														ItemStack newItem = new ItemStack(Material.AIR);
														if(e.getInventory().getType() == InventoryType.CRAFTING) {
															if(e.getRawSlot() >= 5 && e.getRawSlot() <= 8) {
																ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.BROKE, ArmorType.matchType(item), item, newItem);
																Bukkit.getPluginManager().callEvent(event);
															}
														}
														e.setCurrentItem(newItem);
														player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Item-Destroyed")));
													}
												}
												player.setItemOnCursor(new ItemStack(Material.AIR));
												try {
													if(Version.getCurrentVersion().getVersionInteger() >= 191) {
														player.playSound(player.getLocation(), Sound.valueOf("ENTITY_ITEM_BREAK"), 1, 1);
													}else {
														player.playSound(player.getLocation(), Sound.valueOf("ITEM_BREAK"), 1, 1);
													}
												}catch(Exception ex) {
												}
												return;
											}else {
												player.setItemOnCursor(new ItemStack(Material.AIR));
												player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Enchantment-Upgrade.Failed")));
												try {
													if(Version.getCurrentVersion().getVersionInteger() >= 191) {
														player.playSound(player.getLocation(), Sound.valueOf("ENTITY_ITEM_BREAK"), 1, 1);
													}else {
														player.playSound(player.getLocation(), Sound.valueOf("ITEM_BREAK"), 1, 1);
													}
												}catch(Exception ex) {
												}
												return;
											}
										}
									}
									return;
								}
								if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle")) {
									int limit = Main.CE.getPlayerMaxEnchantments(player);
									int total = Methods.getEnchAmount(item);
									if(!player.hasPermission("crazyenchantments.bypass")) {
										if(total >= limit) {
											player.sendMessage(Methods.color(Main.settings.getMessages().getString("Messages.Hit-Enchantment-Max")));
											return;
										}
									}
								}
								e.setCancelled(true);
								if(success || player.getGameMode() == GameMode.CREATIVE) {
									name = Methods.removeColor(name);
									Integer lvl = convertPower(name.split(" ")[1]);
									if(custom) {
										e.setCurrentItem(Main.CustomE.addEnchantment(item, enchant, lvl));
									}else {
										ItemStack oldItem = item.clone();
										ItemStack newItem = Main.CE.addEnchantment(item, en, lvl);
										if(e.getInventory().getType() == InventoryType.CRAFTING) {
											if(e.getRawSlot() >= 5 && e.getRawSlot() <= 8) {
												ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.DRAG, ArmorType.matchType(item), oldItem, newItem);
												Bukkit.getPluginManager().callEvent(event);
											}
										}
										e.setCurrentItem(newItem);
									}
									player.setItemOnCursor(new ItemStack(Material.AIR));
									player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Book-Works")));
									try {
										if(Version.getCurrentVersion().getVersionInteger() >= 191) {
											player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
										}else {
											player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
										}
									}catch(Exception ex) {
									}
									return;
								}
								if(destroy) {
									if(Methods.isProtected(item)) {
										e.setCurrentItem(Methods.removeProtected(item));
										player.setItemOnCursor(new ItemStack(Material.AIR));
										player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Item-Was-Protected")));
										try {
											if(Version.getCurrentVersion().getVersionInteger() >= 191) {
												player.playSound(player.getLocation(), Sound.valueOf("ENTITY_ITEM_BREAK"), 1, 1);
											}else {
												player.playSound(player.getLocation(), Sound.valueOf("ITEM_BREAK"), 1, 1);
											}
										}catch(Exception ex) {
										}
										return;
									}else {
										ItemStack newItem = new ItemStack(Material.AIR);
										if(e.getInventory().getType() == InventoryType.CRAFTING) {
											if(e.getRawSlot() >= 5 && e.getRawSlot() <= 8) {
												ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.BROKE, ArmorType.matchType(item), item, newItem);
												Bukkit.getPluginManager().callEvent(event);
											}
										}
										e.setCurrentItem(newItem);
										player.setItemOnCursor(new ItemStack(Material.AIR));
										player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Item-Destroyed")));
									}
									player.updateInventory();
									return;
								}
								if(!success && !destroy) {
									player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Book-Failed")));
									player.setItemOnCursor(new ItemStack(Material.AIR));
									try {
										if(Version.getCurrentVersion().getVersionInteger() >= 191) {
											player.playSound(player.getLocation(), Sound.valueOf("ENTITY_ITEM_BREAK"), 1, 1);
										}else {
											player.playSound(player.getLocation(), Sound.valueOf("ITEM_BREAK"), 1, 1);
										}
									}catch(Exception ex) {
									}
									player.updateInventory();
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onDescriptionSend(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.Right-Click-Book-Description") || !Main.settings.getConfig().contains("Settings.EnchantmentOptions.Right-Click-Book-Description")) {
				ItemStack item = Methods.getItemInHand(e.getPlayer());
				if(Main.CE.isEnchantmentBook(item)) {
					e.setCancelled(true);
					String name = "";
					Player player = e.getPlayer();
					List<String> desc = new ArrayList<String>();
					for(CEnchantments en : Main.CE.getEnchantments()) {
						if(item.getItemMeta().getDisplayName().contains(Methods.color(en.getBookColor() + en.getCustomName()))) {
							name = Main.settings.getEnchs().getString("Enchantments." + en.getName() + ".Info.Name");
							desc = Main.settings.getEnchs().getStringList("Enchantments." + en.getName() + ".Info.Description");
						}
					}
					for(String en : Main.CustomE.getEnchantments()) {
						if(item.getItemMeta().getDisplayName().contains(Methods.color(Main.CustomE.getBookColor(en) + Main.CustomE.getCustomName(en)))) {
							name = Main.settings.getCustomEnchs().getString("Enchantments." + en + ".Info.Name");
							desc = Main.settings.getCustomEnchs().getStringList("Enchantments." + en + ".Info.Description");
						}
					}
					if(name.length() > 0) {
						player.sendMessage(Methods.color(name));
					}
					for(String msg : desc)
						player.sendMessage(Methods.color(msg));
				}
			}
		}
	}

	@EventHandler
	public void onMilkDrink(PlayerItemConsumeEvent e) {
		Player player = e.getPlayer();
		if(e.getItem() != null) {
			if(e.getItem().getType() == Material.MILK_BUCKET) {
				new BukkitRunnable() {
					@Override
					public void run() {
						Main.CE.updatePlayerEffects(player);
					}
				}.runTaskLater(Main.CE.getPlugin(), 5);
			}
		}
	}

	public static ItemStack pick(String cat) {
		int Smax = Main.settings.getConfig().getInt("Categories." + cat + ".EnchOptions.SuccessPercent.Max");
		int Smin = Main.settings.getConfig().getInt("Categories." + cat + ".EnchOptions.SuccessPercent.Min");
		int Dmax = Main.settings.getConfig().getInt("Categories." + cat + ".EnchOptions.DestroyPercent.Max");
		int Dmin = Main.settings.getConfig().getInt("Categories." + cat + ".EnchOptions.DestroyPercent.Min");
		ArrayList<String> lore = new ArrayList<String>();
		String enchant = getRandomEnchantment(cat);
		for(String l : Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore")) {
			if(l.contains("%Description%") || l.contains("%description%")) {
				if(Main.CE.getFromName(enchant) != null) {
					for(String m : Main.CE.getFromName(enchant).getDiscription()) {
						lore.add(Methods.color(m));
					}
				}else {
					if(Main.CustomE.getEnchantments().contains(enchant)) {
						for(String m : Main.CustomE.getDiscription(enchant)) {
							lore.add(Methods.color(m));
						}
					}
				}
			}else {
				lore.add(Methods.color(l).replaceAll("%Destroy_Rate%", Methods.percentPick(Dmax, Dmin) + "").replaceAll("%destroy_rate%", Methods.percentPick(Dmax, Dmin) + "").replaceAll("%Success_Rate%", Methods.percentPick(Smax, Smin) + "").replaceAll("%success_Rate%", Methods.percentPick(Smax, Smin) + ""));
			}
		}
		ItemStack item = new ItemBuilder().setMaterial(Main.settings.getConfig().getString("Settings.Enchantment-Book-Item")).setName(enchants.get(enchant)).setLore(lore).build();
		if(Main.settings.getConfig().contains("Settings.Enchantment-Book-Glowing")) {
			if(Main.settings.getConfig().getBoolean("Settings.Enchantment-Book-Glowing")) {
				item = Methods.addGlow(item);
			}
		}
		return item;
	}

	public static String powerPicker(String en, String C) {
		Random r = new Random();
		int ench = 5; //Max set by the enchantment
		if(Main.settings.getEnchs().contains("Enchantments." + en)) {
			ench = Main.settings.getEnchs().getInt("Enchantments." + en + ".MaxPower");
		}
		if(Main.settings.getCustomEnchs().contains("Enchantments." + en)) {
			ench = Main.settings.getCustomEnchs().getInt("Enchantments." + en + ".MaxPower");
		}
		int max = Main.settings.getConfig().getInt("Categories." + C + ".EnchOptions.LvlRange.Max"); //Max lvl set by the Category
		int min = Main.settings.getConfig().getInt("Categories." + C + ".EnchOptions.LvlRange.Min"); //Min lvl set by the Category
		int i = 1 + r.nextInt(ench);
		if(Main.settings.getConfig().contains("Categories." + C + ".EnchOptions.MaxLvlToggle")) {
			if(Main.settings.getConfig().getBoolean("Categories." + C + ".EnchOptions.MaxLvlToggle")) {
				if(i > max) {
					for(Boolean l = false; l == false; ) {
						i = 1 + r.nextInt(ench);
						if(i <= max) {
							l = true;
							break;
						}
					}
				}
				if(i < min) {//If i is smaller then the Min of the Category
					i = min;
				}
				if(i > ench) {//If i is bigger then the Enchantment Max
					i = ench;
				}
			}
		}
		if(i == 0) return "I";
		if(i == 1) return "I";
		if(i == 2) return "II";
		if(i == 3) return "III";
		if(i == 4) return "IV";
		if(i == 5) return "V";
		if(i == 6) return "VI";
		if(i == 7) return "VII";
		if(i == 8) return "VII";
		if(i == 9) return "IX";
		if(i == 10) return "X";
		return i + "";
	}

	public static String getCategory(ItemStack item) {
		List<String> lore = item.getItemMeta().getLore();
		List<String> L = Main.settings.getConfig().getStringList("Settings.LostBook.Lore");
		String arg = "";
		int i = 0;
		for(String l : L) {
			l = Methods.color(l);
			String lo = lore.get(i);
			if(l.contains("%Category%")) {
				String[] b = l.split("%Category%");
				if(b.length >= 1) arg = lo.replace(b[0], "");
				if(b.length >= 2) arg = arg.replace(b[1], "");
			}
			if(l.contains("%category%")) {
				String[] b = l.split("%category%");
				if(b.length >= 1) arg = lo.replace(b[0], "");
				if(b.length >= 2) arg = arg.replace(b[1], "");
			}
			i++;
		}
		return arg;
	}

	private Integer convertPower(String num) {
		if(Methods.isInt(num)) return Integer.parseInt(num);
		if(num.equalsIgnoreCase("I")) return 1;
		if(num.equalsIgnoreCase("II")) return 2;
		if(num.equalsIgnoreCase("III")) return 3;
		if(num.equalsIgnoreCase("IV")) return 4;
		if(num.equalsIgnoreCase("V")) return 5;
		if(num.equalsIgnoreCase("VI")) return 6;
		if(num.equalsIgnoreCase("VII")) return 7;
		if(num.equalsIgnoreCase("VIII")) return 8;
		if(num.equalsIgnoreCase("IX")) return 9;
		if(num.equalsIgnoreCase("X")) return 10;
		return 1;
	}

	private boolean successChance(ItemStack item) {
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasLore()) {
				int percent = Methods.getPercent("%success_rate%", item, Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore"));
				if(Methods.randomPicker(percent, 100)) {
					return true;
				}else {
					return false;
				}
			}
		}
		return true;
	}

	private boolean destroyChance(ItemStack item) {
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasLore()) {
				int percent = Methods.getPercent("%destroy_rate%", item, Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore"));
				if(Methods.randomPicker(percent, 100)) {
					return true;
				}else {
					return false;
				}
			}
		}
		return false;
	}

}
package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.ArmorType;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.enums.EnchantmentType;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.events.ArmorEquipEvent;
import me.badbones69.crazyenchantments.api.events.ArmorEquipEvent.EquipMethod;
import me.badbones69.crazyenchantments.api.events.BookDestroyEvent;
import me.badbones69.crazyenchantments.api.events.BookFailEvent;
import me.badbones69.crazyenchantments.api.events.PreBookApplyEvent;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
	
	private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	private static HashMap<String, String> enchants = new HashMap<>();
	
	public static String getRandomEnchantment(String cat) {
		Random number = new Random();
		List<String> enchantments = new ArrayList<>();
		for(CEnchantment en : ce.getRegisteredEnchantments()) {
			for(String C : Files.ENCHANTMENTS.getFile().getStringList("Enchantments." + en.getName() + ".Categories")) {
				if(cat.equalsIgnoreCase(C)) {
					if(en.isActivated()) {
						String power = powerPicker(en, cat);
						enchants.put(en.getName(), en.getBookColor() + en.getCustomName() + " " + power);
						enchantments.add(en.getName());
					}
				}
			}
		}
		try {
			return enchantments.get(number.nextInt(enchantments.size()));
		}catch(Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, Methods.color("&c[Crazy Enchantments]>> The category " + cat + " has no enchantments."
			+ " &7Please add enchantments to the category in the Enchantments.yml. If you do not wish to have the category feel free to delete it from the Config.yml."));
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
						if(book.getType() != ce.getEnchantmentBookItem().getType()) {
							return;
						}
						boolean t = false;
						for(CEnchantment en : ce.getRegisteredEnchantments()) {
							if(book.getItemMeta().getDisplayName().startsWith(en.getBookColor() + en.getCustomName())) {
								t = true;
							}
						}
						if(!t) {
							return;
						}
						String name = book.getItemMeta().getDisplayName();
						CEnchantment enchantment = CEnchantment.getCEnchantmentFromName(CEnchantments.GLOWING.getName());
						EnchantmentType enchantmentType = EnchantmentType.ALL;
						for(CEnchantment ench : ce.getRegisteredEnchantments()) {
							if(name.contains(Methods.color(ench.getBookColor() + ench.getCustomName()))) {
								enchantment = ench;
								enchantmentType = ench.getEnchantmentType();
							}
						}
						if(enchantmentType.getItems().contains(item.getType())) {
							if(book.getAmount() == 1) {
								if(ce.enchantStackedItems() || item.getAmount() == 1) {
									boolean success = successChance(book);
									boolean destroy = destroyChance(book);
									int bookPower = ce.getBookLevel(book, enchantment);
									boolean toggle = false;
									boolean lowerLvl = false;
									if(ce.hasEnchantment(item, enchantment)) {
										toggle = true;
										if(ce.getLevel(item, enchantment) < bookPower) {
											lowerLvl = true;
										}
									}
									if(toggle) {
										if(Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Armor-Upgrade.Toggle")) {
											if(lowerLvl) {
												e.setCancelled(true);
												PreBookApplyEvent preBookApplyEvent = new PreBookApplyEvent(player, enchantment, bookPower, item, book, (player.getGameMode() == GameMode.CREATIVE),
												success, getSuccessChance(book), destroy, getDestroyChance(book));
												Bukkit.getPluginManager().callEvent(preBookApplyEvent);
												if(!preBookApplyEvent.isCancelled()) {
													if(success || player.getGameMode() == GameMode.CREATIVE) {
														BookFailEvent bookApplyEvent = new BookFailEvent(player, enchantment, bookPower, item, book);
														Bukkit.getPluginManager().callEvent(bookApplyEvent);
														if(!bookApplyEvent.isCancelled()) {
															e.setCurrentItem(ce.addEnchantment(item, enchantment, bookPower));
															player.setItemOnCursor(new ItemStack(Material.AIR));
															HashMap<String, String> placeholders = new HashMap<>();
															placeholders.put("%enchantment%", enchantment.getCustomName());
															placeholders.put("%level%", bookPower + "");
															player.sendMessage(Messages.ENCHANTMENT_UPGRADE_SUCCESS.getMessage(placeholders));
															player.playSound(player.getLocation(), ce.getSound("ENTITY_PLAYER_LEVELUP", "LEVEL_UP"), 1, 1);
														}
														return;
													}else if(destroy) {
														BookDestroyEvent bookDestroyEvent = new BookDestroyEvent(player, enchantment, bookPower, item, book);
														Bukkit.getPluginManager().callEvent(bookDestroyEvent);
														if(!bookDestroyEvent.isCancelled()) {
															if(Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Armor-Upgrade.Enchantment-Break")) {
																if(Methods.isProtected(item)) {
																	e.setCurrentItem(Methods.removeProtected(item));
																	player.sendMessage(Messages.ITEM_WAS_PROTECTED.getMessage());
																}else {
																	ItemStack newItem = ce.removeEnchantment(item, enchantment);
																	if(e.getInventory().getType() == InventoryType.CRAFTING) {
																		if(e.getRawSlot() >= 5 && e.getRawSlot() <= 8) {
																			ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.DRAG, ArmorType.matchType(item), item, newItem);
																			Bukkit.getPluginManager().callEvent(event);
																		}
																	}
																	player.sendMessage(Messages.ENCHANTMENT_UPGRADE_DESTROYED.getMessage());
																}
															}else {
																if(Methods.isProtected(item)) {
																	e.setCurrentItem(Methods.removeProtected(item));
																	player.sendMessage(Messages.ITEM_WAS_PROTECTED.getMessage());
																}else {
																	ItemStack newItem = new ItemStack(Material.AIR);
																	e.setCurrentItem(newItem);
																	if(e.getInventory().getType() == InventoryType.CRAFTING) {
																		if(e.getRawSlot() >= 5 && e.getRawSlot() <= 8) {
																			ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.BROKE, ArmorType.matchType(item), item, newItem);
																			Bukkit.getPluginManager().callEvent(event);
																		}
																	}
																	player.sendMessage(Messages.ITEM_DESTROYED.getMessage());
																}
															}
															player.setItemOnCursor(new ItemStack(Material.AIR));
															player.playSound(player.getLocation(), ce.getSound("ENTITY_ITEM_BREAK", "ITEM_BREAK"), 1, 1);
														}
														return;
													}else {
														BookFailEvent bookFailEvent = new BookFailEvent(player, enchantment, bookPower, item, book);
														Bukkit.getPluginManager().callEvent(bookFailEvent);
														if(!bookFailEvent.isCancelled()) {
															player.setItemOnCursor(new ItemStack(Material.AIR));
															player.sendMessage(Messages.ENCHANTMENT_UPGRADE_FAILED.getMessage());
															player.playSound(player.getLocation(), ce.getSound("ENTITY_ITEM_BREAK", "ITEM_BREAK"), 1, 1);
														}
														return;
													}
												}
											}
										}
										return;
									}
									if(Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle")) {
										int limit = ce.getPlayerMaxEnchantments(player);
										int total = Methods.getEnchAmount(item);
										if(!player.hasPermission("crazyenchantments.bypass.limit")) {
											if(total >= limit) {
												player.sendMessage(Messages.HIT_ENCHANTMENT_MAX.getMessage());
												return;
											}
										}
									}
									e.setCancelled(true);
									if(success || player.getGameMode() == GameMode.CREATIVE) {
										name = Methods.removeColor(name);
										Integer lvl = convertPower(name.split(" ")[1]);
										ItemStack newItem = ce.addEnchantment(item, enchantment, lvl);
										ItemStack oldItem = new ItemStack(Material.AIR);
										e.setCurrentItem(newItem);
										if(e.getInventory().getType() == InventoryType.CRAFTING) {
											if(e.getRawSlot() >= 5 && e.getRawSlot() <= 8) {
												ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.DRAG, ArmorType.matchType(item), oldItem, newItem);
												Bukkit.getPluginManager().callEvent(event);
											}
										}
										player.setItemOnCursor(oldItem);
										player.sendMessage(Messages.BOOK_WORKS.getMessage());
										player.playSound(player.getLocation(), ce.getSound("ENTITY_PLAYER_LEVELUP", "LEVEL_UP"), 1, 1);
										return;
									}
									if(destroy) {
										if(Methods.isProtected(item)) {
											e.setCurrentItem(Methods.removeProtected(item));
											player.setItemOnCursor(new ItemStack(Material.AIR));
											player.sendMessage(Messages.ITEM_WAS_PROTECTED.getMessage());
											player.playSound(player.getLocation(), ce.getSound("ENTITY_ITEM_BREAK", "ITEM_BREAK"), 1, 1);
											return;
										}else {
											ItemStack newItem = new ItemStack(Material.AIR);
											ItemStack oldItem = new ItemStack(Material.AIR);
											player.setItemOnCursor(newItem);
											if(e.getInventory().getType() == InventoryType.CRAFTING) {
												if(e.getRawSlot() >= 5 && e.getRawSlot() <= 8) {
													ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.BROKE, ArmorType.matchType(item), item, newItem);
													Bukkit.getPluginManager().callEvent(event);
												}
											}
											e.setCurrentItem(oldItem);
											player.sendMessage(Messages.ITEM_DESTROYED.getMessage());
										}
										player.updateInventory();
										return;
									}
								}
								player.sendMessage(Messages.BOOK_FAILED.getMessage());
								player.setItemOnCursor(new ItemStack(Material.AIR));
								player.playSound(player.getLocation(), ce.getSound("ENTITY_ITEM_BREAK", "ITEM_BREAK"), 1, 1);
								player.updateInventory();
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onDescriptionSend(PlayerInteractEvent e) {
		if(Version.getCurrentVersion().isNewer(Version.v1_8_R3)) {
			if(e.getHand() != EquipmentSlot.HAND) {
				return;
			}
		}
			ItemStack item = Methods.getItemInHand(e.getPlayer());
			if(ce.isEnchantmentBook(item)) {
				e.setCancelled(true);
				String name = "";
				Player player = e.getPlayer();
				List<String> desc = new ArrayList<>();
				for(CEnchantment en : ce.getRegisteredEnchantments()) {
					if(item.getItemMeta().getDisplayName().contains(Methods.color(en.getBookColor() + en.getCustomName()))) {
						name = Files.ENCHANTMENTS.getFile().getString("Enchantments." + en.getName() + ".Info.Name");
						desc = Files.ENCHANTMENTS.getFile().getStringList("Enchantments." + en.getName() + ".Info.Description");
					}
				}
				if(name.length() > 0) {
					player.sendMessage(Methods.color(name));
				}
				for(String msg : desc) {
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
						ce.updatePlayerEffects(player);
					}
				}.runTaskLater(ce.getPlugin(), 5);
			}
		}
	}
	
	public static ItemStack pick(String cat) {
		int Smax = Files.CONFIG.getFile().getInt("Categories." + cat + ".EnchOptions.SuccessPercent.Max");
		int Smin = Files.CONFIG.getFile().getInt("Categories." + cat + ".EnchOptions.SuccessPercent.Min");
		int Dmax = Files.CONFIG.getFile().getInt("Categories." + cat + ".EnchOptions.DestroyPercent.Max");
		int Dmin = Files.CONFIG.getFile().getInt("Categories." + cat + ".EnchOptions.DestroyPercent.Min");
		ArrayList<String> lore = new ArrayList<>();
		String enchant = getRandomEnchantment(cat);
		for(String l : Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore")) {
			if(l.contains("%Description%") || l.contains("%description%")) {
				if(ce.getEnchantmentFromName(enchant) != null) {
					for(String m : ce.getEnchantmentFromName(enchant).getInfoDescription()) {
						lore.add(Methods.color(m));
					}
				}
			}else {
				lore.add(Methods.color(l)
				.replaceAll("%Destroy_Rate%", Methods.percentPick(Dmax, Dmin) + "").replaceAll("%destroy_rate%", Methods.percentPick(Dmax, Dmin) + "")
				.replaceAll("%Success_Rate%", Methods.percentPick(Smax, Smin) + "").replaceAll("%success_rate%", Methods.percentPick(Smax, Smin) + ""));
			}
		}
		ItemStack item = new ItemBuilder().setMaterial(Files.CONFIG.getFile().getString("Settings.Enchantment-Book-Item")).setName(enchants.get(enchant)).setLore(lore).build();
		if(Files.CONFIG.getFile().contains("Settings.Enchantment-Book-Glowing")) {
			if(Files.CONFIG.getFile().getBoolean("Settings.Enchantment-Book-Glowing")) {
				item = Methods.addGlow(item);
			}
		}
		return item;
	}
	
	public static String powerPicker(CEnchantment en, String C) {
		Random r = new Random();
		int ench = en.getMaxLevel(); //Max set by the enchantment
		int max = Files.CONFIG.getFile().getInt("Categories." + C + ".EnchOptions.LvlRange.Max"); //Max lvl set by the Category
		int min = Files.CONFIG.getFile().getInt("Categories." + C + ".EnchOptions.LvlRange.Min"); //Min lvl set by the Category
		int i = 1 + r.nextInt(ench);
		if(Files.CONFIG.getFile().contains("Categories." + C + ".EnchOptions.MaxLvlToggle")) {
			if(Files.CONFIG.getFile().getBoolean("Categories." + C + ".EnchOptions.MaxLvlToggle")) {
				if(i > max) {
					for(Boolean l = false; ; ) {
						i = 1 + r.nextInt(ench);
						if(i <= max) {
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
		List<String> L = Files.CONFIG.getFile().getStringList("Settings.LostBook.Lore");
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
	
	private Integer getSuccessChance(ItemStack item) {
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasLore()) {
				return Methods.getPercent("%success_rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"));
			}
		}
		return 0;
	}
	
	private boolean successChance(ItemStack item) {
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasLore()) {
				int percent = Methods.getPercent("%success_rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"));
				return Methods.randomPicker(percent, 100);
			}
		}
		return true;
	}
	
	private Integer getDestroyChance(ItemStack item) {
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasLore()) {
				return Methods.getPercent("%destroy_rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"));
			}
		}
		return 0;
	}
	
	private boolean destroyChance(ItemStack item) {
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasLore()) {
				int percent = Methods.getPercent("%destroy_rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"));
				return Methods.randomPicker(percent, 100);
			}
		}
		return false;
	}
	
}
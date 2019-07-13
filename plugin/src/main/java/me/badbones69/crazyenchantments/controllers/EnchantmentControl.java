package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.ArmorType;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.events.ArmorEquipEvent;
import me.badbones69.crazyenchantments.api.events.ArmorEquipEvent.EquipMethod;
import me.badbones69.crazyenchantments.api.events.BookDestroyEvent;
import me.badbones69.crazyenchantments.api.events.BookFailEvent;
import me.badbones69.crazyenchantments.api.events.PreBookApplyEvent;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnchantmentControl implements Listener {
	
	private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	private static HashMap<String, String> enchants = new HashMap<>();
	
	@EventHandler
	public void addEnchantment(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		Player player = (Player) e.getWhoClicked();
		if(inv != null) {
			if(e.getCursor() != null && e.getCurrentItem() != null) {
				ItemStack book = e.getCursor();
				ItemStack item = e.getCurrentItem();
				if(ce.isEnchantmentBook(book)) {
					String name = book.getItemMeta().getDisplayName();
					CEnchantment enchantment = ce.getEnchantmentBookEnchantment(book);
					if(enchantment != null) {
						if(enchantment.getEnchantmentType().getEnchantableMaterials().contains(item.getType())) {
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
										int lvl = ce.convertLevelInteger(name.split(" ")[name.split(" ").length - 1]);
										ItemStack newItem = ce.addEnchantment(item, enchantment, lvl);
										ItemStack oldItem = new ItemStack(Material.AIR);
										e.setCurrentItem(newItem);
										if(e.getInventory().getType() == InventoryType.CRAFTING) {
											if(e.getRawSlot() >= 5 && e.getRawSlot() <= 8) {
												ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.DRAG, ArmorType.matchType(item), oldItem, newItem);
												Bukkit.getPluginManager().callEvent(event);
											}
										}
										player.setItemOnCursor(new ItemStack(Material.AIR));
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
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
			if(Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Right-Click-Book-Description")) {
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
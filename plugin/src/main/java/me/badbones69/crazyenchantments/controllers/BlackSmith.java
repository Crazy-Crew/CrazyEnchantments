package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.currencyapi.Currency;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BlackSmith implements Listener {
	
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	public static void openBlackSmith(Player player) {
		Inventory inv = Bukkit.createInventory(null, 27, Methods.color(Files.CONFIG.getFile().getString("Settings.BlackSmith.GUIName")));
		List<Integer> other = Arrays.asList(1, 2, 3, 4, 5, 6, 10, 12, 13, 15, 19, 20, 21, 22, 23, 24);
		List<Integer> result = Arrays.asList(7, 8, 9, 16, 18, 25, 26, 27);
		for(int i : other)
			inv.setItem(i - 1, new ItemBuilder().setMaterial("GRAY_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:7").setName(" ").build());
		for(int i : result)
			inv.setItem(i - 1, new ItemBuilder().setMaterial("RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:14").setName(" ").build());
		ItemStack item = new ItemBuilder().setMaterial(Material.BARRIER).setName(Files.CONFIG.getFile().getString("Settings.BlackSmith.Results.None")).build();
		if(Files.CONFIG.getFile().contains("Settings.BlackSmith.Results.Not-Found-Lore")) {
			for(String line : Files.CONFIG.getFile().getStringList("Settings.BlackSmith.Results.Not-Found-Lore")) {
				item = Methods.addLore(item, line);
			}
		}
		inv.setItem(16, item);
		player.openInventory(inv);
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		List<Integer> result = Arrays.asList(7, 8, 9, 16, 18, 25, 26, 27);
		Player player = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		FileConfiguration config = Files.CONFIG.getFile();
		if(inv != null) {
			if(e.getView().getTitle().equals(Methods.color(config.getString("Settings.BlackSmith.GUIName")))) {
				e.setCancelled(true);
				if(e.getCurrentItem() != null) {
					ItemStack item = e.getCurrentItem();
					if(!inBlackSmith(e.getRawSlot())) {// Click In Players Inventory
						if(item.getAmount() != 1) return;
						if(ce.hasEnchantments(item) || item.getType() == ce.getEnchantmentBookItem().getType()) {
							if(item.getType() == ce.getEnchantmentBookItem().getType()) {//Is a custom enchantment book.
								if(!item.hasItemMeta()) return;
								if(!item.getItemMeta().hasDisplayName()) return;
								boolean T = false;
								for(CEnchantment en : ce.getRegisteredEnchantments()) {
									if(item.getItemMeta().getDisplayName().startsWith(en.getBookColor() + en.getCustomName())) {
										T = true;
									}
								}
								if(!T) {
									return;
								}
							}
							if(inv.getItem(10) == null) {
								e.setCurrentItem(new ItemStack(Material.AIR));
								inv.setItem(10, item);
								playClick(player);
								if(inv.getItem(13) != null) {
									if(getUpgradeCost(player, inv.getItem(10), inv.getItem(13)) > 0) {
										inv.setItem(16, Methods.addLore(getUpgradedItem(player, inv.getItem(10), inv.getItem(13)), config.getString("Settings.BlackSmith.Results.Found").replaceAll("%Cost%", getUpgradeCost(player, inv.getItem(10), inv.getItem(13)) + "").replaceAll("%cost%", getUpgradeCost(player, inv.getItem(10), inv.getItem(13)) + "")));
										for(int i : result)
											inv.setItem(i - 1, new ItemBuilder().setMaterial("LIGHT_BLUE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:3").setName(" ").build());
									}else {
										ItemStack it = new ItemBuilder().setMaterial(Material.BARRIER).setName(Files.CONFIG.getFile().getString("Settings.BlackSmith.Results.None")).build();
										if(config.contains("Settings.BlackSmith.Results.Not-Found-Lore")) {
											for(String line : config.getStringList("Settings.BlackSmith.Results.Not-Found-Lore")) {
												it = Methods.addLore(it, line);
											}
										}
										inv.setItem(16, it);
										for(int i : result)
											inv.setItem(i - 1, new ItemBuilder().setMaterial("RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:14").setName(" ").build());
									}
								}
							}else {
								e.setCurrentItem(new ItemStack(Material.AIR));
								if(inv.getItem(13) != null) {
									e.setCurrentItem(inv.getItem(13));
								}
								inv.setItem(13, item);
								playClick(player);
								if(getUpgradeCost(player, inv.getItem(10), inv.getItem(13)) > 0) {
									inv.setItem(16, Methods.addLore(getUpgradedItem(player, inv.getItem(10), inv.getItem(13)), config.getString("Settings.BlackSmith.Results.Found").replaceAll("%Cost%", getUpgradeCost(player, inv.getItem(10), inv.getItem(13)) + "").replaceAll("%cost%", getUpgradeCost(player, inv.getItem(10), inv.getItem(13)) + "")));
									for(int i : result)
										inv.setItem(i - 1, new ItemBuilder().setMaterial("LIGHT_BLUE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:3").build());
								}else {
									ItemStack it = new ItemBuilder().setMaterial(Material.BARRIER).setName(Files.CONFIG.getFile().getString("Settings.BlackSmith.Results.None")).build();
									if(config.contains("Settings.BlackSmith.Results.Not-Found-Lore")) {
										for(String line : config.getStringList("Settings.BlackSmith.Results.Not-Found-Lore")) {
											it = Methods.addLore(it, line);
										}
									}
									inv.setItem(16, it);
									for(int i : result)
										inv.setItem(i - 1, new ItemBuilder().setMaterial("RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:14").setName(" ").build());
								}
							}
						}
					}else {// Click In the Black Smith
						if(e.getRawSlot() == 10 || e.getRawSlot() == 13) {
							e.setCurrentItem(new ItemStack(Material.AIR));
							if(Methods.isInvFull(player)) {
								player.getWorld().dropItem(player.getLocation(), item);
							}else {
								player.getInventory().addItem(item);
							}
							ItemStack it = new ItemBuilder().setMaterial(Material.BARRIER).setName(Files.CONFIG.getFile().getString("Settings.BlackSmith.Results.None")).build();
							if(config.contains("Settings.BlackSmith.Results.Not-Found-Lore")) {
								for(String line : config.getStringList("Settings.BlackSmith.Results.Not-Found-Lore")) {
									it = Methods.addLore(it, line);
								}
							}
							inv.setItem(16, it);
							for(int i : result)
								inv.setItem(i - 1, new ItemBuilder().setMaterial("RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:14").setName(" ").build());
							playClick(player);
						}
						if(e.getRawSlot() == 16) {
							if(inv.getItem(10) != null && inv.getItem(13) != null) {
								if(getUpgradeCost(player, inv.getItem(10), inv.getItem(13)) > 0) {
									int cost = getUpgradeCost(player, inv.getItem(10), inv.getItem(13));
									if(player.getGameMode() != GameMode.CREATIVE) {
										if(Currency.isCurrency(config.getString("Settings.BlackSmith.Transaction.Currency"))) {
											Currency currency = Currency.getCurrency(config.getString("Settings.BlackSmith.Transaction.Currency"));
											if(CurrencyAPI.canBuy(player, currency, cost)) {
												CurrencyAPI.takeCurrency(player, currency, cost);
											}else {
												String needed = (cost - CurrencyAPI.getCurrency(player, currency)) + "";
												if(currency != null) {
													HashMap<String, String> placeholders = new HashMap<>();
													placeholders.put("%money_needed%", needed);
													placeholders.put("%xp%", needed);
													switch(currency) {
														case VAULT:
															player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(placeholders));
															break;
														case XP_LEVEL:
															player.sendMessage(Messages.NEED_MORE_XP_LEVELS.getMessage(placeholders));
															break;
														case XP_TOTAL:
															player.sendMessage(Messages.NEED_MORE_TOTAL_XP.getMessage(placeholders));
															break;
													}
												}
												return;
											}
										}
									}
									if(Methods.isInvFull(player)) {
										player.getWorld().dropItem(player.getLocation(), getUpgradedItem(player, inv.getItem(10), inv.getItem(13)));
									}else {
										player.getInventory().addItem(getUpgradedItem(player, inv.getItem(10), inv.getItem(13)));
									}
									inv.setItem(10, new ItemStack(Material.AIR));
									inv.setItem(13, new ItemStack(Material.AIR));
									player.playSound(player.getLocation(), ce.getSound("ENTITY_PLAYER_LEVELUP", "LEVEL_UP"), 1, 1);
									ItemStack it = new ItemBuilder().setMaterial(Material.BARRIER).setName(Files.CONFIG.getFile().getString("Settings.BlackSmith.Results.None")).build();
									if(config.contains("Settings.BlackSmith.Results.Not-Found-Lore")) {
										for(String line : config.getStringList("Settings.BlackSmith.Results.Not-Found-Lore")) {
											it = Methods.addLore(it, line);
										}
									}
									inv.setItem(16, it);
									for(int i : result)
										inv.setItem(i - 1, new ItemBuilder().setMaterial("RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:14").setName(" ").build());
								}else {
									player.playSound(player.getLocation(), ce.getSound("ENTITY_VILLAGER_NO", "VILLAGER_NO"), 1, 1);
								}
							}else {
								player.playSound(player.getLocation(), ce.getSound("ENTITY_VILLAGER_NO", "VILLAGER_NO"), 1, 1);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInvClose(final InventoryCloseEvent e) {
		final Inventory inv = e.getInventory();
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ce.getPlugin(), () -> {
			if(inv != null) {
				if(e.getView().getTitle().equals(Methods.color(Files.CONFIG.getFile().getString("Settings.BlackSmith.GUIName")))) {
					List<Integer> slots = new ArrayList<>();
					slots.add(10);
					slots.add(13);
					boolean dead = e.getPlayer().isDead();
					for(int slot : slots) {
						if(inv.getItem(slot) != null) {
							if(inv.getItem(slot).getType() != Material.AIR) {
								if(dead) {
									e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), inv.getItem(slot));
								}else {
									if(Methods.isInvFull(((Player) e.getPlayer()))) {
										e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), inv.getItem(slot));
									}else {
										e.getPlayer().getInventory().addItem(inv.getItem(slot));
									}
								}
							}
						}
					}
					inv.clear();
				}
			}
		}, 0);
	}
	
	private ItemStack getUpgradedItem(Player player, ItemStack mainItem, ItemStack subItem) {
		ItemStack item = mainItem.clone();
		if(mainItem.getType() == ce.getEnchantmentBookItem().getType() && subItem.getType() == ce.getEnchantmentBookItem().getType()) {
			if(Methods.removeColor(mainItem.getItemMeta().getDisplayName()).equalsIgnoreCase(Methods.removeColor(subItem.getItemMeta().getDisplayName()))) {
				for(CEnchantment en : ce.getRegisteredEnchantments()) {
					if(mainItem.getItemMeta().getDisplayName().startsWith(en.getBookColor() + en.getCustomName())) {
						int power = ce.getBookLevel(mainItem, en);
						int max = Files.ENCHANTMENTS.getFile().getInt("Enchantments." + en.getName() + ".MaxPower");
						if(power + 1 <= max) {
							item = new ItemBuilder().setMaterial(Files.CONFIG.getFile().getString("Settings.Enchantment-Book-Item")).setName(en.getBookColor() + en.getCustomName() + " " + ce.convertLevelString(power + 1)).setLore(mainItem.getItemMeta().getLore()).setGlowing(Files.CONFIG.getFile().getBoolean("Settings.Enchantment-Book-Glowing")).build();
						}
					}
				}
			}
		}
		if(mainItem.getType() != ce.getEnchantmentBookItem().getType() || subItem.getType() != ce.getEnchantmentBookItem().getType()) {
			if(mainItem.getType() == subItem.getType()) {
				HashMap<String, Integer> dupEnchants = new HashMap<>();
				HashMap<String, Integer> newEnchants = new HashMap<>();
				HashMap<String, Integer> higherEnchants = new HashMap<>();
				for(CEnchantment enchant : ce.getEnchantmentsOnItem(mainItem)) {
					if(ce.hasEnchantment(subItem, enchant)) {
						if(ce.getLevel(mainItem, enchant).equals(ce.getLevel(subItem, enchant))) {
							if(!dupEnchants.containsKey(enchant.getName())) {
								dupEnchants.put(enchant.getName(), ce.getLevel(mainItem, enchant));
							}
						}else {
							if(ce.getLevel(mainItem, enchant) < ce.getLevel(subItem, enchant)) {
								higherEnchants.put(enchant.getName(), ce.getLevel(subItem, enchant));
							}
						}
					}
				}
				for(CEnchantment enchant : ce.getEnchantmentsOnItem(subItem)) {
					if(!dupEnchants.containsKey(enchant.getName()) && !higherEnchants.containsKey(enchant.getName())) {
						if(!ce.hasEnchantment(mainItem, enchant)) {
							newEnchants.put(enchant.getName(), ce.getLevel(subItem, enchant));
						}
					}
				}
				for(String enchant : dupEnchants.keySet()) {
					if(ce.getEnchantmentFromName(enchant) != null) {
						int power = dupEnchants.get(enchant);
						int max = ce.getEnchantmentFromName(enchant).getMaxLevel();
						if(power + 1 <= max) {
							item = ce.addEnchantment(item, ce.getEnchantmentFromName(enchant), power + 1);
						}
					}
				}
				int maxEnchants = ce.getPlayerMaxEnchantments(player);
				for(String enchant : newEnchants.keySet()) {
					if(Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle")) {
						if((Methods.getEnchAmount(item) + 1) <= maxEnchants) {
							if(ce.getEnchantmentFromName(enchant) != null) {
								item = ce.addEnchantment(item, ce.getEnchantmentFromName(enchant), newEnchants.get(enchant));
							}
						}
					}
				}
				for(String enchant : higherEnchants.keySet()) {
					if(ce.getEnchantmentFromName(enchant) != null) {
						item = ce.addEnchantment(item, ce.getEnchantmentFromName(enchant), higherEnchants.get(enchant));
					}
				}
			}
		}
		return item;
	}
	
	private int getUpgradeCost(Player player, ItemStack mainItem, ItemStack subItem) {
		int total = 0;
		//Is 2 books
		if(mainItem.getType() == ce.getEnchantmentBookItem().getType() && subItem.getType() == ce.getEnchantmentBookItem().getType()) {
			if(Methods.removeColor(mainItem.getItemMeta().getDisplayName()).equalsIgnoreCase(Methods.removeColor(subItem.getItemMeta().getDisplayName()))) {
				for(CEnchantment en : ce.getRegisteredEnchantments()) {
					if(ce.getEnchantmentBookEnchantment(mainItem) == en) {
						int power = ce.getBookLevel(mainItem, en);
						int max = en.getMaxLevel();
						if(power + 1 <= max) {
							total += Files.CONFIG.getFile().getInt("Settings.BlackSmith.Transaction.Costs.Book-Upgrade");
						}
					}
				}
			}
		}
		//Is 2 items
		if(mainItem.getType() != ce.getEnchantmentBookItem().getType() || subItem.getType() != ce.getEnchantmentBookItem().getType()) {
			if(mainItem.getType() == subItem.getType()) {
				ItemStack item = mainItem.clone();
				HashMap<String, Integer> dupEnchants = new HashMap<>();
				HashMap<String, Integer> newEnchants = new HashMap<>();
				HashMap<String, Integer> higherEnchants = new HashMap<>();
				for(CEnchantment enchant : ce.getEnchantmentsOnItem(mainItem)) {
					if(ce.hasEnchantment(subItem, enchant)) {
						if(ce.getLevel(mainItem, enchant).equals(ce.getLevel(subItem, enchant))) {
							if(!dupEnchants.containsKey(enchant.getName())) {
								dupEnchants.put(enchant.getName(), ce.getLevel(mainItem, enchant));
							}
						}else {
							if(ce.getLevel(mainItem, enchant) < ce.getLevel(subItem, enchant)) {
								higherEnchants.put(enchant.getName(), ce.getLevel(subItem, enchant));
							}
						}
					}
				}
				for(CEnchantment enchant : ce.getEnchantmentsOnItem(subItem)) {
					if(!dupEnchants.containsKey(enchant.getName()) && !higherEnchants.containsKey(enchant.getName())) {
						if(!ce.hasEnchantment(mainItem, enchant)) {
							newEnchants.put(enchant.getName(), ce.getLevel(subItem, enchant));
						}
					}
				}
				for(String enchant : dupEnchants.keySet()) {
					if(ce.getEnchantmentFromName(enchant) != null) {
						int power = dupEnchants.get(enchant);
						int max = ce.getEnchantmentFromName(enchant).getMaxLevel();
						if(power + 1 <= max) {
							total += Files.CONFIG.getFile().getInt("Settings.BlackSmith.Transaction.Costs.Power-Up");
						}
					}
				}
				int maxEnchants = ce.getPlayerMaxEnchantments(player);
				for(int i = 0; i < newEnchants.size(); i++) {
					if(Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle")) {
						if((Methods.getEnchAmount(item) + i + 1) <= maxEnchants) {
							total += Files.CONFIG.getFile().getInt("Settings.BlackSmith.Transaction.Costs.Add-Enchantment");
						}
					}
				}
				for(int i = 0; i < higherEnchants.size(); i++) {
					total += Files.CONFIG.getFile().getInt("Settings.BlackSmith.Transaction.Costs.Power-Up");
				}
			}
		}
		return total;
	}
	
	private boolean inBlackSmith(int slot) {
		//The last slot in the tinker is 54
		return slot < 27;
	}
	
	private void playClick(Player player) {
		player.playSound(player.getLocation(), ce.getSound("UI_BUTTON_CLICK", "CLICK"), 1, 1);
	}
	
}

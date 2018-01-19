package me.badbones69.crazyenchantments.controlers;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CEnchantments;
import me.badbones69.crazyenchantments.api.currencyapi.Currency;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlackSmith implements Listener {

	public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments");

	public static void openBlackSmith(Player player) {
		Inventory inv = Bukkit.createInventory(null, 27, Methods.color(Main.settings.getConfig().getString("Settings.BlackSmith.GUIName")));
		List<Integer> other = new ArrayList<>();
		List<Integer> result = new ArrayList<>();
		other.add(1);
		other.add(2);
		other.add(3);
		other.add(4);
		other.add(5);
		other.add(6);
		other.add(10);
		other.add(12);
		other.add(13);
		other.add(15);
		other.add(19);
		other.add(20);
		other.add(21);
		other.add(22);
		other.add(23);
		other.add(24);
		result.add(7);
		result.add(8);
		result.add(9);
		result.add(16);
		result.add(18);
		result.add(25);
		result.add(26);
		result.add(27);
		for(int i : other)
			inv.setItem(i - 1, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setMetaData(7).setName(" ").build());
		for(int i : result)
			inv.setItem(i - 1, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setMetaData(14).setName(" ").build());
		if(Version.getCurrentVersion().getVersionInteger() < 181) {
			ItemStack item = new ItemBuilder().setMaterial(Material.STAINED_CLAY).setName(Main.settings.getConfig().getString("Settings.BlackSmith.Results.None")).build();
			if(Main.settings.getConfig().contains("Settings.BlackSmith.Results.Not-Found-Lore")) {
				for(String line : Main.settings.getConfig().getStringList("Settings.BlackSmith.Results.Not-Found-Lore")) {
					item = Methods.addLore(item, line);
				}
			}
			inv.setItem(16, item);
		}else {
			ItemStack item = new ItemBuilder().setMaterial(Material.BARRIER).setName(Main.settings.getConfig().getString("Settings.BlackSmith.Results.None")).build();
			if(Main.settings.getConfig().contains("Settings.BlackSmith.Results.Not-Found-Lore")) {
				for(String line : Main.settings.getConfig().getStringList("Settings.BlackSmith.Results.Not-Found-Lore")) {
					item = Methods.addLore(item, line);
				}
			}
			inv.setItem(16, item);
		}
		player.openInventory(inv);
	}

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		List<Integer> result = new ArrayList<>();
		result.add(7);
		result.add(8);
		result.add(9);
		result.add(16);
		;
		result.add(18);
		result.add(25);
		result.add(26);
		result.add(27);
		Player player = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		FileConfiguration config = Main.settings.getConfig();
		if(inv != null) {
			if(inv.getName().equals(Methods.color(config.getString("Settings.BlackSmith.GUIName")))) {
				e.setCancelled(true);
				if(e.getCurrentItem() != null) {
					ItemStack item = e.getCurrentItem();
					if(!inBlackSmith(e.getRawSlot())) {// Click In Players Inventory
						if(item.getAmount() != 1) return;
						if(Main.CE.hasEnchantments(item) || item.getType() == Main.CE.getEnchantmentBookItem().getType()) {
							if(item.getType() == Main.CE.getEnchantmentBookItem().getType()) {//Is a custom enchantment book.
								if(!item.hasItemMeta()) return;
								if(!item.getItemMeta().hasDisplayName()) return;
								boolean T = false;
								for(CEnchantments en : Main.CE.getEnchantments()) {
									if(item.getItemMeta().getDisplayName().startsWith(en.getBookColor() + en.getCustomName())) {
										T = true;
									}
								}
								for(String en : Main.CustomE.getEnchantments()) {
									if(item.getItemMeta().getDisplayName().startsWith(Main.CustomE.getBookColor(en) + Main.CustomE.getCustomName(en))) {
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
										inv.setItem(16, Methods.addLore(getUpgradedItem(player, inv.getItem(10), inv.getItem(13), true), config.getString("Settings.BlackSmith.Results.Found").replaceAll("%Cost%", getUpgradeCost(player, inv.getItem(10), inv.getItem(13)) + "").replaceAll("%cost%", getUpgradeCost(player, inv.getItem(10), inv.getItem(13)) + "")));
										for(int i : result)
											inv.setItem(i - 1, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setMetaData(5).setName(" ").build());
									}else {
										if(Version.getCurrentVersion().getVersionInteger() < 181) {
											ItemStack it = new ItemBuilder().setMaterial(Material.STAINED_CLAY).setName(config.getString("Settings.BlackSmith.Results.None")).build();
											if(config.contains("Settings.BlackSmith.Results.Not-Found-Lore")) {
												for(String line : config.getStringList("Settings.BlackSmith.Results.Not-Found-Lore")) {
													it = Methods.addLore(it, line);
												}
											}
											inv.setItem(16, it);
										}else {
											ItemStack it = new ItemBuilder().setMaterial(Material.BARRIER).setName(Main.settings.getConfig().getString("Settings.BlackSmith.Results.None")).build();
											if(config.contains("Settings.BlackSmith.Results.Not-Found-Lore")) {
												for(String line : config.getStringList("Settings.BlackSmith.Results.Not-Found-Lore")) {
													it = Methods.addLore(it, line);
												}
											}
											inv.setItem(16, it);
										}
										for(int i : result)
											inv.setItem(i - 1, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setMetaData(14).setName(" ").build());
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
									inv.setItem(16, Methods.addLore(getUpgradedItem(player, inv.getItem(10), inv.getItem(13), true), config.getString("Settings.BlackSmith.Results.Found").replaceAll("%Cost%", getUpgradeCost(player, inv.getItem(10), inv.getItem(13)) + "").replaceAll("%cost%", getUpgradeCost(player, inv.getItem(10), inv.getItem(13)) + "")));
									for(int i : result)
										inv.setItem(i - 1, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setMetaData(5).build());
								}else {
									if(Version.getCurrentVersion().getVersionInteger() < 181) {
										ItemStack it = new ItemBuilder().setMaterial(Material.STAINED_CLAY).setName(Main.settings.getConfig().getString("Settings.BlackSmith.Results.None")).build();
										if(config.contains("Settings.BlackSmith.Results.Not-Found-Lore")) {
											for(String line : config.getStringList("Settings.BlackSmith.Results.Not-Found-Lore")) {
												it = Methods.addLore(it, line);
											}
										}
										inv.setItem(16, it);
									}else {
										ItemStack it = new ItemBuilder().setMaterial(Material.BARRIER).setName(Main.settings.getConfig().getString("Settings.BlackSmith.Results.None")).build();
										if(config.contains("Settings.BlackSmith.Results.Not-Found-Lore")) {
											for(String line : config.getStringList("Settings.BlackSmith.Results.Not-Found-Lore")) {
												it = Methods.addLore(it, line);
											}
										}
										inv.setItem(16, it);
									}
									for(int i : result)
										inv.setItem(i - 1, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setMetaData(14).setName(" ").build());
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
							if(Version.getCurrentVersion().getVersionInteger() < 181) {
								ItemStack it = new ItemBuilder().setMaterial(Material.STAINED_CLAY).setName(Main.settings.getConfig().getString("Settings.BlackSmith.Results.None")).build();
								if(config.contains("Settings.BlackSmith.Results.Not-Found-Lore")) {
									for(String line : config.getStringList("Settings.BlackSmith.Results.Not-Found-Lore")) {
										it = Methods.addLore(it, line);
									}
								}
								inv.setItem(16, it);
							}else {
								ItemStack it = new ItemBuilder().setMaterial(Material.BARRIER).setName(Main.settings.getConfig().getString("Settings.BlackSmith.Results.None")).build();
								if(config.contains("Settings.BlackSmith.Results.Not-Found-Lore")) {
									for(String line : config.getStringList("Settings.BlackSmith.Results.Not-Found-Lore")) {
										it = Methods.addLore(it, line);
									}
								}
								inv.setItem(16, it);
							}
							for(int i : result)
								inv.setItem(i - 1, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setMetaData(14).setName(" ").build());
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
												switch(currency) {
													case VAULT:
														player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Need-More-Money").replace("%Money_Needed%", needed).replace("%money_needed%", needed)));
														break;
													case XP_LEVEL:
														player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Need-More-XP-Lvls").replace("%XP%", needed).replace("%xp%", needed)));
														break;
													case XP_TOTAL:
														player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Need-More-Total-XP").replace("%XP%", needed).replace("%xp%", needed)));
														break;
												}
												return;
											}
										}
									}
									if(Methods.isInvFull(player)) {
										player.getWorld().dropItem(player.getLocation(), getUpgradedItem(player, inv.getItem(10), inv.getItem(13), true));
									}else {
										player.getInventory().addItem(getUpgradedItem(player, inv.getItem(10), inv.getItem(13), true));
									}
									inv.setItem(10, new ItemStack(Material.AIR));
									inv.setItem(13, new ItemStack(Material.AIR));
									try {
										if(Version.getCurrentVersion().getVersionInteger() >= 191) {
											player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
										}else {
											player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
										}
									}catch(Exception ex) {
									}
									if(Version.getCurrentVersion().getVersionInteger() < 181) {
										ItemStack it = new ItemBuilder().setMaterial(Material.STAINED_CLAY).setName(Main.settings.getConfig().getString("Settings.BlackSmith.Results.None")).build();
										if(config.contains("Settings.BlackSmith.Results.Not-Found-Lore")) {
											for(String line : config.getStringList("Settings.BlackSmith.Results.Not-Found-Lore")) {
												it = Methods.addLore(it, line);
											}
										}
										inv.setItem(16, it);
									}else {
										ItemStack it = new ItemBuilder().setMaterial(Material.BARRIER).setName(Main.settings.getConfig().getString("Settings.BlackSmith.Results.None")).build();
										if(config.contains("Settings.BlackSmith.Results.Not-Found-Lore")) {
											for(String line : config.getStringList("Settings.BlackSmith.Results.Not-Found-Lore")) {
												it = Methods.addLore(it, line);
											}
										}
										inv.setItem(16, it);
									}
									for(int i : result)
										inv.setItem(i - 1, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setMetaData(14).setName(" ").build());
								}else {
									try {
										if(Version.getCurrentVersion().getVersionInteger() >= 191) {
											player.playSound(player.getLocation(), Sound.valueOf("ENTITY_VILLAGER_NO"), 1, 1);
										}
										if(Version.getCurrentVersion().getVersionInteger() < 191) {
											player.playSound(player.getLocation(), Sound.valueOf("VILLAGER_NO"), 1, 1);
										}
									}catch(Exception ex) {
									}
								}
							}else {
								try {
									if(Version.getCurrentVersion().getVersionInteger() >= 191) {
										player.playSound(player.getLocation(), Sound.valueOf("ENTITY_VILLAGER_NO"), 1, 1);
									}
									if(Version.getCurrentVersion().getVersionInteger() < 191) {
										player.playSound(player.getLocation(), Sound.valueOf("VILLAGER_NO"), 1, 1);
									}
								}catch(Exception ex) {
								}
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
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				if(inv != null) {
					if(inv.getName().equals(Methods.color(Main.settings.getConfig().getString("Settings.BlackSmith.GUIName")))) {
						List<Integer> slots = new ArrayList<>();
						slots.add(10);
						slots.add(13);
						Boolean dead = e.getPlayer().isDead();
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
			}
		}, 0);
	}

	private ItemStack getUpgradedItem(Player player, ItemStack master, ItemStack sub, boolean glowing) {
		ItemStack item = master.clone();
		if(master.getType() == Main.CE.getEnchantmentBookItem().getType() && sub.getType() == Main.CE.getEnchantmentBookItem().getType()) {
			if(Methods.removeColor(master.getItemMeta().getDisplayName()).equalsIgnoreCase(Methods.removeColor(sub.getItemMeta().getDisplayName()))) {
				for(CEnchantments en : Main.CE.getEnchantments()) {
					if(master.getItemMeta().getDisplayName().startsWith(en.getBookColor() + en.getCustomName())) {
						int power = Main.CE.getBookPower(master, en);
						int max = Main.settings.getEnchs().getInt("Enchantments." + en.getName() + ".MaxPower");
						if(power + 1 <= max) {
							item = new ItemBuilder().setMaterial(Main.settings.getConfig().getString("Settings.Enchantment-Book-Item")).setName(en.getBookColor() + en.getCustomName() + " " + Methods.getPower(power + 1)).setLore(master.getItemMeta().getLore()).setGlowing(glowing).build();
						}
					}
				}
				for(String en : Main.CustomE.getEnchantments()) {
					if(master.getItemMeta().getDisplayName().startsWith(Main.CustomE.getBookColor(en) + Main.CustomE.getCustomName(en))) {
						int power = Main.CustomE.getBookPower(master, en);
						int max = Main.settings.getCustomEnchs().getInt("Enchantments." + en + ".MaxPower");
						if(power + 1 <= max) {
							item = new ItemBuilder().setMaterial(Main.settings.getConfig().getString("Settings.Enchantment-Book-Item")).setName(Main.CustomE.getBookColor(en) + Main.CustomE.getCustomName(en) + " " + Methods.getPower(power + 1)).setLore(master.getItemMeta().getLore()).setGlowing(glowing).build();
						}
					}
				}
			}
		}else {
			if(master.getType() == sub.getType()) {
				HashMap<String, Integer> dupEnchants = new HashMap<>();
				HashMap<String, Integer> newEnchants = new HashMap<>();
				HashMap<String, Integer> higherEnchants = new HashMap<>();
				for(CEnchantments enchant : Main.CE.getItemEnchantments(master)) {
					if(Main.CE.hasEnchantment(sub, enchant)) {
						if(Main.CE.getPower(master, enchant).equals(Main.CE.getPower(sub, enchant))) {
							if(!dupEnchants.containsKey(enchant.getName())) {
								dupEnchants.put(enchant.getName(), Main.CE.getPower(master, enchant));
							}
						}else {
							if(Main.CE.getPower(master, enchant) < Main.CE.getPower(sub, enchant)) {
								higherEnchants.put(enchant.getName(), Main.CE.getPower(sub, enchant));
							}
						}
					}
				}
				for(CEnchantments enchant : Main.CE.getItemEnchantments(sub)) {
					if(!dupEnchants.containsKey(enchant.getName()) && !higherEnchants.containsKey(enchant.getName())) {
						if(!Main.CE.hasEnchantment(master, enchant)) {
							newEnchants.put(enchant.getName(), Main.CE.getPower(sub, enchant));
						}
					}
				}
				for(String enchant : Main.CustomE.getItemEnchantments(master)) {
					if(Main.CustomE.hasEnchantment(sub, enchant)) {
						if(Main.CustomE.getPower(master, enchant).equals(Main.CustomE.getPower(sub, enchant))) {
							if(!dupEnchants.containsKey(enchant)) {
								dupEnchants.put(enchant, Main.CustomE.getPower(master, enchant));
							}
						}else {
							if(Main.CustomE.getPower(master, enchant) < Main.CustomE.getPower(sub, enchant)) {
								higherEnchants.put(enchant, Main.CustomE.getPower(sub, enchant));
							}
						}
					}
				}
				for(String enchant : Main.CustomE.getItemEnchantments(sub)) {
					if(!dupEnchants.containsKey(enchant) && !higherEnchants.containsKey(enchant)) {
						if(!Main.CustomE.hasEnchantment(master, enchant)) {
							newEnchants.put(enchant, Main.CustomE.getPower(sub, enchant));
						}

					}
				}
				for(String enchant : dupEnchants.keySet()) {
					if(Main.CE.isEnchantment(enchant)) {
						int power = dupEnchants.get(enchant);
						int max = Main.CE.getMaxPower(Main.CE.getFromName(enchant));
						if(power + 1 <= max) {
							item = Main.CE.addEnchantment(item, Main.CE.getFromName(enchant), power + 1);
						}
					}else if(Main.CustomE.isEnchantment(enchant)) {
						int power = dupEnchants.get(enchant);
						int max = Main.CustomE.getMaxPower(enchant);
						if(power + 1 <= max) {
							item = Main.CustomE.addEnchantment(item, enchant, power + 1);
						}
					}
				}
				int maxEnchants = Main.CE.getPlayerMaxEnchantments(player);
				for(String enchant : newEnchants.keySet()) {
					if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle")) {
						if((Methods.getEnchAmount(item) + 1) <= maxEnchants) {
							if(Main.CE.isEnchantment(enchant)) {
								item = Main.CE.addEnchantment(item, Main.CE.getFromName(enchant), newEnchants.get(enchant));
							}else if(Main.CustomE.isEnchantment(enchant)) {
								item = Main.CustomE.addEnchantment(item, enchant, newEnchants.get(enchant));
							}
						}
					}
				}
				for(String enchant : higherEnchants.keySet()) {
					if(Main.CE.isEnchantment(enchant)) {
						item = Main.CE.addEnchantment(item, Main.CE.getFromName(enchant), higherEnchants.get(enchant));
					}else if(Main.CustomE.isEnchantment(enchant)) {
						item = Main.CustomE.addEnchantment(item, enchant, higherEnchants.get(enchant));
					}
				}
			}
		}
		return item;
	}

	private int getUpgradeCost(Player player, ItemStack master, ItemStack sub) {
		int total = 0;
		//Is 2 books
		if(master.getType() == Main.CE.getEnchantmentBookItem().getType() && sub.getType() == Main.CE.getEnchantmentBookItem().getType()) {
			if(Methods.removeColor(master.getItemMeta().getDisplayName()).equalsIgnoreCase(Methods.removeColor(sub.getItemMeta().getDisplayName()))) {
				for(CEnchantments en : Main.CE.getEnchantments()) {
					if(master.getItemMeta().getDisplayName().startsWith(en.getBookColor() + en.getCustomName())) {
						int power = Main.CE.getBookPower(master, en);
						int max = Main.settings.getEnchs().getInt("Enchantments." + en.getName() + ".MaxPower");
						if(power + 1 <= max) {
							total += Main.settings.getConfig().getInt("Settings.BlackSmith.Transaction.Costs.Book-Upgrade");
						}
					}
				}
				for(String en : Main.CustomE.getEnchantments()) {
					if(master.getItemMeta().getDisplayName().startsWith(Main.CustomE.getBookColor(en) + Main.CustomE.getCustomName(en))) {
						int power = Main.CustomE.getBookPower(master, en);
						int max = Main.settings.getCustomEnchs().getInt("Enchantments." + en + ".MaxPower");
						if(power + 1 <= max) {
							total += Main.settings.getConfig().getInt("Settings.BlackSmith.Transaction.Costs.Book-Upgrade");
						}
					}
				}
			}
		}
		//Is 2 items
		if(master.getType() != Main.CE.getEnchantmentBookItem().getType() || sub.getType() != Main.CE.getEnchantmentBookItem().getType()) {
			if(master.getType() == sub.getType()) {
				ItemStack item = master.clone();
				HashMap<String, Integer> dupEnchants = new HashMap<>();
				HashMap<String, Integer> newEnchants = new HashMap<>();
				HashMap<String, Integer> higherEnchants = new HashMap<>();
				for(CEnchantments enchant : Main.CE.getItemEnchantments(master)) {
					if(Main.CE.hasEnchantment(sub, enchant)) {
						if(Main.CE.getPower(master, enchant).equals(Main.CE.getPower(sub, enchant))) {
							if(!dupEnchants.containsKey(enchant.getName())) {
								dupEnchants.put(enchant.getName(), Main.CE.getPower(master, enchant));
							}
						}else {
							if(Main.CE.getPower(master, enchant) < Main.CE.getPower(sub, enchant)) {
								higherEnchants.put(enchant.getName(), Main.CE.getPower(sub, enchant));
							}
						}
					}
				}
				for(CEnchantments enchant : Main.CE.getItemEnchantments(sub)) {
					if(!dupEnchants.containsKey(enchant) && !higherEnchants.containsKey(enchant)) {
						if(!Main.CE.hasEnchantment(master, enchant)) {
							newEnchants.put(enchant.getName(), Main.CE.getPower(sub, enchant));
						}
					}
				}
				for(String enchant : Main.CustomE.getItemEnchantments(master)) {
					if(Main.CustomE.hasEnchantment(sub, enchant)) {
						if(Main.CustomE.getPower(master, enchant).equals(Main.CustomE.getPower(sub, enchant))) {
							if(!dupEnchants.containsKey(enchant)) {
								dupEnchants.put(enchant, Main.CustomE.getPower(master, enchant));
							}
						}else {
							if(Main.CustomE.getPower(master, enchant) < Main.CustomE.getPower(sub, enchant)) {
								higherEnchants.put(enchant, Main.CustomE.getPower(sub, enchant));
							}
						}
					}
				}
				for(String enchant : Main.CustomE.getItemEnchantments(sub)) {
					if(!dupEnchants.containsKey(enchant) && !higherEnchants.containsKey(enchant)) {
						if(!Main.CustomE.hasEnchantment(master, enchant)) {
							newEnchants.put(enchant, Main.CustomE.getPower(sub, enchant));
						}
					}
				}
				for(String enchant : dupEnchants.keySet()) {
					if(Main.CE.isEnchantment(enchant)) {
						int power = dupEnchants.get(enchant);
						int max = Main.CE.getMaxPower(Main.CE.getFromName(enchant));
						if(power + 1 <= max) {
							total += Main.settings.getConfig().getInt("Settings.BlackSmith.Transaction.Costs.Power-Up");
						}
					}else if(Main.CustomE.isEnchantment(enchant)) {
						int power = dupEnchants.get(enchant);
						int max = Main.CustomE.getMaxPower(enchant);
						if(power + 1 <= max) {
							total += Main.settings.getConfig().getInt("Settings.BlackSmith.Transaction.Costs.Power-Up");
						}
					}
				}
				int maxEnchants = Main.CE.getPlayerMaxEnchantments(player);
				for(int i = 0; i < newEnchants.size(); i++) {
					if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle")) {
						if((Methods.getEnchAmount(item) + i + 1) <= maxEnchants) {
							total += Main.settings.getConfig().getInt("Settings.BlackSmith.Transaction.Costs.Add-Enchantment");
						}
					}
				}
				for(int i = 0; i < higherEnchants.size(); i++) {
					total += Main.settings.getConfig().getInt("Settings.BlackSmith.Transaction.Costs.Power-Up");
				}
			}
		}
		return total;
	}

	private boolean inBlackSmith(int slot) {
		//The last slot in the tinker is 54
		if(slot < 27) return true;
		return false;
	}

	private void playClick(Player player) {
		try {
			if(Version.getCurrentVersion().getVersionInteger() >= 191) {
				player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
			}else {
				player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
			}
		}catch(Exception ex) {
		}
	}

}
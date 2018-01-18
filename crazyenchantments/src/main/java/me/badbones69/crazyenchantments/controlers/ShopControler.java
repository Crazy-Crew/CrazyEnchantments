package me.badbones69.crazyenchantments.controlers;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.currencyapi.Currency;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.events.BuyBookEvent;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShopControler implements Listener {

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		Inventory inv = e.getInventory();
		Player player = (Player) e.getWhoClicked();
		FileConfiguration config = Main.settings.getConfig();
		if(inv != null) {
			if(inv.getName().equals(Methods.getInvName())) {
				e.setCancelled(true);
				if(e.getRawSlot() >= inv.getSize()) return;
				if(item == null) return;
				if(item.hasItemMeta()) {
					if(item.getItemMeta().hasDisplayName()) {
						String name = item.getItemMeta().getDisplayName();
						for(String cat : config.getConfigurationSection("Categories").getKeys(false)) {
							if(Main.settings.getConfig().getBoolean("Categories." + cat + ".InGUI")) {
								if(name.equals(Methods.color(config.getString("Categories." + cat + ".Name")))) {
									if(Methods.isInvFull(player)) {
										if(!Main.settings.getMessages().contains("Messages.Inventory-Full")) {
											player.sendMessage(Methods.color("&cYour inventory is to full. Please open up some space to buy that."));
										}else {
											player.sendMessage(Methods.color(Main.settings.getMessages().getString("Messages.Inventory-Full")));
										}
										return;
									}
									Currency currency = null;
									int cost = 0;
									if(player.getGameMode() != GameMode.CREATIVE) {
										if(Currency.isCurrency(config.getString("Categories." + cat + ".Currency"))) {
											currency = Currency.getCurrency(config.getString("Categories." + cat + ".Currency"));
											cost = config.getInt("Categories." + cat + ".Cost");
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
									ItemStack book = EnchantmentControl.pick(cat);
									boolean isCustom = Main.CustomE.isEnchantmentBook(book);
									BuyBookEvent event;
									if(isCustom) {
										event = new BuyBookEvent(Main.CE.getCEPlayer(player), currency, cost, null, Main.CustomE.convertToCEBook(book));
									}else {
										event = new BuyBookEvent(Main.CE.getCEPlayer(player), currency, cost, Main.CE.convertToCEBook(book), null);
									}
									Bukkit.getPluginManager().callEvent(event);
									player.getInventory().addItem(book);
									return;
								}
							}
						}
						for(String cat : config.getConfigurationSection("Categories").getKeys(false)) {
							if(Main.settings.getConfig().getBoolean("Categories." + cat + ".LostBook.InGUI")) {
								if(name.equals(Methods.color(config.getString("Categories." + cat + ".LostBook.Name")))) {
									if(Methods.isInvFull(player)) {
										if(!Main.settings.getMessages().contains("Messages.Inventory-Full")) {
											player.sendMessage(Methods.getPrefix() + Methods.color("&cYour inventory is to full. Please open up some space to buy that."));
										}else {
											player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Inventory-Full")));
										}
										return;
									}
									if(player.getGameMode() != GameMode.CREATIVE) {
										if(Currency.isCurrency(config.getString("Categories." + cat + ".LostBook.Currency"))) {
											Currency currency = Currency.getCurrency(config.getString("Categories." + cat + ".LostBook.Currency"));
											int cost = config.getInt("Categories." + cat + ".LostBook.Cost");
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
									player.getInventory().addItem(LostBook.getLostBook(cat, 1));
									return;
								}
							}
						}
						List<String> options = new ArrayList<String>();
						options.add("BlackScroll");
						options.add("WhiteScroll");
						options.add("TransmogScroll");
						options.add("ProtectionCrystal");
						options.add("Scrambler");
						for(String o : options) {
							if(name.equalsIgnoreCase(Methods.color(config.getString("Settings." + o + ".GUIName")))) {
								if(Methods.isInvFull(player)) {
									player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Inventory-Full")));
									return;
								}
								if(player.getGameMode() != GameMode.CREATIVE) {
									if(Currency.isCurrency(config.getString("Settings.Costs." + o + ".Currency"))) {
										Currency currency = Currency.getCurrency(config.getString("Settings.Costs." + o + ".Currency"));
										int cost = config.getInt("Settings.Costs." + o + ".Cost");
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
								switch(o) {
									case "BlackScroll":
										player.getInventory().addItem(ScrollControl.getBlackScroll(1));
										break;
									case "WhiteScroll":
										player.getInventory().addItem(ScrollControl.getWhiteScroll(1));
										break;
									case "TransmogScroll":
										player.getInventory().addItem(ScrollControl.getTransmogScroll(1));
										break;
									case "ProtectionCrystal":
										player.getInventory().addItem(ProtectionCrystal.getCrystals());
										break;
									case "Scrambler":
										player.getInventory().addItem(Scrambler.getScramblers());
										break;
								}
								return;
							}
						}
						options.clear();
						options.add("DestroyDust");
						options.add("SuccessDust");
						for(String o : options) {
							if(name.equalsIgnoreCase(Methods.color(config.getString("Settings.Dust." + o + ".GUIName")))) {
								if(Methods.isInvFull(player)) {
									if(!Main.settings.getMessages().contains("Messages.Inventory-Full")) {
										player.sendMessage(Methods.getPrefix() + Methods.color("&cYour inventory is to full. Please open up some space to buy that."));
									}else {
										player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Inventory-Full")));
									}
									return;
								}
								if(player.getGameMode() != GameMode.CREATIVE) {
									if(Currency.isCurrency(config.getString("Settings.Costs." + o + ".Currency"))) {
										Currency currency = Currency.getCurrency(config.getString("Settings.Costs." + o + ".Currency"));
										int cost = config.getInt("Settings.Costs." + o + ".Cost");
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
								switch(o) {
									case "DestroyDust":
										player.getInventory().addItem(DustControl.getDust("DestroyDust", 1));
										break;
									case "SuccessDust":
										player.getInventory().addItem(DustControl.getDust("SuccessDust", 1));
										break;
								}
								return;
							}
						}
						if(name.equalsIgnoreCase(Methods.color(config.getString("Settings.GKitz.Name")))) {
							if(!Methods.hasPermission(player, "gkitz", true)) return;
							GKitzControler.openGUI(player);
							return;
						}
						if(name.equalsIgnoreCase(Methods.color(config.getString("Settings.BlackSmith.Name")))) {
							if(!Methods.hasPermission(player, "blacksmith", true)) return;
							BlackSmith.openBlackSmith(player);
							return;
						}
						if(name.equalsIgnoreCase(Methods.color(config.getString("Settings.Tinker.Name")))) {
							if(!Methods.hasPermission(player, "tinker", true)) return;
							Tinkerer.openTinker(player);
							return;
						}
						if(name.equalsIgnoreCase(Methods.color(config.getString("Settings.Info.Name")))) {
							InfoGUIControl.openInfo(player);
							return;
						}
					}
				}
			}
		}
	}

	public static void openGUI(Player player) {
		int size = Main.settings.getConfig().getInt("Settings.GUISize");
		Inventory inv = Bukkit.createInventory(null, size, Methods.getInvName());
		if(Main.settings.getConfig().contains("Settings.GUICustomization")) {
			for(String custom : Main.settings.getConfig().getStringList("Settings.GUICustomization")) {
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
						for(Currency c : Currency.values()) {
							i = i.replaceAll("%" + c.getName().toLowerCase() + "%", CurrencyAPI.getCurrency(player, c) + "");
						}
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
							for(Currency c : Currency.values()) {
								l = l.replaceAll("%" + c.getName().toLowerCase() + "%", CurrencyAPI.getCurrency(player, c) + "");
							}
							lore.add(l);
						}
					}
				}
				if(slot > size || slot <= 0) {
					continue;
				}
				slot--;
				inv.setItem(slot, new ItemBuilder().setMaterial(item).setName(name).setLore(lore).build());
			}
		}
		for(String cat : Main.settings.getConfig().getConfigurationSection("Categories").getKeys(false)) {
			FileConfiguration config = Main.settings.getConfig();
			if(config.getBoolean("Categories." + cat + ".InGUI")) {
				int slot = config.getInt("Categories." + cat + ".Slot");
				if(slot > size) {
					continue;
				}
				String id = config.getString("Categories." + cat + ".Item");
				String name = config.getString("Categories." + cat + ".Name");
				List<String> lore = config.getStringList("Categories." + cat + ".Lore");
				Boolean glowing = false;
				if(config.contains("Categories." + cat + ".Glowing")) {
					if(config.getBoolean("Categories." + cat + ".Glowing")) {
						glowing = true;
					}
				}
				inv.setItem(slot - 1, new ItemBuilder().setMaterial(id).setName(name).setLore(lore).setGlowing(glowing).build());
			}
			if(config.getBoolean("Categories." + cat + ".LostBook.InGUI")) {
				int slot = config.getInt("Categories." + cat + ".LostBook.Slot");
				if(slot > size) {
					continue;
				}
				String id = config.getString("Categories." + cat + ".LostBook.Item");
				String name = config.getString("Categories." + cat + ".LostBook.Name");
				List<String> lore = config.getStringList("Categories." + cat + ".LostBook.Lore");
				Boolean glowing = false;
				if(config.contains("Categories." + cat + ".LostBook.Glowing")) {
					if(config.getBoolean("Categories." + cat + ".LostBook.Glowing")) {
						glowing = true;
					}
				}
				inv.setItem(slot - 1, new ItemBuilder().setMaterial(id).setName(name).setLore(lore).setGlowing(glowing).build());
			}
		}
		ArrayList<String> options = new ArrayList<String>();
		options.add("GKitz");
		options.add("BlackSmith");
		options.add("Tinker");
		options.add("Info");
		for(String op : options) {
			if(Main.settings.getConfig().contains("Settings." + op)) {
				if(Main.settings.getConfig().getBoolean("Settings." + op + ".InGUI")) {
					String name = Main.settings.getConfig().getString("Settings." + op + ".Name");
					String id = Main.settings.getConfig().getString("Settings." + op + ".Item");
					List<String> lore = Main.settings.getConfig().getStringList("Settings." + op + ".Lore");
					int slot = Main.settings.getConfig().getInt("Settings." + op + ".Slot") - 1;
					boolean glowing = false;
					if(Main.settings.getConfig().contains("Settings." + op + ".Glowing")) {
						glowing = Main.settings.getConfig().getBoolean("Settings." + op + ".Glowing");
					}
					if(slot > size) {
						continue;
					}
					inv.setItem(slot, new ItemBuilder().setMaterial(id).setName(name).setLore(lore).setGlowing(glowing).build());
				}
			}
		}
		options.clear();
		options.add("ProtectionCrystal");
		options.add("Dust.SuccessDust");
		options.add("Dust.DestroyDust");
		options.add("Scrambler");
		for(String op : options) {
			if(Main.settings.getConfig().contains("Settings." + op)) {
				if(Main.settings.getConfig().getBoolean("Settings." + op + ".InGUI")) {
					String name = Main.settings.getConfig().getString("Settings." + op + ".GUIName");
					String id = Main.settings.getConfig().getString("Settings." + op + ".Item");
					List<String> lore = Main.settings.getConfig().getStringList("Settings." + op + ".GUILore");
					int slot = Main.settings.getConfig().getInt("Settings." + op + ".Slot") - 1;
					boolean glowing = false;
					if(Main.settings.getConfig().contains("Settings." + op + ".Glowing")) {
						glowing = Main.settings.getConfig().getBoolean("Settings." + op + ".Glowing");
					}
					if(slot > size) {
						continue;
					}
					inv.setItem(slot, new ItemBuilder().setMaterial(id).setName(name).setLore(lore).setGlowing(glowing).build());
				}
			}
		}
		options.clear();
		options.add("BlackScroll");
		options.add("WhiteScroll");
		options.add("TransmogScroll");
		for(String op : options) {
			if(Main.settings.getConfig().contains("Settings." + op)) {
				if(Main.settings.getConfig().getBoolean("Settings." + op + ".InGUI")) {
					String name = Main.settings.getConfig().getString("Settings." + op + ".GUIName");
					String id = Main.settings.getConfig().getString("Settings." + op + ".Item");
					List<String> lore = Main.settings.getConfig().getStringList("Settings." + op + ".Lore");
					int slot = Main.settings.getConfig().getInt("Settings." + op + ".Slot") - 1;
					boolean glowing = false;
					if(Main.settings.getConfig().contains("Settings." + op + ".Glowing")) {
						glowing = Main.settings.getConfig().getBoolean("Settings." + op + ".Glowing");
					}
					if(slot > size) {
						continue;
					}
					inv.setItem(slot, new ItemBuilder().setMaterial(id).setName(name).setLore(lore).setGlowing(glowing).build());
				}
			}
		}
		player.openInventory(inv);
	}

	@EventHandler
	public void onEnchantmentTableClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		Block block = e.getClickedBlock();
		if(block != null) {
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(block.getType() == Material.ENCHANTMENT_TABLE) {
					if(Main.settings.getConfig().contains("Settings.EnchantmentOptions.Right-Click-Enchantment-Table")) {
						if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.Right-Click-Enchantment-Table")) {
							e.setCancelled(true);
							openGUI(player);
						}
					}
				}
			}
		}
	}

}
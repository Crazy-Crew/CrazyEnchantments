package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.currencyapi.Currency;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.enums.Dust;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.enums.Scrolls;
import me.badbones69.crazyenchantments.api.events.BuyBookEvent;
import me.badbones69.crazyenchantments.api.objects.CEBook;
import me.badbones69.crazyenchantments.api.objects.Category;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SignControl implements Listener {
	
	private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getClickedBlock() == null) return;
		Location Loc = e.getClickedBlock().getLocation();
		Player player = e.getPlayer();
		if(!(e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
		if(e.getClickedBlock().getState() instanceof Sign) {
			FileConfiguration config = Files.CONFIG.getFile();
			for(String l : Files.SIGNS.getFile().getConfigurationSection("Locations").getKeys(false)) {
				String type = Files.SIGNS.getFile().getString("Locations." + l + ".Type");
				World world = Bukkit.getWorld(Files.SIGNS.getFile().getString("Locations." + l + ".World"));
				int x = Files.SIGNS.getFile().getInt("Locations." + l + ".X");
				int y = Files.SIGNS.getFile().getInt("Locations." + l + ".Y");
				int z = Files.SIGNS.getFile().getInt("Locations." + l + ".Z");
				Location loc = new Location(world, x, y, z);
				if(Loc.equals(loc)) {
					if(Methods.isInventoryFull(player)) {
						player.sendMessage(Messages.INVENTORY_FULL.getMessage());
						return;
					}
					List<String> options = new ArrayList<>();
					options.add("ProtectionCrystal");
					options.add("Scrambler");
					options.add("DestroyDust");
					options.add("SuccessDust");
					options.add("BlackScroll");
					options.add("WhiteScroll");
					options.add("TransmogScroll");
					for(String o : options) {
						if(o.equalsIgnoreCase(type)) {
							if(player.getGameMode() != GameMode.CREATIVE) {
								if(Currency.isCurrency(config.getString("Settings.Costs." + o + ".Currency"))) {
									Currency currency = Currency.getCurrency(config.getString("Settings.Costs." + o + ".Currency"));
									int cost = config.getInt("Settings.Costs." + o + ".Cost");
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
							if(config.contains("Settings.SignOptions." + o + "Style.Buy-Message")) {
								player.sendMessage(Methods.color(Methods.getPrefix() + config.getString("Settings.SignOptions." + o + "Style.Buy-Message")));
							}
							switch(o) {
								case "ProtectionCrystal":
									player.getInventory().addItem(ProtectionCrystal.getCrystals());
									break;
								case "Scrambler":
									player.getInventory().addItem(Scrambler.getScramblers());
									break;
								case "DestroyDust":
									player.getInventory().addItem(Dust.DESTROY_DUST.getDust());
									break;
								case "SuccessDust":
									player.getInventory().addItem(Dust.SUCCESS_DUST.getDust());
									break;
								case "BlackScroll":
									player.getInventory().addItem(Scrolls.BlACK_SCROLL.getScroll());
									break;
								case "WhiteScroll":
									player.getInventory().addItem(Scrolls.WHITE_SCROLL.getScroll());
									break;
								case "TransmogScroll":
									player.getInventory().addItem(Scrolls.TRANSMOG_SCROLL.getScroll());
									break;
							}
							return;
						}
					}
					Category category = ce.getCategory(type);
					if(category != null) {
						if(player.getGameMode() != GameMode.CREATIVE) {
							if(category.getCurrency() != null) {
								if(CurrencyAPI.canBuy(player, category)) {
									CurrencyAPI.takeCurrency(player, category);
								}else {
									String needed = (category.getCost() - CurrencyAPI.getCurrency(player, category.getCurrency())) + "";
									HashMap<String, String> placeholders = new HashMap<>();
									placeholders.put("%money_needed%", needed);
									placeholders.put("%xp%", needed);
									switch(category.getCurrency()) {
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
									return;
								}
							}
						}
						CEBook book = ce.getRandomEnchantmentBook(category);
						ItemBuilder itemBuilder = book.getItemBuilder();
						if(config.contains("Settings.SignOptions.CategoryShopStyle.Buy-Message")) {
							player.sendMessage(Methods.color(Methods.getPrefix() + config.getString("Settings.SignOptions.CategoryShopStyle.Buy-Message")
							.replaceAll("%BookName%", itemBuilder.getName()).replaceAll("%bookname%", itemBuilder.getName())
							.replaceAll("%Category%", category.getName()).replaceAll("%category%", category.getName())));
						}
						BuyBookEvent event = new BuyBookEvent(ce.getCEPlayer(player), category.getCurrency(), category.getCost(), book);
						Bukkit.getPluginManager().callEvent(event);
						player.getInventory().addItem(itemBuilder.build());
						return;
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBreak(BlockBreakEvent e) {
		if(!e.isCancelled() && !ce.getSkippedBreakEvents().contains(e)) {
			Player player = e.getPlayer();
			Location Loc = e.getBlock().getLocation();
			for(String locationName : Files.SIGNS.getFile().getConfigurationSection("Locations").getKeys(false)) {
				World world = Bukkit.getWorld(Files.SIGNS.getFile().getString("Locations." + locationName + ".World"));
				int x = Files.SIGNS.getFile().getInt("Locations." + locationName + ".X");
				int y = Files.SIGNS.getFile().getInt("Locations." + locationName + ".Y");
				int z = Files.SIGNS.getFile().getInt("Locations." + locationName + ".Z");
				Location location = new Location(world, x, y, z);
				if(Loc.equals(location)) {
					Files.SIGNS.getFile().set("Locations." + locationName, null);
					Files.SIGNS.saveFile();
					player.sendMessage(Messages.BREAK_ENCHANTMENT_SHOP_SIGN.getMessage());
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void onSignMake(SignChangeEvent e) {
		Player player = e.getPlayer();
		Location loc = e.getBlock().getLocation();
		FileConfiguration signs = Files.SIGNS.getFile();
		String id = new Random().nextInt(Integer.MAX_VALUE) + "";
		for(int i = 0; i < 200; i++) {
			if(signs.contains("Locations." + id)) {
				id = new Random().nextInt(Integer.MAX_VALUE) + "";
			}else {
				break;
			}
		}
		String line1 = e.getLine(0);
		String line2 = e.getLine(1);
		if(Methods.hasPermission(player, "sign", false)) {
			if(line1.equalsIgnoreCase("{CrazyEnchant}")) {
				for(Category category : ce.getCategories()) {
					if(line2.equalsIgnoreCase("{" + category.getName() + "}")) {
						e.setLine(0, placeHolders(Files.CONFIG.getFile().getString("Settings.SignOptions.CategoryShopStyle.Line1"), category));
						e.setLine(1, placeHolders(Files.CONFIG.getFile().getString("Settings.SignOptions.CategoryShopStyle.Line2"), category));
						e.setLine(2, placeHolders(Files.CONFIG.getFile().getString("Settings.SignOptions.CategoryShopStyle.Line3"), category));
						e.setLine(3, placeHolders(Files.CONFIG.getFile().getString("Settings.SignOptions.CategoryShopStyle.Line4"), category));
						signs.set("Locations." + id + ".Type", category.getName());
						signs.set("Locations." + id + ".World", loc.getWorld().getName());
						signs.set("Locations." + id + ".X", loc.getBlockX());
						signs.set("Locations." + id + ".Y", loc.getBlockY());
						signs.set("Locations." + id + ".Z", loc.getBlockZ());
						Files.SIGNS.saveFile();
						return;
					}
				}
				HashMap<String, String> types = new HashMap<>();
				types.put("Crystal", "ProtectionCrystal");
				types.put("Scrambler", "Scrambler");
				types.put("DestroyDust", "DestroyDust");
				types.put("SuccessDust", "SuccessDust");
				types.put("BlackScroll", "BlackScroll");
				types.put("WhiteScroll", "WhiteScroll");
				types.put("TransmogScroll", "TransmogScroll");
				for(String type : types.keySet()) {
					if(line2.equalsIgnoreCase("{" + type + "}")) {
						type = types.get(type);
						e.setLine(0, Methods.color(Files.CONFIG.getFile().getString("Settings.SignOptions." + type + "Style.Line1")));
						e.setLine(1, Methods.color(Files.CONFIG.getFile().getString("Settings.SignOptions." + type + "Style.Line2")));
						e.setLine(2, Methods.color(Files.CONFIG.getFile().getString("Settings.SignOptions." + type + "Style.Line3")));
						e.setLine(3, Methods.color(Files.CONFIG.getFile().getString("Settings.SignOptions." + type + "Style.Line4")));
						signs.set("Locations." + id + ".Type", type);
						signs.set("Locations." + id + ".World", loc.getWorld().getName());
						signs.set("Locations." + id + ".X", loc.getBlockX());
						signs.set("Locations." + id + ".Y", loc.getBlockY());
						signs.set("Locations." + id + ".Z", loc.getBlockZ());
						Files.SIGNS.saveFile();
						return;
					}
				}
			}
		}
	}
	
	private String placeHolders(String msg, Category category) {
		return Methods.color(msg
		.replaceAll("%category%", category.getName()).replaceAll("%Category%", category.getName())
		.replaceAll("%cost%", category.getCost() + "").replaceAll("%Cost%", category.getCost() + "")
		.replaceAll("%xp%", category.getCost() + "").replaceAll("%XP%", category.getCost() + ""));
	}
	
}
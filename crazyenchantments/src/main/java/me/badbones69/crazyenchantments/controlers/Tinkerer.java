package me.badbones69.crazyenchantments.controlers;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CEnchantments;
import me.badbones69.crazyenchantments.api.EnchantmentType;
import me.badbones69.crazyenchantments.api.currencyapi.Currency;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tinkerer implements Listener {

	public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments");

	public static void openTinker(Player player) {
		Inventory inv = Bukkit.createInventory(null, 54, Methods.color(Main.settings.getTinker().getString("Settings.GUIName")));
		inv.setItem(0, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setMetaData(14).setName(Main.settings.getTinker().getString("Settings.TradeButton")).build());
		ArrayList<Integer> slots = new ArrayList<Integer>();
		slots.add(4);
		slots.add(13);
		slots.add(22);
		slots.add(31);
		slots.add(40);
		slots.add(49);
		for(int i : slots) {
			inv.setItem(i, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setName(" ").build());
		}
		inv.setItem(8, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setMetaData(14).setName(Main.settings.getTinker().getString("Settings.TradeButton")).build());
		player.openInventory(inv);
	}

	@EventHandler
	public void onXPUse(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(Methods.getItemInHand(player) != null) {
				ItemStack item = Methods.getItemInHand(player);
				if(item.getType() == new ItemBuilder().setMaterial(Main.settings.getTinker().getString("Settings.BottleOptions.Item")).getMaterial()) {
					if(item.hasItemMeta()) {
						if(item.getItemMeta().hasLore() && item.getItemMeta().hasDisplayName()) {
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getTinker().getString("Settings.BottleOptions.Name")))) {
								e.setCancelled(true);
								Methods.setItemInHand(player, Methods.removeItem(item));
								if(Currency.isCurrency(Main.settings.getTinker().getString("Settings.Currency"))) {
									CurrencyAPI.giveCurrency(player, Currency.getCurrency(Main.settings.getTinker().getString("Settings.Currency")), getXP(item));
								}
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
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		Player player = (Player) e.getWhoClicked();
		if(inv != null) {
			if(inv.getName().equals(Methods.color(Main.settings.getTinker().getString("Settings.GUIName")))) {
				e.setCancelled(true);
				ItemStack current = e.getCurrentItem();
				if(current != null) {
					if(current.hasItemMeta()) {
						if(current.getItemMeta().hasLore() || current.getItemMeta().hasDisplayName() || current.getItemMeta().hasEnchants()) {
							if(current.getType() != Material.AIR) {
								// Recycling things
								if(current.getItemMeta().hasDisplayName()) {
									if(current.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getTinker().getString("Settings.TradeButton")))) {
										Boolean toggle = false;
										for(int slot : getSlot().keySet()) {
											if(inv.getItem(getSlot().get(slot)) != null) {
												if(Methods.isInvFull(((Player) player))) {
													player.getWorld().dropItem(player.getLocation(), inv.getItem(getSlot().get(slot)));
												}else {
													player.getInventory().addItem(inv.getItem(getSlot().get(slot)));
												}
												toggle = true;
											}
											e.getInventory().setItem(slot, new ItemStack(Material.AIR));
											e.getInventory().setItem(getSlot().get(slot), new ItemStack(Material.AIR));
										}
										player.closeInventory();
										if(toggle) {
											player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Tinker-Sold-Msg")));
										}
										try {
											if(Version.getCurrentVersion().getVersionInteger() >= 191) {
												player.playSound(player.getLocation(), Sound.valueOf("ENTITY_VILLAGER_TRADING"), 1, 1);
											}else {
												player.playSound(player.getLocation(), Sound.valueOf("VILLAGER_YES"), 1, 1);
											}
										}catch(Exception ex) {
										}
										return;
									}
								}
								if(current.getType() != Material.STAINED_GLASS_PANE) {// Adding/Taking Items
									if(current.getType() == Main.CE.getEnchantmentBookItem().getType()) {// Adding a book
										Boolean custom = false;
										Boolean toggle = false;
										String enchant = "";
										for(CEnchantments en : Main.CE.getEnchantments()) {
											if(current.getItemMeta().getDisplayName().contains(Methods.color(en.getBookColor() + en.getCustomName()))) {
												enchant = en.getName();
												toggle = true;
											}
										}
										for(String en : Main.CustomE.getEnchantments()) {
											if(current.getItemMeta().getDisplayName().contains(Methods.color(Main.CustomE.getBookColor(en) + Main.CustomE.getCustomName(en)))) {
												enchant = en;
												custom = true;
												toggle = true;
											}
										}
										if(toggle) {
											if(inTinker(e.getRawSlot())) {// Clicking in the Tinkers
												e.setCurrentItem(new ItemStack(Material.AIR));
												player.getInventory().addItem(current);
												inv.setItem(getSlot().get(e.getRawSlot()), new ItemStack(Material.AIR));
												try {
													if(Version.getCurrentVersion().getVersionInteger() >= 191) {
														player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK "), 1, 1);
													}else {
														player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
													}
												}catch(Exception ex) {
												}
											}else {// Clicking in their inventory
												if(player.getOpenInventory().getTopInventory().firstEmpty() == -1) {
													player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Tinker-Inventory-Full")));
													return;
												}
												if(current.getAmount() > 1) {
													if(Main.settings.getMessages().contains("Messages.Need-To-UnStack-Item")) {
														player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Need-To-UnStack-Item")));
													}else {
														player.sendMessage(Methods.getPrefix() + Methods.color("&cYou need to unstack that item for it to be used."));
													}
													return;
												}
												e.setCurrentItem(new ItemStack(Material.AIR));
												if(custom) {
													inv.setItem(getSlot().get(inv.firstEmpty()), DustControl.getDust("MysteryDust", 1, Main.settings.getTinker().getInt("Tinker.Custom-Enchantments." + enchant + ".Book")));
												}else {
													inv.setItem(getSlot().get(inv.firstEmpty()), DustControl.getDust("MysteryDust", 1, Main.settings.getTinker().getInt("Tinker.Crazy-Enchantments." + enchant + ".Book")));
												}
												inv.setItem(inv.firstEmpty(), current);
												try {
													if(Version.getCurrentVersion().getVersionInteger() >= 191) {
														player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK "), 1, 1);
													}else {
														player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
													}
												}catch(Exception ex) {
												}
											}
										}
									}
									if(getTotalXP(current) > 0 && current.getType() != Main.CE.getEnchantmentBookItem().getType()) {// Adding an item
										if(inTinker(e.getRawSlot())) {// Clicking in the Tinkers
											if(getSlot().containsKey(e.getRawSlot())) {
												e.setCurrentItem(new ItemStack(Material.AIR));
												player.getInventory().addItem(current);
												inv.setItem(getSlot().get(e.getRawSlot()), new ItemStack(Material.AIR));
												try {
													if(Version.getCurrentVersion().getVersionInteger() >= 191) {
														player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK "), 1, 1);
													}else {
														player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
													}
												}catch(Exception ex) {
												}
											}
										}else {// Clicking in their inventory
											if(player.getOpenInventory().getTopInventory().firstEmpty() == -1) {
												player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Tinker-Inventory-Full")));
												return;
											}
											if(current.getAmount() > 1) {
												if(Main.settings.getMessages().contains("Messages.Need-To-UnStack-Item")) {
													player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Need-To-UnStack-Item")));
												}else {
													player.sendMessage(Methods.getPrefix() + Methods.color("&cYou need to unstack that item for it to be used."));
												}
												return;
											}
											e.setCurrentItem(new ItemStack(Material.AIR));
											inv.setItem(getSlot().get(inv.firstEmpty()), getBottle(current));
											inv.setItem(inv.firstEmpty(), current);
											try {
												if(Version.getCurrentVersion().getVersionInteger() >= 191) {
													player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK "), 1, 1);
												}else {
													player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
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
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInvClose(final InventoryCloseEvent e) {
		final Inventory inv = e.getInventory();
		final Player player = (Player) e.getPlayer();
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				if(inv != null) {
					if(inv.getName().equals(Methods.color(Main.settings.getTinker().getString("Settings.GUIName")))) {
						for(int slot : getSlot().keySet()) {
							if(inv.getItem(slot) != null) {
								if(inv.getItem(slot).getType() != Material.AIR) {
									if(player.isDead()) {
										player.getWorld().dropItem(player.getLocation(), inv.getItem(slot));
									}else {
										if(Methods.isInvFull(player)) {
											player.getWorld().dropItem(player.getLocation(), inv.getItem(slot));
										}else {
											player.getInventory().addItem(inv.getItem(slot));
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

	private ItemStack getBottle(ItemStack item) {
		String id = Main.settings.getTinker().getString("Settings.BottleOptions.Item");
		String name = Main.settings.getTinker().getString("Settings.BottleOptions.Name");
		List<String> lore = new ArrayList<String>();
		for(String l : Main.settings.getTinker().getStringList("Settings.BottleOptions.Lore")) {
			lore.add(l.replaceAll("%Total%", getTotalXP(item) + "").replaceAll("%total%", getTotalXP(item) + ""));
		}
		ItemStack it = new ItemBuilder().setMaterial(id).setName(name).setLore(lore).build();
		if(SupportedPlugins.MEGA_SKILLS.isPluginLoaded()) {
			it = new ItemBuilder().setMaterial(Material.EXP_BOTTLE).setName("&6Enhanced Exp - &a&l" + getTotalXP(item) + " EXP").build();
		}
		return it;
	}

	private HashMap<Integer, Integer> getSlot() {
		HashMap<Integer, Integer> slots = new HashMap<Integer, Integer>();
		slots.put(1, 5);
		slots.put(2, 6);
		slots.put(3, 7);
		slots.put(9, 14);
		slots.put(10, 15);
		slots.put(11, 16);
		slots.put(12, 17);
		slots.put(18, 23);
		slots.put(19, 24);
		slots.put(20, 25);
		slots.put(21, 26);
		slots.put(27, 32);
		slots.put(28, 33);
		slots.put(29, 34);
		slots.put(30, 35);
		slots.put(36, 41);
		slots.put(37, 42);
		slots.put(38, 43);
		slots.put(39, 44);
		slots.put(45, 50);
		slots.put(46, 51);
		slots.put(47, 52);
		slots.put(48, 53);
		return slots;
	}

	private boolean inTinker(int slot) {
		//The last slot in the tinker is 54
		if(slot < 54) return true;
		return false;
	}

	private int getTotalXP(ItemStack item) {
		int total = 0;
		if(EnchantmentType.ALL.getItems().contains(item.getType())) {
			if(Main.CE.hasEnchantments(item)) {
				for(CEnchantments en : Main.CE.getItemEnchantments(item)) {
					total += Main.settings.getTinker().getInt("Tinker.Crazy-Enchantments." + en.getName() + ".Items");
				}
			}
			if(Main.CustomE.hasEnchantments(item)) {
				for(String en : Main.CustomE.getItemEnchantments(item)) {
					if(Main.settings.getTinker().contains("Tinker.Custom-Enchantments." + en)) {
						total += Main.settings.getTinker().getInt("Tinker.Custom-Enchantments." + en + ".Items");
					}
				}
			}
			if(item.hasItemMeta()) {
				if(item.getItemMeta().hasEnchants()) {
					for(Enchantment en : item.getEnchantments().keySet()) {
						total += Main.settings.getTinker().getInt("Tinker.Vanilla-Enchantments." + en.getName());
					}
				}
			}
		}
		return total;
	}

	private Integer getXP(ItemStack item) {
		List<String> lore = item.getItemMeta().getLore();
		List<String> L = Main.settings.getTinker().getStringList("Settings.BottleOptions.Lore");
		String arg = "";
		int i = 0;
		for(String l : L) {
			l = Methods.color(l);
			String lo = lore.get(i);
			if(l.contains("%Total%")) {
				String[] b = l.split("%Total%");
				if(b.length >= 1) arg = lo.replace(b[0], "");
				if(b.length >= 2) arg = arg.replace(b[1], "");
			}
			if(l.contains("%total%")) {
				String[] b = l.split("%total%");
				if(b.length >= 1) arg = lo.replace(b[0], "");
				if(b.length >= 2) arg = arg.replace(b[1], "");
			}
			i++;
		}
		return Integer.parseInt(arg);
	}

}
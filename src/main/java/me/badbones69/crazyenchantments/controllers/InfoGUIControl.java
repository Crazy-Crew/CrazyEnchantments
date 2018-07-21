package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.EnchantmentType;
import me.badbones69.crazyenchantments.api.enums.InfoType;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.FileManager.Files;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class InfoGUIControl implements Listener {
	
	private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	@EventHandler
	public void infoClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if(inv != null) {
			if(inv.getName().equals(Methods.color(Files.MESSAGES.getFile().getString("Messages.InfoGUI.Inventory.Name")))) {
				e.setCancelled(true);
				if(e.getCurrentItem() != null) {
					ItemStack item = e.getCurrentItem();
					if(item.hasItemMeta()) {
						if(item.getItemMeta().hasDisplayName()) {
							Player player = (Player) e.getWhoClicked();
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Files.MESSAGES.getFile().getString("Messages.InfoGUI.Categories-Info.Back.Right"))) || item.getItemMeta().getDisplayName().equals(Methods.color(Files.MESSAGES.getFile().getString("Messages.InfoGUI.Categories-Info.Back.Left")))) {
								openInfo(player);
								return;
							}
							for(InfoType ty : InfoType.getTypes()) {
								String type = ty.getName();
								if(item.getItemMeta().getDisplayName().equals(Methods.color(Files.MESSAGES.getFile().getString("Messages.InfoGUI.Categories-Info." + type + ".Name")))) {
									openInfo(player, ty);
								}
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Files.MESSAGES.getFile().getString("Messages.InfoGUI.Categories-Info.Other.Name")))) {
								Inventory in = Bukkit.createInventory(null, 18, Methods.color(Files.MESSAGES.getFile().getString("Messages.InfoGUI.Inventory.Name")));
								in.setItem(2, new ItemBuilder().setMaterial(Files.CONFIG.getFile().getString("Settings.BlackScroll.Item")).setName(Files.CONFIG.getFile().getString("Settings.BlackScroll.Name")).setLore(Files.MESSAGES.getFile().getStringList("Messages.InfoGUI.Black-Scroll")).build());
								in.setItem(11, new ItemBuilder().setMaterial(Files.CONFIG.getFile().getString("Settings.WhiteScroll.Item")).setName(Files.CONFIG.getFile().getString("Settings.WhiteScroll.Name")).setLore(Files.MESSAGES.getFile().getStringList("Messages.InfoGUI.White-Scroll")).build());
								in.setItem(4, new ItemBuilder().setMaterial(Files.CONFIG.getFile().getString("Settings.Tinker.Item")).setName(Files.CONFIG.getFile().getString("Settings.Tinker.Name")).setLore(Files.MESSAGES.getFile().getStringList("Messages.InfoGUI.Tinker")).build());
								in.setItem(13, new ItemBuilder().setMaterial(Files.CONFIG.getFile().getString("Settings.BlackSmith.Item")).setName(Files.CONFIG.getFile().getString("Settings.BlackSmith.Name")).setLore(Files.MESSAGES.getFile().getStringList("Messages.InfoGUI.BlackSmith")).build());
								in.setItem(6, new ItemBuilder().setMaterial(Files.CONFIG.getFile().getString("Settings.Dust.SuccessDust.Item")).setName(Files.CONFIG.getFile().getString("Settings.Dust.SuccessDust.Name")).setLore(Files.MESSAGES.getFile().getStringList("Messages.InfoGUI.Success-Dust")).build());
								in.setItem(15, new ItemBuilder().setMaterial(Files.CONFIG.getFile().getString("Settings.Dust.DestroyDust.Item")).setName(Files.CONFIG.getFile().getString("Settings.Dust.DestroyDust.Name")).setLore(Files.MESSAGES.getFile().getStringList("Messages.InfoGUI.Destroy-Dust")).build());
								ItemStack left = new ItemBuilder().setMaterial(Material.PRISMARINE_CRYSTALS).setName(Files.MESSAGES.getFile().getString("Messages.InfoGUI.Categories-Info.Back.Left")).build();
								ItemStack right = new ItemBuilder().setMaterial(Material.PRISMARINE_CRYSTALS).setName(Files.MESSAGES.getFile().getString("Messages.InfoGUI.Categories-Info.Back.Right")).build();
								in.setItem(0, left);
								in.setItem(8, right);
								in.setItem(9, left);
								in.setItem(17, right);
								player.openInventory(in);
								return;
							}
							String bar = Methods.color("&a&m------------------------------------------------");
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Files.CONFIG.getFile().getString("Settings.BlackSmith.Name")))) {
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Files.CONFIG.getFile().getString("Settings.BlackSmith.Name")));
								for(String lore : Files.MESSAGES.getFile().getStringList("Messages.InfoGUI.BlackSmith"))
									player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
							}else if(item.getItemMeta().getDisplayName().equals(Methods.color(Files.CONFIG.getFile().getString("Settings.BlackScroll.Name")))) {
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Files.CONFIG.getFile().getString("Settings.BlackScroll.Name")));
								for(String lore : Files.MESSAGES.getFile().getStringList("Messages.InfoGUI.Black-Scroll"))
									player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
							}else if(item.getItemMeta().getDisplayName().equals(Methods.color(Files.CONFIG.getFile().getString("Settings.WhiteScroll.Name")))) {
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Files.CONFIG.getFile().getString("Settings.WhiteScroll.Name")));
								for(String lore : Files.MESSAGES.getFile().getStringList("Messages.InfoGUI.White-Scroll"))
									player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
							}else if(item.getItemMeta().getDisplayName().equals(Methods.color(Files.CONFIG.getFile().getString("Settings.Tinker.Name")))) {
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Files.CONFIG.getFile().getString("Settings.Tinker.Name")));
								for(String lore : Files.MESSAGES.getFile().getStringList("Messages.InfoGUI.Tinker"))
									player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
							}else if(item.getItemMeta().getDisplayName().equals(Methods.color(Files.CONFIG.getFile().getString("Settings.Dust.SuccessDust.Name")))) {
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Files.CONFIG.getFile().getString("Settings.Dust.SuccessDust.Name")));
								for(String lore : Files.MESSAGES.getFile().getStringList("Messages.InfoGUI.Success-Dust"))
									player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
							}else if(item.getItemMeta().getDisplayName().equals(Methods.color(Files.CONFIG.getFile().getString("Settings.Dust.DestroyDust.Name")))) {
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Files.CONFIG.getFile().getString("Settings.Dust.DestroyDust.Name")));
								for(String lore : Files.MESSAGES.getFile().getStringList("Messages.InfoGUI.Destroy-Dust"))
									player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
							}
						}
					}
				}
			}
		}
	}
	
	public static void openInfo(Player player) {
		FileConfiguration msg = Files.MESSAGES.getFile();
		Inventory inv = Bukkit.createInventory(null, msg.getInt("Messages.InfoGUI.Inventory.Size"), Methods.color(msg.getString("Messages.InfoGUI.Inventory.Name")));
		ArrayList<String> options = new ArrayList<>();
		options.add("Helmets");
		options.add("Boots");
		options.add("Armor");
		options.add("Bow");
		options.add("Sword");
		options.add("Axe");
		options.add("Tool");
		options.add("Pickaxe");
		options.add("Misc");
		options.add("Other");
		for(String o : options) {
			inv.setItem(msg.getInt("Messages.InfoGUI.Categories-Info." + o + ".Slot") - 1, new ItemBuilder().setMaterial(msg.getString("Messages.InfoGUI.Categories-Info." + o + ".Item")).setName(msg.getString("Messages.InfoGUI.Categories-Info." + o + ".Name")).setLore(msg.getStringList("Messages.InfoGUI.Categories-Info." + o + ".Lore")).build());
		}
		player.openInventory(inv);
	}
	
	public static void openInfo(Player player, InfoType type) {
		ArrayList<ItemStack> items = getInfo(type.getName());
		int size = items.size() + 1;
		int slots = 9;
		for(; size > 9; size -= 9) slots += 9;
		Inventory in = Bukkit.createInventory(null, slots, Methods.color(Files.MESSAGES.getFile().getString("Messages.InfoGUI.Inventory.Name")));
		for(ItemStack i : items) {
			in.addItem(i);
		}
		Material itemType = Files.MESSAGES.getFile().contains("Messages.InfoGUI.Categories-Info.Back.Item") ? new ItemBuilder().setMaterial(Files.MESSAGES.getFile().getString("Messages.InfoGUI.Categories-Info.Back.Item")).getMaterial() : null;
		if(itemType == null) {
			itemType = Material.PRISMARINE_CRYSTALS;
		}
		in.setItem(slots - 1, new ItemBuilder().setMaterial(itemType).setName(Files.MESSAGES.getFile().getString("Messages.InfoGUI.Categories-Info.Back.Right")).build());
		player.openInventory(in);
	}
	
	public static ArrayList<ItemStack> getInfo(String type) {
		FileConfiguration enchants = Files.ENCHANTMENTS.getFile();
		ArrayList<ItemStack> swords = new ArrayList<>();
		ArrayList<ItemStack> axes = new ArrayList<>();
		ArrayList<ItemStack> bows = new ArrayList<>();
		ArrayList<ItemStack> armor = new ArrayList<>();
		ArrayList<ItemStack> helmets = new ArrayList<>();
		ArrayList<ItemStack> boots = new ArrayList<>();
		ArrayList<ItemStack> picks = new ArrayList<>();
		ArrayList<ItemStack> tools = new ArrayList<>();
		ArrayList<ItemStack> misc = new ArrayList<>();
		for(CEnchantment en : ce.getRegisteredEnchantments()) {
			if(en.isActivated()) {
				EnchantmentType enchantType = en.getEnchantmentType();
				ItemStack i = new ItemBuilder().setMaterial(Files.CONFIG.getFile().getString("Settings.Enchantment-Book-Item")).setName(en.getInfoName()).setLore(en.getInfoDescription()).setGlowing(true).build();
				if(enchantType == EnchantmentType.ARMOR) armor.add(i);
				if(enchantType == EnchantmentType.SWORD) swords.add(i);
				if(enchantType == EnchantmentType.AXE) axes.add(i);
				if(enchantType == EnchantmentType.BOW) bows.add(i);
				if(enchantType == EnchantmentType.HELMET) helmets.add(i);
				if(enchantType == EnchantmentType.BOOTS) boots.add(i);
				if(enchantType == EnchantmentType.PICKAXE) picks.add(i);
				if(enchantType == EnchantmentType.TOOL) tools.add(i);
				if(enchantType == EnchantmentType.ALL) misc.add(i);
				if(enchantType == EnchantmentType.WEAPONS) misc.add(i);
			}
		}
		if(type.equalsIgnoreCase("Armor")) {
			return armor;
		}else if(type.equalsIgnoreCase("Sword")) {
			return swords;
		}else if(type.equalsIgnoreCase("Helmets")) {
			return helmets;
		}else if(type.equalsIgnoreCase("Boots")) {
			return boots;
		}else if(type.equalsIgnoreCase("Bow")) {
			return bows;
		}else if(type.equalsIgnoreCase("Axe")) {
			return axes;
		}else if(type.equalsIgnoreCase("Pickaxe")) {
			return picks;
		}else if(type.equalsIgnoreCase("Tool")) {
			return tools;
		}else if(type.equalsIgnoreCase("Misc")) {
			return misc;
		}
		return new ArrayList<>();
	}
	
}
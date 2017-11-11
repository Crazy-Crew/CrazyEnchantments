package me.badbones69.crazyenchantments.controlers;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.EnchantmentType;
import me.badbones69.crazyenchantments.api.InfoType;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.multisupport.Version;
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
import java.util.List;

public class InfoGUIControl implements Listener {

	@EventHandler
	public void infoClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if(inv != null) {
			if(inv.getName().equals(Methods.color(Main.settings.getMessages().getString("Messages.InfoGUI.Inventory.Name")))) {
				e.setCancelled(true);
				if(e.getCurrentItem() != null) {
					ItemStack item = e.getCurrentItem();
					if(item.hasItemMeta()) {
						if(item.getItemMeta().hasDisplayName()) {
							Player player = (Player) e.getWhoClicked();
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Back.Right"))) || item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Back.Left")))) {
								openInfo((Player) player);
								return;
							}
							for(InfoType ty : InfoType.getTypes()) {
								String type = ty.getName();
								if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info." + type + ".Name")))) {
									openInfo(player, ty);
								}
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Other.Name")))) {
								Inventory in = Bukkit.createInventory(null, 18, Methods.color(Main.settings.getMessages().getString("Messages.InfoGUI.Inventory.Name")));
								in.setItem(2, new ItemBuilder().setMaterial(Main.settings.getConfig().getString("Settings.BlackScroll.Item")).setName(Main.settings.getConfig().getString("Settings.BlackScroll.Name")).setLore(Main.settings.getMessages().getStringList("Messages.InfoGUI.Black-Scroll")).build());
								in.setItem(11, new ItemBuilder().setMaterial(Main.settings.getConfig().getString("Settings.WhiteScroll.Item")).setName(Main.settings.getConfig().getString("Settings.WhiteScroll.Name")).setLore(Main.settings.getMessages().getStringList("Messages.InfoGUI.White-Scroll")).build());
								in.setItem(4, new ItemBuilder().setMaterial(Main.settings.getConfig().getString("Settings.Tinker.Item")).setName(Main.settings.getConfig().getString("Settings.Tinker.Name")).setLore(Main.settings.getMessages().getStringList("Messages.InfoGUI.Tinker")).build());
								in.setItem(13, new ItemBuilder().setMaterial(Main.settings.getConfig().getString("Settings.BlackSmith.Item")).setName(Main.settings.getConfig().getString("Settings.BlackSmith.Name")).setLore(Main.settings.getMessages().getStringList("Messages.InfoGUI.BlackSmith")).build());
								in.setItem(6, new ItemBuilder().setMaterial(Main.settings.getConfig().getString("Settings.Dust.SuccessDust.Item")).setName(Main.settings.getConfig().getString("Settings.Dust.SuccessDust.Name")).setLore(Main.settings.getMessages().getStringList("Messages.InfoGUI.Success-Dust")).build());
								in.setItem(15, new ItemBuilder().setMaterial(Main.settings.getConfig().getString("Settings.Dust.DestroyDust.Item")).setName(Main.settings.getConfig().getString("Settings.Dust.DestroyDust.Name")).setLore(Main.settings.getMessages().getStringList("Messages.InfoGUI.Destroy-Dust")).build());
								if(Version.getCurrentVersion().getVersionInteger() < 181) {
									ItemStack left = new ItemBuilder().setMaterial(Material.FEATHER).setName(Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Back.Left")).build();
									ItemStack right = new ItemBuilder().setMaterial(Material.FEATHER).setName(Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Back.Right")).build();
									in.setItem(0, left);
									in.setItem(8, right);
									in.setItem(9, left);
									in.setItem(17, right);
								}else {
									ItemStack left = new ItemBuilder().setMaterial(Material.PRISMARINE_CRYSTALS).setName(Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Back.Left")).build();
									ItemStack right = new ItemBuilder().setMaterial(Material.PRISMARINE_CRYSTALS).setName(Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Back.Right")).build();
									in.setItem(0, left);
									in.setItem(8, right);
									in.setItem(9, left);
									in.setItem(17, right);
								}
								player.openInventory(in);
								return;
							}
							String bar = Methods.color("&a&m------------------------------------------------");
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.BlackSmith.Name")))) {
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.BlackSmith.Name")));
								for(String lore : Main.settings.getMessages().getStringList("Messages.InfoGUI.BlackSmith"))
									player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
								return;
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.BlackScroll.Name")))) {
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.BlackScroll.Name")));
								for(String lore : Main.settings.getMessages().getStringList("Messages.InfoGUI.Black-Scroll"))
									player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
								return;
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.WhiteScroll.Name")))) {
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.WhiteScroll.Name")));
								for(String lore : Main.settings.getMessages().getStringList("Messages.InfoGUI.White-Scroll"))
									player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.Tinker.Name")))) {
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.Tinker.Name")));
								for(String lore : Main.settings.getMessages().getStringList("Messages.InfoGUI.Tinker"))
									player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
								return;
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.Dust.SuccessDust.Name")))) {
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.Dust.SuccessDust.Name")));
								for(String lore : Main.settings.getMessages().getStringList("Messages.InfoGUI.Success-Dust"))
									player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
								return;
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.Dust.DestroyDust.Name")))) {
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.Dust.DestroyDust.Name")));
								for(String lore : Main.settings.getMessages().getStringList("Messages.InfoGUI.Destroy-Dust"))
									player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
								return;
							}
						}
					}
				}
				return;
			}
		}
	}

	public static void openInfo(Player player) {
		FileConfiguration msg = Main.settings.getMessages();
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
		int size = getInfo(type.getName()).size() + 1;
		int slots = 9;
		for(; size > 9; size -= 9)
			slots += 9;
		Inventory in = Bukkit.createInventory(null, slots, Methods.color(Main.settings.getMessages().getString("Messages.InfoGUI.Inventory.Name")));
		ArrayList<ItemStack> list = getInfo(type.getName());
		if(list != null) {
			for(ItemStack i : list) {
				in.addItem(i);
			}
		}
		Material itemType = Main.settings.getMessages().contains("Messages.InfoGUI.Categories-Info.Back.Item") ? new ItemBuilder().setMaterial(Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Back.Item")).getMaterial() : null;
		if(itemType == null) {
			if(Version.getCurrentVersion().comparedTo(Version.v1_9_R1) >= 0) {
				itemType = Material.FEATHER;
			}else {
				itemType = Material.PRISMARINE_CRYSTALS;
			}
		}
		in.setItem(slots - 1, new ItemBuilder().setMaterial(itemType).setName(Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Back.Right")).build());
		player.openInventory(in);
		return;
	}

	public static ArrayList<ItemStack> getInfo(String type) {
		FileConfiguration enchants = Main.settings.getEnchs();
		FileConfiguration customEnchants = Main.settings.getCustomEnchs();
		ArrayList<ItemStack> swords = new ArrayList<>();
		ArrayList<ItemStack> axes = new ArrayList<>();
		ArrayList<ItemStack> bows = new ArrayList<>();
		ArrayList<ItemStack> armor = new ArrayList<>();
		ArrayList<ItemStack> helmets = new ArrayList<>();
		ArrayList<ItemStack> boots = new ArrayList<>();
		ArrayList<ItemStack> picks = new ArrayList<>();
		ArrayList<ItemStack> tools = new ArrayList<>();
		ArrayList<ItemStack> misc = new ArrayList<>();
		for(String en : enchants.getConfigurationSection("Enchantments").getKeys(false)) {
			if(enchants.getBoolean("Enchantments." + en + ".Enabled")) {
				String name = enchants.getString("Enchantments." + en + ".Info.Name");
				List<String> desc = enchants.getStringList("Enchantments." + en + ".Info.Description");
				EnchantmentType enchantType = Main.CE.getFromName(en).getType();
				ItemStack i = new ItemBuilder().setMaterial(Main.settings.getConfig().getString("Settings.Enchantment-Book-Item")).setName(name).setLore(desc).setGlowing(true).build();
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
		for(String enchantment : Main.CustomE.getEnchantments()) {
			if(Main.CustomE.isEnabled(enchantment)) {
				String name = customEnchants.getString("Enchantments." + enchantment + ".Info.Name");
				List<String> desc = Main.CustomE.getDiscription(enchantment);
				EnchantmentType enchantType = Main.CustomE.getType(enchantment);
				ItemStack i = new ItemBuilder().setMaterial(Main.settings.getConfig().getString("Settings.Enchantment-Book-Item")).setName(name).setLore(desc).setGlowing(true).build();
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
		if(type.equalsIgnoreCase("Armor")) return armor;
		if(type.equalsIgnoreCase("Sword")) return swords;
		if(type.equalsIgnoreCase("Helmets")) return helmets;
		if(type.equalsIgnoreCase("Boots")) return boots;
		if(type.equalsIgnoreCase("Bow")) return bows;
		if(type.equalsIgnoreCase("Axe")) return axes;
		if(type.equalsIgnoreCase("Pickaxe")) return picks;
		if(type.equalsIgnoreCase("Tool")) return tools;
		if(type.equalsIgnoreCase("Misc")) return misc;
		return null;
	}

}
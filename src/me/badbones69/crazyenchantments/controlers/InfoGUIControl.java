package me.badbones69.crazyenchantments.controlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.EnchantmentType;
import me.badbones69.crazyenchantments.api.InfoType;
import me.badbones69.crazyenchantments.multisupport.Version;

public class InfoGUIControl implements Listener{
	
	@EventHandler
	public void infoClick(InventoryClickEvent e){
		Inventory inv = e.getInventory();
		if(inv != null){
			if(inv.getName().equals(Methods.color(Main.settings.getMessages().getString("Messages.InfoGUI.Inventory.Name")))){
				e.setCancelled(true);
				if(e.getCurrentItem() != null){
					ItemStack item = e.getCurrentItem();
					if(item.hasItemMeta()){
						if(item.getItemMeta().hasDisplayName()){
							Player player = (Player) e.getWhoClicked();
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Back.Right")))||item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Back.Left")))){
								openInfo((Player)player);
								return;
							}
							for(InfoType ty : InfoType.getTypes()){
								String type = ty.getName();
								if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info."+type+".Name")))){
									openInfo(player, ty);
								}
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Other.Name")))){
								Inventory in = Bukkit.createInventory(null, 18, Methods.color(Main.settings.getMessages().getString("Messages.InfoGUI.Inventory.Name")));
								in.setItem(2, Methods.makeItem(Main.settings.getConfig().getString("Settings.BlackScroll.Item"),
										1, Main.settings.getConfig().getString("Settings.BlackScroll.Name"),
										Main.settings.getMessages().getStringList("Messages.InfoGUI.Black-Scroll")));
								in.setItem(11, Methods.makeItem(Main.settings.getConfig().getString("Settings.WhiteScroll.Item"),
										1, Main.settings.getConfig().getString("Settings.WhiteScroll.Name"),
										Main.settings.getMessages().getStringList("Messages.InfoGUI.White-Scroll")));
								in.setItem(4, Methods.makeItem(Main.settings.getConfig().getString("Settings.Tinker.Item"),
										1, Main.settings.getConfig().getString("Settings.Tinker.Name"),
										Main.settings.getMessages().getStringList("Messages.InfoGUI.Tinker")));
								in.setItem(13, Methods.makeItem(Main.settings.getConfig().getString("Settings.BlackSmith.Item"),
										1, Main.settings.getConfig().getString("Settings.BlackSmith.Name"),
										Main.settings.getMessages().getStringList("Messages.InfoGUI.BlackSmith")));
								in.setItem(6, Methods.makeItem(Main.settings.getConfig().getString("Settings.Dust.SuccessDust.Item"),
										1, Main.settings.getConfig().getString("Settings.Dust.SuccessDust.Name"),
										Main.settings.getMessages().getStringList("Messages.InfoGUI.Success-Dust")));
								in.setItem(15, Methods.makeItem(Main.settings.getConfig().getString("Settings.Dust.DestroyDust.Item"),
										1, Main.settings.getConfig().getString("Settings.Dust.DestroyDust.Name"),
										Main.settings.getMessages().getStringList("Messages.InfoGUI.Destroy-Dust")));
								if(Version.getVersion().getVersionInteger()<181){
									ItemStack left = Methods.makeItem(Material.FEATHER, 1, 0, Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Back.Left"));
									ItemStack right = Methods.makeItem(Material.FEATHER, 1, 0, Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Back.Right"));
									in.setItem(0, left);
									in.setItem(8, right);
									in.setItem(9, left);
									in.setItem(17, right);
								}else{
									ItemStack left = Methods.makeItem(Material.PRISMARINE_CRYSTALS, 1, 0, Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Back.Left"));
									ItemStack right = Methods.makeItem(Material.PRISMARINE_CRYSTALS, 1, 0, Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Back.Right"));
									in.setItem(0, left);
									in.setItem(8, right);
									in.setItem(9, left);
									in.setItem(17, right);
								}
								player.openInventory(in);
								return;
							}
							String bar = Methods.color("&a&m------------------------------------------------");
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.BlackSmith.Name")))){
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.BlackSmith.Name")));
								for(String lore : Main.settings.getMessages().getStringList("Messages.InfoGUI.BlackSmith"))player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
								return;
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.BlackScroll.Name")))){
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.BlackScroll.Name")));
								for(String lore : Main.settings.getMessages().getStringList("Messages.InfoGUI.Black-Scroll"))player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
								return;
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.WhiteScroll.Name")))){
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.WhiteScroll.Name")));
								for(String lore : Main.settings.getMessages().getStringList("Messages.InfoGUI.White-Scroll"))player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.Tinker.Name")))){
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.Tinker.Name")));
								for(String lore : Main.settings.getMessages().getStringList("Messages.InfoGUI.Tinker"))player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
								return;
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.Dust.SuccessDust.Name")))){
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.Dust.SuccessDust.Name")));
								for(String lore : Main.settings.getMessages().getStringList("Messages.InfoGUI.Success-Dust"))player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
								return;
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.Dust.DestroyDust.Name")))){
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.Dust.DestroyDust.Name")));
								for(String lore : Main.settings.getMessages().getStringList("Messages.InfoGUI.Destroy-Dust"))player.sendMessage(Methods.color(lore));
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
	
	public static void openInfo(Player player){
		FileConfiguration msg = Main.settings.getMessages();
		Inventory inv = Bukkit.createInventory(null, msg.getInt("Messages.InfoGUI.Inventory.Size"), Methods.color(msg.getString("Messages.InfoGUI.Inventory.Name")));
		ArrayList<String> options = new ArrayList<String>();
		options.add("Helmets");options.add("Boots");
		options.add("Armor");options.add("Bow");options.add("Sword");options.add("Axe");
		options.add("Tool");options.add("Pickaxe");options.add("Misc");options.add("Other");
		for(String o : options){
			inv.setItem(msg.getInt("Messages.InfoGUI.Categories-Info." + o + ".Slot") - 1, Methods.makeItem(msg.getString("Messages.InfoGUI.Categories-Info." + o + ".Item"), 1,
					msg.getString("Messages.InfoGUI.Categories-Info." + o + ".Name"), msg.getStringList("Messages.InfoGUI.Categories-Info." + o + ".Lore")));
		}
		player.openInventory(inv);
	}
	
	public static void openInfo(Player player, InfoType type){
		int size = getInfo(type.getName()).size()+1;
		int slots = 9;
		for(;size > 9; size -= 9)slots += 9;
		Inventory in = Bukkit.createInventory(null, slots, Methods.color(Main.settings.getMessages().getString("Messages.InfoGUI.Inventory.Name")));
		for(ItemStack i : getInfo(type.getName())){
			in.addItem(i);
		}
		Material itemType = Main.settings.getMessages().contains("Messages.InfoGUI.Categories-Info.Back.Item") ? 
				Methods.makeItem(Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Back.Item"), 1).getType() : null;
		if(itemType == null) {
			if(Version.getVersion().comparedTo(Version.v1_9_R1) >= 0){
				itemType = Material.FEATHER;
			}else {
				itemType = Material.PRISMARINE_CRYSTALS;
			}
		}
		in.setItem(slots-1, Methods.makeItem(itemType, 1, 0, Main.settings.getMessages().getString("Messages.InfoGUI.Categories-Info.Back.Right")));
		player.openInventory(in);
		return;
	}
	
	public static ArrayList<ItemStack> getInfo(String type){
		FileConfiguration enchants = Main.settings.getEnchs();
		FileConfiguration customEnchants = Main.settings.getCustomEnchs();
		ArrayList<ItemStack> swords = new ArrayList<ItemStack>();
		ArrayList<ItemStack> axes = new ArrayList<ItemStack>();
		ArrayList<ItemStack> bows = new ArrayList<ItemStack>();
		ArrayList<ItemStack> armor = new ArrayList<ItemStack>();
		ArrayList<ItemStack> helmets = new ArrayList<ItemStack>();
		ArrayList<ItemStack> boots = new ArrayList<ItemStack>();
		ArrayList<ItemStack> picks = new ArrayList<ItemStack>();
		ArrayList<ItemStack> tools = new ArrayList<ItemStack>();
		ArrayList<ItemStack> misc = new ArrayList<ItemStack>();
		for(String en : enchants.getConfigurationSection("Enchantments").getKeys(false)){
			if(enchants.getBoolean("Enchantments."+en+".Enabled")){
				String name = enchants.getString("Enchantments."+en+".Info.Name");
				List<String> desc = enchants.getStringList("Enchantments."+en+".Info.Description");
				EnchantmentType enchantType = Main.CE.getFromName(en).getType();
				ItemStack i = Methods.addGlowHide(Methods.makeItem(Main.settings.getConfig().getString("Settings.Enchantment-Book-Item"), 1, name, desc));
				if(enchantType == EnchantmentType.ARMOR)armor.add(i);
				if(enchantType == EnchantmentType.SWORD)swords.add(i);
				if(enchantType == EnchantmentType.AXE)axes.add(i);
				if(enchantType == EnchantmentType.BOW)bows.add(i);
				if(enchantType == EnchantmentType.HELMET)helmets.add(i);
				if(enchantType == EnchantmentType.BOOTS)boots.add(i);
				if(enchantType == EnchantmentType.PICKAXE)picks.add(i);
				if(enchantType == EnchantmentType.TOOL)tools.add(i);
				if(enchantType == EnchantmentType.ALL)misc.add(i);
				if(enchantType == EnchantmentType.WEAPONS)misc.add(i);
			}
		}
		for(String enchantment : Main.CustomE.getEnchantments()){
			if(Main.CustomE.isEnabled(enchantment)){
				String name = customEnchants.getString("Enchantments."+enchantment+".Info.Name");
				List<String> desc = Main.CustomE.getDiscription(enchantment);
				EnchantmentType enchantType = Main.CustomE.getType(enchantment);
				ItemStack i = Methods.addGlowHide(Methods.makeItem(Main.settings.getConfig().getString("Settings.Enchantment-Book-Item"), 1, name, desc));
				if(enchantType == EnchantmentType.ARMOR)armor.add(i);
				if(enchantType == EnchantmentType.SWORD)swords.add(i);
				if(enchantType == EnchantmentType.AXE)axes.add(i);
				if(enchantType == EnchantmentType.BOW)bows.add(i);
				if(enchantType == EnchantmentType.HELMET)helmets.add(i);
				if(enchantType == EnchantmentType.BOOTS)boots.add(i);
				if(enchantType == EnchantmentType.PICKAXE)picks.add(i);
				if(enchantType == EnchantmentType.TOOL)tools.add(i);
				if(enchantType == EnchantmentType.ALL)misc.add(i);
				if(enchantType == EnchantmentType.WEAPONS)misc.add(i);
			}
		}
		if(type.equalsIgnoreCase("Armor"))return armor;
		if(type.equalsIgnoreCase("Sword"))return swords;
		if(type.equalsIgnoreCase("Helmets"))return helmets;
		if(type.equalsIgnoreCase("Boots"))return boots;
		if(type.equalsIgnoreCase("Bow"))return bows;
		if(type.equalsIgnoreCase("Axe"))return axes;
		if(type.equalsIgnoreCase("Pickaxe"))return picks;
		if(type.equalsIgnoreCase("Tool"))return tools;
		if(type.equalsIgnoreCase("Misc"))return misc;
		return null;
	}
	
	
}
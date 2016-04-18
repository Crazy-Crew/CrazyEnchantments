package me.BadBones69.CrazyEnchantments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;

public class Api{
	public static String color(String msg){
		msg = msg.replaceAll("(&([a-f0-9]))", "\u00A7$2");
		msg = msg.replaceAll("&l", ChatColor.BOLD + "");
		msg = msg.replaceAll("&o", ChatColor.ITALIC + "");
		msg = msg.replaceAll("&k", ChatColor.MAGIC + "");
		msg = msg.replaceAll("&n", ChatColor.UNDERLINE + "");
		return msg;
	}
	public static String removeColor(String msg){
		msg = ChatColor.stripColor(msg);
		return msg;
	}
	public static boolean allowsPVP(Entity en){
		if(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")!=null&&Bukkit.getServer().getPluginManager().getPlugin("WorldGuard")!=null){
			ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(en.getWorld()).getApplicableRegions(en.getLocation());
			if (set.queryState(null, DefaultFlag.PVP)==StateFlag.State.DENY)return false;
		}
		return true;
	}
	static ItemStack removeLore(ItemStack item, String i){
		ArrayList<String> lore = new ArrayList<String>();
		ItemMeta m = item.getItemMeta();
		for(String l : item.getItemMeta().getLore()){
			if(!l.equals(i)){
				lore.add(l);
			}
		}
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}
	public static ItemStack makeItem(Material material, int amount, int type, String name){
		ItemStack item = new ItemStack(material, amount, (short) type);
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(color(name));
		item.setItemMeta(m);
		return item;
	}
	public static ItemStack makeItem(Material material, int amount, int type, String name, List<String> lore){
		ArrayList<String> l = new ArrayList<String>();
		ItemStack item = new ItemStack(material, amount, (short) type);
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(color(name));
		for(String L:lore)l.add(color(L));
		m.setLore(l);
		item.setItemMeta(m);
		return item;
	}
	public static ItemStack makeItem(Material material, int amount, int type, String name, List<String> lore, List<String> lore2){
		ArrayList<String> l = new ArrayList<String>();
		ItemStack item = new ItemStack(material, amount, (short) type);
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(color(name));
		for(String L:lore)l.add(color(L));
		for(String L:lore2)l.add(color(L));
		m.setLore(l);
		item.setItemMeta(m);
		return item;
	}
	public static ItemStack makeItem(Material material, int amount, int type, String name, List<String> lore, Map<Enchantment, Integer> enchants){
		ItemStack item = new ItemStack(material, amount, (short) type);
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(name);
		m.setLore(lore);
		item.setItemMeta(m);
		item.addUnsafeEnchantments(enchants);
		return item;
	}
	static String percentPicker(){
		Random i = new Random();
		return Integer.toString(i.nextInt(100));
	}
	static ItemStack setBook(String type){
		String name = removeColor(type);
		name = color(Main.settings.getConfig().getString("Settings.BookNameColor")+name);
		return makeItem(Material.BOOK, 1, 0, name, addDiscription(), Arrays.asList(color("&a" + percentPicker() + "% Success Chance")));
	}
	static ItemStack BlackScroll(int i){
		String name = color(Main.settings.getConfig().getString("Settings.BlackScroll.Name"));
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(Api.color("&7Right Click for more Info."));
		String type = Main.settings.getConfig().getString("Settings.BlackScroll.Item");
		int ty=0;
		if(type.contains(":")){
			String[] b = type.split(":");
			type = b[0];
			ty = Integer.parseInt(b[1]);
		}
		Material m = Material.matchMaterial(type);
		return makeItem(m, i, ty, name, lore);
	}
	static String getPrefix(){
		return color(Main.settings.getConfig().getString("Settings.Prefix"));
	}
	static List<String> addDiscription(){
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(color("&7Drag book and drop on Item."));
		lore.add(color("&7Right click for more Info."));
		return lore;
	}
	public static ItemStack addLore(ItemStack item, String i){
		ArrayList<String> lore = new ArrayList<String>();
		ItemMeta m = item.getItemMeta();
		if(item.getItemMeta().hasLore()){
			lore.addAll(item.getItemMeta().getLore());
		}
		lore.add(i);
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}
	public static boolean isInt(String s) {
	    try {
	        Integer.parseInt(s);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	public static Player getPlayer(String name){
		return Bukkit.getServer().getPlayer(name);
	}
	public static Location getLoc(Player player){
		return player.getLocation();
	}
	public static void runCMD(Player player, String CMD){
		player.performCommand(CMD);
	}
	public static boolean isOnline(String name, CommandSender p){
		for(Player player : Bukkit.getServer().getOnlinePlayers()){
			if(player.getName().equalsIgnoreCase(name)){
				return true;
			}
		}
		p.sendMessage(getPrefix()+color("&cThat player is not online at this time."));
		return false;
	}
	public static boolean permCheck(Player player, String perm){
		if(!player.hasPermission("CrazyEnchantments." + perm)){
			player.sendMessage(color("&cYou do not have permission to use that command!"));
			return false;
		}
		return true;
	}
	static void removeItem(ItemStack item, Player player){
		if(item.getAmount() <= 1){
			player.getInventory().removeItem(item);
		}
		if(item.getAmount() > 1){
			ItemStack i = item;
			i.setAmount(item.getAmount() - 1);
		}
	}
	static String getInvName(){
		return color(Main.settings.getConfig().getString("Settings.InvName"));
	}
	static String getBookColor(){
		return Main.settings.getConfig().getString("Settings.BookNameColor");
	}
	static int getXPLvl(Player player){
		return player.getLevel();
	}
	static void takeXP(Player player, int amount){
		player.setLevel(player.getLevel() - amount);
	}
	static boolean successChance(ItemStack item){
		String[] breakdown = item.getItemMeta().getLore().get(2).split("%");
		String c = ChatColor.stripColor(breakdown[0]);
		Random number = new Random();
		int chance;
		for(int counter = 1; counter<=1; counter++){
			chance = 1 + number.nextInt(99);
			if(chance >= 1 && chance <= Integer.parseInt(c)){
				return true;
			}
		}
		return false;
	}
}
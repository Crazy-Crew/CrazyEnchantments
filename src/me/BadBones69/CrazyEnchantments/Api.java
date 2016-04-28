package me.BadBones69.CrazyEnchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.BadBones69.CrazyEnchantments.MultiSupport.Eight;
import me.BadBones69.CrazyEnchantments.MultiSupport.Nine;

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
	public static ItemStack addGlow(ItemStack item) {
		if(getVersion()==19){
			return Nine.addGlow(item);
		}else{
			return Eight.addGlow(item);
		}
    }
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
	public static Integer getVersion(){
		String ver = Bukkit.getServer().getClass().getPackage().getName();
		ver = ver.substring(ver.lastIndexOf('.')+1);
		ver=ver.replaceAll("_", "").replaceAll("R", "").replaceAll("v", "");
		ver=ver.substring(0, ver.length()-1);
		return Integer.parseInt(ver);
	}
	@SuppressWarnings("deprecation")
	public static ItemStack getItemInHand(Player player){
		if(getVersion()==19){
			return player.getInventory().getItemInMainHand();
		}else{
			return player.getItemInHand();
		}
	}
	@SuppressWarnings("deprecation")
	public static void setItemInHand(Player player, ItemStack item){
		if(Api.getVersion()==19){
			player.getInventory().setItemInMainHand(item);
		}else{
			player.setItemInHand(item);
		}
	}
	public static int getPower(String line, String ench){
		line = line.replace(ench+" ", "");
		line = removeColor(line);
		if(Api.isInt(line))return Integer.parseInt(line);
		if(line.equalsIgnoreCase("I"))return 1;
		if(line.equalsIgnoreCase("II"))return 2;
		if(line.equalsIgnoreCase("III"))return 3;
		if(line.equalsIgnoreCase("IV"))return 4;
		if(line.equalsIgnoreCase("V"))return 5;
		if(line.equalsIgnoreCase("VI"))return 6;
		if(line.equalsIgnoreCase("VII"))return 7;
		if(line.equalsIgnoreCase("VIII"))return 8;
		if(line.equalsIgnoreCase("IX"))return 9;
		if(line.equalsIgnoreCase("X"))return 10;
		return 1;
	}
	public static String getPower(Integer i){
		if(i==0)return "I";
		if(i==1)return "I";
		if(i==2)return "II";
		if(i==3)return "III";
		if(i==4)return "IV";
		if(i==5)return "V";
		if(i==6)return "VI";
		if(i==7)return "VII";
		if(i==8)return "VII";
		if(i==9)return "IX";
		if(i==10)return "X";
		return i+"";
	}
	public static ArrayList<String> getPotions(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("ABSORPTION");
		list.add("BLINDNESS");
		list.add("CONFUSION");
		list.add("DAMAGE_RESISTANCE");
		list.add("FAST_DIGGING");
		list.add("FIRE_RESISTANCE");
		list.add("GLOWING");
		list.add("HARM");
		list.add("HEAL");
		list.add("HEALTH_BOOST");
		list.add("HUNGER");
		list.add("INCREASE_DAMAGE");
		list.add("INVISIBILITY");
		list.add("JUMP");
		list.add("LEVITATION");
		list.add("LUCK");
		list.add("NIGHT_VISION");
		list.add("POISON");
		list.add("REGENERATION");
		list.add("SATURATION");
		list.add("SLOW");
		list.add("SLOW_DIGGING");
		list.add("SPEED");
		list.add("UNLUCK");
		list.add("WATER_BREATHING");
		list.add("WEAKNESS");
		list.add("WITHER");
		return list;
	}
	public static String getEnchName(String ench){
		if(Main.settings.getEnchs().contains("Enchantments."+ench)){
			return Main.settings.getEnchs().getString("Enchantments."+ench+".Name");
		}
		return Main.settings.getCustomEnchs().getString("Enchantments."+ench+".Name");
	}
	public static boolean allowsPVP(Entity en){
		if(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")!=null&&Bukkit.getServer().getPluginManager().getPlugin("WorldGuard")!=null){
			int x = en.getLocation().getBlockX();
			int y = en.getLocation().getBlockY();
			int z = en.getLocation().getBlockZ();
			Location loc = new Location(en.getWorld(),x,y,z);
			ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(en.getWorld()).getApplicableRegions(loc);
			if (set.queryState(null, DefaultFlag.PVP)==StateFlag.State.DENY)return false;
		}
		return true;
	}
	public static boolean allowsExplotions(Entity en){
		if(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")!=null&&Bukkit.getServer().getPluginManager().getPlugin("WorldGuard")!=null){
			int x = en.getLocation().getBlockX();
			int y = en.getLocation().getBlockY();
			int z = en.getLocation().getBlockZ();
			Location loc = new Location(en.getWorld(),x,y,z);
			ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(en.getWorld()).getApplicableRegions(loc);
			if (set.queryState(null, DefaultFlag.OTHER_EXPLOSION)==StateFlag.State.DENY)return false;
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
	public static ItemStack makeItem(String type, int amount, String name, List<String> lore){
		ArrayList<String> l = new ArrayList<String>();
		int ty = 0;
		if(type.contains(":")){
			String[] b = type.split(":");
			type = b[0];
			ty = Integer.parseInt(b[1]);
		}
		Material m = Material.matchMaterial(type);
		ItemStack item = new ItemStack(m, amount, (short) ty);
		ItemMeta me = item.getItemMeta();
		me.setDisplayName(color(name));
		for(String L:lore)l.add(color(L));
		me.setLore(l);
		item.setItemMeta(me);
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
	static double getMoney(Player player){
		return Main.econ.getBalance(player);
	}
	static List<String> addDiscription(){
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(color("&7Drag book and drop on Item."));
		lore.add(color("&7Right click for more Info."));
		return lore;
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
	static int getXPLvl(Player player){
		return player.getLevel();
	}
	public static void takeLvlXP(Player player, int amount){
		player.setLevel(player.getLevel() - amount);
	}
	public static void takeTotalXP(Player player, int amount){
		int total = player.getTotalExperience() - amount;
        player.setTotalExperience(total);
        player.setLevel(0);
        player.setExp(0);
        for(;total > player.getExpToLevel();){
            total -= player.getExpToLevel();
            player.setLevel(player.getLevel()+1);
        }
        float xp = (float)total / (float)player.getExpToLevel();
        player.setExp(xp);
	}
	static boolean isProtected(ItemStack i){
		if(i.hasItemMeta()){
			if(i.getItemMeta().hasLore()){
				if(i.getItemMeta().getLore().contains(color(Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName"))))return true;
			}
		}
		return false;
	}
	static ItemStack addWhiteScroll(int amount){
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("&7Prevents an item from being destroyed");
		lore.add("&7due to a failed Enchantment Book.");
		lore.add("&ePlace scroll on item to apply.");
		return makeItem(Main.settings.getConfig().getString("Settings.WhiteScroll.Item"), amount, Main.settings.getConfig().getString("Settings.WhiteScroll.Name"), lore);
	}
	public static ItemStack addLore(ItemStack item, String i){
		ArrayList<String> lore = new ArrayList<String>();
		ItemMeta m = item.getItemMeta();
		if(item.getItemMeta().hasLore()){
			lore.addAll(item.getItemMeta().getLore());
		}
		lore.add(color(i));
		if(lore.contains(color(Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName")))){
			lore.remove(color(Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName")));
			lore.add(color(Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName")));
		}
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}
	static ItemStack removeProtected(ItemStack item){
		ArrayList<String> lore = new ArrayList<String>();
		ItemMeta m = item.getItemMeta();
		lore.addAll(m.getLore());
		lore.remove(color(Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName")));
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}
	public static int getEnchAmount(ItemStack item){
		int amount = 0;
		if(item.hasItemMeta()){
			if(item.getItemMeta().hasLore()){
				for(String lore : item.getItemMeta().getLore()){
					for(String en : ECControl.allEnchantments().keySet()){
						if(lore.contains(getEnchName(en))){
							amount++;
						}
					}
				}
			}
		}
		if(Main.settings.getConfig().contains("Settings.EnchantmentOptions.IncludeVanillaEnchantments")){
			if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.IncludeVanillaEnchantments")){
				if(item.hasItemMeta()){
					if(item.getItemMeta().hasEnchants()){
						amount=amount+item.getItemMeta().getEnchants().size();
					}
				}
			}
		}
		return amount;
	}
	static boolean successChance(ItemStack item){
		for(String lore : item.getItemMeta().getLore()){
			if(lore.contains("% Success Chance")){
				lore=lore.replaceAll("% Success Chance", "");
				lore=removeColor(lore);
				Random number = new Random();
				int chance;
				for(int counter = 1; counter<=1;){
					chance = 1 + number.nextInt(99);
					if(chance >= 1 && chance <= Integer.parseInt(lore)){
						return true;
					}else{
						return false;
					}
				}
			}
		}
		return true;
	}
	static boolean destroyChance(ItemStack item){
		for(String lore : item.getItemMeta().getLore()){
			if(lore.contains("% Destroy Chance")){
				lore=lore.replaceAll("% Destroy Chance", "");
				lore=removeColor(lore);
				Random number = new Random();
				int chance;
				for(int counter = 1; counter<=1;){
					chance = 1 + number.nextInt(99);
					if(chance >= 1 && chance <= Integer.parseInt(lore)){
						return true;
					}else{
						return false;
					}
				}
			}
		}
		return false;
	}
	public static boolean randomPicker(int max){
		Random number = new Random();
		int chance;
		for(int counter = 1; counter<=1; counter++){
			chance = 1 + number.nextInt(max);
			if(chance == 1){
				return true;
			}
		}
		return false;
	}
	public static boolean isInvFull(Player player){
		if(player.getInventory().firstEmpty()==-1){
			return true;
		}
		return false;
	}
	public static Boolean isEnchantmentEnabled(String ench){
		for(String en : ECControl.allEnchantments().keySet()){
			if(getEnchName(en).equalsIgnoreCase(ench)){
				if(Main.settings.getEnchs().getBoolean("Enchantments."+en+".Enabled")){
					return true;
				}
			}
		}
		return false;
	}
}
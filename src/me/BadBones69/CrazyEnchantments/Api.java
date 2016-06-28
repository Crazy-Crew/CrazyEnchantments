package me.BadBones69.CrazyEnchantments;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import me.BadBones69.CrazyEnchantments.API.CrazyEnchantments;
import me.BadBones69.CrazyEnchantments.MultiSupport.FactionsSupport;
import me.BadBones69.CrazyEnchantments.MultiSupport.FactionsUUID;
import me.BadBones69.CrazyEnchantments.MultiSupport.NMS_v1_10_R1;
import me.BadBones69.CrazyEnchantments.MultiSupport.NMS_v1_7_R4;
import me.BadBones69.CrazyEnchantments.MultiSupport.NMS_v1_8_R1;
import me.BadBones69.CrazyEnchantments.MultiSupport.NMS_v1_8_R2;
import me.BadBones69.CrazyEnchantments.MultiSupport.NMS_v1_8_R3;
import me.BadBones69.CrazyEnchantments.MultiSupport.NMS_v1_9_R1;
import me.BadBones69.CrazyEnchantments.MultiSupport.NMS_v1_9_R2;
import me.BadBones69.CrazyEnchantments.MultiSupport.WorldGuard;

public class Api{
	static CrazyEnchantments CE = CrazyEnchantments.getInstance();
	public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments");
	@SuppressWarnings("static-access")
	public Api(Plugin plugin){
		this.plugin = plugin;
	}
	public static String color(String msg){
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	public static String removeColor(String msg){
		msg = ChatColor.stripColor(msg);
		return msg;
	}
	public static Integer getVersion(){
		String ver = Bukkit.getServer().getClass().getPackage().getName();
		ver = ver.substring(ver.lastIndexOf('.')+1);
		ver=ver.replaceAll("_", "").replaceAll("R", "").replaceAll("v", "");
		return Integer.parseInt(ver);
	}
	public static ItemStack addGlow(ItemStack item) {
		if(getVersion()==1101){
			return NMS_v1_10_R1.addGlow(item);
		}
		if(getVersion()==192){
			return NMS_v1_9_R2.addGlow(item);
		}
		if(getVersion()==191){
			return NMS_v1_9_R1.addGlow(item);
		}
		if(getVersion()==183){
			return NMS_v1_8_R3.addGlow(item);
		}
		if(getVersion()==182){
			return NMS_v1_8_R2.addGlow(item);
		}
		if(getVersion()==181){
			return NMS_v1_8_R1.addGlow(item);
		}
		if(getVersion()==174){
			return NMS_v1_7_R4.addGlow(item);
		}else{
			Bukkit.getLogger().log(Level.SEVERE, "[Crazy Enchantments]>> Your server is too far out of date. "
					+ "Please update or remove this plugin to stop further Errors.");
			return item;
		}
    }
	public static ItemStack addGlow(ItemStack item, boolean toggle) {
		if(toggle){
			if(getVersion()==1101){
				return NMS_v1_10_R1.addGlow(item);
			}
			if(getVersion()==192){
				return NMS_v1_9_R2.addGlow(item);
			}
			if(getVersion()==191){
				return NMS_v1_9_R1.addGlow(item);
			}
			if(getVersion()==183){
				return NMS_v1_8_R3.addGlow(item);
			}
			if(getVersion()==182){
				return NMS_v1_8_R2.addGlow(item);
			}
			if(getVersion()==181){
				return NMS_v1_8_R1.addGlow(item);
			}
			if(getVersion()==174){
				return NMS_v1_7_R4.addGlow(item);
			}else{
				Bukkit.getLogger().log(Level.SEVERE, "[Crazy Enchantments]>> Your server is too far out of date. "
						+ "Please update or remove this plugin to stop further Errors.");
				return item;
			}
		}else{
			return item;
		}
    }
	@SuppressWarnings("deprecation")
	public static ItemStack getItemInHand(Player player){
		if(getVersion()>=191){
			return player.getInventory().getItemInMainHand();
		}else{
			return player.getItemInHand();
		}
	}
	@SuppressWarnings("deprecation")
	public static void setItemInHand(Player player, ItemStack item){
		if(Api.getVersion()>=191){
			player.getInventory().setItemInMainHand(item);
		}else{
			player.setItemInHand(item);
		}
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
		if(i==8)return "VIII";
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
	public static boolean hasFactions(){
		if(Bukkit.getServer().getPluginManager().getPlugin("Factions")!=null)return true;
		return false;
	}
	public static boolean canBreakBlock(Player player, Block block){
		Plugin factions = Bukkit.getServer().getPluginManager().getPlugin("Factions");
		if(factions!=null){
			if(player!=null){
				if(factions.getDescription().getAuthors().contains("drtshock")){
					if(FactionsUUID.canBreakBlock(player, block))return true;
					if(!FactionsUUID.canBreakBlock(player, block))return false;
				}
				if(factions.getDescription().getWebsite()!=null){
					if(factions.getDescription().getWebsite().equalsIgnoreCase("https://www.massivecraft.com/factions")){
						if(FactionsSupport.canBreakBlock(player, block))return true;
						if(!FactionsSupport.canBreakBlock(player, block))return false;
					}
				}
			}
		}
		return true;
	}
	public static boolean inTerritory(Player player){
		Plugin factions = Bukkit.getServer().getPluginManager().getPlugin("Factions");
		if(factions!=null){
			if(factions.getDescription().getAuthors().contains("drtshock")){
				if(FactionsUUID.inTerritory(player))return true;
				if(!FactionsUUID.inTerritory(player))return false;
			}
			if(factions.getDescription().getWebsite()!=null){
				if(factions.getDescription().getWebsite().equalsIgnoreCase("https://www.massivecraft.com/factions")){
					if(FactionsSupport.inTerritory(player))return true;
					if(!FactionsSupport.inTerritory(player))return false;
				}
			}
		}
		return false;
	}
	public static boolean isFriendly(Entity P, Entity O){
		if(P instanceof Player&&O instanceof Player){
			Plugin factions = Bukkit.getServer().getPluginManager().getPlugin("Factions");
			if(factions!=null){
				Player player = (Player) P;
				Player other = (Player) O;
				if(factions.getDescription().getAuthors().contains("drtshock")){
					if(FactionsUUID.isFriendly(player, other))return true;
					if(!FactionsUUID.isFriendly(player, other))return false;
				}
				if(factions.getDescription().getWebsite()!=null){
					if(factions.getDescription().getWebsite().equalsIgnoreCase("https://www.massivecraft.com/factions")){
						if(FactionsSupport.isFriendly(player, other))return true;
						if(!FactionsSupport.isFriendly(player, other))return false;
					}
				}
			}
		}
		return false;
	}
	public static boolean allowsPVP(Entity en){
		if(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")!=null&&Bukkit.getServer().getPluginManager().getPlugin("WorldGuard")!=null){
			if(WorldGuard.allowsPVP(en))return true;
			if(!WorldGuard.allowsPVP(en))return false;
		}
		return true;
	}
	public static boolean allowsBreak(Entity en){
		if(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")!=null&&Bukkit.getServer().getPluginManager().getPlugin("WorldGuard")!=null){
			if(WorldGuard.allowsBreak(en))return true;
			if(!WorldGuard.allowsBreak(en))return false;
		}
		return true;
	}
	public static boolean allowsExplotions(Entity en){
		if(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")!=null&&Bukkit.getServer().getPluginManager().getPlugin("WorldGuard")!=null){
			if(WorldGuard.allowsExplotions(en))return true;
			if(!WorldGuard.allowsExplotions(en))return false;
		}
		return true;
	}
	public static ItemStack removeLore(ItemStack item, String i){
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
	public static ItemStack replaceLore(ItemStack item, String oldlore, String newlore){
		ArrayList<String> lore = new ArrayList<String>();
		ItemMeta m = item.getItemMeta();
		for(String l : item.getItemMeta().getLore()){
			if(l.equals(oldlore)){
				lore.add(color(newlore));
			}else{
				lore.add(l);
			}
		}
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}
	public static ItemStack getLostBook(String cat, int amount){
		String id = Main.settings.getConfig().getString("Settings.LostBook.Item");
		String name = Main.settings.getConfig().getString("Settings.LostBook.Name");
		List<String> lore = new ArrayList<String>();
		String tn = Main.settings.getConfig().getString("Categories."+cat+".Name");
		for(String l : Main.settings.getConfig().getStringList("Settings.LostBook.Lore")){
			lore.add(l.replaceAll("%Category%", tn).replaceAll("%category%", tn));
		}
		return makeItem(id, amount, name, lore);
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
	public static ItemStack makeItem(String type, int amount){
		int ty = 0;
		if(type.contains(":")){
			String[] b = type.split(":");
			type = b[0];
			ty = Integer.parseInt(b[1]);
		}
		Material m = Material.matchMaterial(type);
		ItemStack item = new ItemStack(m, amount, (short) ty);
		ItemMeta me = item.getItemMeta();
		item.setItemMeta(me);
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
	public static String percentPicker(){
		Random i = new Random();
		return Integer.toString(i.nextInt(100));
	}
	public static String getPrefix(){
		return color(Main.settings.getConfig().getString("Settings.Prefix"));
	}
	public static double getMoney(Player player){
		return Main.econ.getBalance(player);
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
		p.sendMessage(getPrefix()+color(Main.settings.getMsg().getString("Messages.Not-Online")));
		return false;
	}
	public static boolean hasPermission(Player player, String perm, Boolean toggle){
		if(!player.hasPermission("CrazyEnchantments." + perm)){
			if(toggle){
				player.sendMessage(color(Main.settings.getMsg().getString("Messages.No-Perm")));
			}
			return false;
		}
		return true;
	}
	public static boolean hasPermission(CommandSender sender, String perm, Boolean toggle){
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(!player.hasPermission("CrazyEnchantments." + perm)){
				if(toggle){
					player.sendMessage(color(Main.settings.getMsg().getString("Messages.No-Perm")));
				}
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	}
	public static void removeItem(ItemStack item, Player player){
		if(item.getAmount() <= 1){
			player.getInventory().removeItem(item);
		}
		if(item.getAmount() > 1){
			ItemStack i = item;
			i.setAmount(item.getAmount() - 1);
		}
	}
	public static ItemStack removeItem(ItemStack item){
		if(item.getAmount() <= 1){
			return new ItemStack(Material.AIR);
		}
		else{
			ItemStack i = item;
			i.setAmount(item.getAmount() - 1);
			return i;
		}
	}
	public static String getInvName(){
		return color(Main.settings.getConfig().getString("Settings.InvName"));
	}
	public static int getXPLvl(Player player){
		return player.getLevel();
	}
	public static void takeLvlXP(Player player, int amount){
		player.setLevel(player.getLevel() - amount);
	}
	public static int getTotalExperience(Player player){// https://www.spigotmc.org/threads/72804
		int experience = 0;
		int level = player.getLevel();
		if(level >= 0 && level <= 15) {
			experience = (int) Math.ceil(Math.pow(level, 2) + (6 * level));
			int requiredExperience = 2 * level + 7;
			double currentExp = Double.parseDouble(Float.toString(player.getExp()));
			experience += Math.ceil(currentExp * requiredExperience);
			return experience;
		} else if(level > 15 && level <= 30) {
			experience = (int) Math.ceil((2.5 * Math.pow(level, 2) - (40.5 * level) + 360));
			int requiredExperience = 5 * level - 38;
			double currentExp = Double.parseDouble(Float.toString(player.getExp()));
			experience += Math.ceil(currentExp * requiredExperience);
			return experience;
		} else {
			experience = (int) Math.ceil(((4.5 * Math.pow(level, 2) - (162.5 * level) + 2220)));
			int requiredExperience = 9 * level - 158;
			double currentExp = Double.parseDouble(Float.toString(player.getExp()));
			experience += Math.ceil(currentExp * requiredExperience);
			return experience;       
		}
	}
	public static void takeTotalXP(Player player, int amount){
		if(getVersion()>=181){
			int total = getTotalExperience(player) - amount;
			player.setTotalExperience(0);
	        player.setTotalExperience(total);
	        player.setLevel(0);
	        player.setExp(0);
	        for(;total > player.getExpToLevel();){
	            total -= player.getExpToLevel();
	            player.setLevel(player.getLevel()+1);
	        }
	        float xp = (float)total / (float)player.getExpToLevel();
	        player.setExp(xp);
		}else{
			player.giveExp(-amount);
		}
	}
	public static boolean isProtected(ItemStack i){
		if(i.hasItemMeta()){
			if(i.getItemMeta().hasLore()){
				for(String lore : i.getItemMeta().getLore())if(lore.equals(color(Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName")))){
					return true;
				}
			}
		}
		return false;
	}
	public static ItemStack BlackScroll(int i){
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
	public static ItemStack addWhiteScroll(int amount){
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
		if(lore.contains(color(Main.settings.getConfig().getString("Settings.ProtectionCrystal.Protected")))){
			lore.remove(color(Main.settings.getConfig().getString("Settings.ProtectionCrystal.Protected")));
			lore.add(color(Main.settings.getConfig().getString("Settings.ProtectionCrystal.Protected")));
		}
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}
	public static ItemStack removeProtected(ItemStack item){
		ArrayList<String> lore = new ArrayList<String>();
		ItemMeta m = item.getItemMeta();
		lore.addAll(m.getLore());
		lore.remove(color(Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName")));
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}
	public static void hasUpdate(){
		try {
			HttpURLConnection c = (HttpURLConnection)new URL("http://www.spigotmc.org/api/general.php").openConnection();
			c.setDoOutput(true);
			c.setRequestMethod("POST");
			c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=16470").getBytes("UTF-8"));
			String oldVersion = plugin.getDescription().getVersion();
			String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z ]", "");
			if(!newVersion.equals(oldVersion)) {
				Bukkit.getConsoleSender().sendMessage(Api.getPrefix()+Api.color("&cYour server is running &7v"+oldVersion+"&c and the newest is &7v"+newVersion+"&c."));
			}
		}
		catch(Exception e) {
			return;
		}
	}
	public static void hasUpdate(Player player){
		try {
			HttpURLConnection c = (HttpURLConnection)new URL("http://www.spigotmc.org/api/general.php").openConnection();
			c.setDoOutput(true);
			c.setRequestMethod("POST");
			c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=16470").getBytes("UTF-8"));
			String oldVersion = plugin.getDescription().getVersion();
			String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z ]", "");
			if(!newVersion.equals(oldVersion)) {
				player.sendMessage(Api.getPrefix()+Api.color("&cYour server is running &7v"+oldVersion+"&c and the newest is &7v"+newVersion+"&c."));
			}
		}
		catch(Exception e) {
			return;
		}
	}
	public static int getEnchAmount(ItemStack item){
		int amount = 0;
		if(item.hasItemMeta()){
			if(item.getItemMeta().hasLore()){
				for(String lore : item.getItemMeta().getLore()){
					for(String en : ECControl.allEnchantments().keySet()){
						if(lore.contains(CE.getFromName(en).getCustomName())){
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
	public static boolean successChance(ItemStack item){
		if(item.hasItemMeta()){
			if(item.getItemMeta().hasLore()){
				int percent = getPercent("%success_rate%", item, Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore"));
				if(randomPicker(percent, 100)){
					return true;
				}else{
					return false;
				}
			}
		}
		return true;
	}
	public static boolean destroyChance(ItemStack item){
		if(item.hasItemMeta()){
			if(item.getItemMeta().hasLore()){
				if(randomPicker(getPercent("%destroy_rate%", item, Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore")), 100)){
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}
	public static Integer getPercent(String Argument, ItemStack item, List<String> Msg){
		List<String> lore = item.getItemMeta().getLore();
		String arg = "100";
		int i = 0;
		for(String l : Msg){
			l = Api.color(l).toLowerCase();
			String lo = lore.get(i).toLowerCase();
			if(l.contains(Argument.toLowerCase())){
				String[] b = l.split(Argument.toLowerCase());
				if(b.length>=1)arg = lo.replace(b[0], "");
				if(b.length>=2)arg = arg.replace(b[1], "");
			}
			i++;
		}
		return Integer.parseInt(arg);
	}
	public static Boolean hasArgument(String Argument, List<String> Msg){
		for(String l : Msg){
			l = Api.color(l).toLowerCase();
			if(l.contains(Argument.toLowerCase())){
				return true;
			}
		}
		return false;
	}
	public static boolean randomPicker(int max){
		Random number = new Random();
		int chance = 1 + number.nextInt(max);
		if(chance == 1){
			return true;
		}
		return false;
	}
	public static boolean randomPicker(int min, int max){
		if(max==min)return true;
		Random number = new Random();
		int chance = 1 + number.nextInt(max);
		if(chance>=1&&chance<=min){
			return true;
		}
		return false;
	}
	public static Integer percentPick(int max, int min){
		Random i = new Random();
		return min+i.nextInt(max-min);
	}
	public static boolean isInvFull(Player player){
		if(player.getInventory().firstEmpty()==-1){
			return true;
		}
		return false;
	}
}
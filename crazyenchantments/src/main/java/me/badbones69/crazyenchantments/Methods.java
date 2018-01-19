package me.badbones69.crazyenchantments;

import me.badbones69.crazyenchantments.controlers.FireworkDamageAPI;
import me.badbones69.crazyenchantments.multisupport.AACSupport;
import me.badbones69.crazyenchantments.multisupport.SpartanSupport;
import me.badbones69.crazyenchantments.multisupport.Support;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import me.badbones69.crazyenchantments.multisupport.Version;
import me.badbones69.crazyenchantments.multisupport.nms.NMS_v1_7_R4;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Methods {

	public static String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static String removeColor(String msg) {
		msg = ChatColor.stripColor(msg);
		return msg;
	}

	public static void sendMultiMessage(Player player, List<String> messages) {
		for(String msg : messages) {
			player.sendMessage(color(msg));
		}
	}

	public static Integer getRandomNumber(int range) {
		if(range == 0) {
			range++;
		}
		return new Random().nextInt(range) + 1;
	}

	public static Integer getRandomNumber(String range) {
		int number = 1;
		String[] split = range.split("-");
		if(isInt(split[0]) && isInt(split[1])) {
			int max = Integer.parseInt(split[1]) + 1;
			int min = Integer.parseInt(split[0]);
			number = min + new Random().nextInt(max - min);
		}
		return number;
	}

	public static boolean hasPermission(Player player, String perm, Boolean toggle) {
		if(player.hasPermission("crazyenchantments." + perm) || player.hasPermission("crazyenchantments.admin")) {
			return true;
		}else {
			if(toggle) {
				player.sendMessage(getPrefix() + color(Main.settings.getMessages().getString("Messages.No-Perm")));
			}
			return false;
		}
	}

	public static boolean hasPermission(CommandSender sender, String perm, Boolean toggle) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(player.hasPermission("crazyenchantments." + perm) || player.hasPermission("crazyenchantments.admin")) {
				return true;
			}else {
				if(toggle) {
					player.sendMessage(getPrefix() + color(Main.settings.getMessages().getString("Messages.No-Perm")));
				}
				return false;
			}
		}else {
			return true;
		}
	}

	public static ItemStack addGlow(ItemStack item) {
		switch(Version.getCurrentVersion()) {
			case v1_7_R4:
				return NMS_v1_7_R4.addGlow(item);
			default:
				return addGlowHide(item);
		}
	}

	public static ItemStack addGlow(ItemStack item, boolean toggle) {
		if(toggle) {
			switch(Version.getCurrentVersion()) {
				case v1_7_R4:
					return NMS_v1_7_R4.addGlow(item);
				default:
					return addGlowHide(item);
			}
		}
		return item;
	}

	public static ItemStack addGlowHide(ItemStack item) {
		try {
			if(item != null) {
				if(item.hasItemMeta()) {
					if(item.getItemMeta().hasEnchants()) {
						return item;
					}
				}
				item.addUnsafeEnchantment(Enchantment.LUCK, 1);
				ItemMeta meta = item.getItemMeta();
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
			}
			return item;
		}catch(NoClassDefFoundError e) {
			return item;
		}
	}

	public static ItemStack addGlowHide(ItemStack item, boolean toggle) {
		try {
			if(toggle) {
				if(item != null) {
					if(item.hasItemMeta()) {
						if(item.getItemMeta().hasEnchants()) {
							return item;
						}
					}
					item.addUnsafeEnchantment(Enchantment.LUCK, 1);
					ItemMeta meta = item.getItemMeta();
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					item.setItemMeta(meta);
				}
			}
			return item;
		}catch(NoClassDefFoundError e) {
			return item;
		}
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getItemInHand(Player player) {
		if(Version.getCurrentVersion().getVersionInteger() >= 191) {
			return player.getInventory().getItemInMainHand();
		}else {
			return player.getItemInHand();
		}
	}

	@SuppressWarnings("deprecation")
	public static void setItemInHand(Player player, ItemStack item) {
		if(Version.getCurrentVersion().getVersionInteger() >= 191) {
			player.getInventory().setItemInMainHand(item);
		}else {
			player.setItemInHand(item);
		}
	}

	public static String getPower(Integer i) {
		if(i == 0) return "I";
		if(i == 1) return "I";
		if(i == 2) return "II";
		if(i == 3) return "III";
		if(i == 4) return "IV";
		if(i == 5) return "V";
		if(i == 6) return "VI";
		if(i == 7) return "VII";
		if(i == 8) return "VIII";
		if(i == 9) return "IX";
		if(i == 10) return "X";
		return i + "";
	}

	public static ArrayList<String> getPotions() {
		ArrayList<String> list = new ArrayList<>();
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

	public static ItemStack removeLore(ItemStack item, String i) {
		ArrayList<String> lore = new ArrayList<>();
		ItemMeta m = item.getItemMeta();
		for(String l : item.getItemMeta().getLore()) {
			if(!l.equals(i)) {
				lore.add(l);
			}
		}
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}

	public static ItemStack replaceLore(ItemStack item, String oldlore, String newlore) {
		ArrayList<String> lore = new ArrayList<>();
		ItemMeta m = item.getItemMeta();
		for(String l : item.getItemMeta().getLore()) {
			if(l.equals(oldlore)) {
				lore.add(color(newlore));
			}else {
				lore.add(l);
			}
		}
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}

	public static String percentPicker() {
		Random i = new Random();
		return Integer.toString(i.nextInt(100));
	}

	public static String getPrefix() {
		return color(Main.settings.getConfig().getString("Settings.Prefix"));
	}

	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		}catch(NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static Player getPlayer(String name) {
		return Bukkit.getServer().getPlayer(name);
	}

	public static Location getLoc(Player player) {
		return player.getLocation();
	}

	public static void runCMD(Player player, String CMD) {
		player.performCommand(CMD);
	}

	public static boolean isOnline(String name, CommandSender p) {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			if(player.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		p.sendMessage(getPrefix() + color(Main.settings.getMessages().getString("Messages.Not-Online")));
		return false;
	}

	public static void removeItem(ItemStack item, Player player) {
		if(item.getAmount() <= 1) {
			player.getInventory().removeItem(item);
		}
		if(item.getAmount() > 1) {
			item.setAmount(item.getAmount() - 1);
		}
	}

	public static ItemStack removeItem(ItemStack item) {
		ItemStack i = item.clone();
		if(item.getAmount() <= 1) {
			i = new ItemStack(Material.AIR);
		}else {
			i.setAmount(item.getAmount() - 1);
		}
		return i;
	}

	public static String getInvName() {
		return color(Main.settings.getConfig().getString("Settings.InvName"));
	}

	public static boolean isProtected(ItemStack i) {
		if(i.hasItemMeta()) {
			if(i.getItemMeta().hasLore()) {
				for(String lore : i.getItemMeta().getLore())
					if(lore.equals(color(Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName")))) {
						return true;
					}
			}
		}
		return false;
	}

	public static ItemStack addLore(ItemStack item, String i) {
		ArrayList<String> lore = new ArrayList<>();
		ItemMeta m = item.getItemMeta();
		if(item.getItemMeta().hasLore()) {
			lore.addAll(item.getItemMeta().getLore());
		}
		lore.add(color(i));
		if(lore.contains(color(Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName")))) {
			lore.remove(color(Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName")));
			lore.add(color(Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName")));
		}
		if(lore.contains(color(Main.settings.getConfig().getString("Settings.ProtectionCrystal.Protected")))) {
			lore.remove(color(Main.settings.getConfig().getString("Settings.ProtectionCrystal.Protected")));
			lore.add(color(Main.settings.getConfig().getString("Settings.ProtectionCrystal.Protected")));
		}
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}

	public static ItemStack removeProtected(ItemStack item) {
		ItemMeta m = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<>(m.getLore());
		lore.remove(color(Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName")));
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}

	public static void hasUpdate() {
		try {
			HttpURLConnection c = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
			c.setDoOutput(true);
			c.setRequestMethod("POST");
			c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=16470").getBytes("UTF-8"));
			String oldVersion = Main.CE.getPlugin().getDescription().getVersion();
			String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z ]", "");
			if(!newVersion.equals(oldVersion)) {
				Bukkit.getConsoleSender().sendMessage(Methods.getPrefix() + Methods.color("&cYour server is running &7v" + oldVersion + "&c and the newest is &7v" + newVersion + "&c."));
			}
		}catch(Exception e) {
		}
	}

	public static void hasUpdate(Player player) {
		try {
			HttpURLConnection c = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
			c.setDoOutput(true);
			c.setRequestMethod("POST");
			c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=16470").getBytes("UTF-8"));
			String oldVersion = Main.CE.getPlugin().getDescription().getVersion();
			String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z ]", "");
			if(!newVersion.equals(oldVersion)) {
				player.sendMessage(Methods.getPrefix() + Methods.color("&cYour server is running &7v" + oldVersion + "&c and the newest is &7v" + newVersion + "&c."));
			}
		}catch(Exception e) {
		}
	}

	public static int getEnchAmount(ItemStack item) {
		int amount = 0;
		amount += Main.CE.getItemEnchantments(item).size();
		amount += Main.CustomE.getItemEnchantments(item).size();
		if(Main.settings.getConfig().contains("Settings.EnchantmentOptions.IncludeVanillaEnchantments")) {
			if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.IncludeVanillaEnchantments")) {
				if(item.hasItemMeta()) {
					if(item.getItemMeta().hasEnchants()) {
						amount = +item.getItemMeta().getEnchants().size();
					}
				}
			}
		}
		return amount;
	}

	public static Integer getPercent(String Argument, ItemStack item, List<String> Msg) {
		List<String> lore = item.getItemMeta().getLore();
		String arg = "100";
		for(String oLine : Msg) {
			oLine = Methods.color(oLine).toLowerCase();
			if(oLine.contains(Argument.toLowerCase())) {
				String[] b = oLine.split(Argument.toLowerCase());
				for(String iline : lore) {
					Boolean toggle = false;// Checks to make sure the lore is the same.
					if(b.length >= 1) {
						if(iline.toLowerCase().startsWith(b[0])) {
							arg = iline.toLowerCase().replace(b[0], "");
							toggle = true;
						}
					}
					if(b.length >= 2) {
						if(iline.toLowerCase().endsWith(b[1])) {
							arg = arg.toLowerCase().replace(b[1], "");
						}else {
							toggle = false;
						}
					}
					if(toggle) {
						break;
					}
				}
				if(isInt(arg)) {
					break;
				}else {
				}
			}
		}
		int percent = 100;
		if(isInt(arg)) {
			percent = Integer.parseInt(arg);
		}
		return percent;
	}

	public static Boolean hasArgument(String Argument, List<String> Msg) {
		for(String l : Msg) {
			l = Methods.color(l).toLowerCase();
			if(l.contains(Argument.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public static boolean randomPicker(int max) {
		Random number = new Random();
		if(max <= 0) {
			return true;
		}
		int chance = 1 + number.nextInt(max);
		return chance == 1;
	}

	public static boolean randomPicker(int min, int max) {
		if(max == min || max <= min || max <= 0) {
			return true;
		}
		Random number = new Random();
		int chance = 1 + number.nextInt(max);
		return chance >= 1 && chance <= min;
	}

	public static Integer percentPick(int max, int min) {
		Random i = new Random();
		if(max == min) {
			return max;
		}else {
			return min + i.nextInt(max - min);
		}
	}

	public static boolean isInvFull(Player player) {
		return player.getInventory().firstEmpty() == -1;
	}

	public static List<LivingEntity> getNearbyLivingEntities(Location loc, double radius, Entity ent) {
		List<Entity> out = ent.getNearbyEntities(radius, radius, radius);
		List<LivingEntity> entities = new ArrayList<>();
		for(Entity en : out) {
			if(en instanceof LivingEntity) {
				entities.add((LivingEntity) en);
			}
		}
		return entities;
	}

	public static List<Entity> getNearbyEntitiess(Location loc, double radius, Entity ent) {
		return ent.getNearbyEntities(radius, radius, radius);
	}

	public static void fireWork(Location loc, ArrayList<Color> colors) {
		Firework fw = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fm = fw.getFireworkMeta();
		fm.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(colors).trail(false).flicker(false).build());
		fm.setPower(0);
		fw.setFireworkMeta(fm);
		FireworkDamageAPI.addFirework(fw);
		detonate(fw);
	}

	private static void detonate(final Firework f) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.CE.getPlugin(), f::detonate, 2);
	}

	public static Color getColor(String color) {
		switch(color.toUpperCase()) {
			case "AQUA":
				return Color.AQUA;
			case "BLACK":
				return Color.BLACK;
			case "BLUE":
				return Color.BLUE;
			case "FUCHSIA":
				return Color.FUCHSIA;
			case "GRAY":
				return Color.GRAY;
			case "GREEN":
				return Color.GREEN;
			case "LIME":
				return Color.LIME;
			case "MAROON":
				return Color.MAROON;
			case "NAVY":
				return Color.NAVY;
			case "OLIVE":
				return Color.OLIVE;
			case "ORANGE":
				return Color.ORANGE;
			case "PURPLE":
				return Color.PURPLE;
			case "RED":
				return Color.RED;
			case "SILVER":
				return Color.SILVER;
			case "TEAL":
				return Color.TEAL;
			case "WHITE":
				return Color.WHITE;
			case "YELLOW":
				return Color.YELLOW;
			default:
				return Color.WHITE;
		}
	}

	public static String getEnchantmentName(Enchantment en) {
		HashMap<String, String> enchants = new HashMap<>();
		enchants.put("ARROW_DAMAGE", "Power");
		enchants.put("ARROW_FIRE", "Flame");
		enchants.put("ARROW_INFINITE", "Infinity");
		enchants.put("ARROW_KNOCKBACK", "Punch");
		enchants.put("DAMAGE_ALL", "Sharpness");
		enchants.put("DAMAGE_ARTHROPODS", "Bane_Of_Arthropods");
		enchants.put("DAMAGE_UNDEAD", "Smite");
		enchants.put("DEPTH_STRIDER", "Depth_Strider");
		enchants.put("DIG_SPEED", "Efficiency");
		enchants.put("DURABILITY", "Unbreaking");
		enchants.put("FIRE_ASPECT", "Fire_Aspect");
		enchants.put("KNOCKBACK", "KnockBack");
		enchants.put("LOOT_BONUS_BLOCKS", "Fortune");
		enchants.put("LOOT_BONUS_MOBS", "Looting");
		enchants.put("LUCK", "Luck_Of_The_Sea");
		enchants.put("LURE", "Lure");
		enchants.put("OXYGEN", "Respiration");
		enchants.put("PROTECTION_ENVIRONMENTAL", "Protection");
		enchants.put("PROTECTION_EXPLOSIONS", "Blast_Protection");
		enchants.put("PROTECTION_FALL", "Feather_Falling");
		enchants.put("PROTECTION_FIRE", "Fire_Protection");
		enchants.put("PROTECTION_PROJECTILE", "Projectile_Protection");
		enchants.put("SILK_TOUCH", "Silk_Touch");
		enchants.put("THORNS", "Thorns");
		enchants.put("WATER_WORKER", "Aqua_Affinity");
		enchants.put("BINDING_CURSE", "Curse_Of_Binding");
		enchants.put("MENDING", "Mending");
		enchants.put("FROST_WALKER", "Frost_Walker");
		enchants.put("VANISHING_CURSE", "Curse_Of_Vanishing");
		if(enchants.get(en.getName()) == null) {
			return "None Found";
		}
		return enchants.get(en.getName());
	}

	@SuppressWarnings("deprecation")
	public static void removeDurability(ItemStack item, Player player) {
		if(item.hasItemMeta()) {
			try {
				if(item.getItemMeta().isUnbreakable()) {
					return;
				}
			}catch(NoSuchMethodError e) {
			}
			try {
				if(item.getItemMeta().spigot().isUnbreakable()) {
					return;
				}
			}catch(NoSuchMethodError e) {
			}
			if(item.getItemMeta().hasEnchants()) {
				if(item.getItemMeta().hasEnchant(Enchantment.DURABILITY)) {
					if(Methods.randomPicker(1, 1 + item.getEnchantmentLevel(Enchantment.DURABILITY))) {
						if(item.getType().getMaxDurability() < item.getDurability()) {
							player.getInventory().remove(item);
						}else {
							item.setDurability((short) (item.getDurability() + 1));
						}
					}
					return;
				}
			}
		}
		if(item.getType().getMaxDurability() < item.getDurability()) {
			player.getInventory().remove(item);
		}else {
			item.setDurability((short) (item.getDurability() + 1));
		}
	}

	public static boolean isSimilar(ItemStack one, ItemStack two) {
		if(one.getType() == two.getType()) {
			if(one.hasItemMeta() && two.hasItemMeta()) {
				if(one.getItemMeta().hasDisplayName() && two.getItemMeta().hasDisplayName()) {
					if(one.getItemMeta().getDisplayName().equalsIgnoreCase(two.getItemMeta().getDisplayName())) {
						if(one.getItemMeta().hasLore() && two.getItemMeta().hasLore()) {
							int i = 0;
							for(String lore : one.getItemMeta().getLore()) {
								if(!lore.equals(two.getItemMeta().getLore().get(i))) {
									return false;
								}
								i++;
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static Set<String> getEnchantments() {
		HashMap<String, String> enchants = new HashMap<>();
		enchants.put("ARROW_DAMAGE", "Power");
		enchants.put("ARROW_FIRE", "Flame");
		enchants.put("ARROW_INFINITE", "Infinity");
		enchants.put("ARROW_KNOCKBACK", "Punch");
		enchants.put("DAMAGE_ALL", "Sharpness");
		enchants.put("DAMAGE_ARTHROPODS", "Bane_Of_Arthropods");
		enchants.put("DAMAGE_UNDEAD", "Smite");
		enchants.put("DEPTH_STRIDER", "Depth_Strider");
		enchants.put("DIG_SPEED", "Efficiency");
		enchants.put("DURABILITY", "Unbreaking");
		enchants.put("FIRE_ASPECT", "Fire_Aspect");
		enchants.put("KNOCKBACK", "KnockBack");
		enchants.put("LOOT_BONUS_BLOCKS", "Fortune");
		enchants.put("LOOT_BONUS_MOBS", "Looting");
		enchants.put("LUCK", "Luck_Of_The_Sea");
		enchants.put("LURE", "Lure");
		enchants.put("OXYGEN", "Respiration");
		enchants.put("PROTECTION_ENVIRONMENTAL", "Protection");
		enchants.put("PROTECTION_EXPLOSIONS", "Blast_Protection");
		enchants.put("PROTECTION_FALL", "Feather_Falling");
		enchants.put("PROTECTION_FIRE", "Fire_Protection");
		enchants.put("PROTECTION_PROJECTILE", "Projectile_Protection");
		enchants.put("SILK_TOUCH", "Silk_Touch");
		enchants.put("THORNS", "Thorns");
		enchants.put("WATER_WORKER", "Aqua_Affinity");
		enchants.put("BINDING_CURSE", "Curse_Of_Binding");
		enchants.put("MENDING", "Mending");
		enchants.put("FROST_WALKER", "Frost_Walker");
		enchants.put("VANISHING_CURSE", "Curse_Of_Vanishing");
		return enchants.keySet();
	}

	public static void explode(Entity player) {
		ParticleEffect.FLAME.display(0, 0, 0, 1, 200, player.getLocation().add(0, 1, 0), 100);
		ParticleEffect.CLOUD.display(.4F, .5F, .4F, 1, 30, player.getLocation().add(0, 1, 0), 100);
		ParticleEffect.EXPLOSION_HUGE.display(0, 0, 0, 0, 2, player.getLocation().add(0, 1, 0), 100);
		for(Entity e : Methods.getNearbyEntitiess(player.getLocation(), 3D, player)) {
			if(Support.allowsPVP(e.getLocation())) {
				if(e.getType() == EntityType.DROPPED_ITEM) {
					e.remove();
				}else {
					if(e instanceof LivingEntity) {
						LivingEntity en = (LivingEntity) e;
						if(!Support.isFriendly(player, en)) {
							if(!player.getName().equalsIgnoreCase(e.getName())) {
								en.damage(5D);
								if(en instanceof Player) {
									if(SupportedPlugins.SPARTAN.isPluginLoaded()) {
										SpartanSupport.cancelSpeed((Player) player);
										SpartanSupport.cancelFly((Player) player);
										SpartanSupport.cancelClip((Player) player);
										SpartanSupport.cancelNormalMovements((Player) player);
										SpartanSupport.cancelNoFall((Player) player);
										SpartanSupport.cancelJesus((Player) player);
									}
									if(SupportedPlugins.AAC.isPluginLoaded()) {
										AACSupport.exemptPlayerTime((Player) player);
									}
								}
								en.setVelocity(en.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1).setY(.5));
							}
						}
					}
				}
			}
		}
	}

	public static void explode(Entity player, Entity arrow) {
		ParticleEffect.FLAME.display(0, 0, 0, 1, 200, arrow.getLocation(), 100);
		ParticleEffect.CLOUD.display(.4F, .5F, .4F, 1, 30, arrow.getLocation(), 100);
		ParticleEffect.EXPLOSION_HUGE.display(0, 0, 0, 0, 2, arrow.getLocation(), 100);
		for(Entity e : Methods.getNearbyEntitiess(arrow.getLocation(), 3D, arrow)) {
			if(Support.allowsPVP(e.getLocation())) {
				if(e.getType() == EntityType.DROPPED_ITEM) {
					e.remove();
				}else {
					if(e instanceof LivingEntity) {
						LivingEntity en = (LivingEntity) e;
						if(!Support.isFriendly(player, en)) {
							if(!player.getName().equalsIgnoreCase(e.getName())) {
								en.damage(5D);
								if(en instanceof Player) {
									if(SupportedPlugins.SPARTAN.isPluginLoaded()) {
										SpartanSupport.cancelSpeed((Player) player);
										SpartanSupport.cancelFly((Player) player);
										SpartanSupport.cancelClip((Player) player);
										SpartanSupport.cancelNormalMovements((Player) player);
										SpartanSupport.cancelNoFall((Player) player);
										SpartanSupport.cancelJesus((Player) player);
									}
									if(SupportedPlugins.AAC.isPluginLoaded()) {
										AACSupport.exemptPlayerTime((Player) player);
									}
								}
								en.setVelocity(en.getLocation().toVector().subtract(arrow.getLocation().toVector()).normalize().multiply(1).setY(.5));
							}
						}
					}
				}
			}
		}
	}

}
package me.badbones69.crazyenchantments;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.controllers.FireworkDamageAPI;
import me.badbones69.crazyenchantments.multisupport.AACSupport;
import me.badbones69.crazyenchantments.multisupport.SpartanSupport;
import me.badbones69.crazyenchantments.multisupport.Support;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import me.badbones69.crazyenchantments.multisupport.Version;
import me.badbones69.crazyenchantments.multisupport.particles.ParticleEffect;
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
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Methods {
	
	private static Random random = new Random();
	private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
	private static boolean isV1_13_Up = Version.getCurrentVersion().isNewer(Version.v1_12_R1);
	
	public static String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public static String removeColor(String msg) {
		return ChatColor.stripColor(msg);
	}
	
	public static int getRandomNumber(int range) {
		return random.nextInt(range - 1) + 1;
	}
	
	public static int getRandomNumber(String range) {
		int number = 1;
		String[] split = range.split("-");
		if(isInt(split[0]) && isInt(split[1])) {
			int max = Integer.parseInt(split[1]) + 1;
			int min = Integer.parseInt(split[0]);
			number = min + random.nextInt(max - min);
		}
		return number;
	}
	
	public static boolean hasPermission(CommandSender sender, String perm, boolean toggle) {
		if(sender instanceof Player) {
			return hasPermission((Player) sender, perm, toggle);
		}else {
			return true;
		}
	}
	
	public static boolean hasPermission(Player player, String perm, boolean toggle) {
		if(player.hasPermission("crazyenchantments." + perm) || player.hasPermission("crazyenchantments.admin")) {
			return true;
		}else {
			if(toggle) {
				player.sendMessage(Messages.NO_PERMISSION.getMessage());
			}
			return false;
		}
	}
	
	public static ItemStack addGlow(ItemStack item) {
		return addGlow(item, true);
	}
	
	public static ItemStack addGlow(ItemStack item, boolean toggle) {
		ItemStack it = item.clone();
		try {
			if(toggle) {
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
			return it;
		}
	}
	
	public static ItemStack getItemInHand(Player player) {
		if(Version.getCurrentVersion().isNewer(Version.v1_8_R3)) {
			return player.getInventory().getItemInMainHand();
		}else {
			return player.getItemInHand();
		}
	}
	
	public static void setItemInHand(Player player, ItemStack item) {
		if(Version.getCurrentVersion().isNewer(Version.v1_8_R3)) {
			player.getInventory().setItemInMainHand(item);
		}else {
			player.setItemInHand(item);
		}
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
	
	public static String getPrefix() {
		return getPrefix("");
	}
	
	public static String getPrefix(String string) {
		return color(Files.CONFIG.getFile().getString("Settings.Prefix") + string);
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
	
	public static boolean isPlayerOnline(String playerName, CommandSender sender) {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			if(player.getName().equalsIgnoreCase(playerName)) {
				return true;
			}
		}
		sender.sendMessage(Messages.NOT_ONLINE.getMessage());
		return false;
	}
	
	public static void removeItem(ItemStack item, Player player) {
		removeItem(item, player, 1);
	}
	
	public static void removeItem(ItemStack item, Player player, int amount) {
		if(item.getAmount() <= amount) {
			player.getInventory().removeItem(item);
		}
		if(item.getAmount() > amount) {
			item.setAmount(item.getAmount() - amount);
		}
		player.updateInventory();
	}
	
	public static ItemStack removeItem(ItemStack item) {
		return removeItem(item, 1);
	}
	
	public static ItemStack removeItem(ItemStack item, int amount) {
		ItemStack itemStack = item.clone();
		if(item.getAmount() <= amount) {
			itemStack = new ItemStack(Material.AIR);
		}else {
			itemStack.setAmount(item.getAmount() - amount);
		}
		return itemStack;
	}
	
	public static boolean isProtected(ItemStack i) {
		if(i.hasItemMeta()) {
			if(i.getItemMeta().hasLore()) {
				for(String lore : i.getItemMeta().getLore())
					if(lore.equals(color(Files.CONFIG.getFile().getString("Settings.WhiteScroll.ProtectedName")))) {
						return true;
					}
			}
		}
		return false;
	}
	
	public static ItemStack removeProtected(ItemStack item) {
		ItemMeta m = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<>(m.getLore());
		lore.remove(color(Files.CONFIG.getFile().getString("Settings.WhiteScroll.ProtectedName")));
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}
	
	public static ItemStack addLore(ItemStack item, String i) {
		ArrayList<String> lore = new ArrayList<>();
		ItemMeta m = item.getItemMeta();
		if(item.getItemMeta().hasLore()) {
			lore.addAll(item.getItemMeta().getLore());
		}
		lore.add(color(i));
		if(lore.contains(color(Files.CONFIG.getFile().getString("Settings.WhiteScroll.ProtectedName")))) {
			lore.remove(color(Files.CONFIG.getFile().getString("Settings.WhiteScroll.ProtectedName")));
			lore.add(color(Files.CONFIG.getFile().getString("Settings.WhiteScroll.ProtectedName")));
		}
		if(lore.contains(color(Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected")))) {
			lore.remove(color(Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected")));
			lore.add(color(Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected")));
		}
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}
	
	public static void hasUpdate() {
		hasUpdate(null);
	}
	
	public static void hasUpdate(Player player) {
		try {
			HttpURLConnection c = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
			c.setDoOutput(true);
			c.setRequestMethod("POST");
			c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=16470").getBytes(StandardCharsets.UTF_8));
			String oldVersion = ce.getPlugin().getDescription().getVersion();
			String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z ]", "");
			if(!newVersion.equals(oldVersion)) {
				if(player != null) {
					player.sendMessage(Methods.getPrefix() + Methods.color("&cYour server is running &7v" + oldVersion + "&c and the newest is &7v" + newVersion + "&c."));
				}else {
					Bukkit.getConsoleSender().sendMessage(Methods.getPrefix() + Methods.color("&cYour server is running &7v" + oldVersion + "&c and the newest is &7v" + newVersion + "&c."));
				}
			}
		}catch(Exception e) {
		}
	}
	
	public static int getEnchantmentAmount(ItemStack item) {
		int amount = 0;
		amount += ce.getEnchantmentsOnItem(item).size();
		if(Files.CONFIG.getFile().contains("Settings.EnchantmentOptions.IncludeVanillaEnchantments")) {
			if(Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.IncludeVanillaEnchantments")) {
				if(item.hasItemMeta()) {
					if(item.getItemMeta().hasEnchants()) {
						amount = +item.getItemMeta().getEnchants().size();
					}
				}
			}
		}
		return amount;
	}
	
	public static int getPercent(String Argument, ItemStack item, List<String> Msg) {
		List<String> lore = item.getItemMeta().getLore();
		String arg = "100";
		for(String oLine : Msg) {
			oLine = Methods.color(oLine).toLowerCase();
			if(oLine.contains(Argument.toLowerCase())) {
				String[] b = oLine.split(Argument.toLowerCase());
				for(String iline : lore) {
					boolean toggle = false;// Checks to make sure the lore is the same.
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
				}
			}
		}
		int percent = 100;
		if(isInt(arg)) {
			percent = Integer.parseInt(arg);
		}
		return percent;
	}
	
	public static boolean hasArgument(String Argument, List<String> Msg) {
		for(String l : Msg) {
			l = Methods.color(l).toLowerCase();
			if(l.contains(Argument.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean randomPicker(int max) {
		Random number = random;
		if(max <= 0) {
			return true;
		}
		int chance = 1 + number.nextInt(max);
		return chance == 1;
	}
	
	public static boolean randomPicker(int min, int max) {
		if(max <= min || max <= 0) {
			return true;
		}
		int chance = 1 + random.nextInt(max);
		return chance >= 1 && chance <= min;
	}
	
	public static Integer percentPick(int max, int min) {
		Random i = random;
		if(max == min) {
			return max;
		}else {
			return min + i.nextInt(max - min);
		}
	}
	
	public static boolean isInventoryFull(Player player) {
		return player.getInventory().firstEmpty() == -1;
	}
	
	public static List<LivingEntity> getNearbyLivingEntities(Location loc, double radius, Entity entity) {
		List<Entity> out = entity.getNearbyEntities(radius, radius, radius);
		List<LivingEntity> entities = new ArrayList<>();
		for(Entity en : out) {
			if(en instanceof LivingEntity) {
				entities.add((LivingEntity) en);
			}
		}
		return entities;
	}
	
	public static List<Entity> getNearbyEntitiess(Location loc, double radius, Entity entity) {
		return entity.getNearbyEntities(radius, radius, radius);
	}
	
	public static void fireWork(Location loc, List<Color> colors) {
		fireWork(loc, new ArrayList<>(colors));
	}
	
	public static void fireWork(Location loc, ArrayList<Color> colors) {
		Firework fw = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fm = fw.getFireworkMeta();
		fm.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE)
		.withColor(colors)
		.trail(false)
		.flicker(false)
		.build());
		fm.setPower(0);
		fw.setFireworkMeta(fm);
		FireworkDamageAPI.addFirework(fw);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ce.getPlugin(), fw :: detonate, 2);
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
			case "YELLOW":
				return Color.YELLOW;
			default:
				return Color.WHITE;
		}
	}
	
	public static Enchantment getEnchantment(String enchantmentName) {
		HashMap<String, String> enchantments = getEnchantments();
		for(Enchantment enchantment : Enchantment.values()) {
			if(isV1_13_Up) {
				if(enchantment.getKey().getKey().equalsIgnoreCase(enchantmentName)) {//MC 1.13+ has the correct names.
					return enchantment;
				}
			}else {
				if(enchantment.getName().equalsIgnoreCase(enchantmentName) ||
				(enchantments.get(enchantment.getName()) != null &&
				enchantments.get(enchantment.getName()).equalsIgnoreCase(enchantmentName))) {
					return enchantment;
				}
			}
		}
		return null;
	}
	
	public static HashMap<String, String> getEnchantments() {
		HashMap<String, String> enchantments = new HashMap<>();
		enchantments.put("ARROW_DAMAGE", "Power");
		enchantments.put("ARROW_FIRE", "Flame");
		enchantments.put("ARROW_INFINITE", "Infinity");
		enchantments.put("ARROW_KNOCKBACK", "Punch");
		enchantments.put("DAMAGE_ALL", "Sharpness");
		enchantments.put("DAMAGE_ARTHROPODS", "Bane_Of_Arthropods");
		enchantments.put("DAMAGE_UNDEAD", "Smite");
		enchantments.put("DEPTH_STRIDER", "Depth_Strider");
		enchantments.put("DIG_SPEED", "Efficiency");
		enchantments.put("DURABILITY", "Unbreaking");
		enchantments.put("FIRE_ASPECT", "Fire_Aspect");
		enchantments.put("KNOCKBACK", "KnockBack");
		enchantments.put("LOOT_BONUS_BLOCKS", "Fortune");
		enchantments.put("LOOT_BONUS_MOBS", "Looting");
		enchantments.put("LUCK", "Luck_Of_The_Sea");
		enchantments.put("LURE", "Lure");
		enchantments.put("OXYGEN", "Respiration");
		enchantments.put("PROTECTION_ENVIRONMENTAL", "Protection");
		enchantments.put("PROTECTION_EXPLOSIONS", "Blast_Protection");
		enchantments.put("PROTECTION_FALL", "Feather_Falling");
		enchantments.put("PROTECTION_FIRE", "Fire_Protection");
		enchantments.put("PROTECTION_PROJECTILE", "Projectile_Protection");
		enchantments.put("SILK_TOUCH", "Silk_Touch");
		enchantments.put("THORNS", "Thorns");
		enchantments.put("WATER_WORKER", "Aqua_Affinity");
		enchantments.put("BINDING_CURSE", "Curse_Of_Binding");
		enchantments.put("MENDING", "Mending");
		enchantments.put("FROST_WALKER", "Frost_Walker");
		enchantments.put("VANISHING_CURSE", "Curse_Of_Vanishing");
		return enchantments;
	}
	
	public static void removeDurability(ItemStack item, Player player) {
		if(item.hasItemMeta()) {
			try {
				if(item.getItemMeta().isUnbreakable()) {
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
	
	public static void explode(Entity player) {
		if(Version.getCurrentVersion().isNewer(Version.v1_8_R3)) {
			player.getLocation().getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 200);
			player.getLocation().getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 30, .4F, .5F, .4F);
			player.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation(), 2);
		}else {
			ParticleEffect.FLAME.display(0, 0, 0, 1, 200, player.getLocation().add(0, 1, 0), 100);
			ParticleEffect.CLOUD.display(.4F, .5F, .4F, 1, 30, player.getLocation().add(0, 1, 0), 100);
			ParticleEffect.EXPLOSION_HUGE.display(0, 0, 0, 0, 2, player.getLocation().add(0, 1, 0), 100);
		}
		player.getWorld().playSound(player.getLocation(), ce.getSound("ENTITY_GENERIC_EXPLODE", "EXPLODE"), 1, 1);
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
		if(Version.getCurrentVersion().isNewer(Version.v1_8_R3)) {
			arrow.getLocation().getWorld().spawnParticle(Particle.FLAME, arrow.getLocation(), 200);
			arrow.getLocation().getWorld().spawnParticle(Particle.CLOUD, arrow.getLocation(), 30, .4F, .5F, .4F);
			arrow.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, arrow.getLocation(), 2);
		}else {
			ParticleEffect.FLAME.display(0, 0, 0, 1, 200, arrow.getLocation().add(0, 1, 0), 100);
			ParticleEffect.CLOUD.display(.4F, .5F, .4F, 1, 30, arrow.getLocation().add(0, 1, 0), 100);
			ParticleEffect.EXPLOSION_HUGE.display(0, 0, 0, 0, 2, arrow.getLocation().add(0, 1, 0), 100);
		}
		player.getWorld().playSound(player.getLocation(), ce.getSound("ENTITY_GENERIC_EXPLODE", "EXPLODE"), 1, 1);
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
	
	public static ItemBuilder getRandomPaneColor() {
		boolean newMaterial = ce.useNewMaterial();
		List<String> colors = Arrays.asList(
		newMaterial ? "WHITE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:0",// 0
		newMaterial ? "ORANGE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:1",// 1
		newMaterial ? "MAGENTA_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:2",// 2
		newMaterial ? "LIGHT_BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:3",// 3
		newMaterial ? "YELLOW_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:4",// 4
		newMaterial ? "LIME_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:5",// 5
		newMaterial ? "PINK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:6",// 6
		newMaterial ? "GRAY_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:7",// 7
		//Skipped 8 due to it being basically invisible in a GUI.
		newMaterial ? "CYAN_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:9",// 9
		newMaterial ? "PURPLE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:10",// 10
		newMaterial ? "BLUE_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:11",// 11
		newMaterial ? "BROWN_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:12",// 12
		newMaterial ? "GREEN_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:13",// 13
		newMaterial ? "RED_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:14",// 14
		newMaterial ? "BLACK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:15");// 15
		return new ItemBuilder().setMaterial(colors.get(random.nextInt(colors.size())));
	}
	
}
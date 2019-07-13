package me.badbones69.crazyenchantments.commands;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.enums.Dust;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.enums.Scrolls;
import me.badbones69.crazyenchantments.api.objects.CEBook;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.Category;
import me.badbones69.crazyenchantments.api.objects.EnchantmentType;
import me.badbones69.crazyenchantments.controllers.ProtectionCrystal;
import me.badbones69.crazyenchantments.controllers.Scrambler;
import me.badbones69.crazyenchantments.controllers.ShopControler;
import me.badbones69.crazyenchantments.enchantments.Boots;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CECommand implements CommandExecutor {
	
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	private FileManager fileManager = FileManager.getInstance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
		if(args.length == 0) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
				return true;
			}
			if(!Methods.hasPermission(sender, "gui", true)) {
				return true;
			}
			ShopControler.openGUI((Player) sender);
			return true;
		}else {
			if(args[0].equalsIgnoreCase("help")) {
				if(!Methods.hasPermission(sender, "access", true)) {
					return true;
				}
				sender.sendMessage(Methods.color("&2&l&nCrazy Enchantments"));
				sender.sendMessage(Methods.color("&b/ce - &9Opens the GUI."));
				sender.sendMessage(Methods.color("&b/Tinker - &9Opens up the Tinkerer."));
				sender.sendMessage(Methods.color("&b/BlackSmith - &9Opens up the Black Smith."));
				sender.sendMessage(Methods.color("&b/GKitz [Kit] [Player] - &9Open the GKitz GUI or get a GKit."));
				sender.sendMessage(Methods.color("&b/GKitz Reset <Kit> [Player] - &9Reset a players gkit cooldown."));
				sender.sendMessage(Methods.color("&b/ce Help - &9Shows all ce Commands."));
				sender.sendMessage(Methods.color("&b/ce Debug - &9Does a small debug for some errors."));
				sender.sendMessage(Methods.color("&b/ce Info [Enchantment] - &9Shows info on all enchantments."));
				sender.sendMessage(Methods.color("&b/ce Reload - &9Reloads the Config.yml."));
				sender.sendMessage(Methods.color("&b/ce Remove <Enchantment> - &9Removes an enchantment from the item in your hand."));
				sender.sendMessage(Methods.color("&b/ce Add <Enchantment> [LvL] - &9Adds an enchantment to the item in your hand."));
				sender.sendMessage(Methods.color("&b/ce Spawn <Enchantment/Category> [(Level:#/Min-Max)/World:<World>/X:#/Y:#/Z:#] - &9Drops an enchantment book where you tell it to."));
				sender.sendMessage(Methods.color("&b/ce Scroll <Black/White/Transmog> [Amount] [Player] - &9Gives a player scrolls."));
				sender.sendMessage(Methods.color("&b/ce Crystal [Amount] [Player] - &9Gives a player Protection Crystal."));
				sender.sendMessage(Methods.color("&b/ce Scrambler [Amount] [Player] - &9Gives a player Scramblers."));
				sender.sendMessage(Methods.color("&b/ce Dust <Success/Destroy/Mystery> [Amount] [Player] [Percent] - &9Give a player a some Magical Dust."));
				sender.sendMessage(Methods.color("&b/ce Book <Enchantment> [Lvl/Min-Max] [Amount] [Player] - &9Gives a player a Enchantment Book."));
				sender.sendMessage(Methods.color("&b/ce LostBook <Category> [Amount] [Player] - &9Gives a player a Lost Book."));
				return true;
			}
			if(args[0].equalsIgnoreCase("reload")) {
				if(!Methods.hasPermission(sender, "reload", true)) {
					return true;
				}
				Files.CONFIG.relaodFile();
				Files.ENCHANTMENTS.relaodFile();
				Files.MESSAGES.relaodFile();
				Files.SIGNS.relaodFile();
				Files.TINKER.relaodFile();
				Files.BLOCKLIST.relaodFile();
				Files.GKITZ.relaodFile();
				Files.DATA.relaodFile();
				fileManager.setup(ce.getPlugin());
				ce.load();
				Boots.onStart();
				for(Player player : Bukkit.getOnlinePlayers()) {
					ce.unloadCEPlayer(player);
					ce.loadCEPlayer(player);
				}
				sender.sendMessage(Messages.CONFIG_RELOAD.getMessage());
				return true;
			}
			if(args[0].equalsIgnoreCase("debug")) {
				if(!Methods.hasPermission(sender, "debug", true)) {
					return true;
				}
				ArrayList<String> brokenEnchantments = new ArrayList<>();
				for(CEnchantments enchantment : CEnchantments.values()) {
					if(!Files.ENCHANTMENTS.getFile().contains("Enchantments." + enchantment.getName())) {
						brokenEnchantments.add(enchantment.getName());
					}
				}
				if(brokenEnchantments.isEmpty()) {
					sender.sendMessage(Methods.getPrefix("&aAll enchantments are loaded."));
				}else {
					int i = 1;
					sender.sendMessage(Methods.getPrefix("&cBroken Enchantments:"));
					for(String broke : brokenEnchantments) {
						sender.sendMessage(Methods.color("&c#" + i + ": &6" + broke));
						i++;
					}
					sender.sendMessage(Methods.getPrefix("&7These enchantments are broken due to one of the following reasons:"));
					sender.sendMessage(Methods.color("&7- &cMissing from the Enchantments.yml"));
					sender.sendMessage(Methods.color("&7- &c<Enchantment Name>: option was changed"));
					sender.sendMessage(Methods.color("&7- &cYaml format has been broken."));
				}
				return true;
			}
			if(args[0].equalsIgnoreCase("fix")) {
				if(!Methods.hasPermission(sender, "fix", true)) {
					return true;
				}
				ArrayList<CEnchantments> brokenEnchantments = new ArrayList<>();
				YamlFile file = Files.ENCHANTMENTS.getFile();
				for(CEnchantments enchantment : CEnchantments.values()) {
					if(!file.contains("Enchantments." + enchantment.getName())) {
						brokenEnchantments.add(enchantment);
					}
				}
				sender.sendMessage(Methods.color("&7Fixed a total of " + brokenEnchantments.size() + " enchantments."));
				for(CEnchantments enchantment : brokenEnchantments) {
					String path = "Enchantments." + enchantment.getName();
					file.set(path + ".Enabled", true);
					file.set(path + ".Name", enchantment.getName());
					file.set(path + ".Color", "&7");
					file.set(path + ".BookColor", "&b&l");
					file.set(path + ".MaxPower", 1);
					file.set(path + ".Enchantment-Type", enchantment.getType().getName());
					file.set(path + ".Info.Name", "&e&l" + enchantment.getName() + " &7(&bI&7)");
					file.set(path + ".Info.Description", Arrays.asList("Line 1", "Line 2"));
					List<String> categories = new ArrayList<>();
					ce.getCategories().forEach(category -> categories.add(category.getName()));
					file.set(path + ".Categories", categories);
					Files.ENCHANTMENTS.saveFile();
				}
				return true;
			}
			if(args[0].equalsIgnoreCase("info")) {
				if(!Methods.hasPermission(sender, "info", true)) {
					return true;
				}
				if(args.length == 1) {
					if(!(sender instanceof Player)) {
						sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
						return true;
					}
					ce.getInfoMenuManager().openInfoMenu((Player) sender);
					return true;
				}else {
					for(EnchantmentType enchantmentType : ce.getInfoMenuManager().getEnchantmentTypes()) {
						if(args[1].equalsIgnoreCase(enchantmentType.getName())) {
							ce.getInfoMenuManager().openInfoMenu((Player) sender, enchantmentType);
							return true;
						}
					}
					for(CEnchantment enchantment : ce.getRegisteredEnchantments()) {
						if(enchantment.getName().equalsIgnoreCase(args[1]) || enchantment.getCustomName().equalsIgnoreCase(args[1])) {
							sender.sendMessage(enchantment.getInfoName());
							for(String m : enchantment.getInfoDescription()) sender.sendMessage(Methods.color(m));
							return true;
						}
					}
					sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
					return true;
				}
			}
			if(args[0].equalsIgnoreCase("spawn")) {// /ce Spawn <Enchantment> [Level:#/World:<World>/X:#/Y:#/Z:#]
				if(!Methods.hasPermission(sender, "spawn", true)) {
					return true;
				}
				if(args.length >= 2) {
					CEnchantment enchantment = ce.getEnchantmentFromName(args[1]);
					Category category = ce.getCategory(args[1]);
					Location loc = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
					int level = 1;
					if(enchantment == null) {
						if(category == null) {
							sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
							return true;
						}
					}
					if(sender instanceof Player) {
						loc = ((Player) sender).getLocation();
					}
					for(String arg : args) {
						arg = arg.toLowerCase();
						if(arg.startsWith("level:")) {
							arg = arg.replaceAll("level:", "");
							if(Methods.isInt(arg)) {
								level = Integer.parseInt(arg);
							}else if(arg.contains("-")) {
								level = Methods.getRandomNumber(arg);
							}
						}
						if(arg.startsWith("world:")) {
							arg = arg.replaceAll("world:", "");
							if(Bukkit.getWorld(arg) != null) {
								loc.setWorld(Bukkit.getWorld(arg));
							}
						}
						if(arg.startsWith("x:")) {
							arg = arg.replaceAll("x:", "");
							if(Methods.isInt(arg)) {
								loc.setX(Integer.parseInt(arg));
							}
						}
						if(arg.startsWith("y:")) {
							arg = arg.replaceAll("y:", "");
							if(Methods.isInt(arg)) {
								loc.setY(Integer.parseInt(arg));
							}
						}
						if(arg.startsWith("z:")) {
							arg = arg.replaceAll("z:", "");
							if(Methods.isInt(arg)) {
								loc.setZ(Integer.parseInt(arg));
							}
						}
					}
					loc.getWorld().dropItemNaturally(loc, category == null ? new CEBook(enchantment, level).buildBook() : category.getLostBook().getLostBook(category).build());
					HashMap<String, String> placeholders = new HashMap<>();
					placeholders.put("%world%", loc.getWorld().getName());
					placeholders.put("%x%", loc.getBlockX() + "");
					placeholders.put("%y%", loc.getBlockY() + "");
					placeholders.put("%z%", loc.getBlockZ() + "");
					sender.sendMessage(Messages.SPAWNED_BOOK.getMessage(placeholders));
					return true;
				}
				sender.sendMessage(Methods.getPrefix() + Methods.color("&c/ce Spawn <Enchantment/Category> [(Level:#/Min-Max)/World:<World>/X:#/Y:#/Z:#]"));
				return true;
			}
			if(args[0].equalsIgnoreCase("lostbook") || args[0].equalsIgnoreCase("LB")) {// /ce LostBook <Category> [Amount] [Player]
				if(!Methods.hasPermission(sender, "lostbook", true)) {
					return true;
				}
				if(args.length >= 2) {// /ce LostBook <Category> [Amount] [Player]
					if(args.length <= 3) {
						if(!(sender instanceof Player)) {
							sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
							return true;
						}
					}
					int amount = 1;
					if(args.length >= 3) {
						if(!Methods.isInt(args[2])) {
							sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
							.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2]));
							return true;
						}
						amount = Integer.parseInt(args[2]);
					}
					Player player;
					if(args.length >= 4) {
						if(!Methods.isPlayerOnline(args[3], sender)) {
							return true;
						}
						player = Methods.getPlayer(args[3]);
					}else {
						player = (Player) sender;
					}
					Category category = ce.getCategory(args[1]);
					if(category != null) {
						if(Methods.isInventoryFull(player)) {
							player.getWorld().dropItemNaturally(player.getLocation(), category.getLostBook().getLostBook(category, amount).build());
						}else {
							player.getInventory().addItem(category.getLostBook().getLostBook(category, amount).build());
						}
						return true;
					}
					HashMap<String, String> placeholders = new HashMap<>();
					placeholders.put("%category%", category.getName());
					sender.sendMessage(Messages.NOT_A_CATEGORY.getMessage(placeholders));
					return true;
				}
				sender.sendMessage(Methods.getPrefix() + Methods.color("&c/ce LostBook <Category> [Amount] [Player]"));
				return true;
			}
			if(args[0].equalsIgnoreCase("scrambler") || args[0].equalsIgnoreCase("S")) {// /ce Scrambler [Amount] [Player]
				if(!Methods.hasPermission(sender, "scrambler", true)) {
					return true;
				}
				int amount = 1;
				if(args.length <= 2) {
					if(!(sender instanceof Player)) {
						sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
						return true;
					}
				}
				if(args.length >= 2) {
					if(!Methods.isInt(args[1])) {
						sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
						.replaceAll("%Arg%", args[1]).replaceAll("%arg%", args[1]));
						return true;
					}
					amount = Integer.parseInt(args[1]);
				}
				Player player;
				if(args.length >= 3) {
					if(!Methods.isPlayerOnline(args[2], sender)) {
						return true;
					}
					player = Methods.getPlayer(args[2]);
				}else {
					player = (Player) sender;
				}
				if(Methods.isInventoryFull(player)) {
					sender.sendMessage(Messages.INVENTORY_FULL.getMessage());
					return true;
				}
				player.getInventory().addItem(Scrambler.getScramblers(amount));
				HashMap<String, String> placeholders = new HashMap<>();
				placeholders.put("%amount%", amount + "");
				placeholders.put("%player%", player.getName());
				sender.sendMessage(Messages.GIVE_SCRAMBLER_CRYSTAL.getMessage(placeholders));
				player.sendMessage(Messages.GET_SCRAMBLER.getMessage(placeholders));
				return true;
			}
			if(args[0].equalsIgnoreCase("crystal") || args[0].equalsIgnoreCase("C")) {// /ce Crystal [Amount] [Player]
				if(!Methods.hasPermission(sender, "crystal", true)) {
					return true;
				}
				int amount = 1;
				if(args.length <= 2) {
					if(!(sender instanceof Player)) {
						sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
						return true;
					}
				}
				if(args.length >= 2) {
					if(!Methods.isInt(args[1])) {
						sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
						.replaceAll("%Arg%", args[1]).replaceAll("%arg%", args[1]));
						return true;
					}
					amount = Integer.parseInt(args[1]);
				}
				Player player;
				if(args.length >= 3) {
					if(!Methods.isPlayerOnline(args[2], sender)) {
						return true;
					}
					player = Methods.getPlayer(args[2]);
				}else {
					player = (Player) sender;
				}
				if(Methods.isInventoryFull(player)) {
					sender.sendMessage(Messages.INVENTORY_FULL.getMessage());
					return true;
				}
				player.getInventory().addItem(ProtectionCrystal.getCrystals(amount));
				HashMap<String, String> placeholders = new HashMap<>();
				placeholders.put("%amount%", amount + "");
				placeholders.put("%player%", player.getName());
				sender.sendMessage(Messages.GIVE_PROTECTION_CRYSTAL.getMessage(placeholders));
				player.sendMessage(Messages.GET_PROTECTION_CRYSTAL.getMessage(placeholders));
				return true;
			}
			if(args[0].equalsIgnoreCase("dust")) {// /ce Dust <Success/Destroy/Mystery> [Amount] [Player] [Percent]
				if(!Methods.hasPermission(sender, "dust", true)) {
					return true;
				}
				if(args.length >= 2) {
					Player player = Methods.getPlayer(sender.getName());
					int amount = 1;
					int percent = 0;
					if(args.length == 2) {
						if(!(sender instanceof Player)) {
							sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
							return true;
						}
					}
					if(args.length >= 3) {
						if(!Methods.isInt(args[2])) {
							sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
							.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2]));
							return true;
						}
						amount = Integer.parseInt(args[2]);
					}
					if(args.length >= 4) {
						if(!Methods.isPlayerOnline(args[3], sender)) {
							return true;
						}
						player = Methods.getPlayer(args[3]);
					}else {
						if(!(sender instanceof Player)) {
							sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
							return true;
						}
					}
					if(args.length >= 5) {
						if(!Methods.isInt(args[4])) {
							sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
							.replaceAll("%Arg%", args[4]).replaceAll("%arg%", args[4]));
							return true;
						}
						percent = Integer.parseInt(args[4]);
					}
					for(Dust dust : Dust.values()) {
						if(dust.getKnownNames().contains(args[1].toLowerCase())) {
							if(args.length >= 5) {
								player.getInventory().addItem(dust.getDust(percent, amount));
							}else {
								player.getInventory().addItem(dust.getDust(amount));
							}
							HashMap<String, String> placeholders = new HashMap<>();
							placeholders.put("%amount%", amount + "");
							placeholders.put("%player%", player.getName());
							Messages message;
							switch(dust) {
								case SUCCESS_DUST:
									player.sendMessage(Messages.GET_SUCCESS_DUST.getMessage(placeholders));
									sender.sendMessage(Messages.GIVE_SUCCESS_DUST.getMessage(placeholders));
									break;
								case DESTROY_DUST:
									player.sendMessage(Messages.GET_DESTROY_DUST.getMessage(placeholders));
									sender.sendMessage(Messages.GIVE_DESTROY_DUST.getMessage(placeholders));
									break;
								case MYSTERY_DUST:
									player.sendMessage(Messages.GET_MYSTERY_DUST.getMessage(placeholders));
									sender.sendMessage(Messages.GIVE_MYSTERY_DUST.getMessage(placeholders));
									break;
							}
							return true;
						}
					}
				}
				sender.sendMessage(Methods.getPrefix() + Methods.color("&c/ce Dust <Success/Destroy/Mystery> <Amount> [Player] [Percent]"));
				return true;
			}
			if(args[0].equalsIgnoreCase("scroll")) {// /ce Scroll <Scroll> [Amount] [Player]
				if(!Methods.hasPermission(sender, "scroll", true)) {
					return true;
				}
				if(args.length >= 2) {
					int i = 1;
					String name = sender.getName();
					if(args.length >= 3) {
						if(!Methods.isInt(args[2])) {
							sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
							.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2]));
							return true;
						}
						i = Integer.parseInt(args[2]);
					}
					if(args.length >= 4) {
						name = args[3];
						if(!Methods.isPlayerOnline(name, sender)) {
							return true;
						}
					}else {
						if(!(sender instanceof Player)) {
							sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
							return true;
						}
					}
					for(Scrolls scroll : Scrolls.values()) {
						if(scroll.getKnownNames().contains(args[1].toLowerCase())) {
							Methods.getPlayer(name).getInventory().addItem(scroll.getScroll(i));
							return true;
						}
					}
				}
				sender.sendMessage(Methods.getPrefix() + Methods.color("&c/ce Scroll <White/Black/Transmog> [Amount] [Player]"));
				return true;
			}
			if(args[0].equalsIgnoreCase("remove")) {
				if(!(sender instanceof Player)) {
					sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
					return true;
				}
				if(args.length != 2) {
					sender.sendMessage(Methods.getPrefix() + Methods.color("&c/ce Remove <Enchantment>"));
					return true;
				}
				Player player = (Player) sender;
				if(!Methods.hasPermission(sender, "remove", true)) {
					return true;
				}
				Enchantment vanillaEnchantment = Methods.getEnchantment(args[1]);
				CEnchantment ceEnchantment = ce.getEnchantmentFromName(args[1]);
				boolean isVanilla = vanillaEnchantment != null;
				if(vanillaEnchantment == null && ceEnchantment == null) {
					sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
					return true;
				}
				if(Methods.getItemInHand(player).getType() == Material.AIR) {
					sender.sendMessage(Messages.DOESNT_HAVE_ITEM_IN_HAND.getMessage());
					return true;
				}
				ItemStack item = Methods.getItemInHand(player);
				if(isVanilla) {
					ItemStack it = Methods.getItemInHand(player).clone();
					it.removeEnchantment(vanillaEnchantment);
					Methods.setItemInHand(player, it);
					return true;
				}else {
					if(ce.hasEnchantment(item, ceEnchantment)) {
						Methods.setItemInHand(player, ce.removeEnchantment(item, ceEnchantment));
						HashMap<String, String> placeholders = new HashMap<>();
						placeholders.put("%enchantment%", ceEnchantment.getCustomName());
						player.sendMessage(Messages.REMOVED_ENCHANTMENT.getMessage(placeholders));
						return true;
					}
				}
				HashMap<String, String> placeholders = new HashMap<>();
				placeholders.put("%enchantment%", args[1]);
				sender.sendMessage(Messages.DOESNT_HAVE_ENCHANTMENT.getMessage(placeholders));
				return true;
			}
			if(args[0].equalsIgnoreCase("add")) {
				if(!(sender instanceof Player)) {
					sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
					return true;
				}
				if(args.length <= 1) {
					sender.sendMessage(Methods.getPrefix() + Methods.color("&c/ce Add <Enchantment> [LvL]"));
					return true;
				}
				Player player = (Player) sender;
				if(!Methods.hasPermission(sender, "add", true)) {
					return true;
				}
				String level = "1";
				if(args.length >= 3) {
					if(!Methods.isInt(args[2])) {
						sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
						.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2]));
						return true;
					}
					level = args[2];
				}
				Enchantment vanillaEnchantment = Methods.getEnchantment(args[1]);
				CEnchantment ceEnchantment = ce.getEnchantmentFromName(args[1]);
				boolean isVanilla = vanillaEnchantment != null;
				if(vanillaEnchantment == null && ceEnchantment == null) {
					sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
					return true;
				}
				if(Methods.getItemInHand(player).getType() == Material.AIR) {
					sender.sendMessage(Messages.DOESNT_HAVE_ITEM_IN_HAND.getMessage());
					return true;
				}
				if(isVanilla) {
					ItemStack item = Methods.getItemInHand(player).clone();
					item.addUnsafeEnchantment(vanillaEnchantment, Integer.parseInt(level));
					Methods.setItemInHand(player, item);
				}else {
					Methods.setItemInHand(player, ce.addEnchantment(Methods.getItemInHand(player), ceEnchantment, Integer.parseInt(level)));
				}
				return true;
			}
			if(args[0].equalsIgnoreCase("book")) {// /ce Book <Enchantment> [Lvl] [Amount] [Player]
				if(args.length <= 1) {
					sender.sendMessage(Methods.getPrefix() + Methods.color("&c/ce Book <Enchantment> [Lvl] [Amount] [Player]"));
					return true;
				}
				if(args.length <= 2) {
					if(!(sender instanceof Player)) {
						sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
						return true;
					}
				}
				if(!Methods.hasPermission(sender, "book", true)) {
					return true;
				}
				CEnchantment enchantment = ce.getEnchantmentFromName(args[1]);
				int lvl = 1;
				int amount = 1;
				Player player = Methods.getPlayer(sender.getName());
				if(args.length >= 3) {
					if(Methods.isInt(args[2])) {
						lvl = Integer.parseInt(args[2]);
					}else if(args[2].contains("-")) {
						lvl = Methods.getRandomNumber(args[2]);
					}else {
						sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
						.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2]));
						return true;
					}
				}
				if(args.length >= 4) {
					if(!Methods.isInt(args[3])) {
						sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
						.replaceAll("%Arg%", args[3]).replaceAll("%arg%", args[3]));
						return true;
					}
					amount = Integer.parseInt(args[3]);
				}
				if(args.length >= 5) {
					if(!Methods.isPlayerOnline(args[4], sender)) {
						return true;
					}
					player = Methods.getPlayer(args[4]);
				}
				if(enchantment == null) {
					sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
					return true;
				}
				HashMap<String, String> placeholders = new HashMap<>();
				placeholders.put("%player%", player.getName());
				sender.sendMessage(Messages.SEND_ENCHANTMENT_BOOK.getMessage(placeholders));
				player.getInventory().addItem(new CEBook(enchantment, lvl, amount).buildBook());
				return true;
			}
		}
		sender.sendMessage(Methods.getPrefix() + Methods.color("&cDo /ce Help for more info."));
		return false;
	}
	
}
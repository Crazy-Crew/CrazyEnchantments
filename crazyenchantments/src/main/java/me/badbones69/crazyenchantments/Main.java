package me.badbones69.crazyenchantments;

import me.badbones69.crazyenchantments.api.*;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.events.ArmorListener;
import me.badbones69.crazyenchantments.api.events.AuraListener;
import me.badbones69.crazyenchantments.controlers.*;
import me.badbones69.crazyenchantments.enchantments.*;
import me.badbones69.crazyenchantments.multisupport.*;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

	public static CrazyEnchantments CE = CrazyEnchantments.getInstance();
	public static SettingsManager settings = SettingsManager.getInstance();
	public static CustomEnchantments CustomE = CustomEnchantments.getInstance();

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		settings.setup(this);
		Methods.hasUpdate();
		Boots.onStart();
		CEnchantments.load();
		DataStorage.load();
		CustomE.update();
		CurrencyAPI.loadCurrency();
		for(Player player : Bukkit.getOnlinePlayers()) {
			CE.loadCEPlayer(player);
			if(settings.getConfig().contains("Settings.Reset-Players-Max-Health")) {
				if(settings.getConfig().getBoolean("Settings.Reset-Players-Max-Health")) {
					player.setMaxHealth(20);
				}
			}
		}
		PluginManager pm = Bukkit.getServer().getPluginManager();
		//==========================================================================\\
		pm.registerEvents(this, this);
		pm.registerEvents(new ShopControler(), this);
		pm.registerEvents(new InfoGUIControl(), this);
		pm.registerEvents(new GKitzControler(), this);
		pm.registerEvents(new LostBook(), this);
		pm.registerEvents(new EnchantmentControl(), this);
		pm.registerEvents(new SignControl(), this);
		pm.registerEvents(new DustControl(), this);
		pm.registerEvents(new Tinkerer(), this);
		pm.registerEvents(new AuraListener(), this);
		pm.registerEvents(new ScrollControl(), this);
		pm.registerEvents(new BlackSmith(), this);
		pm.registerEvents(new ArmorListener(), this);
		pm.registerEvents(new ProtectionCrystal(), this);
		pm.registerEvents(new Scrambler(), this);
		pm.registerEvents(new CustomEnchantments(), this);
		pm.registerEvents(new CommandChecker(), this);
		try {
			if(Version.getCurrentVersion().comparedTo(Version.v1_11_R1) >= 0) {
				pm.registerEvents(new FireworkDamageAPI(this), this);
			}
		}catch(Exception e) {
		}
		//==========================================================================\\
		pm.registerEvents(new Bows(), this);
		pm.registerEvents(new Axes(), this);
		pm.registerEvents(new Tools(), this);
		pm.registerEvents(new Helmets(), this);
		pm.registerEvents(new PickAxes(), this);
		pm.registerEvents(new Boots(), this);
		pm.registerEvents(new Armor(), this);
		pm.registerEvents(new Swords(), this);
		if(SupportedPlugins.AAC.isPluginLoaded()) {
			pm.registerEvents(new AACSupport(), this);
		}
		if(SupportedPlugins.SILK_SPAWNERS.isPluginLoaded()) {
			pm.registerEvents(new SilkSpawners(), this);
		}
		if(SupportedPlugins.STACK_MOB.isPluginLoaded()) {
			pm.registerEvents(new StackMobSupport(), this);
		}
		if(SupportedPlugins.DAKATA.isPluginLoaded()) {
			pm.registerEvents(new DakataAntiCheatSupport(), this);
		}
		//==========================================================================\\
		try {
			if(settings.getConfig().contains("Settings.Update-Checker")) {
				new MCUpdate(this, true).checkUpdate(settings.getConfig().getBoolean("Settings.Update-Checker"));
			}else {
				new MCUpdate(this, true);
			}
		}catch(IOException e) {
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				for(CEPlayer player : CE.getCEPlayers()) {
					CE.backupCEPlayer(player);
				}
				//				if(settings.getConfig().contains("Settings.Player-Info-Backup-Message")) {
				//					if(settings.getConfig().getBoolean("Settings.Player-Info-Backup-Message")) {
				//						Bukkit.getLogger().log(Level.INFO, "[Crazy Enchantments]>> All player data has been backed up. Next back up is in 5 minutes.");
				//					}
				//				}else {
				//					Bukkit.getLogger().log(Level.INFO, "[Crazy Enchantments]>> All player data has been backed up. Next back up is in 5 minutes.");
				//				}
			}
		}.runTaskTimerAsynchronously(this, 5 * 20 * 60, 5 * 20 * 60);
	}

	@Override
	public void onDisable() {
		Armor.removeAllies();
		for(Player player : Bukkit.getOnlinePlayers()) {
			CE.unloadCEPlayer(player);
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
		FileConfiguration config = settings.getConfig();
		FileConfiguration msg = settings.getMessages();
		if(commandLable.equalsIgnoreCase("BlackSmith") || commandLable.equalsIgnoreCase("BSmith") || commandLable.equalsIgnoreCase("BlackS") || commandLable.equalsIgnoreCase("BS")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
				return true;
			}
			if(!Methods.hasPermission(sender, "blacksmith", true)) return true;
			Player player = (Player) sender;
			BlackSmith.openBlackSmith(player);
			return true;
		}
		if(commandLable.equalsIgnoreCase("Tinkerer") || commandLable.equalsIgnoreCase("Tinker")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
				return true;
			}
			if(!Methods.hasPermission(sender, "tinker", true)) return true;
			Player player = (Player) sender;
			Tinkerer.openTinker(player);
			return true;
		}
		if(commandLable.equalsIgnoreCase("CE") || commandLable.equalsIgnoreCase("CrazyEnchantments") || commandLable.equalsIgnoreCase("enchanter")) {
			if(args.length == 0) {
				if(!(sender instanceof Player)) {
					sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
					return true;
				}
				Player player = (Player) sender;
				if(!Methods.hasPermission(sender, "access", true)) return true;
				ShopControler.openGUI(player);
				return true;
			}
			if(args.length >= 1) {
				if(args[0].equalsIgnoreCase("Help")) {
					if(!Methods.hasPermission(sender, "access", true)) return true;
					sender.sendMessage(Methods.color("&2&l&nCrazy Enchantments"));
					sender.sendMessage(Methods.color("&b/CE - &9Opens the GUI."));
					sender.sendMessage(Methods.color("&b/Tinker - &9Opens up the Tinkerer."));
					sender.sendMessage(Methods.color("&b/BlackSmith - &9Opens up the Black Smith."));
					sender.sendMessage(Methods.color("&b/GKitz [Kit] [Player] - &9Open the GKitz GUI or get a GKit."));
					sender.sendMessage(Methods.color("&b/GKitz Reset <Kit> [Player] - &9Reset a players gkit cooldown."));
					sender.sendMessage(Methods.color("&b/CE Help - &9Shows all CE Commands."));
					sender.sendMessage(Methods.color("&b/CE Info [Enchantment] - &9Shows info on all Enchantmnets."));
					sender.sendMessage(Methods.color("&b/CE Reload - &9Reloads the Config.yml."));
					sender.sendMessage(Methods.color("&b/CE Remove <Enchantment> - &9Removes an enchantment from the item in your hand."));
					sender.sendMessage(Methods.color("&b/CE Add <Enchantment> [LvL] - &9Adds an enchantment to the item in your hand."));
					sender.sendMessage(Methods.color("&b/CE Spawn <Enchantment/Category> [(Level:#/Min-Max)/World:<World>/X:#/Y:#/Z:#] - &9Drops an enchantment book where you tell it to."));
					sender.sendMessage(Methods.color("&b/CE Scroll <Black/White/Transmog> [Amount] [Player] - &9Gives a player scrolls."));
					sender.sendMessage(Methods.color("&b/CE Crystal [Amount] [Player] - &9Gives a player Protection Crystal."));
					sender.sendMessage(Methods.color("&b/CE Scrambler [Amount] [Player] - &9Gives a player Scramblers."));
					sender.sendMessage(Methods.color("&b/CE Dust <Success/Destroy/Mystery> [Amount] [Player] [Percent] - &9Give a player a some Magical Dust."));
					sender.sendMessage(Methods.color("&b/CE Book <Enchantment> [Lvl/Min-Max] [Amount] [Player] - &9Gives a player a Enchantment Book."));
					sender.sendMessage(Methods.color("&b/CE LostBook <Category> [Amount] [Player] - &9Gives a player a Lost Book."));
					return true;
				}
				if(args[0].equalsIgnoreCase("Reload")) {
					if(!Methods.hasPermission(sender, "reload", true)) return true;
					settings.reloadConfig();
					settings.reloadEnchs();
					settings.reloadMessages();
					settings.reloadCustomEnchs();
					settings.reloadSigns();
					settings.reloadTinker();
					settings.reloadBlockList();
					settings.reloadGKitz();
					settings.reloadData();
					settings.setup(this);
					CEnchantments.load();
					DataStorage.load();
					CustomE.update();
					Boots.onStart();
					for(Player player : Bukkit.getOnlinePlayers()) {
						CE.unloadCEPlayer(player);
						CE.loadCEPlayer(player);
					}
					sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Config-Reload")));
					return true;
				}
				if(args[0].equalsIgnoreCase("Info")) {
					if(args.length == 1) {
						if(!(sender instanceof Player)) {
							sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
							return true;
						}
						Player player = (Player) sender;
						if(!Methods.hasPermission(sender, "info", true)) return true;
						InfoGUIControl.openInfo(player);
						return true;
					}else {
						for(InfoType type : InfoType.getTypes()) {
							if(args[1].equalsIgnoreCase(type.getName())) {
								InfoGUIControl.openInfo((Player) sender, type);
								return true;
							}
						}
						String ench = args[1];
						for(CEnchantments en : CE.getEnchantments()) {
							if(en.getName().equalsIgnoreCase(ench) || en.getCustomName().equalsIgnoreCase(ench)) {
								String name = settings.getEnchs().getString("Enchantments." + en.getName() + ".Info.Name");
								List<String> desc = settings.getEnchs().getStringList("Enchantments." + en.getName() + ".Info.Description");
								sender.sendMessage(Methods.color(name));
								for(String m : desc)
									sender.sendMessage(Methods.color(m));
								return true;
							}
						}
						for(String enchantment : CustomE.getEnchantments()) {
							if(enchantment.equalsIgnoreCase(ench) || CustomE.getCustomName(enchantment).equalsIgnoreCase(ench)) {
								String name = settings.getCustomEnchs().getString("Enchantments." + enchantment + ".Info.Name");
								List<String> desc = settings.getCustomEnchs().getStringList("Enchantments." + enchantment + ".Info.Description");
								sender.sendMessage(Methods.color(name));
								for(String m : desc)
									sender.sendMessage(Methods.color(m));
								return true;
							}
						}
						sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-An-Enchantment")));
						return true;
					}
				}
				if(args[0].equalsIgnoreCase("Spawn")) {// /CE Spawn <Enchantment> [Level:#/World:<World>/X:#/Y:#/Z:#]
					if(!Methods.hasPermission(sender, "spawn", true)) return true;
					if(args.length >= 2) {
						CEnchantments enchant = null;
						String cEnchant = null;
						String category = null;
						Boolean isCustom = false;
						Location loc = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
						int level = 1;
						if(CE.isEnchantment(args[1])) {
							enchant = CE.getFromName(args[1]);
							isCustom = false;
						}else if(CustomE.isEnchantment(args[1])) {
							cEnchant = CustomE.getFromName(args[1]);
							isCustom = true;
						}else {
							for(String cat : CE.getCategories()) {
								if(cat.equalsIgnoreCase(args[1])) {
									category = cat;
								}
							}
							if(category == null) {
								sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-An-Enchantment")));
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
						ItemStack book;
						if(isCustom) {
							book = new CustomEBook(cEnchant, level).buildBook();
						}else if(category == null) {
							book = new CEBook(enchant, level).buildBook();
						}else {
							book = LostBook.getLostBook(category, 1);
						}
						loc.getWorld().dropItemNaturally(loc, book);
						sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Spawned-Book").replaceAll("%World%", loc.getWorld().getName()).replaceAll("%world%", loc.getWorld().getName()).replaceAll("%X%", loc.getBlockX() + "").replaceAll("%x%", loc.getBlockX() + "").replaceAll("%Y%", loc.getBlockY() + "").replaceAll("%y%", loc.getBlockY() + "").replaceAll("%Z%", loc.getBlockZ() + "").replaceAll("%z%", loc.getBlockZ() + "")));
						return true;
					}
					sender.sendMessage(Methods.getPrefix() + Methods.color("&c/CE Spawn <Enchantment/Category> [(Level:#/Min-Max)/World:<World>/X:#/Y:#/Z:#]"));
					return true;
				}
				if(args[0].equalsIgnoreCase("LostBook") || args[0].equalsIgnoreCase("LB")) {// /CE LostBook <Category> [Amount] [Player]
					if(!Methods.hasPermission(sender, "lostbook", true)) return true;
					if(args.length >= 2) {// /CE LostBook <Category> [Amount] [Player]
						if(args.length <= 3) {
							if(!(sender instanceof Player)) {
								sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
								return true;
							}
						}
						int amount = 1;
						if(args.length >= 3) {
							if(!Methods.isInt(args[2])) {
								sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-A-Number").replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
								return true;
							}
							amount = Integer.parseInt(args[2]);
						}
						Player player = null;
						if(args.length >= 4) {
							if(!Methods.isOnline(args[3], sender)) return true;
							player = Methods.getPlayer(args[3]);
						}else {
							player = (Player) sender;
						}
						String cat = args[1];
						for(String C : config.getConfigurationSection("Categories").getKeys(false)) {
							if(cat.equalsIgnoreCase(C)) {
								cat = C;
								if(Methods.isInvFull(player)) {
									player.getWorld().dropItemNaturally(player.getLocation(), LostBook.getLostBook(cat, amount));
								}else {
									player.getInventory().addItem(LostBook.getLostBook(cat, amount));
								}
								return true;
							}
						}
						sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-A-Category").replaceAll("%Category%", cat).replaceAll("%category%", cat)));
						return true;
					}
					sender.sendMessage(Methods.getPrefix() + Methods.color("&c/CE LostBook <Category> [Amount] [Player]"));
					return true;
				}
				if(args[0].equalsIgnoreCase("Scrambler") || args[0].equalsIgnoreCase("S")) {// /CE Scrambler [Amount] [Player]
					if(!Methods.hasPermission(sender, "scrambler", true)) return true;
					int amount = 1;
					if(args.length <= 2) {
						if(!(sender instanceof Player)) {
							sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
							return true;
						}
					}
					if(args.length >= 2) {
						if(!Methods.isInt(args[1])) {
							sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-A-Number").replaceAll("%Arg%", args[1]).replaceAll("%arg%", args[1])));
							return true;
						}
						amount = Integer.parseInt(args[1]);
					}
					Player player = null;
					if(args.length >= 3) {
						if(!Methods.isOnline(args[2], sender)) return true;
						player = Methods.getPlayer(args[2]);
					}else {
						player = (Player) sender;
					}
					if(Methods.isInvFull(player)) {
						sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Inventory-Full")));
						return true;
					}
					player.getInventory().addItem(Scrambler.getScramblers(amount));
					sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Give-Scrambler-Crystal").replaceAll("%Amount%", amount + "").replaceAll("%amount%", amount + "").replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
					player.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Get-Scrambler-Crystal").replaceAll("%Amount%", amount + "").replaceAll("%amount%", amount + "")));
					return true;
				}
				if(args[0].equalsIgnoreCase("Crystal") || args[0].equalsIgnoreCase("C")) {// /CE Crystal [Amount] [Player]
					if(!Methods.hasPermission(sender, "crystal", true)) return true;
					int amount = 1;
					if(args.length <= 2) {
						if(!(sender instanceof Player)) {
							sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
							return true;
						}
					}
					if(args.length >= 2) {
						if(!Methods.isInt(args[1])) {
							sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-A-Number").replaceAll("%Arg%", args[1]).replaceAll("%arg%", args[1])));
							return true;
						}
						amount = Integer.parseInt(args[1]);
					}
					Player player = null;
					if(args.length >= 3) {
						if(!Methods.isOnline(args[2], sender)) return true;
						player = Methods.getPlayer(args[2]);
					}else {
						player = (Player) sender;
					}
					if(Methods.isInvFull(player)) {
						sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Inventory-Full")));
						return true;
					}
					player.getInventory().addItem(ProtectionCrystal.getCrystals(amount));
					sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Give-Protection-Crystal").replaceAll("%Amount%", amount + "").replaceAll("%amount%", amount + "").replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
					player.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Get-Protection-Crystal").replaceAll("%Amount%", amount + "").replaceAll("%amount%", amount + "")));
					return true;
				}
				if(args[0].equalsIgnoreCase("Dust")) {// /CE Dust <Success/Destroy/Mystery> [Amount] [Player] [Percent]
					if(!Methods.hasPermission(sender, "dust", true)) return true;
					if(args.length >= 2) {
						Player player = Methods.getPlayer(sender.getName());
						int amount = 1;
						int percent = 0;
						if(args.length == 2) {
							if(!(sender instanceof Player)) {
								sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
								return true;
							}
						}
						if(args.length >= 3) {
							if(!Methods.isInt(args[2])) {
								sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-A-Number").replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
								return true;
							}
							amount = Integer.parseInt(args[2]);
						}
						if(args.length >= 4) {
							if(!Methods.isOnline(args[3], sender)) return true;
							player = Methods.getPlayer(args[3]);
						}else {
							if(!(sender instanceof Player)) {
								sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
								return true;
							}
						}
						if(args.length >= 5) {
							if(!Methods.isInt(args[4])) {
								sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-A-Number").replaceAll("%Arg%", args[4]).replaceAll("%arg%", args[4])));
								return true;
							}
							percent = Integer.parseInt(args[4]);
						}
						if(args[1].equalsIgnoreCase("Success") || args[1].equalsIgnoreCase("S")) {
							if(args.length >= 5) {
								player.getInventory().addItem(DustControl.getDust("SuccessDust", amount, percent));
							}else {
								player.getInventory().addItem(DustControl.getDust("SuccessDust", amount));
							}
							player.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Get-Success-Dust").replaceAll("%Amount%", amount + "").replaceAll("%amount%", amount + "")));
							sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Give-Success-Dust").replaceAll("%Amount%", amount + "").replaceAll("%amount%", amount + "").replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
							return true;
						}
						if(args[1].equalsIgnoreCase("Destroy") || args[1].equalsIgnoreCase("D")) {
							if(args.length >= 5) {
								player.getInventory().addItem(DustControl.getDust("DestroyDust", amount, percent));
							}else {
								player.getInventory().addItem(DustControl.getDust("DestroyDust", amount));
							}
							player.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Get-Destroy-Dust").replaceAll("%Amount%", amount + "").replaceAll("%amount%", amount + "")));
							sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Give-Destroy-Dust").replaceAll("%Amount%", amount + "").replaceAll("%amount%", amount + "").replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
							return true;
						}
						if(args[1].equalsIgnoreCase("Mystery") || args[1].equalsIgnoreCase("M")) {
							if(args.length >= 5) {
								player.getInventory().addItem(DustControl.getDust("MysteryDust", amount, percent));
							}else {
								player.getInventory().addItem(DustControl.getMysteryDust(amount));
							}
							player.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Get-Mystery-Dust").replaceAll("%Amount%", amount + "").replaceAll("%amount%", amount + "")));
							sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Give-Mystery-Dust").replaceAll("%Amount%", amount + "").replaceAll("%amount%", amount + "").replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
							return true;
						}
					}
					sender.sendMessage(Methods.getPrefix() + Methods.color("&c/CE Dust <Success/Destroy/Mystery> <Amount> [Player] [Percent]"));
					return true;
				}
				if(args[0].equalsIgnoreCase("Scroll")) {// /CE Scroll <Scroll> [Amount] [Player]
					if(!Methods.hasPermission(sender, "scroll", true)) return true;
					if(args.length >= 2) {
						int i = 1;
						String name = sender.getName();
						if(args.length >= 3) {
							if(!Methods.isInt(args[2])) {
								sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-A-Number").replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
								return true;
							}
							i = Integer.parseInt(args[2]);
						}
						if(args.length >= 4) {
							name = args[3];
							if(!Methods.isOnline(name, sender)) return true;
						}else {
							if(!(sender instanceof Player)) {
								sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
								return true;
							}
						}
						if(args[1].equalsIgnoreCase("B") || args[1].equalsIgnoreCase("Black") || args[1].equalsIgnoreCase("BlackScroll")) {
							Methods.getPlayer(name).getInventory().addItem(ScrollControl.getBlackScroll(i));
							return true;
						}
						if(args[1].equalsIgnoreCase("W") || args[1].equalsIgnoreCase("White") || args[1].equalsIgnoreCase("WhiteScroll")) {
							Methods.getPlayer(name).getInventory().addItem(ScrollControl.getWhiteScroll(i));
							return true;
						}
						if(args[1].equalsIgnoreCase("T") || args[1].equalsIgnoreCase("Transmog") || args[1].equalsIgnoreCase("Transmogscroll")) {
							Methods.getPlayer(name).getInventory().addItem(ScrollControl.getTransmogScroll(i));
							return true;
						}
					}
					sender.sendMessage(Methods.getPrefix() + Methods.color("&c/CE Scroll <White/Black/Transmog> [Amount] [Player]"));
					return true;
				}
				if(args[0].equalsIgnoreCase("Remove")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
						return true;
					}
					if(args.length != 2) {
						sender.sendMessage(Methods.getPrefix() + Methods.color("&c/CE Remove <Enchantment>"));
						return true;
					}
					Player player = (Player) sender;
					if(!Methods.hasPermission(sender, "remove", true)) return true;
					boolean T = false;
					boolean isVanilla = false;
					boolean customEnchant = false;
					String ench = "Glowing";
					Enchantment enchant = Enchantment.LUCK;
					CEnchantments en = null;
					for(Enchantment enc : Enchantment.values()) {
						if(args[1].equalsIgnoreCase(enc.getName()) || args[1].equalsIgnoreCase(Methods.getEnchantmentName(enc))) {
							T = true;
							isVanilla = true;
							enchant = enc;
						}
					}
					for(CEnchantments En : CE.getEnchantments()) {
						if(En.getCustomName().equalsIgnoreCase(args[1])) {
							en = En;
							T = true;
						}
					}
					for(String i : CustomE.getEnchantments()) {
						if(CustomE.getCustomName(i).equalsIgnoreCase(args[1])) {
							ench = i;
							customEnchant = true;
							T = true;
						}
					}
					if(!T) {
						sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-An-Enchantment")));
						return true;
					}
					if(Methods.getItemInHand(player).getType() == Material.AIR) {
						sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Doesnt-Have-Item-In-Hand")));
						return true;
					}
					ItemStack item = Methods.getItemInHand(player);
					String enchantment = args[1];
					if(isVanilla) {
						ItemStack it = Methods.getItemInHand(player).clone();
						it.removeEnchantment(enchant);
						Methods.setItemInHand(player, it);
						return true;
					}else if(customEnchant) {
						if(CustomE.hasEnchantment(item, ench)) {
							Methods.setItemInHand(player, CustomE.removeEnchantment(item, ench));
							String m = Methods.getPrefix() + Methods.color(msg.getString("Messages.Remove-Enchantment").replaceAll("%Enchantment%", CustomE.getCustomName(ench)).replaceAll("%enchantment%", CustomE.getCustomName(ench)));
							player.sendMessage(m);
							return true;
						}
					}else {
						if(CE.hasEnchantment(item, en)) {
							Methods.setItemInHand(player, CE.removeEnchantment(item, en));
							String m = Methods.getPrefix() + Methods.color(msg.getString("Messages.Remove-Enchantment").replaceAll("%Enchantment%", en.getCustomName()).replaceAll("%enchantment%", en.getCustomName()));
							player.sendMessage(m);
							return true;
						}
					}
					sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Doesnt-Have-Enchantment").replaceAll("%Enchantment%", enchantment).replaceAll("%enchantment%", enchantment)));
					return true;
				}
				if(args[0].equalsIgnoreCase("Add")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
						return true;
					}
					if(args.length <= 1) {
						sender.sendMessage(Methods.getPrefix() + Methods.color("&c/CE Add <Enchantment> [LvL]"));
						return true;
					}
					Player player = (Player) sender;
					if(!Methods.hasPermission(sender, "add", true)) return true;
					boolean T = false;
					boolean isVanilla = false;
					Enchantment enchant = Enchantment.LUCK;
					boolean customEnchant = false;
					String ench = "Glowing";
					CEnchantments en = null;
					String lvl = "1";
					if(args.length >= 3) {
						if(!Methods.isInt(args[2])) {
							sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-A-Number").replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
							return true;
						}
						lvl = args[2];
					}
					for(Enchantment enc : Enchantment.values()) {
						if(args[1].equalsIgnoreCase(enc.getName()) || args[1].equalsIgnoreCase(Methods.getEnchantmentName(enc))) {
							T = true;
							isVanilla = true;
							enchant = enc;
						}
					}
					for(CEnchantments i : CE.getEnchantments()) {
						if(i.getCustomName().equalsIgnoreCase(args[1])) {
							T = true;
							en = i;
						}
					}
					for(String i : CustomE.getEnchantments()) {
						if(CustomE.getCustomName(i).equalsIgnoreCase(args[1])) {
							ench = i;
							customEnchant = true;
							T = true;
						}
					}
					if(!T) {
						sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-An-Enchantment")));
						return true;
					}
					if(Methods.getItemInHand(player).getType() == Material.AIR) {
						sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Doesnt-Have-Item-In-Hand")));
						return false;
					}
					if(isVanilla) {
						ItemStack it = Methods.getItemInHand(player).clone();
						it.addUnsafeEnchantment(enchant, Integer.parseInt(lvl));
						Methods.setItemInHand(player, it);
					}else if(customEnchant) {
						if(Version.getCurrentVersion().isOlder(Version.v1_11_R1)) {
							Methods.setItemInHand(player, Methods.addGlow(CustomE.addEnchantment(Methods.getItemInHand(player), ench, Integer.parseInt(lvl))));
						}else {
							Methods.setItemInHand(player, CustomE.addEnchantment(Methods.getItemInHand(player), ench, Integer.parseInt(lvl)));
						}
					}else {
						if(Version.getCurrentVersion().isOlder(Version.v1_11_R1)) {
							Methods.setItemInHand(player, Methods.addGlow(CE.addEnchantment(Methods.getItemInHand(player), en, Integer.parseInt(lvl))));
						}else {
							Methods.setItemInHand(player, CE.addEnchantment(Methods.getItemInHand(player), en, Integer.parseInt(lvl)));
						}
					}
					return true;
				}
				if(args[0].equalsIgnoreCase("Book")) {// /CE Book <Enchantment> [Lvl] [Amount] [Player]
					if(args.length <= 1) {
						sender.sendMessage(Methods.getPrefix() + Methods.color("&c/CE Book <Enchantment> [Lvl] [Amount] [Player]"));
						return true;
					}
					if(args.length <= 2) {
						if(!(sender instanceof Player)) {
							sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
							return true;
						}
					}
					if(!Methods.hasPermission(sender, "book", true)) return true;
					String ench = args[1];
					int lvl = 1;
					int amount = 1;
					Player player = Methods.getPlayer(sender.getName());
					if(args.length >= 3) {
						if(Methods.isInt(args[2])) {
							lvl = Integer.parseInt(args[2]);
						}else if(args[2].contains("-")) {
							lvl = Methods.getRandomNumber(args[2]);
						}else {
							sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-A-Number").replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
							return true;
						}
					}
					if(args.length >= 4) {
						if(!Methods.isInt(args[3])) {
							sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-A-Number").replaceAll("%Arg%", args[3]).replaceAll("%arg%", args[3])));
							return true;
						}
						amount = Integer.parseInt(args[3]);
					}
					if(args.length >= 5) {
						if(!Methods.isOnline(args[4], sender)) return true;
						player = Methods.getPlayer(args[4]);
					}
					boolean toggle = false;
					boolean customEnchant = false;
					for(CEnchantments en : CE.getEnchantments()) {
						if(ench.equalsIgnoreCase(en.getName()) || ench.equalsIgnoreCase(en.getCustomName())) {
							ench = en.getName();
							toggle = true;
						}
					}
					for(String i : CustomE.getEnchantments()) {
						if(CustomE.getCustomName(i).equalsIgnoreCase(args[1])) {
							ench = i;
							customEnchant = true;
							toggle = true;
						}
					}
					if(!toggle) {
						sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-An-Enchantment")));
						return true;
					}
					sender.sendMessage(Methods.color(Methods.getPrefix() + msg.getString("Messages.Send-Enchantment-Book").replace("%Player%", player.getName()).replace("%player%", player.getName())));
					int Smax = config.getInt("Settings.BlackScroll.SuccessChance.Max");
					int Smin = config.getInt("Settings.BlackScroll.SuccessChance.Min");
					int Dmax = config.getInt("Settings.BlackScroll.DestroyChance.Max");
					int Dmin = config.getInt("Settings.BlackScroll.DestroyChance.Min");
					if(customEnchant) {
						CustomEBook book = new CustomEBook(ench, lvl, amount);
						book.setDestoryRate(Methods.percentPick(Dmax, Dmin));
						book.setSuccessRate(Methods.percentPick(Smax, Smin));
						player.getInventory().addItem(book.buildBook());
					}else {
						CEBook book = new CEBook(CE.getFromName(ench), lvl, amount);
						book.setDestoryRate(Methods.percentPick(Dmax, Dmin));
						book.setSuccessRate(Methods.percentPick(Smax, Smin));
						player.getInventory().addItem(book.buildBook());
					}
					return true;
				}
			}
			sender.sendMessage(Methods.getPrefix() + Methods.color("&cDo /CE Help for more info."));
			return true;
		}
		if(commandLable.equalsIgnoreCase("gkitz") || commandLable.equalsIgnoreCase("gkits") || commandLable.equalsIgnoreCase("gkit")) {
			if(args.length == 0) {
				if(!(sender instanceof Player)) {
					sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
					return true;
				}
				if(!Methods.hasPermission(sender, "access", true)) return true;
				GKitzControler.openGUI((Player) sender);
				return true;
			}
			if(args.length >= 1) {
				GKitz kit = null;
				Player player = null;
				if(args[0].equalsIgnoreCase("reset")) {// /GKitz Reset <Kit> [Player]
					if(!Methods.hasPermission(sender, "reset", true)) return true;
					if(args.length >= 2) {
						if(CE.getGKitFromName(args[1]) != null) {
							kit = CE.getGKitFromName(args[1]);
						}else {
							sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-A-GKit").replaceAll("%Kit%", args[1]).replaceAll("%kit%", args[1])));
							return true;
						}
						if(args.length >= 3) {
							if(!Methods.isOnline(args[2], sender)) {
								return true;
							}else {
								player = Methods.getPlayer(args[2]);
							}
						}else {
							if(!(sender instanceof Player)) {
								sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
								return true;
							}else {
								player = (Player) sender;
							}
						}
						CE.getCEPlayer(player).removeCooldown(kit);
						sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Reset-GKit").replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName()).replaceAll("%GKit%", kit.getName()).replaceAll("%gkit%", kit.getName())));
						return true;
					}else {
						sender.sendMessage(Methods.getPrefix() + Methods.color("&c/GKitz Reset <Kit> [Player]"));
						return true;
					}
				}else {
					if(CE.getGKitFromName(args[0]) != null) {// /GKitz [Kit] [Player]
						kit = CE.getGKitFromName(args[0]);
					}else {
						sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-A-GKit").replaceAll("%Kit%", args[0]).replaceAll("%kit%", args[0])));
						return true;
					}
					if(args.length >= 2) {
						if(!Methods.hasPermission(sender, "gkitz", true)) {
							return true;
						}else {
							if(!Methods.isOnline(args[1], sender)) {
								return true;
							}else {
								player = Methods.getPlayer(args[1]);
							}
						}
					}else {
						if(!(sender instanceof Player)) {
							sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
							return true;
						}else {
							player = (Player) sender;
						}
					}
					CEPlayer p = CE.getCEPlayer(player);
					String name = kit.getDisplayItem().getItemMeta().getDisplayName();
					if(p.hasGkitPermission(kit) || args.length >= 2) {
						if(p.canUseGKit(kit) || sender.hasPermission("crazyenchantments.admin")) {
							p.giveGKit(kit);
							player.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Received-GKit").replaceAll("%Kit%", name).replaceAll("%kit%", name)));
							if(!player.getName().equalsIgnoreCase(sender.getName())) {
								sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Given-GKit").replaceAll("%Kit%", name).replaceAll("%kit%", name).replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
							}
							if(args.length == 1) {
								p.addCooldown(kit);
							}
						}else {
							sender.sendMessage(Methods.getPrefix() + p.getCooldown(kit).getCooldownLeft(msg.getString("Messages.Still-In-Cooldown")).replaceAll("%Kit%", name).replaceAll("%kit%", name));
							return true;
						}
					}else {
						sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.No-GKit-Permission").replaceAll("%Kit%", kit.getName()).replaceAll("%kit%", kit.getName())));
						return true;
					}
					return true;
				}
			}
			sender.sendMessage(Methods.getPrefix() + Methods.color("&c/GKitz [Kit] [Player]"));
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		final Player player = e.getPlayer();
		CE.loadCEPlayer(player);
		if(settings.getConfig().contains("Settings.Reset-Players-Max-Health")) {
			if(settings.getConfig().getBoolean("Settings.Reset-Players-Max-Health")) {
				player.setMaxHealth(20);
			}
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if(player.getName().equals("BadBones69")) {
					player.sendMessage(Methods.getPrefix() + Methods.color("&7This server is running your Crazy Enchantments Plugin. " + "&7It is running version &av" + Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments").getDescription().getVersion() + "&7."));
				}
				if(player.isOp()) {
					if(settings.getConfig().contains("Settings.Update-Checker")) {
						if(settings.getConfig().getBoolean("Settings.Update-Checker")) {
							Methods.hasUpdate(player);
						}
					}else {
						Methods.hasUpdate(player);
					}
				}
			}
		}.runTaskLaterAsynchronously(this, 20);
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		CE.unloadCEPlayer(e.getPlayer());
	}

}
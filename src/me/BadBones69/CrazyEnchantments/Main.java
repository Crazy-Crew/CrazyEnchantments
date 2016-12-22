package me.BadBones69.CrazyEnchantments;

import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.BadBones69.CrazyEnchantments.API.CEBook;
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.CrazyEnchantments;
import me.BadBones69.CrazyEnchantments.API.CustomEBook;
import me.BadBones69.CrazyEnchantments.API.CustomEnchantments;
import me.BadBones69.CrazyEnchantments.API.GKitz;
import me.BadBones69.CrazyEnchantments.API.Events.ArmorListener;
import me.BadBones69.CrazyEnchantments.API.Events.AuraListener;
import me.BadBones69.CrazyEnchantments.Controlers.BlackSmith;
import me.BadBones69.CrazyEnchantments.Controlers.DustControl;
import me.BadBones69.CrazyEnchantments.Controlers.EnchantmentControl;
import me.BadBones69.CrazyEnchantments.Controlers.FireworkDamageAPI;
import me.BadBones69.CrazyEnchantments.Controlers.GKitzGUI;
import me.BadBones69.CrazyEnchantments.Controlers.LostBook;
import me.BadBones69.CrazyEnchantments.Controlers.ProtectionCrystal;
import me.BadBones69.CrazyEnchantments.Controlers.ScrollControl;
import me.BadBones69.CrazyEnchantments.Controlers.ShopGUI;
import me.BadBones69.CrazyEnchantments.Controlers.SignControl;
import me.BadBones69.CrazyEnchantments.Controlers.Tinkerer;
import me.BadBones69.CrazyEnchantments.Enchantments.Armor;
import me.BadBones69.CrazyEnchantments.Enchantments.Axes;
import me.BadBones69.CrazyEnchantments.Enchantments.Boots;
import me.BadBones69.CrazyEnchantments.Enchantments.Bows;
import me.BadBones69.CrazyEnchantments.Enchantments.Helmets;
import me.BadBones69.CrazyEnchantments.Enchantments.PickAxes;
import me.BadBones69.CrazyEnchantments.Enchantments.Swords;
import me.BadBones69.CrazyEnchantments.Enchantments.Tools;
import me.BadBones69.CrazyEnchantments.multisupport.SilkSpawners;
import me.BadBones69.CrazyEnchantments.multisupport.StackMobSupport;
import me.BadBones69.CrazyEnchantments.multisupport.Support;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Main extends JavaPlugin implements Listener{
	
	public static SettingsManager settings = SettingsManager.getInstance();
	public static Economy econ = null;
	public static EconomyResponse r;
	public static CrazyEnchantments CE = CrazyEnchantments.getInstance();
	public static CustomEnchantments CustomE = CustomEnchantments.getInstance();
	
	@Override
	public void onEnable(){
		PluginManager pm = Bukkit.getServer().getPluginManager();
		//==========================================================================\\
		pm.registerEvents(this, this);
		pm.registerEvents(new ShopGUI(), this);
		pm.registerEvents(new GKitz(), this);
		pm.registerEvents(new GKitzGUI(), this);
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
		pm.registerEvents(new CustomEnchantments(), this);
		pm.registerEvents(new FireworkDamageAPI(this), this);		
		//==========================================================================\\
		pm.registerEvents(new Bows(), this);
		pm.registerEvents(new Axes(), this);
		pm.registerEvents(new Tools(), this);
		pm.registerEvents(new Helmets(), this);
		pm.registerEvents(new PickAxes(), this);
		pm.registerEvents(new Boots(), this);
		pm.registerEvents(new Armor(), this);
		pm.registerEvents(new Swords(), this);
		if(pm.getPlugin("SilkSpawners")!=null){
			pm.registerEvents(new SilkSpawners(), this);
		}
		if(Support.hasStackMob()){
			pm.registerEvents(new StackMobSupport(), this);
		}
		//==========================================================================\\
		settings.setup(this);
		Methods.hasUpdate();
		Boots.onStart();
		CE.load();
		CEnchantments.load();
		CustomE.update();
		GKitz.load();
		if(!setupEconomy()){
	   		saveDefaultConfig();
	    }
		try{
			Metrics metrics = new Metrics(this); metrics.start();
		}catch (IOException e) {
			System.out.println("Error Submitting stats!");
		}
	}
	
	@Override
	public void onDisable(){
		Armor.removeAllies();
		GKitz.unload();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args){
		if(commandLable.equalsIgnoreCase("BlackSmith")||commandLable.equalsIgnoreCase("BSmith")
				||commandLable.equalsIgnoreCase("BlackS")||commandLable.equalsIgnoreCase("BS")){
			if(!(sender instanceof Player)){
				sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Players-Only")));
				return true;
			}
			if(!Methods.hasPermission(sender, "BlackSmith", true))return true;
			Player player = (Player) sender;
			BlackSmith.openBlackSmith(player);
			return true;
		}
		if(commandLable.equalsIgnoreCase("Tinkerer")||commandLable.equalsIgnoreCase("Tinker")){
			if(!(sender instanceof Player)){
				sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Players-Only")));
				return true;
			}
			if(!Methods.hasPermission(sender, "Tinker", true))return true;
			Player player = (Player) sender;
			Tinkerer.openTinker(player);
			return true;
		}
		if(commandLable.equalsIgnoreCase("CE")||commandLable.equalsIgnoreCase("CrazyEnchantments")
				||commandLable.equalsIgnoreCase("Enchanter")){
			if(args.length == 0){
				if(!(sender instanceof Player)){
					sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Players-Only")));
					return true;
				}
				Player player = (Player)sender;
				if(!Methods.hasPermission(sender, "Access", true))return true;
				ShopGUI.openGUI(player);
				return true;
			}
			if(args.length>=1){
				if(args[0].equalsIgnoreCase("Help")){
					if(!Methods.hasPermission(sender, "Access", true))return true;
					sender.sendMessage(Methods.color("&2&l&nCrazy Enchantments"));
					sender.sendMessage(Methods.color("&b/CE - &9Opens the GUI."));
					sender.sendMessage(Methods.color("&b/Tinker - &9Opens up the Tinkerer."));
					sender.sendMessage(Methods.color("&b/BlackSmith - &9Opens up the Black Smith."));
					sender.sendMessage(Methods.color("&b/GKitz [Kit] [Player] - &9Open the GKitz GUI or get a GKit."));
					sender.sendMessage(Methods.color("&b/CE Help - &9Shows all CE Commands."));
					sender.sendMessage(Methods.color("&b/CE Info [Enchantment] - &9Shows info on all Enchantmnets."));
					sender.sendMessage(Methods.color("&b/CE Reload - &9Reloads the Config.yml."));
					sender.sendMessage(Methods.color("&b/CE Remove <Enchantment> - &9Removes an enchantment from the item in your hand."));
					sender.sendMessage(Methods.color("&b/CE Add <Enchantment> [LvL] - &9Adds and enchantment to the item in your hand."));
					sender.sendMessage(Methods.color("&b/CE Scroll <Scroll> [Amount] [Player] - &9Gives a player scrolls."));
					sender.sendMessage(Methods.color("&b/CE Crystal [Amount] [Player] - &9Gives a player Protection Crystal."));
					sender.sendMessage(Methods.color("&b/CE Dust <Success/Destroy> [Amount] [Player] [Percent] - &9Give a player a some Magical Dust."));
					sender.sendMessage(Methods.color("&b/CE Book <Enchantment> [Lvl] [Amount] [Player] - &9Gives a player a Enchantment Book."));
					sender.sendMessage(Methods.color("&b/CE LostBook <Category> [Amount] [Player] - &9Gives a player a Lost Book."));
					return true;
				}
				if(args[0].equalsIgnoreCase("Reload")){
					if(!Methods.hasPermission(sender, "Admin", true))return true;
					settings.reloadConfig();
					settings.reloadEnchs();
					settings.reloadMsg();
					settings.reloadCustomEnchs();
					settings.reloadSigns();
					settings.reloadTinker();
					settings.reloadBlockList();
					settings.reloadGKitz();
					settings.reloadData();
					settings.setup(this);
					CE.load();
					CEnchantments.load();
					CustomE.update();
					Boots.onStart();
					GKitz.load();
					sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Config-Reload")));
					return true;
				}
				if(args[0].equalsIgnoreCase("Info")){
					if(args.length==1){
						if(!(sender instanceof Player)){
							sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Players-Only")));
							return true;
						}
						Player player = (Player)sender;
						if(!Methods.hasPermission(sender, "Info", true))return true;
						ShopGUI.openInfo(player);
						return true;
					}else{
						String ench = args[1];
						for(CEnchantments en : CE.getEnchantments()){
							if(en.getName().equalsIgnoreCase(ench)||en.getCustomName().equalsIgnoreCase(ench)){
								String name = settings.getEnchs().getString("Enchantments."+en.getName()+".Info.Name");
								List<String> desc = settings.getEnchs().getStringList("Enchantments."+en.getName()+".Info.Description");
								sender.sendMessage(Methods.color(name));
								for(String msg : desc)sender.sendMessage(Methods.color(msg));
								return true;
							}
						}
						for(String enchantment : CustomE.getEnchantments()){
							if(enchantment.equalsIgnoreCase(ench)||CustomE.getCustomName(enchantment).equalsIgnoreCase(ench)){
								String name = settings.getCustomEnchs().getString("Enchantments."+enchantment+".Info.Name");
								List<String> desc = settings.getCustomEnchs().getStringList("Enchantments."+enchantment+".Info.Description");
								sender.sendMessage(Methods.color(name));
								for(String msg : desc)sender.sendMessage(Methods.color(msg));
								return true;
							}
						}
						sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Not-An-Enchantment")));
						return true;
					}
				}
				if(args[0].equalsIgnoreCase("LostBook") || args[0].equalsIgnoreCase("LB")){// /CE LostBook <Category> [Amount] [Player]
					if(!Methods.hasPermission(sender, "Admin", true))return true;
					if(args.length>=2){// /CE LostBook <Category> [Amount] [Player]
						if(args.length<=3){
							if(!(sender instanceof Player)){
								sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Players-Only")));
								return true;
							}
						}
						int amount = 1;
						if(args.length>=3){
							if(!Methods.isInt(args[2])){
								sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Not-A-Number")
										.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
								return true;
							}
							amount=Integer.parseInt(args[2]);
						}
						Player player = null;
						if(args.length>=4){
							if(!Methods.isOnline(args[3], sender))return true;
							player=Methods.getPlayer(args[3]);
						}else{
							player = (Player) sender;
						}
						String cat = args[1];
						for(String C : settings.getConfig().getConfigurationSection("Categories").getKeys(false)){
							if(cat.equalsIgnoreCase(C)){
								cat=C;
								if(Methods.isInvFull(player)){
									player.getWorld().dropItemNaturally(player.getLocation(), LostBook.getLostBook(cat, amount));
								}else{
									player.getInventory().addItem(LostBook.getLostBook(cat, amount));
								}
								return true;
							}
						}
						sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Not-A-Category")
								.replaceAll("%Category%", cat).replaceAll("%category%", cat)));
						return true;
					}
					sender.sendMessage(Methods.getPrefix()+Methods.color("&c/CE LostBook <Category> [Amount] [Player]"));
					return true;
				}
				if(args[0].equalsIgnoreCase("Crystal")||args[0].equalsIgnoreCase("C")){// /CE Crystal [Amount] [Player]
					if(!Methods.hasPermission(sender, "Admin", true))return true;
					int amount = 1;
					if(args.length<=2){
						if(!(sender instanceof Player)){
							sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Players-Only")));
							return true;
						}
					}
					if(args.length>=2){
						if(!Methods.isInt(args[1])){
							sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Not-A-Number")
									.replaceAll("%Arg%", args[1]).replaceAll("%arg%", args[1])));
							return true;
						}
						amount=Integer.parseInt(args[1]);
					}
					Player player = null;
					if(args.length>=3){
						if(!Methods.isOnline(args[2], sender))return true;
						player=Methods.getPlayer(args[2]);
					}else{
						player = (Player) sender;
					}
					if(Methods.isInvFull(player)){
						sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Inventory-Full")));
						return true;
					}
					player.getInventory().addItem(ProtectionCrystal.getCrystals(amount));
					sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Give-Protection-Crystal")
							.replaceAll("%Amount%", amount+"").replaceAll("%amount%", amount+"")
							.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
					player.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Get-Protection-Crystal")
							.replaceAll("%Amount%", amount+"").replaceAll("%amount%", amount+"")));
					return true;
				}
				if(args[0].equalsIgnoreCase("Dust")){// /CE Dust <Success/Destroy> [Amount] [Player] [Percent]
					if(!Methods.hasPermission(sender, "Admin", true))return true;
					if(args.length>=2){
						Player player = Methods.getPlayer(sender.getName());
						int amount = 1;
						int percent = 0;
						if(args.length==2){
							if(!(sender instanceof Player)){
								sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Players-Only")));
								return true;
							}
						}
						if(args.length>=3){
							if(!Methods.isInt(args[2])){
								sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Not-A-Number")
										.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
								return true;
							}
							amount = Integer.parseInt(args[2]);
						}
						if(args.length>=4){
							if(!Methods.isOnline(args[3], sender))return true;
							player = Methods.getPlayer(args[3]);
						}
						if(args.length>=5){
							if(!Methods.isInt(args[4])){
								sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Not-A-Number")
										.replaceAll("%Arg%", args[4]).replaceAll("%arg%", args[4])));
								return true;
							}
							percent = Integer.parseInt(args[4]);
						}
						if(args[1].equalsIgnoreCase("Success")||args[1].equalsIgnoreCase("S")){
							if(args.length>=5){
								player.getInventory().addItem(DustControl.getDust("SuccessDust", amount, percent));
							}else{
								player.getInventory().addItem(DustControl.getDust("SuccessDust", amount));
							}
							player.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Get-Success-Dust")
									.replaceAll("%Amount%", amount+"").replaceAll("%amount%", amount+"")));
							sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Give-Success-Dust")
									.replaceAll("%Amount%", amount+"").replaceAll("%amount%", amount+"")
									.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
							return true;
						}
						if(args[1].equalsIgnoreCase("Destroy")||args[1].equalsIgnoreCase("D")){
							if(args.length>=5){
								player.getInventory().addItem(DustControl.getDust("DestroyDust", amount, percent));
							}else{
								player.getInventory().addItem(DustControl.getDust("DestroyDust", amount));
							}
							player.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Get-Destroy-Dust")
									.replaceAll("%Amount%", amount+"").replaceAll("%amount%", amount+"")));
							sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Give-Destroy-Dust")
									.replaceAll("%Amount%", amount+"").replaceAll("%amount%", amount+"")
									.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
							return true;
						}
					}
					sender.sendMessage(Methods.getPrefix()+Methods.color("&c/CE Dust <Success/Destroy> <Amount> [Player] [Percent]"));
					return true;
				}
				if(args[0].equalsIgnoreCase("Scroll")){// /CE Scroll <Scroll> [Amount] [Player]
					if(!Methods.hasPermission(sender, "Admin", true))return true;
					if(args.length >= 2){
						int i = 1;
						String name = sender.getName();
						if(args.length >= 3){
							if(!Methods.isInt(args[2])){
								sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Not-A-Number")
										.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
								return true;
							}
							i = Integer.parseInt(args[2]);
						}
						if(args.length >= 4){
							name = args[3];
							if(!Methods.isOnline(name, sender))return true;
						}else{
							if(!(sender instanceof Player)){
								sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Players-Only")));
								return true;
							}
						}
						if(args[1].equalsIgnoreCase("B") || args[1].equalsIgnoreCase("Black") || args[1].equalsIgnoreCase("BlackScroll")){
							Methods.getPlayer(name).getInventory().addItem(Methods.BlackScroll(i));
							return true;
						}
						if(args[1].equalsIgnoreCase("W") || args[1].equalsIgnoreCase("White") || args[1].equalsIgnoreCase("WhiteScroll")){
							Methods.getPlayer(name).getInventory().addItem(Methods.addWhiteScroll(i));
							return true;
						}
					}
					sender.sendMessage(Methods.getPrefix()+Methods.color("&c/CE Scroll <Scroll> [Amount] [Player]"));
					return true;
				}
				if(args[0].equalsIgnoreCase("Remove")){
					if(!(sender instanceof Player)){
						sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Players-Only")));
						return true;
					}
					if(args.length!=2){
						sender.sendMessage(Methods.getPrefix()+Methods.color("&c/CE Remove <Enchantment>"));
						return true;
					}
					Player player = (Player) sender;
					if(!Methods.hasPermission(sender, "Admin", true))return true;
					boolean T=false;
					boolean customEnchant = false;
					String ench = "Glowing";
					CEnchantments en = null;
					for(CEnchantments En : CE.getEnchantments()){
						if(En.getCustomName().equalsIgnoreCase(args[1])){
							en = En;
							T=true;
						}
					}
					for(String i : CustomE.getEnchantments()){
						if(CustomE.getCustomName(i).equalsIgnoreCase(args[1])){
							ench = i;
							customEnchant = true;
							T = true;
						}
					}
					if(!T){
						sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Not-An-Enchantment")));
						return true;
					}
					if(Methods.getItemInHand(player).getType()==Material.AIR){
						sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Doesnt-Have-Item-In-Hand")));
						return true;
					}
					ItemStack item = Methods.getItemInHand(player);
					String enchantment = args[1];
					if(customEnchant){
						if(CustomE.hasEnchantment(item, ench)){
							Methods.setItemInHand(player, CustomE.removeEnchantment(item, ench));
							String msg = Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Remove-Enchantment")
									.replaceAll("%Enchantment%", CustomE.getCustomName(ench)).replaceAll("%enchantment%", CustomE.getCustomName(ench)));
							player.sendMessage(msg);
							return true;
						}
					}else{
						if(CE.hasEnchantment(item, en)){
							Methods.setItemInHand(player, CE.removeEnchantment(item, en));
							String msg = Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Remove-Enchantment")
									.replaceAll("%Enchantment%", en.getCustomName()).replaceAll("%enchantment%", en.getCustomName()));
							player.sendMessage(msg);
							return true;
						}
					}
					sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Doesnt-Have-Enchantment")
							.replaceAll("%Enchantment%", enchantment).replaceAll("%enchantment%", enchantment)));
					return true;
				}
				if(args[0].equalsIgnoreCase("Add")){
					if(!(sender instanceof Player)){
						sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Players-Only")));
						return true;
					}
					if(args.length<=1){
						sender.sendMessage(Methods.getPrefix()+Methods.color("&c/CE Add <Enchantment> [LvL]"));
						return true;
					}
					Player player = (Player) sender;
					if(!Methods.hasPermission(sender, "Admin", true))return true;
					boolean T = false;
					boolean customEnchant = false;
					String ench = "Glowing";
					CEnchantments en = null;
					String lvl = "1";
					if(args.length>=3){
						if(!Methods.isInt(args[2])){
							sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Not-A-Number")
									.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
							return true;
						}
						lvl = args[2];
					}
					for(CEnchantments i : CE.getEnchantments()){
						if(i.getCustomName().equalsIgnoreCase(args[1])){
							T = true;
							en = i;
						}
					}
					for(String i : CustomE.getEnchantments()){
						if(CustomE.getCustomName(i).equalsIgnoreCase(args[1])){
							ench = i;
							customEnchant = true;
							T = true;
						}
					}
					if(!T){
						sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Not-An-Enchantment")));
						return true;
					}
					if(Methods.getItemInHand(player).getType() == Material.AIR){
						sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Doesnt-Have-Item-In-Hand")));
						return false;
					}
					if(customEnchant){
						Methods.setItemInHand(player, Methods.addGlow(CustomE.addEnchantment(Methods.getItemInHand(player), ench, Integer.parseInt(lvl))));
					}else{
						Methods.setItemInHand(player, Methods.addGlow(CE.addEnchantment(Methods.getItemInHand(player), en, Integer.parseInt(lvl))));
					}
					return true;
				}
				if(args[0].equalsIgnoreCase("Book")){// /CE Book <Enchantment> [Lvl] [Amount] [Player]
					if(args.length<=1){
						sender.sendMessage(Methods.getPrefix()+Methods.color("&c/CE Book <Enchantment> [Lvl] [Amount] [Player]"));
						return true;
					}
					if(args.length<=2){
						if(!(sender instanceof Player)){
							sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Players-Only")));
							return true;
						}
					}
					if(!Methods.hasPermission(sender, "Admin", true))return true;
					String ench = args[1];
					int lvl = 1;
					int amount = 1;
					Player player = Methods.getPlayer(sender.getName());
					if(args.length>=3){
						if(!Methods.isInt(args[2])){
							sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Not-A-Number")
									.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
							return true;
						}
						lvl = Integer.parseInt(args[2]);
					}
					if(args.length>=4){
						if(!Methods.isInt(args[3])){
							sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Not-A-Number")
									.replaceAll("%Arg%", args[3]).replaceAll("%arg%", args[3])));
							return true;
						}
						amount = Integer.parseInt(args[3]);
					}
					if(args.length>=5){
						if(!Methods.isOnline(args[4], sender))return true;
						player = Methods.getPlayer(args[4]);
					}
					boolean toggle = false;
					boolean customEnchant = false;
					for(CEnchantments en : CE.getEnchantments()){
						if(ench.equalsIgnoreCase(en.getCustomName())){
							ench=en.getName();
							toggle=true;
						}
					}
					for(String i : CustomE.getEnchantments()){
						if(CustomE.getCustomName(i).equalsIgnoreCase(args[1])){
							ench = i;
							customEnchant = true;
							toggle = true;
						}
					}
					if(!toggle){
						sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Not-An-Enchantment")));
						return true;
					}
					sender.sendMessage(Methods.color(Methods.getPrefix()+settings.getMsg().getString("Messages.Send-Enchantment-Book").replace("%Player%", player.getName()).replace("%player%", player.getName())));
					int Smax = settings.getConfig().getInt("Settings.BlackScroll.SuccessChance.Max");
					int Smin = settings.getConfig().getInt("Settings.BlackScroll.SuccessChance.Min");
					int Dmax = settings.getConfig().getInt("Settings.BlackScroll.DestroyChance.Max");
					int Dmin = settings.getConfig().getInt("Settings.BlackScroll.DestroyChance.Min");
					if(customEnchant){
						CustomEBook book = new CustomEBook(ench, lvl, amount);
						book.setDestoryRate(Methods.percentPick(Dmax, Dmin));
						book.setSuccessRate(Methods.percentPick(Smax, Smin));
						player.getInventory().addItem(Methods.addGlow(book.buildBook()));
					}else{
						CEBook book = new CEBook(CE.getFromName(ench), lvl, amount);
						book.setDestoryRate(Methods.percentPick(Dmax, Dmin));
						book.setSuccessRate(Methods.percentPick(Smax, Smin));
						player.getInventory().addItem(Methods.addGlow(book.buildBook()));
					}
					return true;
				}
			}
			sender.sendMessage(Methods.getPrefix()+Methods.color("&cDo /CE Help for more info."));
			return true;
		}
		if(commandLable.equalsIgnoreCase("gkitz") || commandLable.equalsIgnoreCase("gkits") ||
				commandLable.equalsIgnoreCase("gkit")){// /GKitz [Kit] [Player]
			if(args.length == 0){
				if(!(sender instanceof Player)){
					sender.sendMessage(Methods.getPrefix()+Methods.color(settings.getMsg().getString("Messages.Players-Only")));
					return true;
				}
				GKitzGUI.openGUI((Player) sender);
				return true;
			}
			if(args.length >= 1){
				String kit = "";
				Player player = null;
				if(GKitz.isGKit(args[0])){
					kit = GKitz.getGKitName(args[0]);
				}else{
					sender.sendMessage(Methods.getPrefix() + Methods.color(settings.getMsg().getString("Messages.Not-A-GKit").replaceAll("%Kit%", args[0]).replaceAll("%kit%", args[0])));
					return true;
				}
				if(args.length >= 2){
					if(!Methods.hasPermission(sender, "Admin", true)){
						return true;
					}else{
						if(!Methods.isOnline(args[1], sender)){
							return true;
						}else{
							player = Methods.getPlayer(args[1]);
						}
					}
				}else{
					if(!(sender instanceof Player)){
						sender.sendMessage(Methods.getPrefix() + Methods.color(settings.getMsg().getString("Messages.Players-Only")));
						return true;
					}else{
						player = (Player) sender;
					}
				}
				if(GKitz.hasGKitPermission(player, kit) || args.length >= 2){
					if(GKitz.canGetGKit(player, kit)){
						GKitz.giveKit(player, kit);
						GKitz.runKitCommands(player, kit);
						player.sendMessage(Methods.getPrefix() + Methods.color(settings.getMsg().getString("Messages.Received-GKit")
								.replaceAll("%Kit%", GKitz.getGKitDisplayName(kit)).replaceAll("%kit%", GKitz.getGKitDisplayName(kit))));
						if(!player.getName().equalsIgnoreCase(sender.getName())){
							sender.sendMessage(Methods.getPrefix() + Methods.color(settings.getMsg().getString("Messages.Given-GKit")
									.replaceAll("%Kit%", GKitz.getGKitDisplayName(kit)).replaceAll("%kit%", GKitz.getGKitDisplayName(kit))
									.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
						}
						if(args.length == 1){
							GKitz.addCooldown(player, kit);
						}
					}else{
						sender.sendMessage(Methods.getPrefix() + GKitz.getCooldownLeft(GKitz.getCooldown(player, kit), settings.getMsg().getString("Messages.Still-In-Cooldown")
								.replaceAll("%Kit%", GKitz.getGKitDisplayName(kit)).replaceAll("%kit%", GKitz.getGKitDisplayName(kit))));
						return true;
					}
				}else{
					sender.sendMessage(Methods.getPrefix() + Methods.color(settings.getMsg().getString("Messages.No-GKit-Permission")
							.replaceAll("%Kit%", kit).replaceAll("%kit%", kit)));
					return true;
				}
				return true;
			}
			sender.sendMessage(Methods.getPrefix()+Methods.color("&c/GKitz [Kit] [Player]"));
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		final Player player = e.getPlayer();
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run() {
				if(player.getName().equals("BadBones69")){
					player.sendMessage(Methods.getPrefix()+Methods.color("&7This server is running your Crazy Enchantments Plugin. "
						+ "&7It is running version &av"+Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments").getDescription().getVersion()+"&7."));
				}
				if(player.isOp()){
					Methods.hasUpdate(player);
				}
			}
		}, 1*20);
	}
	
	private boolean setupEconomy(){
        if (getServer().getPluginManager().getPlugin("Vault") == null){
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null){
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
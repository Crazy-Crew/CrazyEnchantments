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

import ca.thederpygolems.armorequip.ArmorListener;
import me.BadBones69.CrazyEnchantments.API.CEBook;
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.CrazyEnchantments;
import me.BadBones69.CrazyEnchantments.API.CustomEBook;
import me.BadBones69.CrazyEnchantments.API.CustomEnchantments;
import me.BadBones69.CrazyEnchantments.Controlers.BlackSmith;
import me.BadBones69.CrazyEnchantments.Controlers.DustControl;
import me.BadBones69.CrazyEnchantments.Controlers.ProtectionCrystal;
import me.BadBones69.CrazyEnchantments.Controlers.ScrollControl;
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
import me.BadBones69.CrazyEnchantments.MultiSupport.SilkSpawners;
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
		pm.registerEvents(new GUI(), this);
		pm.registerEvents(new Tinkerer(this), this);
		pm.registerEvents(new ECControl(), this);
		pm.registerEvents(new BlackSmith(this), this);
		pm.registerEvents(new SignControl(), this);
		pm.registerEvents(new DustControl(), this);
		pm.registerEvents(new ArmorListener(this), this);
		pm.registerEvents(new ScrollControl(), this);
		pm.registerEvents(new ProtectionCrystal(), this);
		pm.registerEvents(new CustomEnchantments(), this);
		//==========================================================================\\
		pm.registerEvents(new Bows(), this);
		pm.registerEvents(new Axes(), this);
		pm.registerEvents(new Boots(this), this);
		pm.registerEvents(new Tools(), this);
		pm.registerEvents(new Helmets(), this);
		pm.registerEvents(new PickAxes(), this);
		pm.registerEvents(new Armor(this), this);
		pm.registerEvents(new Swords(this), this);
		if(pm.getPlugin("SilkSpawners")!=null){
			pm.registerEvents(new SilkSpawners(), this);
		}
		//==========================================================================\\
		settings.setup(this);
		Api.hasUpdate();
		Boots.onStart();
		CE.load();
		CustomE.update();
		if(!setupEconomy()){
	   		saveDefaultConfig();
	    }
		try{
			Metrics metrics = new Metrics(this); metrics.start();
		}catch (IOException e) {
			System.out.println("Error Submitting stats!");
		}
	}
	public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args){
		if(commandLable.equalsIgnoreCase("BlackSmith")||commandLable.equalsIgnoreCase("BSmith")
				||commandLable.equalsIgnoreCase("BlackS")||commandLable.equalsIgnoreCase("BS")){
			if(!(sender instanceof Player)){
				sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Players-Only")));
				return true;
			}
			if(!Api.hasPermission(sender, "BlackSmith", true))return true;
			Player player = (Player) sender;
			BlackSmith.openBlackSmith(player);
			return true;
		}
		if(commandLable.equalsIgnoreCase("Tinkerer")||commandLable.equalsIgnoreCase("Tinker")){
			if(!(sender instanceof Player)){
				sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Players-Only")));
				return true;
			}
			if(!Api.hasPermission(sender, "Tinker", true))return true;
			Player player = (Player) sender;
			Tinkerer.openTinker(player);
			return true;
		}
		if(commandLable.equalsIgnoreCase("CE")||commandLable.equalsIgnoreCase("CrazyEnchantments")
				||commandLable.equalsIgnoreCase("Enchanter")){
			if(args.length == 0){
				if(!(sender instanceof Player)){
					sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Players-Only")));
					return true;
				}
				Player player = (Player)sender;
				if(!Api.hasPermission(sender, "Access", true))return true;
				GUI.openGUI(player);
				return true;
			}
			if(args.length>=1){
				if(args[0].equalsIgnoreCase("Help")){
					if(!Api.hasPermission(sender, "Access", true))return true;
					sender.sendMessage(Api.color("&2&l&nCrazy Enchantments"));
					sender.sendMessage(Api.color("&b/CE - &9Opens the GUI."));
					sender.sendMessage(Api.color("&b/Tinker - &9Opens up the Tinkerer."));
					sender.sendMessage(Api.color("&b/BlackSmith - &9Opens up the Black Smith."));
					sender.sendMessage(Api.color("&b/CE Help - &9Shows all CE Commands."));
					sender.sendMessage(Api.color("&b/CE Info [Enchantment] - &9Shows info on all Enchantmnets."));
					sender.sendMessage(Api.color("&b/CE Reload - &9Reloads the Config.yml."));
					sender.sendMessage(Api.color("&b/CE Remove <Enchantment> - &9Removes an enchantment from the item in your hand."));
					sender.sendMessage(Api.color("&b/CE Add <Enchantment> [LvL] - &9Adds and enchantment to the item in your hand."));
					sender.sendMessage(Api.color("&b/CE Scroll <Player> <Scroll> <Amount> - &9Gives a player scrolls."));
					sender.sendMessage(Api.color("&b/CE Crystal [Amount] [Player] - &9Gives a player Protection Crystal."));
					sender.sendMessage(Api.color("&b/CE Dust <Success/Destroy> [Amount] [Player] [Percent] - &9Give a player a some Magical Dust."));
					sender.sendMessage(Api.color("&b/CE Book <Enchantment> [Lvl] [Amount] [Player] - &9Gives a player a Enchantment Book."));
					sender.sendMessage(Api.color("&b/CE LostBook <Category> [Amount] [Player] - &9Gives a player a Lost Book."));
					return true;
				}
				if(args[0].equalsIgnoreCase("Reload")){
					if(!Api.hasPermission(sender, "Admin", true))return true;
					settings.reloadConfig();
					settings.reloadEnchs();
					settings.reloadMsg();
					settings.reloadCustomEnchs();
					settings.reloadSigns();
					settings.reloadTinker();
					settings.reloadBlockList();
					settings.setup(this);
					CE.load();
					CustomE.update();
					Boots.onStart();
					sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Config-Reload")));
					return true;
				}
				if(args[0].equalsIgnoreCase("Info")){
					if(args.length==1){
						if(!(sender instanceof Player)){
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Players-Only")));
							return true;
						}
						Player player = (Player)sender;
						if(!Api.hasPermission(sender, "Info", true))return true;
						GUI.openInfo(player);
						return true;
					}else{
						String ench = args[1];
						for(CEnchantments en : CE.getEnchantments()){
							if(en.getName().equalsIgnoreCase(ench)||en.getCustomName().equalsIgnoreCase(ench)){
								String name = settings.getEnchs().getString("Enchantments."+en.getName()+".Info.Name");
								List<String> desc = settings.getEnchs().getStringList("Enchantments."+en.getName()+".Info.Description");
								sender.sendMessage(Api.color(name));
								for(String msg : desc)sender.sendMessage(Api.color(msg));
								return true;
							}
						}
						for(String enchantment : CustomE.getEnchantments()){
							if(enchantment.equalsIgnoreCase(ench)||CustomE.getCustomName(enchantment).equalsIgnoreCase(ench)){
								String name = settings.getCustomEnchs().getString("Enchantments."+enchantment+".Info.Name");
								List<String> desc = settings.getCustomEnchs().getStringList("Enchantments."+enchantment+".Info.Description");
								sender.sendMessage(Api.color(name));
								for(String msg : desc)sender.sendMessage(Api.color(msg));
								return true;
							}
						}
						sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-An-Enchantment")));
						return true;
					}
				}
				if(args[0].equalsIgnoreCase("LostBook")||args[0].equalsIgnoreCase("LB")){// /CE LostBook <Category> [Amount] [Player]
					if(!Api.hasPermission(sender, "Admin", true))return true;
					if(args.length>=2){// /CE LostBook <Category> [Amount] [Player]
						if(args.length<=3){
							if(!(sender instanceof Player)){
								sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Players-Only")));
								return true;
							}
						}
						int amount = 1;
						if(args.length>=3){
							if(!Api.isInt(args[2])){
								sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Number")
										.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
								return true;
							}
							amount=Integer.parseInt(args[2]);
						}
						Player player = null;
						if(args.length>=4){
							if(!Api.isOnline(args[3], sender))return true;
							player=Api.getPlayer(args[3]);
						}else{
							player = (Player) sender;
						}
						String cat = args[1];
						for(String C : settings.getConfig().getConfigurationSection("Categories").getKeys(false)){
							if(cat.equalsIgnoreCase(C)){
								cat=C;
								if(Api.isInvFull(player)){
									player.getWorld().dropItemNaturally(player.getLocation(), Api.getLostBook(cat, amount));
								}else{
									player.getInventory().addItem(Api.getLostBook(cat, amount));
								}
								return true;
							}
						}
						sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Category")
								.replaceAll("%Category%", cat).replaceAll("%category%", cat)));
						return true;
					}
					sender.sendMessage(Api.getPrefix()+Api.color("&c/CE LostBook <Category> [Amount] [Player]"));
					return true;
				}
				if(args[0].equalsIgnoreCase("Crystal")||args[0].equalsIgnoreCase("C")){// /CE Crystal [Amount] [Player]
					if(!Api.hasPermission(sender, "Admin", true))return true;
					int amount = 1;
					if(args.length<=2){
						if(!(sender instanceof Player)){
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Players-Only")));
							return true;
						}
					}
					if(args.length>=2){
						if(!Api.isInt(args[1])){
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Number")
									.replaceAll("%Arg%", args[1]).replaceAll("%arg%", args[1])));
							return true;
						}
						amount=Integer.parseInt(args[1]);
					}
					Player player = null;
					if(args.length>=3){
						if(!Api.isOnline(args[2], sender))return true;
						player=Api.getPlayer(args[2]);
					}else{
						player = (Player) sender;
					}
					if(Api.isInvFull(player)){
						sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Inventory-Full")));
						return true;
					}
					player.getInventory().addItem(ProtectionCrystal.getCrystals(amount));
					sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Give-Protection-Crystal")
							.replaceAll("%Amount%", amount+"").replaceAll("%amount%", amount+"")
							.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
					player.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Get-Protection-Crystal")
							.replaceAll("%Amount%", amount+"").replaceAll("%amount%", amount+"")));
					return true;
				}
				if(args[0].equalsIgnoreCase("Dust")){// /CE Dust <Success/Destroy> [Amount] [Player] [Percent]
					if(!Api.hasPermission(sender, "Admin", true))return true;
					if(args.length>=2){// /CE Dust <Success/Destroy> <Amount> [Player] [Percent]
						Player player = Api.getPlayer(sender.getName());
						int amount = 1;
						int percent = 0;
						if(args.length==2){
							if(!(sender instanceof Player)){
								sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Players-Only")));
								return true;
							}
						}
						if(args.length>=3){
							if(!Api.isInt(args[2])){
								sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Number")
										.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
								return true;
							}
							amount = Integer.parseInt(args[2]);
						}
						if(args.length>=4){
							if(!Api.isOnline(args[3], sender))return true;
							player = Api.getPlayer(args[3]);
						}
						if(args.length>=5){
							if(!Api.isInt(args[4])){
								sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Number")
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
							player.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Get-Success-Dust")
									.replaceAll("%Amount%", amount+"").replaceAll("%amount%", amount+"")));
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Give-Success-Dust")
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
							player.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Get-Destroy-Dust")
									.replaceAll("%Amount%", amount+"").replaceAll("%amount%", amount+"")));
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Give-Destroy-Dust")
									.replaceAll("%Amount%", amount+"").replaceAll("%amount%", amount+"")
									.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
							return true;
						}
					}
					sender.sendMessage(Api.getPrefix()+Api.color("&c/CE Dust <Success/Destroy> <Amount> [Player] [Percent]"));
					return true;
				}
				if(args[0].equalsIgnoreCase("Scroll")){// /CE Scroll <Player> <Scroll> <Amount>
					if(args.length!=4){
						sender.sendMessage(Api.getPrefix()+Api.color("&c/CE Scroll <Player> <Scroll> <Amount>"));
						return true;
					}
					if(!Api.hasPermission(sender, "Admin", true))return true;
					String name = args[1];
					if(!Api.isInt(args[3])){
						sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Number")
								.replaceAll("%Arg%", args[3]).replaceAll("%arg%", args[3])));
						return true;
					}
					int i = Integer.parseInt(args[3]);
					if(!Api.isOnline(name, sender))return true;
					if(args[2].equalsIgnoreCase("Black")||args[2].equalsIgnoreCase("BlackScroll")){
						Api.getPlayer(name).getInventory().addItem(Api.BlackScroll(i));
						return true;
					}
					if(args[2].equalsIgnoreCase("White")||args[2].equalsIgnoreCase("WhiteScroll")){
						Api.getPlayer(name).getInventory().addItem(Api.addWhiteScroll(i));
						return true;
					}
					sender.sendMessage(Api.getPrefix()+Api.color("&c/CE Scroll <Player> <Scroll> <Amount>"));
					return true;
				}
				if(args[0].equalsIgnoreCase("Remove")){
					if(!(sender instanceof Player)){
						sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Players-Only")));
						return true;
					}
					if(args.length!=2){
						sender.sendMessage(Api.getPrefix()+Api.color("&c/CE Remove <Enchantment>"));
						return true;
					}
					Player player = (Player) sender;
					if(!Api.hasPermission(sender, "Admin", true))return true;
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
						sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-An-Enchantment")));
						return true;
					}
					if(Api.getItemInHand(player).getType()==Material.AIR){
						sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Doesnt-Have-Item-In-Hand")));
						return true;
					}
					ItemStack item = Api.getItemInHand(player);
					String enchantment = args[1];
					if(customEnchant){
						if(CustomE.hasEnchantment(item, ench)){
							Api.setItemInHand(player, CustomE.removeEnchantment(item, ench));
							String msg = Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Remove-Enchantment")
									.replaceAll("%Enchantment%", CustomE.getCustomName(ench)).replaceAll("%enchantment%", CustomE.getCustomName(ench)));
							player.sendMessage(msg);
							return true;
						}
					}else{
						if(CE.hasEnchantment(item, en)){
							Api.setItemInHand(player, CE.removeEnchantment(item, en));
							String msg = Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Remove-Enchantment")
									.replaceAll("%Enchantment%", en.getCustomName()).replaceAll("%enchantment%", en.getCustomName()));
							player.sendMessage(msg);
							return true;
						}
					}
					sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Doesnt-Have-Enchantment")
							.replaceAll("%Enchantment%", enchantment).replaceAll("%enchantment%", enchantment)));
					return true;
				}
				if(args[0].equalsIgnoreCase("Add")){
					if(!(sender instanceof Player)){
						sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Players-Only")));
						return true;
					}
					if(args.length<=1){
						sender.sendMessage(Api.getPrefix()+Api.color("&c/CE Add <Enchantment> [LvL]"));
						return true;
					}
					Player player = (Player) sender;
					if(!Api.hasPermission(sender, "Admin", true))return true;
					boolean T = false;
					boolean customEnchant = false;
					String ench = "Glowing";
					CEnchantments en = null;
					String lvl = "1";
					if(args.length>=3){
						if(!Api.isInt(args[2])){
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Number")
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
						sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-An-Enchantment")));
						return true;
					}
					if(Api.getItemInHand(player).getType() == Material.AIR){
						sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Doesnt-Have-Item-In-Hand")));
						return false;
					}
					if(customEnchant){
						Api.setItemInHand(player, Api.addGlow(CustomE.addEnchantment(Api.getItemInHand(player), ench, Integer.parseInt(lvl))));
					}else{
						Api.setItemInHand(player, Api.addGlow(CE.addEnchantment(Api.getItemInHand(player), en, Integer.parseInt(lvl))));
					}
					return true;
				}
				if(args[0].equalsIgnoreCase("Book")){// /CE Book <Enchantment> [Lvl] [Amount] [Player]
					if(args.length<=1){
						sender.sendMessage(Api.getPrefix()+Api.color("&c/CE Book <Enchantment> [Lvl] [Amount] [Player]"));
						return true;
					}
					if(args.length<=2){
						if(!(sender instanceof Player)){
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Players-Only")));
							return true;
						}
					}
					if(!Api.hasPermission(sender, "Admin", true))return true;
					String ench = args[1];
					int lvl = 1;
					int amount = 1;
					Player player = Api.getPlayer(sender.getName());
					if(args.length>=3){
						if(!Api.isInt(args[2])){
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Number")
									.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
							return true;
						}
						lvl = Integer.parseInt(args[2]);
					}
					if(args.length>=4){
						if(!Api.isInt(args[3])){
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Number")
									.replaceAll("%Arg%", args[3]).replaceAll("%arg%", args[3])));
							return true;
						}
						amount = Integer.parseInt(args[3]);
					}
					if(args.length>=5){
						if(!Api.isOnline(args[4], sender))return true;
						player = Api.getPlayer(args[4]);
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
						sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-An-Enchantment")));
						return true;
					}
					sender.sendMessage(Api.color(Api.getPrefix()+settings.getMsg().getString("Messages.Send-Enchantment-Book").replace("%Player%", player.getName()).replace("%player%", player.getName())));
					int Smax = settings.getConfig().getInt("Settings.BlackScroll.SuccessChance.Max");
					int Smin = settings.getConfig().getInt("Settings.BlackScroll.SuccessChance.Min");
					int Dmax = settings.getConfig().getInt("Settings.BlackScroll.DestroyChance.Max");
					int Dmin = settings.getConfig().getInt("Settings.BlackScroll.DestroyChance.Min");
					if(customEnchant){
						CustomEBook book = new CustomEBook(ench, lvl, amount);
						book.setDestoryRate(Api.percentPick(Dmax, Dmin));
						book.setSuccessRate(Api.percentPick(Smax, Smin));
						player.getInventory().addItem(Api.addGlow(book.buildBook()));
					}else{
						CEBook book = new CEBook(CE.getFromName(ench), lvl, amount);
						book.setDestoryRate(Api.percentPick(Dmax, Dmin));
						book.setSuccessRate(Api.percentPick(Smax, Smin));
						player.getInventory().addItem(Api.addGlow(book.buildBook()));
					}
					return true;
				}
			}
			sender.sendMessage(Api.getPrefix()+Api.color("&cDo /CE Help for more info."));
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
					player.sendMessage(Api.getPrefix()+Api.color("&7This server is running your Crazy Enchantments Plugin. "
						+ "&7It is running version &av"+Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments").getDescription().getVersion()+"&7."));
				}
				if(player.isOp()){
					Api.hasUpdate(player);
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
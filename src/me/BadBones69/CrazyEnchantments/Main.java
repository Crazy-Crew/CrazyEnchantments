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
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import ca.thederpygolems.armorequip.ArmorListener;
import me.BadBones69.CrazyEnchantments.API.CrazyEnchantments;
import me.BadBones69.CrazyEnchantments.Controlers.BlackSmith;
import me.BadBones69.CrazyEnchantments.Controlers.CustomEnchantments;
import me.BadBones69.CrazyEnchantments.Controlers.DustControl;
import me.BadBones69.CrazyEnchantments.Controlers.ProtectionCrystal;
import me.BadBones69.CrazyEnchantments.Controlers.ScrollControl;
import me.BadBones69.CrazyEnchantments.Controlers.Tinkerer;
import me.BadBones69.CrazyEnchantments.Enchantments.Armor;
import me.BadBones69.CrazyEnchantments.Enchantments.Axes;
import me.BadBones69.CrazyEnchantments.Enchantments.Boots;
import me.BadBones69.CrazyEnchantments.Enchantments.Bows;
import me.BadBones69.CrazyEnchantments.Enchantments.Helmets;
import me.BadBones69.CrazyEnchantments.Enchantments.PickAxes;
import me.BadBones69.CrazyEnchantments.Enchantments.Swords;
import me.BadBones69.CrazyEnchantments.Enchantments.Tools;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Main extends JavaPlugin implements Listener{
	public static SettingsManager settings = SettingsManager.getInstance();
	static CrazyEnchantments CE = CrazyEnchantments.getInstance();
	public static Economy econ = null;
	public static EconomyResponse r;
	@Override
	public void onEnable(){
		saveDefaultConfig();
		settings.setup(this);
		Api.hasUpdate();
		//==========================================================================\\
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getServer().getPluginManager().registerEvents(new ArmorListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new CustomEnchantments(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ECControl(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new DustControl(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new BlackSmith(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Tinkerer(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new GUI(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ScrollControl(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ProtectionCrystal(), this);
		//==========================================================================\\
		Bukkit.getServer().getPluginManager().registerEvents(new Swords(this), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Armor(this), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Bows(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Axes(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Boots(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Helmets(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PickAxes(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Tools(), this);
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
			if(sender instanceof Player)if(!Api.permCheck((Player)sender, "BlackSmith"))return true;
			Player player = (Player) sender;
			BlackSmith.openBlackSmith(player);
			return true;
		}
		if(commandLable.equalsIgnoreCase("Tinkerer")||commandLable.equalsIgnoreCase("Tinker")){
			if(!(sender instanceof Player)){
				sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Players-Only")));
				return true;
			}
			if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Tinker"))return true;
			Player player = (Player) sender;
			Tinkerer.openTinker(player);
			return true;
		}
		if(commandLable.equalsIgnoreCase("CE")||commandLable.equalsIgnoreCase("CrazyEnchantments")
				||commandLable.equalsIgnoreCase("Enchant")||commandLable.equalsIgnoreCase("Enchanter")){
			if(args.length == 0){
				if(!(sender instanceof Player)){
					sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Players-Only")));
					return true;
				}
				Player player = (Player)sender;
				if(!Api.permCheck(player, "Access"))return true;
				GUI.openGUI(player);
				return true;
			}
			if(args.length>=1){
				if(args[0].equalsIgnoreCase("Help")){
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Access"))return true;
					sender.sendMessage(Api.color("&2&l&nCrazy Enchantments"));
					sender.sendMessage(Api.color("&b/CE - &9Opens the GUI."));
					sender.sendMessage(Api.color("&b/Tinker - &9Opens up the Tinkerer."));
					sender.sendMessage(Api.color("&b/BlackSmith - &9Opens up the Black Smith."));
					sender.sendMessage(Api.color("&b/CE Dust <Success/Destroy> <Amount> [Player] [Percent] - &9Give a player a some Magical Dust."));
					sender.sendMessage(Api.color("&b/CE Help - &9Shows all CE Commands."));
					sender.sendMessage(Api.color("&b/CE Info [Enchantment] - &9Shows info on all Enchantmnets."));
					sender.sendMessage(Api.color("&b/CE Reload - &9Reloads the Config.yml."));
					sender.sendMessage(Api.color("&b/CE Remove <Enchantment> - &9Removes an enchantment from the item in your hand."));
					sender.sendMessage(Api.color("&b/CE Add <Enchantment> <LvL> - &9Adds and enchantment to the item in your hand."));
					sender.sendMessage(Api.color("&b/CE Scroll <Player> <Scroll> <Amount> - &9Gives a player scrolls."));
					sender.sendMessage(Api.color("&b/CE Crystal [Amount] [Player] - &9Gives a player Protection Crystal."));
					sender.sendMessage(Api.color("&b/CE Book <Enchantment> <Lvl> <Amount> <Player> - &9Gives a player a Enchantment Book."));
					sender.sendMessage(Api.color("&b/CE LostBook <Category> [Amount] [Player] - &9Gives a player a Lost Book."));
					return true;
				}
				if(args[0].equalsIgnoreCase("Reload")){
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
					settings.reloadConfig();
					settings.reloadEnchs();
					settings.reloadMsg();
					settings.reloadCustomEnchs();
					settings.reloadSigns();
					settings.reloadTinker();
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
						if(!Api.permCheck(player, "Info"))return true;
						GUI.openInfo(player);
						return true;
					}else{
						String ench = args[1];
						for(String en : Main.settings.getEnchs().getConfigurationSection("Enchantments").getKeys(false)){
							if(en.equalsIgnoreCase(ench)||Api.getEnchName(en).equalsIgnoreCase(ench)){
								String name = Main.settings.getEnchs().getString("Enchantments."+en+".Info.Name");
								List<String> desc = Main.settings.getEnchs().getStringList("Enchantments."+en+".Info.Description");
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
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
					if(args.length==2){// /CE LostBook <Category>
						if(!(sender instanceof Player)){
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Players-Only")));
							return true;
						}
						Player player = (Player)sender;
						String cat = args[1];
						for(String C : settings.getConfig().getConfigurationSection("Categories").getKeys(false)){
							if(cat.equalsIgnoreCase(C)){
								cat=C;
								if(Api.isInvFull(player)){
									player.getWorld().dropItemNaturally(player.getLocation(), Api.getLostBook(cat, 1));
								}else{
									player.getInventory().addItem(Api.getLostBook(cat, 1));
								}
								return true;
							}
						}
						sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Category")
								.replaceAll("%Category%", cat).replaceAll("%category%", cat)));
						return true;
					}
					if(args.length==3){// /CE LostBook <Category> [Amount]
						if(!(sender instanceof Player)){
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Players-Only")));
							return true;
						}
						if(!Api.isInt(args[2])){
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Number")
									.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
							return true;
						}
						Player player = (Player)sender;
						String cat = args[1];
						int amount = Integer.parseInt(args[2]);
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
					if(args.length==4){// /CE LostBook <Category> [Amount] [Player]
						if(!Api.isInt(args[2])){
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Number")
									.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
							return true;
						}
						if(!Api.isOnline(args[3], sender))return true;
						Player player = Api.getPlayer(args[3]);
						String cat = args[1];
						int amount = Integer.parseInt(args[2]);
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
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
					int amount = 1;
					Player player = (Player) sender;
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
					if(args.length>=3){
						if(!Api.isOnline(args[2], sender))return true;
						player=Api.getPlayer(args[2]);
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
				if(args[0].equalsIgnoreCase("Dust")){// /CE Dust <Success/Destroy> <Amount> [Player] [Percent]
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
					if(args.length==3){
						if(!(sender instanceof Player)){
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Players-Only")));
							return true;
						}
						if(!Api.isInt(args[2])){
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Number")
									.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
							return true;
						}
						Player player = (Player)sender;
						if(args[1].equalsIgnoreCase("Success")||args[1].equalsIgnoreCase("S")){
							player.getInventory().addItem(DustControl.getDust("SuccessDust", Integer.parseInt(args[2])));
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Get-Success-Dust")
									.replaceAll("%Amount%", args[2]).replaceAll("%amount%", args[2])));
							return true;
						}
						if(args[1].equalsIgnoreCase("Destroy")||args[1].equalsIgnoreCase("D")){
							player.getInventory().addItem(DustControl.getDust("DestroyDust", Integer.parseInt(args[2])));
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Get-Destroy-Dust")
									.replaceAll("%Amount%", args[2]).replaceAll("%amount%", args[2])));
							return true;
						}
					}
					if(args.length==4){// /CE Dust <Success/Destroy> <Amount> [Player]
						if(!Api.isOnline(args[3], sender))return true;
						if(!Api.isInt(args[2])){
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Number")
									.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
							return true;
						}
						Player player = Api.getPlayer(args[3]);
						if(args[1].equalsIgnoreCase("Success")||args[1].equalsIgnoreCase("S")){
							player.getInventory().addItem(DustControl.getDust("SuccessDust", Integer.parseInt(args[2])));
							player.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Get-Success-Dust")
									.replaceAll("%Amount%", args[2]).replaceAll("%amount%", args[2])));
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Give-Success-Dust")
									.replaceAll("%Amount%", args[2]).replaceAll("%amount%", args[2])));
							return true;
						}
						if(args[1].equalsIgnoreCase("Destroy")||args[1].equalsIgnoreCase("D")){
							player.getInventory().addItem(DustControl.getDust("DestroyDust", Integer.parseInt(args[2])));
							player.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Get-Destroy-Dust")
									.replaceAll("%Amount%", args[2]).replaceAll("%amount%", args[2])));
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Give-Destroy-Dust")
									.replaceAll("%Amount%", args[2]).replaceAll("%amount%", args[2])));
							return true;
						}
					}
					if(args.length==5){// /CE Dust <Success/Destroy> <Amount> [Player] [Percent]
						if(!Api.isOnline(args[3], sender))return true;
						Player player = Api.getPlayer(args[3]);
						if(!Api.isInt(args[2])){
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Number")
									.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
							return true;
						}
						if(!Api.isInt(args[4])){
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Number")
									.replaceAll("%Arg%", args[4]).replaceAll("%arg%", args[4])));
							return true;
						}
						if(args[1].equalsIgnoreCase("Success")||args[1].equalsIgnoreCase("S")){
							player.getInventory().addItem(DustControl.getDust("SuccessDust", Integer.parseInt(args[2]), Integer.parseInt(args[4])));
							player.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Get-Success-Dust")
									.replaceAll("%Amount%", args[2]).replaceAll("%amount%", args[2])));
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Give-Success-Dust")
									.replaceAll("%Amount%", args[2]).replaceAll("%amount%", args[2])));
							return true;
						}
						if(args[1].equalsIgnoreCase("Destroy")||args[1].equalsIgnoreCase("D")){
							player.getInventory().addItem(DustControl.getDust("DestroyDust", Integer.parseInt(args[2]), Integer.parseInt(args[4])));
							player.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Get-Destroy-Dust")
									.replaceAll("%Amount%", args[2]).replaceAll("%amount%", args[2])));
							sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Give-Destroy-Dust")
									.replaceAll("%Amount%", args[2]).replaceAll("%amount%", args[2])));
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
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
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
					if(!Api.permCheck(player, "Admin"))return true;
					boolean T=false;
					for(String i : ECControl.allEnchantments().keySet()){
						if(Api.getEnchName(i).equalsIgnoreCase(args[1])){
							T=true;
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
					if(item.hasItemMeta()){
						if(item.getItemMeta().hasLore()){
							for(String lore : item.getItemMeta().getLore()){
								for(String i : ECControl.allEnchantments().keySet()){
									if(Api.getEnchName(i).equalsIgnoreCase(enchantment)){
										enchantment=Api.getEnchName(i);
										if(lore.contains(Api.getEnchName(i))){
											Api.setItemInHand(player, Api.removeLore(item, lore));
											String msg = Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Remove-Enchantment")
													.replaceAll("%Enchantment%", Api.getEnchName(i)).replaceAll("%enchantment%", Api.getEnchName(i)));
											player.sendMessage(msg);
											return true;
										}
									}
								}
							}
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
					if(args.length!=3){
						sender.sendMessage(Api.getPrefix()+Api.color("&c/CE Add <Enchantment> <LvL>"));
						return true;
					}
					Player player = (Player) sender;
					if(!Api.permCheck((Player)sender, "Admin"))return true;
					boolean T = false;
					String en = "";
					String color = "&7";
					for(String i : ECControl.allEnchantments().keySet()){
						if(Api.getEnchName(i).equalsIgnoreCase(args[1])){
							T = true;
							if(Main.settings.getEnchs().contains("Enchantments."+i)){
								color = Main.settings.getEnchs().getString("Enchantments."+i+".Color");
							}
							if(Main.settings.getCustomEnchs().contains("Enchantments."+i)){
								color = Main.settings.getCustomEnchs().getString("Enchantments."+i+".Color");
							}
							en = Api.getEnchName(i);
						}
					}
					if(!T){
						sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-An-Enchantment")));
						return true;
					}
					String lvl = args[2];
					if(lvl.equals("1"))lvl="I";
					if(lvl.equals("2"))lvl="II";
					if(lvl.equals("3"))lvl="III";
					if(lvl.equals("4"))lvl="IV";
					if(lvl.equals("5"))lvl="V";
					if(lvl.equals("6"))lvl="VI";
					if(lvl.equals("7"))lvl="VII";
					if(lvl.equals("8"))lvl="VIII";
					if(lvl.equals("9"))lvl="IX";
					if(lvl.equals("10"))lvl="X";
					if(Api.getItemInHand(player).getType() == Material.AIR){
						sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Doesnt-Have-Item-In-Hand")));
						return false;
					}
					Api.setItemInHand(player, Api.addGlow(Api.addLore(Api.getItemInHand(player), Api.color(color+en+" "+lvl))));
					return true;
				}
				if(args[0].equalsIgnoreCase("Book")){// /CE Book <Enchantment> <Lvl> <Amount> <Player>
					if(args.length!=5){
						sender.sendMessage(Api.getPrefix()+Api.color("&c/CE Book <Enchantment> <Lvl> <Amount> <Player>"));
						return true;
					}
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Admin"))return true;
					String ench = args[1];
					if(!Api.isInt(args[2])){
						sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Number")
								.replaceAll("%Arg%", args[2]).replaceAll("%arg%", args[2])));
						return true;
					}
					if(!Api.isInt(args[3])){
						sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-A-Number")
								.replaceAll("%Arg%", args[3]).replaceAll("%arg%", args[3])));
						return true;
					}
					int lvl = Integer.parseInt(args[2]);
					int amount = Integer.parseInt(args[3]);
					if(!Api.isOnline(args[4], sender))return true;
					Player player = Api.getPlayer(args[4]);
					boolean toggle = false;
					for(String en : ECControl.allEnchantments().keySet()){
						if(ench.equalsIgnoreCase(Api.getEnchName(en))){
							ench=en;
							toggle=true;
						}
					}
					if(!toggle){
						sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Not-An-Enchantment")));
						return true;
					}
					sender.sendMessage(Api.color(Api.getPrefix()+"&7You have sent &6"+player.getName()+" &7an Crazy Enchantment Book."));
					player.getInventory().addItem(Api.addGlow(ScrollControl.makeEnchantBook(ench, Api.getPower(lvl), amount)));
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
				if(player.isOp())Api.hasUpdate(player);
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
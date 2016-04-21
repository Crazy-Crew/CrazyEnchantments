package me.BadBones69.CrazyEnchantments;

import me.BadBones69.CrazyEnchantments.API.CrazyEnchantments;
import me.BadBones69.CrazyEnchantments.Enchantments.Armor.BurnShield;
import me.BadBones69.CrazyEnchantments.Enchantments.Armor.Enlightened;
import me.BadBones69.CrazyEnchantments.Enchantments.Armor.Fortify;
import me.BadBones69.CrazyEnchantments.Enchantments.Armor.Freeze;
import me.BadBones69.CrazyEnchantments.Enchantments.Armor.Hulk;
import me.BadBones69.CrazyEnchantments.Enchantments.Armor.Molten;
import me.BadBones69.CrazyEnchantments.Enchantments.Armor.Ninja;
import me.BadBones69.CrazyEnchantments.Enchantments.Armor.Nursery;
import me.BadBones69.CrazyEnchantments.Enchantments.Armor.OverLoad;
import me.BadBones69.CrazyEnchantments.Enchantments.Armor.PainGiver;
import me.BadBones69.CrazyEnchantments.Enchantments.Armor.Savior;
import me.BadBones69.CrazyEnchantments.Enchantments.Armor.SelfDestruct;
import me.BadBones69.CrazyEnchantments.Enchantments.Axes.Berserk;
import me.BadBones69.CrazyEnchantments.Enchantments.Axes.Blessed;
import me.BadBones69.CrazyEnchantments.Enchantments.Axes.Cursed;
import me.BadBones69.CrazyEnchantments.Enchantments.Axes.Dizzy;
import me.BadBones69.CrazyEnchantments.Enchantments.Axes.FeedMe;
import me.BadBones69.CrazyEnchantments.Enchantments.Axes.Rekt;
import me.BadBones69.CrazyEnchantments.Enchantments.Boots.AntiGravity;
import me.BadBones69.CrazyEnchantments.Enchantments.Boots.Gears;
import me.BadBones69.CrazyEnchantments.Enchantments.Boots.Springs;
import me.BadBones69.CrazyEnchantments.Enchantments.Bow.Boom;
import me.BadBones69.CrazyEnchantments.Enchantments.Bow.Piercing;
import me.BadBones69.CrazyEnchantments.Enchantments.Bow.Venom;
import me.BadBones69.CrazyEnchantments.Enchantments.Helmets.Glowing;
import me.BadBones69.CrazyEnchantments.Enchantments.Helmets.Mermaid;
import me.BadBones69.CrazyEnchantments.Enchantments.Swords.Blindness;
import me.BadBones69.CrazyEnchantments.Enchantments.Swords.DoubleDamage;
import me.BadBones69.CrazyEnchantments.Enchantments.Swords.FastTurn;
import me.BadBones69.CrazyEnchantments.Enchantments.Swords.LifeSteal;
import me.BadBones69.CrazyEnchantments.Enchantments.Swords.LightWeight;
import me.BadBones69.CrazyEnchantments.Enchantments.Swords.SlowMo;
import me.BadBones69.CrazyEnchantments.Enchantments.Swords.Vampire;
import me.BadBones69.CrazyEnchantments.Enchantments.Swords.Viper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import ca.thederpygolems.armorequip.ArmorListener;

public class Main extends JavaPlugin{
	public static SettingsManager settings = SettingsManager.getInstance();
	static CrazyEnchantments CE = CrazyEnchantments.getInstance();
	static Main plugin;
	@Override
	public void onEnable(){
		saveDefaultConfig();
		settings.setup(this);
		Bukkit.getServer().getPluginManager().registerEvents(new ECControl(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new GUI(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ArmorListener(null), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ScrollControl(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new OverLoad(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Glowing(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new BurnShield(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Piercing(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Springs(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Gears(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Hulk(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new SelfDestruct(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Mermaid(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Ninja(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Vampire(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new LifeSteal(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Boom(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Venom(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new DoubleDamage(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new SlowMo(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Blindness(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Viper(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new AntiGravity(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new FastTurn(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new LightWeight(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Blessed(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new FeedMe(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Dizzy(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Berserk(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Cursed(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Rekt(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Enlightened(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Freeze(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Fortify(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Molten(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PainGiver(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Savior(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Nursery(), this);
	}
	public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args){
		if(commandLable.equalsIgnoreCase("CE")||commandLable.equalsIgnoreCase("CrazyEnchantments")){
			if(args.length == 0){
				if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Access"))return true;
				GUI.openGUI((Player)sender);
				return true;
			}
			if(args.length == 1){
				if(args[0].equalsIgnoreCase("Help")){
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Access"))return true;
					sender.sendMessage(Api.color("&2&l&nCrazy Enchantments"));
					sender.sendMessage(Api.color("&b/CE - &9Opens the GUI."));
					sender.sendMessage(Api.color("&b/CE Help - &9Shows all CE Commands."));
					sender.sendMessage(Api.color("&b/CE Info - &9Shows info on all Enchantmnets."));
					sender.sendMessage(Api.color("&b/CE Reload - &9Reloads the Config.yml."));
					sender.sendMessage(Api.color("&b/CE Add <Enchantment> <LvL> - &9Adds and enchantment to the item in your hand."));
					sender.sendMessage(Api.color("&b/CE BlackScroll <Player> <Amount> - &9Gives a player Black Scrolls."));
					return true;
				}
				if(args[0].equalsIgnoreCase("Reload")){
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "Reload"))return true;
					settings.reloadConfig();
					settings.reloadEnchs();
					settings.reloadMsg();
					sender.sendMessage(Api.getPrefix()+Api.color(settings.getMsg().getString("Messages.Config-Reload")));
					return true;
				}
				if(args[0].equalsIgnoreCase("Info")){
					if(!(sender instanceof Player)){
						sender.sendMessage(Api.color("&cYou need to be a Player to use this command."));
						return true;
					}
					Player player = (Player)sender;
					if(!Api.permCheck(player, "Info"))return true;
					Inventory inv = Bukkit.createInventory(null, 54, Api.color("&6&lEnchantment Info"));
					for(ItemStack i : GUI.addInfo()){
						inv.addItem(i);
					}
					player.openInventory(inv);
					return true;
				}
			}
			if(args.length == 3){
				if(args[0].equalsIgnoreCase("BlackScroll")||args[0].equalsIgnoreCase("BS")){
					if(sender instanceof Player)if(!Api.permCheck((Player)sender, "BlackScroll"))return true;
					String name = args[1];
					int i = Integer.parseInt(args[2]);
					if(!Api.isOnline(name, sender))return true;
					Api.getPlayer(name).getInventory().addItem(Api.BlackScroll(i));
					return true;
				}
				if(args[0].equalsIgnoreCase("Add")){
					Player player = (Player) sender;
					if(!Api.permCheck((Player)sender, "Admin"))return true;
					boolean T = false;
					String en = "";
					for(String i : ECControl.allEnchantments().keySet()){
						if(i.equalsIgnoreCase(args[1])){
							T = true;
							en = i;
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
					if(player.getItemInHand().getType() == Material.AIR){
						player.sendMessage(Api.getPrefix()+Api.color("&cYou must have an item in your hand."));return true;
					}
					player.setItemInHand(Api.addLore(player.getItemInHand(), Api.color("&7"+en+" "+lvl)));
					return true;
				}
			}
			sender.sendMessage(Api.getPrefix()+Api.color("&cDo /CE Help for more info."));
		}
		return false;
	}
}
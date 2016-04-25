package me.BadBones69.CrazyEnchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignControl implements Listener{
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getClickedBlock()==null)return;
		Location Loc = e.getClickedBlock().getLocation();
		Player player = e.getPlayer();
		if(!(e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
		if(e.getClickedBlock().getState() instanceof Sign){
			//Sign s = (Sign)e.getClickedBlock().getState();
			for(String l : Main.settings.getSigns().getConfigurationSection("Locations").getKeys(false)){
				String type = Main.settings.getSigns().getString("Locations."+l+".Type");;
				World world = Bukkit.getWorld(Main.settings.getSigns().getString("Locations."+l+".World"));
				int x = Main.settings.getSigns().getInt("Locations."+l+".X");
				int y = Main.settings.getSigns().getInt("Locations."+l+".Y");
				int z = Main.settings.getSigns().getInt("Locations."+l+".Z");
				Location loc = new Location(world,x,y,z);
				if(Loc.equals(loc)){
					if(type.equalsIgnoreCase("BlackScroll")){
						int price = Main.settings.getConfig().getInt("Settings.SignOptions.BlackScrollStyle.Cost");
						if(Main.settings.getConfig().getString("Settings.SignOptions.BlackScrollStyle.Money/XP").equalsIgnoreCase("Money")){
							if(Api.getMoney(player)<price){
								double needed = price-Api.getMoney(player);
								player.sendMessage(Api.color(Main.settings.getMsg().getString("Messages.Need-More-Money").replace("%Money_Needed%", needed+"").replace("%money_needed%", needed+"")));
								return;
							}
							Main.econ.withdrawPlayer(player, price);
						}else{
							if(Main.settings.getConfig().getString("Settings.SignOptions.BlackScrollStyle.Lvl/Total").equalsIgnoreCase("Lvl")){
								if(Api.getXPLvl(player)<price){
									String xp = price - Api.getXPLvl(player)+"";
									player.sendMessage(Api.color(Main.settings.getMsg().getString("Messages.Need-More-XP-Lvls").replace("%XP%", xp).replace("%xp%", xp)));
									return;
								}
								Api.takeLvlXP(player, price);
							}
							if(Main.settings.getConfig().getString("Settings.SignOptions.BlackScrollStyle.Lvl/Total").equalsIgnoreCase("Total")){
								if(player.getTotalExperience()<price){
									String xp = price - player.getTotalExperience()+"";
									player.sendMessage(Api.color(Main.settings.getMsg().getString("Messages.Need-More-Total-XP").replace("%XP%", xp).replace("%xp%", xp)));
									return;
								}
								Api.takeTotalXP(player, price);
							}
						}
						player.getInventory().addItem(Api.BlackScroll(1));
						return;
					}
					if(type.equalsIgnoreCase("WhiteScroll")){
						int price = Main.settings.getConfig().getInt("Settings.SignOptions.WhiteScrollStyle.Cost");
						if(Main.settings.getConfig().getString("Settings.SignOptions.WhiteScrollStyle.Money/XP").equalsIgnoreCase("Money")){
							if(Api.getMoney(player)<price){
								double needed = price-Api.getMoney(player);
								player.sendMessage(Api.color(Main.settings.getMsg().getString("Messages.Need-More-Money").replace("%Money_Needed%", needed+"").replace("%money_needed%", needed+"")));
								return;
							}
							Main.econ.withdrawPlayer(player, price);
						}else{
							if(Main.settings.getConfig().getString("Settings.SignOptions.WhiteScrollStyle.Lvl/Total").equalsIgnoreCase("Lvl")){
								if(Api.getXPLvl(player)<price){
									String xp = price - Api.getXPLvl(player)+"";
									player.sendMessage(Api.color(Main.settings.getMsg().getString("Messages.Need-More-XP-Lvls").replace("%XP%", xp).replace("%xp%", xp)));
									return;
								}
								Api.takeLvlXP(player, price);
							}
							if(Main.settings.getConfig().getString("Settings.SignOptions.WhiteScrollStyle.Lvl/Total").equalsIgnoreCase("Total")){
								if(player.getTotalExperience()<price){
									String xp = price - player.getTotalExperience()+"";
									player.sendMessage(Api.color(Main.settings.getMsg().getString("Messages.Need-More-Total-XP").replace("%XP%", xp).replace("%xp%", xp)));
									return;
								}
								Api.takeTotalXP(player, price);
							}
						}
						player.getInventory().addItem(Api.addWhiteScroll(1));
						return;
					}
					for(String cat : Main.settings.getConfig().getConfigurationSection("Categories").getKeys(false)){
						if(type.equalsIgnoreCase(cat)){
							if(Main.settings.getConfig().getString("Categories."+cat+".Lvl/Total").equalsIgnoreCase("Lvl")){
								if(Api.getXPLvl(player)<Main.settings.getConfig().getInt("Categories."+cat+".XP")){
									String xp = Main.settings.getConfig().getInt("Categories."+cat+".XP") - Api.getXPLvl(player)+"";
									player.sendMessage(Api.color(Main.settings.getMsg().getString("Messages.Need-More-XP-Lvls").replace("%XP%", xp).replace("%xp%", xp)));
									return;
								}
								Api.takeLvlXP(player, Main.settings.getConfig().getInt("Categories."+cat+".XP"));
							}
							if(Main.settings.getConfig().getString("Categories."+cat+".Lvl/Total").equalsIgnoreCase("Total")){
								if(player.getTotalExperience()<Main.settings.getConfig().getInt("Categories."+cat+".XP")){
									String xp = Main.settings.getConfig().getInt("Categories."+cat+".XP") - player.getTotalExperience()+"";
									player.sendMessage(Api.color(Main.settings.getMsg().getString("Messages.Need-More-Total-XP").replace("%XP%", xp).replace("%xp%", xp)));
									return;
								}
								Api.takeTotalXP(player, Main.settings.getConfig().getInt("Categories."+cat+".XP"));
							}
							player.getInventory().addItem(ECControl.pick(cat));
							return;
						}
					}
				}
			}
		}
    }
	@EventHandler
	public void onBreak(BlockBreakEvent e){
		Location Loc = e.getBlock().getLocation();
		for(String l : Main.settings.getSigns().getConfigurationSection("Locations").getKeys(false)){
			World world = Bukkit.getWorld(Main.settings.getSigns().getString("Locations."+l+".World"));
			int x = Main.settings.getSigns().getInt("Locations."+l+".X");
			int y = Main.settings.getSigns().getInt("Locations."+l+".Y");
			int z = Main.settings.getSigns().getInt("Locations."+l+".Z");
			Location loc = new Location(world,x,y,z);
			if(Loc.equals(loc)){
				Main.settings.getSigns().set("Locations."+l, null);
				Main.settings.saveSigns();
				e.getPlayer().sendMessage(Api.color("&cYou have just removed a Crazy Enchantment Shop Sign."));
				return;
			}
		}
	}
	private String placeHolders(String msg, String cat){
		msg=Api.color(msg);
		msg=msg.replaceAll("%category%", cat).replaceAll("%Category%", cat);
		msg=msg.replaceAll("%xp%", Main.settings.getConfig().getInt("Categories."+cat+".XP")+"").replaceAll("%XP%", Main.settings.getConfig().getInt("Categories."+cat+".XP")+"");
		return msg;
	}
	@EventHandler
	public void onSignMake(SignChangeEvent e){
		Player player = e.getPlayer();
		Location loc = e.getBlock().getLocation();
		int size = Main.settings.getSigns().getConfigurationSection("Locations").getKeys(false).size()+1;
		if(!Api.permCheck(player, "Sign"))return;
		String line1 = e.getLine(0);
		String line2 = e.getLine(1);
		if(line1.equalsIgnoreCase("{CrazyEnchant}")){
			for(String cat : Main.settings.getConfig().getConfigurationSection("Categories").getKeys(false)){
				if(line2.equalsIgnoreCase("{"+cat+"}")){
					e.setLine(0, placeHolders(Main.settings.getConfig().getString("Settings.SignOptions.CategoryShopStyle.Line1"),cat));
					e.setLine(1, placeHolders(Main.settings.getConfig().getString("Settings.SignOptions.CategoryShopStyle.Line2"),cat));
					e.setLine(2, placeHolders(Main.settings.getConfig().getString("Settings.SignOptions.CategoryShopStyle.Line3"),cat));
					e.setLine(3, placeHolders(Main.settings.getConfig().getString("Settings.SignOptions.CategoryShopStyle.Line4"),cat));
					Main.settings.getSigns().set("Locations."+size+".Type", cat);
					Main.settings.getSigns().set("Locations."+size+".World", loc.getWorld().getName());
					Main.settings.getSigns().set("Locations."+size+".X", loc.getBlockX());
					Main.settings.getSigns().set("Locations."+size+".Y", loc.getBlockY());
					Main.settings.getSigns().set("Locations."+size+".Z", loc.getBlockZ());
					Main.settings.saveSigns();
					return;
				}
			}
			if(line2.equalsIgnoreCase("{BlackScroll}")){
				e.setLine(0, Api.color(Main.settings.getConfig().getString("Settings.SignOptions.BlackScrollStyle.Line1")));
				e.setLine(1, Api.color(Main.settings.getConfig().getString("Settings.SignOptions.BlackScrollStyle.Line2")));
				e.setLine(2, Api.color(Main.settings.getConfig().getString("Settings.SignOptions.BlackScrollStyle.Line3")));
				e.setLine(3, Api.color(Main.settings.getConfig().getString("Settings.SignOptions.BlackScrollStyle.Line4")));
				Main.settings.getSigns().set("Locations."+size+".Type", "BlackScroll");
				Main.settings.getSigns().set("Locations."+size+".World", loc.getWorld().getName());
				Main.settings.getSigns().set("Locations."+size+".X", loc.getBlockX());
				Main.settings.getSigns().set("Locations."+size+".Y", loc.getBlockY());
				Main.settings.getSigns().set("Locations."+size+".Z", loc.getBlockZ());
				Main.settings.saveSigns();
				return;
			}
			if(line2.equalsIgnoreCase("{WhiteScroll}")){
				e.setLine(0, Api.color(Main.settings.getConfig().getString("Settings.SignOptions.WhiteScrollStyle.Line1")));
				e.setLine(1, Api.color(Main.settings.getConfig().getString("Settings.SignOptions.WhiteScrollStyle.Line2")));
				e.setLine(2, Api.color(Main.settings.getConfig().getString("Settings.SignOptions.WhiteScrollStyle.Line3")));
				e.setLine(3, Api.color(Main.settings.getConfig().getString("Settings.SignOptions.WhiteScrollStyle.Line4")));
				Main.settings.getSigns().set("Locations."+size+".Type", "WhiteScroll");
				Main.settings.getSigns().set("Locations."+size+".World", loc.getWorld().getName());
				Main.settings.getSigns().set("Locations."+size+".X", loc.getBlockX());
				Main.settings.getSigns().set("Locations."+size+".Y", loc.getBlockY());
				Main.settings.getSigns().set("Locations."+size+".Z", loc.getBlockZ());
				Main.settings.saveSigns();
				return;
			}
		}
	}
}
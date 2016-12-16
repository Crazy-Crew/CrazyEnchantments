package me.BadBones69.CrazyEnchantments.Controlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.BadBones69.CrazyEnchantments.Methods;
import me.BadBones69.CrazyEnchantments.Main;

public class SignControl implements Listener{
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getClickedBlock()==null)return;
		Location Loc = e.getClickedBlock().getLocation();
		Player player = e.getPlayer();
		if(!(e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
		if(e.getClickedBlock().getState() instanceof Sign){
			FileConfiguration config = Main.settings.getConfig();
			for(String l : Main.settings.getSigns().getConfigurationSection("Locations").getKeys(false)){
				String type = Main.settings.getSigns().getString("Locations."+l+".Type");;
				World world = Bukkit.getWorld(Main.settings.getSigns().getString("Locations."+l+".World"));
				int x = Main.settings.getSigns().getInt("Locations."+l+".X");
				int y = Main.settings.getSigns().getInt("Locations."+l+".Y");
				int z = Main.settings.getSigns().getInt("Locations."+l+".Z");
				Location loc = new Location(world,x,y,z);
				if(Loc.equals(loc)){
					if(Methods.isInvFull(player)){
						if(!Main.settings.getMsg().contains("Messages.Inventory-Full")){
							player.sendMessage(Methods.color("&cYour inventory is to full. Please open up some space to buy that."));
						}else{
							player.sendMessage(Methods.color(Main.settings.getMsg().getString("Messages.Inventory-Full")));
						}
						return;
					}
					List<String> types = new ArrayList<String>();
					types.add("ProtectionCrystal");
					types.add("DestroyDust");
					types.add("SuccessDust");
					types.add("BlackScroll");
					types.add("WhiteScroll");
					for(String ty : types){
						if(ty.equalsIgnoreCase(type)){
							int price = config.getInt("Settings.SignOptions."+ty+"Style.Cost");
							if(config.getString("Settings.SignOptions."+ty+"Style.Money/XP").equalsIgnoreCase("Money")){
								if(Methods.getMoney(player)<price){
									double needed = price-Methods.getMoney(player);
									player.sendMessage(Methods.color(Main.settings.getMsg().getString("Messages.Need-More-Money").replace("%Money_Needed%", needed+"").replace("%money_needed%", needed+"")));
									return;
								}
								Main.econ.withdrawPlayer(player, price);
							}else{
								if(config.getString("Settings.SignOptions."+ty+"Style.Lvl/Total").equalsIgnoreCase("Lvl")){
									if(Methods.getXPLvl(player)<price){
										String xp = price - Methods.getXPLvl(player)+"";
										player.sendMessage(Methods.color(Main.settings.getMsg().getString("Messages.Need-More-XP-Lvls").replace("%XP%", xp).replace("%xp%", xp)));
										return;
									}
									Methods.takeLvlXP(player, price);
								}
								if(config.getString("Settings.SignOptions."+ty+"Style.Lvl/Total").equalsIgnoreCase("Total")){
									if(player.getTotalExperience()<price){
										String xp = price - player.getTotalExperience()+"";
										player.sendMessage(Methods.color(Main.settings.getMsg().getString("Messages.Need-More-Total-XP").replace("%XP%", xp).replace("%xp%", xp)));
										return;
									}
									Methods.takeTotalXP(player, price);
								}
							}
							if(config.contains("Settings.SignOptions."+ty+"Style.Buy-Message")){
								player.sendMessage(Methods.color(Methods.getPrefix()+config.getString("Settings.SignOptions."+ty+"Style.Buy-Message")));
							}
							switch(ty){
								case "ProtectionCrystal": player.getInventory().addItem(ProtectionCrystal.getCrystals(1));break;
								case "DestroyDust": player.getInventory().addItem(DustControl.getDust("DestroyDust", 1));break;
								case "SuccessDust": player.getInventory().addItem(DustControl.getDust("SuccessDust", 1));break;
								case "BlackScroll": player.getInventory().addItem(Methods.BlackScroll(1));break;
								case "WhiteScroll": player.getInventory().addItem(Methods.addWhiteScroll(1));break;
							}
							return;
						}
					}
					for(String cat : config.getConfigurationSection("Categories").getKeys(false)){
						if(type.equalsIgnoreCase(cat)){
							if(player.getGameMode() != GameMode.CREATIVE){
								if(config.contains("Categories."+cat+".Money/XP")&&config.getString("Categories."+cat+".Money/XP").equalsIgnoreCase("Money")){
									if(Methods.getMoney(player)<config.getInt("Categories."+cat+".Cost")){
										String money = config.getInt("Categories."+cat+".Cost") - Methods.getMoney(player)+"";
										player.sendMessage(Methods.color(Main.settings.getMsg().getString("Messages.Need-More-Money").replace("%Money_Needed%", money).replace("%money_needed%", money)));
										return;
									}
									Main.econ.withdrawPlayer(player, config.getInt("Categories."+cat+".Cost"));
								}else{
									if(config.getString("Categories."+cat+".Lvl/Total").equalsIgnoreCase("Lvl")){
										if(Methods.getXPLvl(player)<config.getInt("Categories."+cat+".Cost")){
											String xp = config.getInt("Categories."+cat+".Cost") - Methods.getXPLvl(player)+"";
											player.sendMessage(Methods.color(Main.settings.getMsg().getString("Messages.Need-More-XP-Lvls").replace("%XP%", xp).replace("%xp%", xp)));
											return;
										}
										Methods.takeLvlXP(player, config.getInt("Categories."+cat+".Cost"));
									}
									if(config.getString("Categories."+cat+".Lvl/Total").equalsIgnoreCase("Total")){
										if(player.getTotalExperience()<config.getInt("Categories."+cat+".Cost")){
											String xp = config.getInt("Categories."+cat+".Cost") - player.getTotalExperience()+"";
											player.sendMessage(Methods.color(Main.settings.getMsg().getString("Messages.Need-More-Total-XP").replace("%XP%", xp).replace("%xp%", xp)));
											return;
										}
										Methods.takeTotalXP(player, config.getInt("Categories."+cat+".Cost"));
									}
								}
							}
							ItemStack item = Methods.addGlow(EnchantmentControl.pick(cat));
							String C = config.getString("Categories." + cat + ".Name");
							if(config.contains("Settings.SignOptions.CategoryShopStyle.Buy-Message")){
								player.sendMessage(Methods.color(Methods.getPrefix()+config.getString("Settings.SignOptions.CategoryShopStyle.Buy-Message")
								.replaceAll("%BookName%", item.getItemMeta().getDisplayName()).replaceAll("%bookname%", item.getItemMeta().getDisplayName())
								.replaceAll("%Category%", C).replaceAll("%category%", C)));
							}
							player.getInventory().addItem(item);
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
				e.getPlayer().sendMessage(Methods.color(Main.settings.getMsg().getString("Messages.Break-Enchantment-Shop-Sign")));
				return;
			}
		}
	}
	
	private String placeHolders(String msg, String cat){
		msg=Methods.color(msg);
		msg=msg.replaceAll("%category%", cat).replaceAll("%Category%", cat);
		msg=msg.replaceAll("%cost%", Main.settings.getConfig().getInt("Categories."+cat+".Cost")+"").replaceAll("%Cost%", Main.settings.getConfig().getInt("Categories."+cat+".Cost")+"");
		msg=msg.replaceAll("%xp%", Main.settings.getConfig().getInt("Categories."+cat+".Cost")+"").replaceAll("%XP%", Main.settings.getConfig().getInt("Categories."+cat+".Cost")+"");
		return msg;
	}
	
	@EventHandler
	public void onSignMake(SignChangeEvent e){
		Player player = e.getPlayer();
		Location loc = e.getBlock().getLocation();
		int size = Main.settings.getSigns().getConfigurationSection("Locations").getKeys(false).size()+1;
		String line1 = e.getLine(0);
		String line2 = e.getLine(1);
		if(Methods.hasPermission(player, "Sign", false)){
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
				if(line2.equalsIgnoreCase("{ProtectCrystal}")){
					e.setLine(0, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.ProtectionCrystalStyle.Line1")));
					e.setLine(1, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.ProtectionCrystalStyle.Line2")));
					e.setLine(2, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.ProtectionCrystalStyle.Line3")));
					e.setLine(3, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.ProtectionCrystalStyle.Line4")));
					Main.settings.getSigns().set("Locations."+size+".Type", "ProtectionCrystal");
					Main.settings.getSigns().set("Locations."+size+".World", loc.getWorld().getName());
					Main.settings.getSigns().set("Locations."+size+".X", loc.getBlockX());
					Main.settings.getSigns().set("Locations."+size+".Y", loc.getBlockY());
					Main.settings.getSigns().set("Locations."+size+".Z", loc.getBlockZ());
					Main.settings.saveSigns();
					return;
				}
				if(line2.equalsIgnoreCase("{SuccessDust}")){
					e.setLine(0, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.SuccessDustStyle.Line1")));
					e.setLine(1, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.SuccessDustStyle.Line2")));
					e.setLine(2, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.SuccessDustStyle.Line3")));
					e.setLine(3, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.SuccessDustStyle.Line4")));
					Main.settings.getSigns().set("Locations."+size+".Type", "SuccessDust");
					Main.settings.getSigns().set("Locations."+size+".World", loc.getWorld().getName());
					Main.settings.getSigns().set("Locations."+size+".X", loc.getBlockX());
					Main.settings.getSigns().set("Locations."+size+".Y", loc.getBlockY());
					Main.settings.getSigns().set("Locations."+size+".Z", loc.getBlockZ());
					Main.settings.saveSigns();
					return;
				}
				if(line2.equalsIgnoreCase("{DestroyDust}")){
					e.setLine(0, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.DestroyDustStyle.Line1")));
					e.setLine(1, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.DestroyDustStyle.Line2")));
					e.setLine(2, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.DestroyDustStyle.Line3")));
					e.setLine(3, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.DestroyDustStyle.Line4")));
					Main.settings.getSigns().set("Locations."+size+".Type", "DestroyDust");
					Main.settings.getSigns().set("Locations."+size+".World", loc.getWorld().getName());
					Main.settings.getSigns().set("Locations."+size+".X", loc.getBlockX());
					Main.settings.getSigns().set("Locations."+size+".Y", loc.getBlockY());
					Main.settings.getSigns().set("Locations."+size+".Z", loc.getBlockZ());
					Main.settings.saveSigns();
					return;
				}
				if(line2.equalsIgnoreCase("{BlackScroll}")){
					e.setLine(0, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.BlackScrollStyle.Line1")));
					e.setLine(1, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.BlackScrollStyle.Line2")));
					e.setLine(2, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.BlackScrollStyle.Line3")));
					e.setLine(3, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.BlackScrollStyle.Line4")));
					Main.settings.getSigns().set("Locations."+size+".Type", "BlackScroll");
					Main.settings.getSigns().set("Locations."+size+".World", loc.getWorld().getName());
					Main.settings.getSigns().set("Locations."+size+".X", loc.getBlockX());
					Main.settings.getSigns().set("Locations."+size+".Y", loc.getBlockY());
					Main.settings.getSigns().set("Locations."+size+".Z", loc.getBlockZ());
					Main.settings.saveSigns();
					return;
				}
				if(line2.equalsIgnoreCase("{WhiteScroll}")){
					e.setLine(0, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.WhiteScrollStyle.Line1")));
					e.setLine(1, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.WhiteScrollStyle.Line2")));
					e.setLine(2, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.WhiteScrollStyle.Line3")));
					e.setLine(3, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions.WhiteScrollStyle.Line4")));
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
	
}
package me.badbones69.crazyenchantments.controlers;

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

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.currencyapi.Currency;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;

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
					List<String> options = new ArrayList<String>();
					options.add("ProtectionCrystal");
					options.add("Scrambler");
					options.add("DestroyDust");
					options.add("SuccessDust");
					options.add("BlackScroll");
					options.add("WhiteScroll");
					options.add("TransmogScroll");
					for(String o : options){
						if(o.equalsIgnoreCase(type)){
							if(player.getGameMode() != GameMode.CREATIVE){
								if(Currency.isCurrency(config.getString("Settings.Costs." + o + ".Currency"))){
									Currency currency = Currency.getCurrency(config.getString("Settings.Costs." + o + ".Currency"));
									int cost = config.getInt("Settings.Costs." + o + ".Cost");
									if(CurrencyAPI.canBuy(player, currency, cost)){
										CurrencyAPI.takeCurrency(player, currency, cost);
									}else{
										String needed = (cost - CurrencyAPI.getCurrency(player, currency)) + "";
										switch(currency){
											case VAULT:
												player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Need-More-Money")
														.replace("%Money_Needed%", needed).replace("%money_needed%", needed)));
												break;
											case XP_LEVEL:
												player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Need-More-XP-Lvls")
														.replace("%XP%", needed).replace("%xp%", needed)));
												break;
											case XP_TOTAL:
												player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Need-More-Total-XP")
														.replace("%XP%", needed).replace("%xp%", needed)));
												break;
										}
										return;
									}
								}
							}
							if(config.contains("Settings.SignOptions."+o+"Style.Buy-Message")){
								player.sendMessage(Methods.color(Methods.getPrefix()+config.getString("Settings.SignOptions."+o+"Style.Buy-Message")));
							}
							switch(o){
								case "ProtectionCrystal": player.getInventory().addItem(ProtectionCrystal.getCrystals());
									break;
								case "Scrambler": player.getInventory().addItem(Scrambler.getScramblers());
									break;
								case "DestroyDust": player.getInventory().addItem(DustControl.getDust("DestroyDust", 1));
									break;
								case "SuccessDust": player.getInventory().addItem(DustControl.getDust("SuccessDust", 1));
									break;
								case "BlackScroll": player.getInventory().addItem(ScrollControl.getBlackScroll(1));
									break;
								case "WhiteScroll": player.getInventory().addItem(ScrollControl.getWhiteScroll(1));
									break;
								case "TransmogScroll": player.getInventory().addItem(ScrollControl.getTransmogScroll(1));
									break;
							}
							return;
						}
					}
					for(String cat : config.getConfigurationSection("Categories").getKeys(false)){
						if(type.equalsIgnoreCase(cat)){
							if(player.getGameMode() != GameMode.CREATIVE){
								if(Currency.isCurrency(config.getString("Categories." + cat + ".Currency"))){
									Currency currency = Currency.getCurrency(config.getString("Categories." + cat + ".Currency"));
									int cost = config.getInt("Categories." + cat + ".Cost");
									if(CurrencyAPI.canBuy(player, currency, cost)){
										CurrencyAPI.takeCurrency(player, currency, cost);
									}else{
										String needed = (cost - CurrencyAPI.getCurrency(player, currency)) + "";
										switch(currency){
											case VAULT:
												player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Need-More-Money")
														.replace("%Money_Needed%", needed).replace("%money_needed%", needed)));
												break;
											case XP_LEVEL:
												player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Need-More-XP-Lvls")
														.replace("%XP%", needed).replace("%xp%", needed)));
												break;
											case XP_TOTAL:
												player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Need-More-Total-XP")
														.replace("%XP%", needed).replace("%xp%", needed)));
												break;
										}
										return;
									}
								}
							}
							ItemStack item = EnchantmentControl.pick(cat);
							item = Methods.addGlow(item);
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
				e.getPlayer().sendMessage(Methods.color(Methods.getPrefix() + Main.settings.getMsg().getString("Messages.Break-Enchantment-Shop-Sign")));
				return;
			}
		}
	}
	
	private String placeHolders(String msg, String cat){
		msg = Methods.color(msg);
		msg = msg.replaceAll("%category%", cat).replaceAll("%Category%", cat);
		msg = msg.replaceAll("%cost%", Main.settings.getConfig().getInt("Categories."+cat+".Cost")+"").replaceAll("%Cost%", Main.settings.getConfig().getInt("Categories."+cat+".Cost")+"");
		msg = msg.replaceAll("%xp%", Main.settings.getConfig().getInt("Categories."+cat+".Cost")+"").replaceAll("%XP%", Main.settings.getConfig().getInt("Categories."+cat+".Cost")+"");
		return msg;
	}
	
	@EventHandler
	public void onSignMake(SignChangeEvent e){
		Player player = e.getPlayer();
		Location loc = e.getBlock().getLocation();
		FileConfiguration signs = Main.settings.getSigns();
		int size = signs.getConfigurationSection("Locations").getKeys(false).size()+1;
		String line1 = e.getLine(0);
		String line2 = e.getLine(1);
		if(Methods.hasPermission(player, "sign", false)){
			if(line1.equalsIgnoreCase("{CrazyEnchant}")){
				for(String cat : Main.settings.getConfig().getConfigurationSection("Categories").getKeys(false)){
					if(line2.equalsIgnoreCase("{"+cat+"}")){
						e.setLine(0, placeHolders(Main.settings.getConfig().getString("Settings.SignOptions.CategoryShopStyle.Line1"),cat));
						e.setLine(1, placeHolders(Main.settings.getConfig().getString("Settings.SignOptions.CategoryShopStyle.Line2"),cat));
						e.setLine(2, placeHolders(Main.settings.getConfig().getString("Settings.SignOptions.CategoryShopStyle.Line3"),cat));
						e.setLine(3, placeHolders(Main.settings.getConfig().getString("Settings.SignOptions.CategoryShopStyle.Line4"),cat));
						signs.set("Locations." + size + ".Type", cat);
						signs.set("Locations." + size + ".World", loc.getWorld().getName());
						signs.set("Locations." + size + ".X", loc.getBlockX());
						signs.set("Locations." + size + ".Y", loc.getBlockY());
						signs.set("Locations." + size + ".Z", loc.getBlockZ());
						Main.settings.saveSigns();
						return;
					}
				}
				ArrayList<String> types = new ArrayList<String>();
				types.add("ProtectionCrystal");
				types.add("Scrambler");
				types.add("DestroyDust");
				types.add("SuccessDust");
				types.add("BlackScroll");
				types.add("WhiteScroll");
				types.add("TransmogScroll");
				for(String type : types){
					if(line2.equalsIgnoreCase("{" + type + "}")){
						e.setLine(0, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions." + type + "Style.Line1")));
						e.setLine(1, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions." + type + "Style.Line2")));
						e.setLine(2, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions." + type + "Style.Line3")));
						e.setLine(3, Methods.color(Main.settings.getConfig().getString("Settings.SignOptions." + type + "Style.Line4")));
						signs.set("Locations." + size + ".Type", type);
						signs.set("Locations." + size + ".World", loc.getWorld().getName());
						signs.set("Locations." + size + ".X", loc.getBlockX());
						signs.set("Locations." + size + ".Y", loc.getBlockY());
						signs.set("Locations." + size + ".Z", loc.getBlockZ());
						Main.settings.saveSigns();
						return;
					}
				}
			}
		}
	}
	
}
package me.BadBones69.CrazyEnchantments.Controlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.BadBones69.CrazyEnchantments.Main;
import me.BadBones69.CrazyEnchantments.Methods;
import me.BadBones69.CrazyEnchantments.API.EnchantmentType;
import me.BadBones69.CrazyEnchantments.API.InfoType;
import me.BadBones69.CrazyEnchantments.API.Version;

public class ShopGUI implements Listener{
	
	public static void openGUI(Player player){
		Inventory inv = Bukkit.createInventory(null, Main.settings.getConfig().getInt("Settings.GUISize"), Methods.getInvName());
		if(Main.settings.getConfig().contains("Settings.GUICustomization")){
			for(String custom : Main.settings.getConfig().getStringList("Settings.GUICustomization")){
				String name = "";
				String item = "1";
				int slot = 0;
				ArrayList<String> lore = new ArrayList<String>();
				String[] b = custom.split(", ");
				for(String i : b){
					if(i.contains("Item:")){
						i = i.replace("Item:", "");
						item = i;
					}
					if(i.contains("Name:")){
						i = i.replace("Name:", "");
						name = i;
					}
					if(i.contains("Slot:")){
						i = i.replace("Slot:", "");
						slot = Integer.parseInt(i);
					}
					if(i.contains("Lore:")){
						i = i.replace("Lore:", "");
						String[] d = i.split("_");
						for(String l : d){
							lore.add(l);
						}
					}
				}
				slot--;
				inv.setItem(slot, Methods.makeItem(item, 1, name, lore));
			}
		}
		for(String cat : Main.settings.getConfig().getConfigurationSection("Categories").getKeys(false)){
			if(Main.settings.getConfig().contains("Categories."+cat+".InGUI")){
				if(Main.settings.getConfig().getBoolean("Categories."+cat+".InGUI")){
					inv.setItem(Main.settings.getConfig().getInt("Categories."+cat+".Slot")-1, Methods.makeItem(Main.settings.getConfig().getString("Categories."+cat+".Item"), 1, 
							Main.settings.getConfig().getString("Categories."+cat+".Name"), Main.settings.getConfig().getStringList("Categories."+cat+".Lore")));
				}
			}else{
				inv.setItem(Main.settings.getConfig().getInt("Categories."+cat+".Slot")-1, Methods.makeItem(Main.settings.getConfig().getString("Categories."+cat+".Item"), 1, 
						Main.settings.getConfig().getString("Categories."+cat+".Name"), Main.settings.getConfig().getStringList("Categories."+cat+".Lore")));
			}
			FileConfiguration config = Main.settings.getConfig();
			if(config.contains("Categories."+cat+".LostBook")){
				if(config.getBoolean("Categories."+cat+".LostBook.InGUI")){
					int slot = config.getInt("Categories."+cat+".LostBook.Slot");
					String id = config.getString("Categories."+cat+".LostBook.Item");
					String name = config.getString("Categories."+cat+".LostBook.Name");
					List<String> lore = config.getStringList("Categories."+cat+".LostBook.Lore");
					if(config.getBoolean("Categories."+cat+".LostBook.Glowing")){
						inv.setItem(slot-1, Methods.addGlow(Methods.makeItem(id, 1, name, lore)));
					}else{
						inv.setItem(slot-1, Methods.makeItem(id, 1, name, lore));
					}
				}
			}
		}
		ArrayList<String> options = new ArrayList<String>();
		options.add("GKitz");
		options.add("BlackSmith");
		options.add("Tinker");
		options.add("Info");
		for(String op : options){
			if(Main.settings.getConfig().contains("Settings." + op)){
				if(Main.settings.getConfig().getBoolean("Settings." + op + ".InGUI")){
					String name = Main.settings.getConfig().getString("Settings." + op + ".Name");
					String id = Main.settings.getConfig().getString("Settings." + op + ".Item");
					List<String> lore = Main.settings.getConfig().getStringList("Settings." + op + ".Lore");
					int slot = Main.settings.getConfig().getInt("Settings." + op + ".Slot")-1;
					boolean glowing = false;
					if(Main.settings.getConfig().contains("Settings." + op + ".Glowing")){
						glowing = Main.settings.getConfig().getBoolean("Settings." + op + ".Glowing");
					}
					inv.setItem(slot, Methods.addGlow(Methods.makeItem(id, 1, name, lore), glowing));
				}
			}
		}
		options.clear();
		options.add("ProtectionCrystal");
		options.add("Dust.SuccessDust");
		options.add("Dust.DestroyDust");
		options.add("Scrambler");
		for(String op : options){
			if(Main.settings.getConfig().contains("Settings." + op)){
				if(Main.settings.getConfig().getBoolean("Settings." + op + ".InGUI")){
					String name = Main.settings.getConfig().getString("Settings." + op + ".GUIName");
					String id = Main.settings.getConfig().getString("Settings." + op + ".Item");
					List<String> lore = Main.settings.getConfig().getStringList("Settings." + op + ".GUILore");
					int slot = Main.settings.getConfig().getInt("Settings." + op + ".Slot")-1;
					boolean glowing = false;
					if(Main.settings.getConfig().contains("Settings." + op + ".Glowing")){
						glowing = Main.settings.getConfig().getBoolean("Settings." + op + ".Glowing");
					}
					inv.setItem(slot, Methods.addGlow(Methods.makeItem(id, 1, name, lore), glowing));
				}
			}
		}
		options.clear();
		options.add("BlackScroll");
		options.add("WhiteScroll");
		options.add("TransmogScroll");
		for(String op : options){
			if(Main.settings.getConfig().contains("Settings." + op)){
				if(Main.settings.getConfig().getBoolean("Settings." + op + ".InGUI")){
					String name = Main.settings.getConfig().getString("Settings." + op + ".GUIName");
					String id = Main.settings.getConfig().getString("Settings." + op + ".Item");
					List<String> lore = Main.settings.getConfig().getStringList("Settings." + op + ".Lore");
					int slot = Main.settings.getConfig().getInt("Settings." + op + ".Slot")-1;
					boolean glowing = false;
					if(Main.settings.getConfig().contains("Settings." + op + ".Glowing")){
						glowing = Main.settings.getConfig().getBoolean("Settings." + op + ".Glowing");
					}
					inv.setItem(slot, Methods.addGlow(Methods.makeItem(id, 1, name, lore), glowing));
				}
			}
		}
		player.openInventory(inv);
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		ItemStack item = e.getCurrentItem();
		Inventory inv = e.getInventory();
		Player player = (Player) e.getWhoClicked();
		if(inv!=null){
			if(inv.getName().equals(Methods.getInvName())){
				e.setCancelled(true);
				if(e.getRawSlot()>=inv.getSize())return;
				if(item == null)return;
				if(item.hasItemMeta()){
					if(item.getItemMeta().hasDisplayName()){
						String name = item.getItemMeta().getDisplayName();
						for(String cat : Main.settings.getConfig().getConfigurationSection("Categories").getKeys(false)){
							if(name.equals(Methods.color(Main.settings.getConfig().getString("Categories."+cat+".Name")))){
								if(Methods.isInvFull(player)){
									if(!Main.settings.getMsg().contains("Messages.Inventory-Full")){
										player.sendMessage(Methods.color("&cYour inventory is to full. Please open up some space to buy that."));
									}else{
										player.sendMessage(Methods.color(Main.settings.getMsg().getString("Messages.Inventory-Full")));
									}
									return;
								}
								if(player.getGameMode() != GameMode.CREATIVE){
									if(Main.settings.getConfig().contains("Categories."+cat+".Money/XP")&&Main.settings.getConfig().getString("Categories."+cat+".Money/XP").equalsIgnoreCase("Money")){
										if(Methods.getMoney(player)<Main.settings.getConfig().getInt("Categories."+cat+".Cost")){
											String money = Main.settings.getConfig().getInt("Categories."+cat+".Cost") - Methods.getMoney(player)+"";
											player.sendMessage(Methods.color(Main.settings.getMsg().getString("Messages.Need-More-Money").replace("%Money_Needed%", money).replace("%money_needed%", money)));
											return;
										}
										Main.econ.withdrawPlayer(player, Main.settings.getConfig().getInt("Categories."+cat+".Cost"));
									}else{
										if(Main.settings.getConfig().getString("Categories."+cat+".Lvl/Total").equalsIgnoreCase("Lvl")){
											if(Methods.getXPLvl(player)<Main.settings.getConfig().getInt("Categories."+cat+".Cost")){
												String xp = Main.settings.getConfig().getInt("Categories."+cat+".Cost") - Methods.getXPLvl(player)+"";
												player.sendMessage(Methods.color(Main.settings.getMsg().getString("Messages.Need-More-XP-Lvls").replace("%XP%", xp).replace("%xp%", xp)));
												return;
											}
											Methods.takeLvlXP(player, Main.settings.getConfig().getInt("Categories."+cat+".Cost"));
										}
										if(Main.settings.getConfig().getString("Categories."+cat+".Lvl/Total").equalsIgnoreCase("Total")){
											if(player.getTotalExperience()<Main.settings.getConfig().getInt("Categories."+cat+".Cost")){
												String xp = Main.settings.getConfig().getInt("Categories."+cat+".Cost") - player.getTotalExperience()+"";
												player.sendMessage(Methods.color(Main.settings.getMsg().getString("Messages.Need-More-Total-XP").replace("%XP%", xp).replace("%xp%", xp)));
												return;
											}
											Methods.takeTotalXP(player, Main.settings.getConfig().getInt("Categories."+cat+".Cost"));
										}
									}
								}
								player.getInventory().addItem(EnchantmentControl.pick(cat));
								return;
							}
						}
						for(String cat : Main.settings.getConfig().getConfigurationSection("Categories").getKeys(false)){
							if(name.equals(Methods.color(Main.settings.getConfig().getString("Categories."+cat+".LostBook.Name")))){
								if(Methods.isInvFull(player)){
									if(!Main.settings.getMsg().contains("Messages.Inventory-Full")){
										player.sendMessage(Methods.getPrefix() + Methods.color("&cYour inventory is to full. Please open up some space to buy that."));
									}else{
										player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Inventory-Full")));
									}
									return;
								}
								if(player.getGameMode() != GameMode.CREATIVE){
									if(Main.settings.getConfig().getString("Categories."+cat+".LostBook.Money/XP").equalsIgnoreCase("Money")){
										if(Methods.getMoney(player)<Main.settings.getConfig().getInt("Categories."+cat+".LostBook.Cost")){
											String money = Main.settings.getConfig().getInt("Categories."+cat+".LostBook.Cost") - Methods.getMoney(player)+"";
											player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Need-More-Money").replace("%Money_Needed%", money).replace("%money_needed%", money)));
											return;
										}
										Main.econ.withdrawPlayer(player, Main.settings.getConfig().getInt("Categories."+cat+".LostBook.Cost"));
									}else{
										if(Main.settings.getConfig().getString("Categories."+cat+".LostBook.Lvl/Total").equalsIgnoreCase("Lvl")){
											if(Methods.getXPLvl(player)<Main.settings.getConfig().getInt("Categories."+cat+".LostBook.Cost")){
												String xp = Main.settings.getConfig().getInt("Categories."+cat+".LostBook.Cost") - Methods.getXPLvl(player)+"";
												player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Need-More-XP-Lvls").replace("%XP%", xp).replace("%xp%", xp)));
												return;
											}
											Methods.takeLvlXP(player, Main.settings.getConfig().getInt("Categories."+cat+".LostBook.Cost"));
										}
										if(Main.settings.getConfig().getString("Categories."+cat+".LostBook.Lvl/Total").equalsIgnoreCase("Total")){
											if(player.getTotalExperience() < Main.settings.getConfig().getInt("Categories."+cat+".LostBook.Cost")){
												String xp = Main.settings.getConfig().getInt("Categories."+cat+".LostBook.Cost") - player.getTotalExperience()+"";
												player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Need-More-Total-XP").replace("%XP%", xp).replace("%xp%", xp)));
												return;
											}
											Methods.takeTotalXP(player, Main.settings.getConfig().getInt("Categories."+cat+".LostBook.Cost"));
										}
									}
								}
								player.getInventory().addItem(LostBook.getLostBook(cat, 1));
								return;
							}
						}
						if(name.equalsIgnoreCase(Methods.color(Main.settings.getConfig().getString("Settings.GKitz.Name")))){
							if(!Methods.hasPermission(player, "gkitz", true))return;
							GKitzGUI.openGUI(player);
							return;
						}
						if(name.equalsIgnoreCase(Methods.color(Main.settings.getConfig().getString("Settings.BlackSmith.Name")))){
							if(!Methods.hasPermission(player, "BlackSmith", true))return;
							BlackSmith.openBlackSmith(player);
							return;
						}
						if(name.equalsIgnoreCase(Methods.color(Main.settings.getConfig().getString("Settings.Tinker.Name")))){
							if(!Methods.hasPermission(player, "Tinker", true))return;
							Tinkerer.openTinker(player);
							return;
						}
						if(name.equalsIgnoreCase(Methods.color(Main.settings.getConfig().getString("Settings.Info.Name")))){
							openInfo(player);
							return;
						}
						List<String> options = new ArrayList<String>();
						options.add("BlackScroll");
						options.add("WhiteScroll");
						options.add("TransmogScroll");
						options.add("ProtectionCrystal");
						options.add("Scrambler");
						for(String o : options){
							if(name.equalsIgnoreCase(Methods.color(Main.settings.getConfig().getString("Settings." + o + ".GUIName")))){
								if(Methods.isInvFull(player)){
									if(!Main.settings.getMsg().contains("Messages.Inventory-Full")){
										player.sendMessage(Methods.getPrefix() + Methods.color("&cYour inventory is to full. Please open up some space to buy that."));
									}else{
										player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Inventory-Full")));
									}
									return;
								}
								int price = Main.settings.getConfig().getInt("Settings.SignOptions." + o + "Style.Cost");
								if(player.getGameMode() != GameMode.CREATIVE){
									if(Main.settings.getConfig().getString("Settings.SignOptions." + o + "Style.Money/XP").equalsIgnoreCase("Money")){
										if(Methods.getMoney(player)<price){
											double needed = price-Methods.getMoney(player);
											player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Need-More-Money").replace("%Money_Needed%", needed+"").replace("%money_needed%", needed+"")));
											return;
										}
										Main.econ.withdrawPlayer(player, price);
									}else{
										if(Main.settings.getConfig().getString("Settings.SignOptions." + o + "Style.Lvl/Total").equalsIgnoreCase("Lvl")){
											if(Methods.getXPLvl(player)<price){
												String xp = price - Methods.getXPLvl(player)+"";
												player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Need-More-XP-Lvls").replace("%XP%", xp).replace("%xp%", xp)));
												return;
											}
											Methods.takeLvlXP(player, price);
										}
										if(Main.settings.getConfig().getString("Settings.SignOptions." + o + "Style.Lvl/Total").equalsIgnoreCase("Total")){
											if(player.getTotalExperience()<price){
												String xp = price - player.getTotalExperience()+"";
												player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Need-More-Total-XP").replace("%XP%", xp).replace("%xp%", xp)));
												return;
											}
											Methods.takeTotalXP(player, price);
										}
									}
								}
								switch(o){
									case "BlackScroll":
										player.getInventory().addItem(ScrollControl.getBlackScroll(1));
										break;
									case "WhiteScroll":
										player.getInventory().addItem(ScrollControl.getWhiteScroll(1));
										break;
									case "TransmogScroll":
										player.getInventory().addItem(ScrollControl.getTransmogScroll(1));
										break;
									case "ProtectionCrystal":
										player.getInventory().addItem(ProtectionCrystal.getCrystals());
										break;
									case "Scrambler":
										player.getInventory().addItem(Scrambler.getScramblers());
										break;
								}
								return;
							}
						}
						options.clear();
						options.add("DestroyDust");
						options.add("SuccessDust");
						for(String o : options){
							if(name.equalsIgnoreCase(Methods.color(Main.settings.getConfig().getString("Settings.Dust." + o + ".GUIName")))){
								if(Methods.isInvFull(player)){
									if(!Main.settings.getMsg().contains("Messages.Inventory-Full")){
										player.sendMessage(Methods.getPrefix() + Methods.color("&cYour inventory is to full. Please open up some space to buy that."));
									}else{
										player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Inventory-Full")));
									}
									return;
								}
								int price = Main.settings.getConfig().getInt("Settings.SignOptions." + o + "Style.Cost");
								if(player.getGameMode() != GameMode.CREATIVE){
									if(Main.settings.getConfig().getString("Settings.SignOptions." + o + "Style.Money/XP").equalsIgnoreCase("Money")){
										if(Methods.getMoney(player)<price){
											double needed = price-Methods.getMoney(player);
											player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Need-More-Money").replace("%Money_Needed%", needed+"").replace("%money_needed%", needed+"")));
											return;
										}
										Main.econ.withdrawPlayer(player, price);
									}else{
										if(Main.settings.getConfig().getString("Settings.SignOptions." + o + "Style.Lvl/Total").equalsIgnoreCase("Lvl")){
											if(Methods.getXPLvl(player)<price){
												String xp = price - Methods.getXPLvl(player)+"";
												player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Need-More-XP-Lvls").replace("%XP%", xp).replace("%xp%", xp)));
												return;
											}
											Methods.takeLvlXP(player, price);
										}
										if(Main.settings.getConfig().getString("Settings.SignOptions." + o + "Style.Lvl/Total").equalsIgnoreCase("Total")){
											if(player.getTotalExperience()<price){
												String xp = price - player.getTotalExperience()+"";
												player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Need-More-Total-XP").replace("%XP%", xp).replace("%xp%", xp)));
												return;
											}
											Methods.takeTotalXP(player, price);
										}
									}
								}
								switch(o){
									case "DestroyDust":
										player.getInventory().addItem(DustControl.getDust("DestroyDust", 1));
										break;
									case "SuccessDust":
										player.getInventory().addItem(DustControl.getDust("SuccessDust", 1));
										break;
								}
								return;
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void infoClick(InventoryClickEvent e){
		Inventory inv = e.getInventory();
		if(inv != null){
			if(inv.getName().equals(Methods.color(Main.settings.getMsg().getString("Messages.InfoGUI.Inventory.Name")))){
				e.setCancelled(true);
				if(e.getCurrentItem() != null){
					ItemStack item = e.getCurrentItem();
					if(item.hasItemMeta()){
						if(item.getItemMeta().hasDisplayName()){
							Player player = (Player) e.getWhoClicked();
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getMsg().getString("Messages.InfoGUI.Categories-Info.Back.Right")))||item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getMsg().getString("Messages.InfoGUI.Categories-Info.Back.Left")))){
								openInfo((Player)player);
								return;
							}
							for(InfoType ty : InfoType.getTypes()){
								String type = ty.getName();
								if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getMsg().getString("Messages.InfoGUI.Categories-Info."+type+".Name")))){
									openInfo(player, ty);
								}
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getMsg().getString("Messages.InfoGUI.Categories-Info.Other.Name")))){
								Inventory in = Bukkit.createInventory(null, 18, Methods.color(Main.settings.getMsg().getString("Messages.InfoGUI.Inventory.Name")));
								in.setItem(2, Methods.makeItem(Main.settings.getConfig().getString("Settings.BlackScroll.Item"),
										1, Main.settings.getConfig().getString("Settings.BlackScroll.Name"),
										Main.settings.getMsg().getStringList("Messages.InfoGUI.Black-Scroll")));
								in.setItem(11, Methods.makeItem(Main.settings.getConfig().getString("Settings.WhiteScroll.Item"),
										1, Main.settings.getConfig().getString("Settings.WhiteScroll.Name"),
										Main.settings.getMsg().getStringList("Messages.InfoGUI.White-Scroll")));
								in.setItem(4, Methods.makeItem(Main.settings.getConfig().getString("Settings.Tinker.Item"),
										1, Main.settings.getConfig().getString("Settings.Tinker.Name"),
										Main.settings.getMsg().getStringList("Messages.InfoGUI.Tinker")));
								in.setItem(13, Methods.makeItem(Main.settings.getConfig().getString("Settings.BlackSmith.Item"),
										1, Main.settings.getConfig().getString("Settings.BlackSmith.Name"),
										Main.settings.getMsg().getStringList("Messages.InfoGUI.BlackSmith")));
								in.setItem(6, Methods.makeItem(Main.settings.getConfig().getString("Settings.Dust.SuccessDust.Item"),
										1, Main.settings.getConfig().getString("Settings.Dust.SuccessDust.Name"),
										Main.settings.getMsg().getStringList("Messages.InfoGUI.Success-Dust")));
								in.setItem(15, Methods.makeItem(Main.settings.getConfig().getString("Settings.Dust.DestroyDust.Item"),
										1, Main.settings.getConfig().getString("Settings.Dust.DestroyDust.Name"),
										Main.settings.getMsg().getStringList("Messages.InfoGUI.Destroy-Dust")));
								if(Version.getVersion().getVersionInteger()<181){
									ItemStack left = Methods.makeItem(Material.FEATHER, 1, 0, Main.settings.getMsg().getString("Messages.InfoGUI.Categories-Info.Back.Left"));
									ItemStack right = Methods.makeItem(Material.FEATHER, 1, 0, Main.settings.getMsg().getString("Messages.InfoGUI.Categories-Info.Back.Right"));
									in.setItem(0, left);
									in.setItem(8, right);
									in.setItem(9, left);
									in.setItem(17, right);
								}else{
									ItemStack left = Methods.makeItem(Material.PRISMARINE_CRYSTALS, 1, 0, Main.settings.getMsg().getString("Messages.InfoGUI.Categories-Info.Back.Left"));
									ItemStack right = Methods.makeItem(Material.PRISMARINE_CRYSTALS, 1, 0, Main.settings.getMsg().getString("Messages.InfoGUI.Categories-Info.Back.Right"));
									in.setItem(0, left);
									in.setItem(8, right);
									in.setItem(9, left);
									in.setItem(17, right);
								}
								player.openInventory(in);
								return;
							}
							String bar = Methods.color("&a&m------------------------------------------------");
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.BlackSmith.Name")))){
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.BlackSmith.Name")));
								for(String lore : Main.settings.getMsg().getStringList("Messages.InfoGUI.BlackSmith"))player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
								return;
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.BlackScroll.Name")))){
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.BlackScroll.Name")));
								for(String lore : Main.settings.getMsg().getStringList("Messages.InfoGUI.Black-Scroll"))player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
								return;
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.WhiteScroll.Name")))){
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.WhiteScroll.Name")));
								for(String lore : Main.settings.getMsg().getStringList("Messages.InfoGUI.White-Scroll"))player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.Tinker.Name")))){
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.Tinker.Name")));
								for(String lore : Main.settings.getMsg().getStringList("Messages.InfoGUI.Tinker"))player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
								return;
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.Dust.SuccessDust.Name")))){
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.Dust.SuccessDust.Name")));
								for(String lore : Main.settings.getMsg().getStringList("Messages.InfoGUI.Success-Dust"))player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
								return;
							}
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.Dust.DestroyDust.Name")))){
								player.closeInventory();
								player.sendMessage(bar);
								player.sendMessage(Methods.color(Main.settings.getConfig().getString("Settings.Dust.DestroyDust.Name")));
								for(String lore : Main.settings.getMsg().getStringList("Messages.InfoGUI.Destroy-Dust"))player.sendMessage(Methods.color(lore));
								player.sendMessage(bar);
								return;
							}
						}
					}
				}
				return;
			}
		}
	}
	
	public static void openInfo(Player player){
		FileConfiguration msg = Main.settings.getMsg();
		Inventory inv = Bukkit.createInventory(null, msg.getInt("Messages.InfoGUI.Inventory.Size"), Methods.color(msg.getString("Messages.InfoGUI.Inventory.Name")));
		ArrayList<String> options = new ArrayList<String>();
		options.add("Helmets");options.add("Boots");
		options.add("Armor");options.add("Bow");options.add("Sword");options.add("Axe");
		options.add("Tool");options.add("Pickaxe");options.add("Misc");options.add("Other");
		for(String o : options){
			inv.setItem(msg.getInt("Messages.InfoGUI.Categories-Info." + o + ".Slot") - 1, Methods.makeItem(msg.getString("Messages.InfoGUI.Categories-Info." + o + ".Item"), 1,
					msg.getString("Messages.InfoGUI.Categories-Info." + o + ".Name"), msg.getStringList("Messages.InfoGUI.Categories-Info." + o + ".Lore")));
		}
		player.openInventory(inv);
	}
	
	public static void openInfo(Player player, InfoType type){
		int size = getInfo(type.getName()).size()+1;
		int slots = 9;
		for(;size > 9; size -= 9)slots += 9;
		Inventory in = Bukkit.createInventory(null, slots, Methods.color(Main.settings.getMsg().getString("Messages.InfoGUI.Inventory.Name")));
		for(ItemStack i : getInfo(type.getName())){
			in.addItem(i);
		}
		if(Version.getVersion().getVersionInteger() < 181){
			in.setItem(slots-1, Methods.makeItem(Material.FEATHER, 1, 0, Main.settings.getMsg().getString("Messages.InfoGUI.Categories-Info.Back.Right")));
		}else{
			in.setItem(slots-1, Methods.makeItem(Material.PRISMARINE_CRYSTALS, 1, 0, Main.settings.getMsg().getString("Messages.InfoGUI.Categories-Info.Back.Right")));
		}
		player.openInventory(in);
		return;
	}
	
	public static ArrayList<ItemStack> getInfo(String type){
		FileConfiguration enchants = Main.settings.getEnchs();
		FileConfiguration customEnchants = Main.settings.getCustomEnchs();
		ArrayList<ItemStack> swords = new ArrayList<ItemStack>();
		ArrayList<ItemStack> axes = new ArrayList<ItemStack>();
		ArrayList<ItemStack> bows = new ArrayList<ItemStack>();
		ArrayList<ItemStack> armor = new ArrayList<ItemStack>();
		ArrayList<ItemStack> helmets = new ArrayList<ItemStack>();
		ArrayList<ItemStack> boots = new ArrayList<ItemStack>();
		ArrayList<ItemStack> picks = new ArrayList<ItemStack>();
		ArrayList<ItemStack> tools = new ArrayList<ItemStack>();
		ArrayList<ItemStack> misc = new ArrayList<ItemStack>();
		for(String en : enchants.getConfigurationSection("Enchantments").getKeys(false)){
			if(enchants.getBoolean("Enchantments."+en+".Enabled")){
				String name = enchants.getString("Enchantments."+en+".Info.Name");
				List<String> desc = enchants.getStringList("Enchantments."+en+".Info.Description");
				EnchantmentType enchantType = Main.CE.getFromName(en).getType();
				ItemStack i = Methods.addGlow(Methods.makeItem(Main.settings.getConfig().getString("Settings.Enchantment-Book-Item"), 1, name, desc));
				if(enchantType == EnchantmentType.ARMOR)armor.add(i);
				if(enchantType == EnchantmentType.SWORD)swords.add(i);
				if(enchantType == EnchantmentType.AXE)axes.add(i);
				if(enchantType == EnchantmentType.BOW)bows.add(i);
				if(enchantType == EnchantmentType.HELMET)helmets.add(i);
				if(enchantType == EnchantmentType.BOOTS)boots.add(i);
				if(enchantType == EnchantmentType.PICKAXE)picks.add(i);
				if(enchantType == EnchantmentType.TOOL)tools.add(i);
				if(enchantType == EnchantmentType.ALL)misc.add(i);
				if(enchantType == EnchantmentType.WEAPONS)misc.add(i);
			}
		}
		for(String enchantment : Main.CustomE.getEnchantments()){
			if(Main.CustomE.isEnabled(enchantment)){
				String name = customEnchants.getString("Enchantments."+enchantment+".Info.Name");
				List<String> desc = Main.CustomE.getDiscription(enchantment);
				EnchantmentType enchantType = Main.CustomE.getType(enchantment);
				ItemStack i = Methods.addGlow(Methods.makeItem(Main.settings.getConfig().getString("Settings.Enchantment-Book-Item"), 1, name, desc));
				if(enchantType == EnchantmentType.ARMOR)armor.add(i);
				if(enchantType == EnchantmentType.SWORD)swords.add(i);
				if(enchantType == EnchantmentType.AXE)axes.add(i);
				if(enchantType == EnchantmentType.BOW)bows.add(i);
				if(enchantType == EnchantmentType.HELMET)helmets.add(i);
				if(enchantType == EnchantmentType.BOOTS)boots.add(i);
				if(enchantType == EnchantmentType.PICKAXE)picks.add(i);
				if(enchantType == EnchantmentType.TOOL)tools.add(i);
				if(enchantType == EnchantmentType.ALL)misc.add(i);
				if(enchantType == EnchantmentType.WEAPONS)misc.add(i);
			}
		}
		if(type.equalsIgnoreCase("Armor"))return armor;
		if(type.equalsIgnoreCase("Sword"))return swords;
		if(type.equalsIgnoreCase("Helmets"))return helmets;
		if(type.equalsIgnoreCase("Boots"))return boots;
		if(type.equalsIgnoreCase("Bow"))return bows;
		if(type.equalsIgnoreCase("Axe"))return axes;
		if(type.equalsIgnoreCase("Pickaxe"))return picks;
		if(type.equalsIgnoreCase("Tool"))return tools;
		if(type.equalsIgnoreCase("Misc"))return misc;
		return null;
	}
	
}
package me.BadBones69.CrazyEnchantments.Controlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.BadBones69.CrazyEnchantments.Api;
import me.BadBones69.CrazyEnchantments.Main;
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.EnchantmentType;

public class EnchantmentControl implements Listener{
	
	static HashMap<String, String> enchants = new HashMap<String, String>();
	
	public static String Enchants(String cat){
		Random number = new Random();
		List<String> enchantments = new ArrayList<String>();
		for(CEnchantments en : Main.CE.getEnchantments()){
			for(String C : Main.settings.getEnchs().getStringList("Enchantments."+en.getName()+".Categories")){
				if(cat.equalsIgnoreCase(C)){
					if(en.isEnabled()){
						String power = powerPicker(en.getName(), cat);
						enchants.put(en.getName(), en.getBookColor()+en.getCustomName()+" "+power);
						enchantments.add(en.getName());
					}
				}
			}
		}
		if(Main.settings.getCustomEnchs().contains("Enchantments")){
			for(String en : Main.CustomE.getEnchantments()){
				for(String C : Main.settings.getCustomEnchs().getStringList("Enchantments."+en+".Categories")){
					if(cat.equalsIgnoreCase(C)){
						if(Main.CustomE.isEnabled(en)){
							String power = powerPicker(en, cat);
							enchants.put(en, Main.CustomE.getBookColor(en)+Main.CustomE.getCustomName(en)+" "+power);
							enchantments.add(en);
						}
					}
				}
			}
		}
		return enchantments.get(number.nextInt(enchantments.size()));
	}
	
	@EventHandler
	public void addEnchantment(InventoryClickEvent e){
		Inventory inv = e.getInventory();
		Player player = (Player) e.getWhoClicked();
		if(inv != null){
			if(e.getCursor() != null&&e.getCurrentItem() != null){
				ItemStack c = e.getCursor();
				ItemStack item  = e.getCurrentItem();
				if(c.hasItemMeta()){
					if(c.getItemMeta().hasDisplayName()){
						if(c.getType()!=Main.CE.getEnchantmentBookItem().getType())return;
						String name = c.getItemMeta().getDisplayName();
						String enchant = "Glowing";
						String enchantColor = "&7";
						EnchantmentType type = EnchantmentType.ALL;
						for(CEnchantments en : Main.CE.getEnchantments()){
							if(name.contains(Api.color(en.getBookColor()+en.getCustomName()))){
								enchant = en.getCustomName();
								enchantColor = en.getEnchantmentColor();
								type = en.getType();
							}
						}
						for(String en : Main.CustomE.getEnchantments()){
							if(name.contains(Api.color(Main.CustomE.getBookColor(en)+Main.CustomE.getCustomName(en)))){
								enchant = Main.CustomE.getCustomName(en);
								enchantColor = Main.CustomE.getEnchantmentColor(en);
								type = Main.CustomE.getType(en);
							}
						}
						if(type.getItems().contains(item.getType())){
							if(c.getAmount() == 1){
								boolean success = successChance(c);
								boolean destroy = destroyChance(c);
								if(item.getItemMeta().hasLore()){
									for(String l : item.getItemMeta().getLore()){
										if(l.contains(enchant)){
											return;
										}
									}
								}
								if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle")){
									int limit = 0;
									int total = Api.getEnchAmount(item);
									for(PermissionAttachmentInfo Permission : player.getEffectivePermissions()){
										String perm = Permission.getPermission();
										if(perm.startsWith("crazyenchantments.limit.")){
											perm=perm.replace("crazyenchantments.limit.", "");
											if(Api.isInt(perm)){
												if(limit<Integer.parseInt(perm)){
													limit = Integer.parseInt(perm);
												}
											}
										}
									}
									if(!player.hasPermission("crazyenchantments.bypass")){
										if(total>=limit){
											player.sendMessage(Api.color(Main.settings.getMsg().getString("Messages.Hit-Enchantment-Max")));
											return;
										}
									}
								}
								e.setCancelled(true);
								if(success||player.getGameMode() == GameMode.CREATIVE){
									name = Api.removeColor(name);
									String[] breakdown = name.split(" ");
									String color = "&7";
									color = enchantColor;
									String enchantment = enchant;
									String lvl = breakdown[1];
									String full = Api.color(color+enchantment+" "+lvl);
									player.setItemOnCursor(new ItemStack(Material.AIR));
									e.setCurrentItem(Api.addGlow(Api.addLore(item, full)));
									player.sendMessage(Api.getPrefix()+Api.color(Main.settings.getMsg().getString("Messages.Book-Works")));
									try{
										if(Api.getVersion()>=191){
											player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
										}else{
											player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
										}
									}catch(Exception ex){}
									return;
								}
								if(destroy){
									if(Api.isProtected(item)){
										e.setCurrentItem(Api.removeProtected(item));
										player.setItemOnCursor(new ItemStack(Material.AIR));
										player.sendMessage(Api.getPrefix()+Api.color(Main.settings.getMsg().getString("Messages.Item-Was-Protected")));
										try{
											if(Api.getVersion()>=191){
												player.playSound(player.getLocation(), Sound.valueOf("ENTITY_ITEM_BREAK"), 1, 1);
											}else{
												player.playSound(player.getLocation(), Sound.valueOf("ITEM_BREAK"), 1, 1);
											}
										}catch(Exception ex){}
										return;
									}else{
										player.setItemOnCursor(new ItemStack(Material.AIR));
										e.setCurrentItem(new ItemStack(Material.AIR));
										player.sendMessage(Api.getPrefix()+Api.color(Main.settings.getMsg().getString("Messages.Item-Destroyed")));
									}
									player.updateInventory();
									return;
								}
								if(!success&&!destroy){
									player.sendMessage(Api.getPrefix()+Api.color(Main.settings.getMsg().getString("Messages.Book-Failed")));
									player.setItemOnCursor(new ItemStack(Material.AIR));
									try{
										if(Api.getVersion()>=191){
											player.playSound(player.getLocation(), Sound.valueOf("ENTITY_ITEM_BREAK"), 1, 1);
										}else{
											player.playSound(player.getLocation(), Sound.valueOf("ITEM_BREAK"), 1, 1);
										}
									}catch(Exception ex){}
									player.updateInventory();
									return;
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onDescriptionSend(PlayerInteractEvent e){
		if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.Right-Click-Book-Description") || !Main.settings.getConfig().contains("Settings.EnchantmentOptions.Right-Click-Book-Description")){
			if(e.getItem()!=null){
				ItemStack item = e.getItem();
				if(item.getType()!=Api.makeItem(Main.settings.getConfig().getString("Settings.Enchantment-Book-Item"), 1).getType())return;
				if(item.hasItemMeta()){
					if(item.getItemMeta().hasDisplayName()){
						String name = "";
						Player player = e.getPlayer();
						List<String> desc = new ArrayList<String>();
						for(CEnchantments en : Main.CE.getEnchantments()){
							if(item.getItemMeta().getDisplayName().contains(Api.color(en.getBookColor()+en.getCustomName()))){
								name = Main.settings.getEnchs().getString("Enchantments."+en.getName()+".Info.Name");
								desc = Main.settings.getEnchs().getStringList("Enchantments."+en.getName()+".Info.Description");
							}
						}
						for(String en : Main.CustomE.getEnchantments()){
							if(item.getItemMeta().getDisplayName().contains(Api.color(Main.CustomE.getBookColor(en)+Main.CustomE.getCustomName(en)))){
								name = Main.settings.getCustomEnchs().getString("Enchantments."+en+".Info.Name");
								desc = Main.settings.getCustomEnchs().getStringList("Enchantments."+en+".Info.Description");
							}
						}
						if(name.length()>0){
							player.sendMessage(Api.color(name));
						}
						for(String msg : desc)player.sendMessage(Api.color(msg));
						return;
					}
				}
			}
		}
	}
	
	public static ItemStack pick(String cat){
		int Smax = Main.settings.getConfig().getInt("Categories."+cat+".EnchOptions.SuccessPercent.Max");
		int Smin = Main.settings.getConfig().getInt("Categories."+cat+".EnchOptions.SuccessPercent.Min");
		int Dmax = Main.settings.getConfig().getInt("Categories."+cat+".EnchOptions.DestroyPercent.Max");
		int Dmin = Main.settings.getConfig().getInt("Categories."+cat+".EnchOptions.DestroyPercent.Min");
		ArrayList<String> lore = new ArrayList<String>();
		String enchant = Enchants(cat);
		for(String l : Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore")){
			if(l.contains("%Description%")||l.contains("%description%")){
				if(Main.CE.getFromName(enchant)!=null){
					for(String m : Main.CE.getFromName(enchant).getDiscription()){
						lore.add(Api.color(m));
					}
				}else{
					if(Main.CustomE.getEnchantments().contains(enchant)){
						for(String m : Main.CustomE.getDiscription(enchant)){
							lore.add(Api.color(m));
						}
					}
				}
			}else{
				lore.add(Api.color(l)
						.replaceAll("%Destroy_Rate%", Api.percentPick(Dmax, Dmin)+"").replaceAll("%destroy_rate%", Api.percentPick(Dmax, Dmin)+"")
						.replaceAll("%Success_Rate%", Api.percentPick(Smax, Smin)+"").replaceAll("%success_Rate%", Api.percentPick(Smax, Smin)+""));
			}
		}
		return Api.makeItem(Main.settings.getConfig().getString("Settings.Enchantment-Book-Item"), 1, enchants.get(enchant), lore);
	}
	
	public static String powerPicker(String en, String C){
		Random r = new Random();
		int ench = 5; //Max set by the enchantment
		if(Main.settings.getEnchs().contains("Enchantments."+en)){
			ench=Main.settings.getEnchs().getInt("Enchantments."+en+".MaxPower");
		}
		if(Main.settings.getCustomEnchs().contains("Enchantments."+en)){
			ench=Main.settings.getCustomEnchs().getInt("Enchantments."+en+".MaxPower");
		}
		int max = Main.settings.getConfig().getInt("Categories."+C+".EnchOptions.LvlRange.Max"); //Max lvl set by the Category
		int min = Main.settings.getConfig().getInt("Categories."+C+".EnchOptions.LvlRange.Min"); //Min lvl set by the Category
		int i = 1+r.nextInt(ench);
		if(Main.settings.getConfig().contains("Categories."+C+".EnchOptions.MaxLvlToggle")){
			if(Main.settings.getConfig().getBoolean("Categories."+C+".EnchOptions.MaxLvlToggle")){
				if(i>max){
					for(Boolean l=false;l==false;){
						i=1+r.nextInt(ench);
						if(i<=max){
							l=true;
							break;
						}
					}
				}
				if(i<min){//If i is smaller then the Min of the Category
					i=min;
				}
				if(i>ench){//If i is bigger then the Enchantment Max
					i=ench;
				}
			}
		}
		if(i==0)return "I";
		if(i==1)return "I";
		if(i==2)return "II";
		if(i==3)return "III";
		if(i==4)return "IV";
		if(i==5)return "V";
		if(i==6)return "VI";
		if(i==7)return "VII";
		if(i==8)return "VII";
		if(i==9)return "IX";
		if(i==10)return "X";
		return i+"";
	}
	
	public static String getCategory(ItemStack item){
		List<String> lore = item.getItemMeta().getLore();
		List<String> L = Main.settings.getConfig().getStringList("Settings.LostBook.Lore");
		String arg = "";
		int i = 0;
		for(String l : L){
			l = Api.color(l);
			String lo = lore.get(i);
			if(l.contains("%Category%")){
				String[] b = l.split("%Category%");
				if(b.length>=1)arg = lo.replace(b[0], "");
				if(b.length>=2)arg = arg.replace(b[1], "");
			}
			if(l.contains("%category%")){
				String[] b = l.split("%category%");
				if(b.length>=1)arg = lo.replace(b[0], "");
				if(b.length>=2)arg = arg.replace(b[1], "");
			}
			i++;
		}
		return arg;
	}
	
	private boolean successChance(ItemStack item){
		if(item.hasItemMeta()){
			if(item.getItemMeta().hasLore()){
				int percent = Api.getPercent("%success_rate%", item, Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore"));
				if(Api.randomPicker(percent, 100)){
					return true;
				}else{
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean destroyChance(ItemStack item){
		if(item.hasItemMeta()){
			if(item.getItemMeta().hasLore()){
				int percent = Api.getPercent("%destroy_rate%", item, Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore"));
				if(Api.randomPicker(percent, 100)){
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}
	
}
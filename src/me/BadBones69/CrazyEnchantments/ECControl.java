package me.BadBones69.CrazyEnchantments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.CrazyEnchantments;
import me.BadBones69.CrazyEnchantments.API.CustomEnchantments;

public class ECControl implements Listener{
	static CrazyEnchantments CE = CrazyEnchantments.getInstance();
	static CustomEnchantments CustomE = CustomEnchantments.getInstance();
	static HashMap<String, String> enchants = new HashMap<String, String>();
	public static String Enchants(String cat){
		Random number = new Random();
		List<String> enchantments = new ArrayList<String>();
		for(CEnchantments en : CE.getEnchantments()){
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
			for(String en : CustomE.getEnchantments()){
				for(String C : Main.settings.getCustomEnchs().getStringList("Enchantments."+en+".Categories")){
					if(cat.equalsIgnoreCase(C)){
						if(CustomE.isEnabled(en)){
							String power = powerPicker(en, cat);
							enchants.put(en, CustomE.getBookColor(en)+CustomE.getCustomName(en)+" "+power);
							enchantments.add(en);
						}
					}
				}
			}
		}
		return enchantments.get(number.nextInt(enchantments.size()));
	}
	@EventHandler
	public void onBookClean(PlayerInteractEvent e){
		Player player = e.getPlayer();
		if(e.getItem()!=null){
			ItemStack item = e.getItem();
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasDisplayName()){
					if(item.getItemMeta().getDisplayName().equals(Api.color(Main.settings.getConfig().getString("Settings.LostBook.Name")))){
						for(String C : Main.settings.getConfig().getConfigurationSection("Categories").getKeys(false)){
							if(Api.color(Main.settings.getConfig().getString("Categories."+C+".Name")).equalsIgnoreCase(getCategory(item))){
								Api.removeItem(item, player);
								ItemStack book = Api.addGlow(pick(C));
								player.getInventory().addItem(book);
								player.updateInventory();
								player.sendMessage(Api.getPrefix()+Api.color(Main.settings.getMsg().getString("Messages.Clean-Lost-Book")
										.replaceAll("%Found%", book.getItemMeta().getDisplayName()).replaceAll("%found%", book.getItemMeta().getDisplayName())));
								if(Main.settings.getConfig().contains("Categories."+C+".LostBook.FireworkToggle")){
									if(Main.settings.getConfig().contains("Categories."+C+".LostBook.FireworkColors")){
										if(Main.settings.getConfig().getBoolean("Categories."+C+".LostBook.FireworkToggle")){
											ArrayList<Color> colors = new ArrayList<Color>();
											String Cs = Main.settings.getConfig().getString("Categories."+C+".LostBook.FireworkColors");
											if(Cs.contains(", ")){
												for(String color : Cs.split(", ")){
													Color c = Api.getColor(color);
													if(c != null){
														colors.add(c);
													}
												}
											}else{
												Color c = Api.getColor(Cs);
												if(c != null){
													colors.add(c);
												}
											}
											Api.fireWork(player.getLocation().add(0, 1, 0), colors);
										}
									}
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
	public void onClick(PlayerInteractEvent e){
		if(e.getItem()!=null){
			ItemStack item = e.getItem();
			if(item.getType()!=Api.makeItem(Main.settings.getConfig().getString("Settings.Enchantment-Book-Item"), 1).getType())return;
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasDisplayName()){
					String name = "";
					Player player = e.getPlayer();
					List<String> desc = new ArrayList<String>();
					for(CEnchantments en : CE.getEnchantments()){
						if(item.getItemMeta().getDisplayName().contains(Api.color(en.getBookColor()+en.getCustomName()))){
							name = Main.settings.getEnchs().getString("Enchantments."+en.getName()+".Info.Name");
							desc = Main.settings.getEnchs().getStringList("Enchantments."+en.getName()+".Info.Description");
						}
					}
					for(String en : CustomE.getEnchantments()){
						if(item.getItemMeta().getDisplayName().contains(Api.color(CustomE.getBookColor(en)+CustomE.getCustomName(en)))){
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
	public static ItemStack pick(String cat){
		int Smax = Main.settings.getConfig().getInt("Categories."+cat+".EnchOptions.SuccessPercent.Max");
		int Smin = Main.settings.getConfig().getInt("Categories."+cat+".EnchOptions.SuccessPercent.Min");
		int Dmax = Main.settings.getConfig().getInt("Categories."+cat+".EnchOptions.DestroyPercent.Max");
		int Dmin = Main.settings.getConfig().getInt("Categories."+cat+".EnchOptions.DestroyPercent.Min");
		ArrayList<String> lore = new ArrayList<String>();
		String enchant = Enchants(cat);
		for(String l : Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore")){
			if(l.contains("%Description%")||l.contains("%description%")){
				if(CE.getFromName(enchant)!=null){
					for(String m : CE.getFromName(enchant).getDiscription()){
						lore.add(Api.color(m));
					}
				}else{
					if(CustomE.getEnchantments().contains(enchant)){
						for(String m : CustomE.getDiscription(enchant)){
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
}
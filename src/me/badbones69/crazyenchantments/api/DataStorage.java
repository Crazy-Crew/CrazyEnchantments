package me.badbones69.crazyenchantments.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;

public class DataStorage {
	
	private static int rageMaxLevel;
	private static ArrayList<GKitz> gkitz = new ArrayList<GKitz>();
	private static ArrayList<CEPlayer> players = new ArrayList<CEPlayer>();
	private static ArrayList<Material> blockList = new ArrayList<Material>();
	private static ArrayList<CEnchantment> registeredEnchantments = new ArrayList<CEnchantment>();
	
	public static void load(){
		blockList.clear();
		gkitz.clear();
		registeredEnchantments.clear();
		for(String id : Main.settings.getBlockList().getStringList("Block-List")){
			try{
				blockList.add(Methods.makeItem(id, 1).getType());
			}catch(Exception e){}
		}
		if(Main.settings.getConfig().contains("Settings.EnchantmentOptions.MaxRageLevel")){
			rageMaxLevel = Main.settings.getConfig().getInt("Settings.EnchantmentOptions.MaxRageLevel");
		}else{
			rageMaxLevel = 4;
		}
		FileConfiguration enchants = Main.settings.getEnchantments();
		for(CEnchantments enchant : CEnchantments.values()){
			String name = enchant.getName();
			if(enchants.contains("Enchantments." + name)){// To make sure the enchantment isn't broken.
				CEnchantment en = new CEnchantment(name)
						.setCustomName(enchants.getString("Enchantments." + name + ".Name"))
						.setActivated(enchants.getBoolean("Enchantments." + name + ".Enabled"))
						.setColor(enchants.getString("Enchantments." + name + ".Color"))
						.setBookColor(enchants.getString("Enchantments." + name + ".BookColor"))
						.setMaxLevel(enchants.getInt("Enchantments." + name + ".MaxPower"))
						.setEnchantmentType(enchant.getType())
						.setInfoName(enchants.getString("Enchantments." + name + ".Info.Name"))
						.setInfoDescription(enchants.getStringList("Enchantments." + name + ".Info.Description"))
						.setCategories(enchants.getStringList("Enchantments." + name + ".Categories"));
				if(enchants.contains("Enchantments." + name + ".Enchantment-Type")){// Sets the custom type set in the enchantments.yml.
					en.setEnchantmentType(EnchantmentType.getFromName(enchants.getString("Enchantments." + name + ".Enchantment-Type")));
				}
				en.registerEnchantment();
			}
		}
		FileConfiguration gkit = Main.settings.getGKitz();
		for(String kit : gkit.getConfigurationSection("GKitz").getKeys(false)){
			int slot = gkit.getInt("GKitz." + kit + ".Display.Slot");
			String time = gkit.getString("GKitz." + kit + ".Cooldown");
			Boolean autoEquip = false;
			if(gkit.contains("GKitz." + kit + ".Auto-Equip")){
				autoEquip = gkit.getBoolean("GKitz." + kit + ".Auto-Equip");
			}
			ItemStack displayItem = Methods.makeItem(gkit.getString("GKitz." + kit + ".Display.Item"), 1, gkit.getString("GKitz." + kit + ".Display.Name"),
					gkit.getStringList("GKitz." + kit + ".Display.Lore"), gkit.getBoolean("GKitz." + kit + ".Display.Glowing"));
			ArrayList<String> commands = (ArrayList<String>) gkit.getStringList("GKitz." + kit + ".Commands");
			ArrayList<String> itemStrings = (ArrayList<String>) gkit.getStringList("GKitz." + kit + ".Items");
			gkitz.add(new GKitz(kit, slot, time, displayItem, getInfoGKit(itemStrings), commands, getKitItems(itemStrings), itemStrings, autoEquip));
		}
	}
	
	public static int getRageMaxLevel(){
		return rageMaxLevel;
	}
	
	public static void setRageMaxLevel(int level){
		rageMaxLevel = level;
	}
	
	public static ArrayList<Material> getBlockList(){
		return blockList;
	}
	
	public static ArrayList<CEPlayer> getCEPlayers(){
		return players;
	}
	
	public static void addCEPlayer(CEPlayer player){
		players.add(player);
	}
	
	public static void removeCEPlayer(CEPlayer player){
		players.remove(player);
	}
	
	public static ArrayList<GKitz> getGKitz(){
		return gkitz;
	}
	
	public static void addGKit(GKitz kit){
		gkitz.add(kit);
	}
	
	public static void removeGKit(GKitz kit){
		gkitz.remove(kit);
	}
	
	private static ArrayList<ItemStack> getInfoGKit(ArrayList<String> itemStrings){
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for(String i : itemStrings){
			String type = "";
			int amount = 0;
			String name = "";
			ArrayList<String> lore = new ArrayList<String>();
			ArrayList<String> customEnchantments = new ArrayList<String>();
			HashMap<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
			for(String d : i.split(", ")){
				if(d.startsWith("Item:")){
					d = d.replace("Item:", "");
					type = d;
				}else if(d.startsWith("Amount:")){
					d = d.replace("Amount:", "");
					if(Methods.isInt(d)){
						amount = Integer.parseInt(d);
					}
				}else if(d.startsWith("Name:")){
					d = d.replaceAll("Name:", "");
					name = d;
				}else if(d.startsWith("Lore:")){
					d = d.replace("Lore:", "");
					if(d.contains(",")){
						for(String D : d.split(",")){
							lore.add(D);
						}
					}else{
						lore.add(d);
					}
				}else{
					for(Enchantment en : Enchantment.values()){
						if(d.split(":")[0].equalsIgnoreCase(Methods.getEnchantmentName(en)) ||
								Enchantment.getByName(d.split(":")[0]) != null){
							String power = d.split(":")[1];
							if(power.contains("-")){
								customEnchantments.add("&7" + d.split(":")[0] + " " + power);
							}else{
								enchantments.put(en, Integer.parseInt(power));
							}
							break;
						}
					}
					for(CEnchantment en : Main.CE.getRegisteredEnchantments()){
						if(d.split(":")[0].equalsIgnoreCase(en.getName()) ||
								d.split(":")[0].equalsIgnoreCase(en.getCustomName())){
							customEnchantments.add(en.getColor() + en.getCustomName() + " " + d.split(":")[1]);
							break;
						}
					}
				}
			}
			lore.addAll(0, customEnchantments);
			items.add(Methods.makeItem(type, amount, name, lore, enchantments));
		}
		return items;
	}
	
	public static ArrayList<ItemStack> getKitItems(ArrayList<String> itemStrings){
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for(String i : itemStrings){
			GKitzItem item = new GKitzItem();
			for(String d : i.split(", ")){
				if(d.startsWith("Item:")){
					item.setItem(d.replace("Item:", ""));
				}else if(d.startsWith("Amount:")){
					if(Methods.isInt(d.replace("Amount:", ""))){
						item.setAmount(Integer.parseInt(d.replace("Amount:", "")));
					}
				}
				else if(d.startsWith("Name:")){
					item.setName(d.replace("Name:", ""));
				}
				else if(d.startsWith("Lore:")){
					d = d.replace("Lore:", "");
					ArrayList<String> lore = new ArrayList<String>();
					if(d.contains(",")){
						for(String D : d.split(",")){
							lore.add(D);
						}
					}else{
						lore.add(d);
					}
					item.setLore(lore);
				}else{
					for(Enchantment en : Enchantment.values()){
						if(d.split(":")[0].equalsIgnoreCase(Methods.getEnchantmentName(en)) ||
								Enchantment.getByName(d.split(":")[0]) != null){
							String power = d.split(":")[1];
							if(power.contains("-")){
								int level = pickLevel(Integer.parseInt(d.split(":")[1].split("-")[0]),
										Integer.parseInt(d.split(":")[1].split("-")[1]));
								if(level > 0){
									item.addEnchantment(en, level);
								}
							}else{
								item.addEnchantment(en, Integer.parseInt(d.split(":")[1]));
							}
							break;
						}
					}
					for(CEnchantment en : Main.CE.getRegisteredEnchantments()){
						if(d.split(":")[0].equalsIgnoreCase(en.getName()) ||
								d.split(":")[0].equalsIgnoreCase(en.getCustomName())){
							String power = d.split(":")[1];
							if(power.contains("-")){
								int level = pickLevel(Integer.parseInt(d.split(":")[1].split("-")[0]),
										Integer.parseInt(d.split(":")[1].split("-")[1]));
								if(level > 0){
									item.addCEEnchantment(en, level);
								}
							}else{
								item.addCEEnchantment(en, Integer.parseInt(d.split(":")[1]));
							}
							break;
						}
					}
				}
			}
			items.add(item.build());
		}
		return items;
	}
	
	private static Integer pickLevel(int min, int max){
		max++;
		return min + new Random().nextInt(max - min);
	}
	
	public static ArrayList<CEnchantment> getRegisteredEnchantments(){
		return new ArrayList<CEnchantment>(registeredEnchantments);
	}
	
	public static void registerEnchantment(CEnchantment enchantment){
		registeredEnchantments.add(enchantment);
	}
	
	public static void unregisterEnchantment(CEnchantment enchantment){
		registeredEnchantments.remove(enchantment);
	}
	
}
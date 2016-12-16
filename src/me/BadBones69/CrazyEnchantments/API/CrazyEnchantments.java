package me.BadBones69.CrazyEnchantments.API;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BadBones69.CrazyEnchantments.Methods;
import me.BadBones69.CrazyEnchantments.Main;

public class CrazyEnchantments {
	
	public static CrazyEnchantments instance = new CrazyEnchantments();
	
	ArrayList<Material> BlockList = new ArrayList<Material>();
	
	int rageMaxLevel;
	
	/**
	 * 
	 * @return The instance of CrazyEnchantments.
	 */
	public static CrazyEnchantments getInstance() {
		return instance;
	}
	
	/**
	 * 
	 * @return Returns the item the enchantment book will be.
	 */
	public ItemStack getEnchantmentBookItem(){
		return Methods.makeItem(Main.settings.getConfig().getString("Settings.Enchantment-Book-Item"), 1);
	}
	
	/**
	 * 
	 * @param enchantment The enchantment you wish to get the color from.
	 * @return Returns the color of the enchantment that goes on books.
	 */
	public String getBookColor(CEnchantments enchantment){
		return Methods.color(Main.settings.getEnchs().getString("Enchantments."+enchantment.getName()+".BookColor"));
	}
	
	/**
	 * 
	 * @param enchantment The enchantment you wish to get the color from.
	 * @return Returns the color of the enchantment that goes on an item.
	 */
	public String getEnchantmentColor(CEnchantments enchantment){
		return Methods.color(Main.settings.getEnchs().getString("Enchantments."+enchantment.getName()+".Color"));
	}
	
	/**
	 * 
	 * @param enchantment The enchantment you wish to check.
	 * @return True if enchantment is enabled / False if the enchantment is disabled.
	 */
	public Boolean isEnchantmentEnabled(CEnchantments enchantment){
		return Main.settings.getEnchs().getBoolean("Enchantments."+enchantment.getName()+".Enabbled");
	}
	
	/**
	 * 
	 * @return List of all the enum enchantments.
	 */
	public ArrayList<CEnchantments> getEnchantments(){
		ArrayList<CEnchantments> enchs = new ArrayList<CEnchantments>();
		for(CEnchantments en : CEnchantments.values()){
			enchs.add(en);
		}
		return enchs;
	}
	
	/**
	 * 
	 * @param enchantment Enchantment that is being checked
	 * @return Returns true if its real and false if not
	 */
	public Boolean isEnchantment(String enchantment){
		for(CEnchantments en : getEnchantments()){
			if(enchantment.equalsIgnoreCase(en.getName()) || enchantment.equalsIgnoreCase(en.getCustomName())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param name The name or custom name of the enchantment.
	 * @return The enchantment.
	 */
	public CEnchantments getFromName(String name){
		for(CEnchantments en : getEnchantments()){
			if(en.getName().equalsIgnoreCase(name)){
				return en;
			}
			if(en.getCustomName().equalsIgnoreCase(name)){
				return en;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param item Item you want to check to see if it has enchantments.
	 * @return True if it has enchantments / False if it doesn't have enchantments.
	 */
	public Boolean hasEnchantments(ItemStack item){
		if(item!=null){
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasLore()){
					for(String lore : item.getItemMeta().getLore()){
						for(CEnchantments enchantment : getEnchantments()){
							if(lore.startsWith(enchantment.getEnchantmentColor() + enchantment.getCustomName())){
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param item Item that you want to check if it has an enchantment.
	 * @param enchantment The enchantment you want to check if the item has.
	 * @return True if the item has the enchantment / False if it doesn't have the enchantment.
	 */
	public Boolean hasEnchantment(ItemStack item, CEnchantments enchantment){
		if(item!=null){
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasLore()){
					for(String lore : item.getItemMeta().getLore()){
						if(lore.startsWith(enchantment.getEnchantmentColor() + enchantment.getCustomName())){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param item Item you want to add the enchantment to.
	 * @param enchant Enchantment you want added.
	 * @param level Tier of the enchantment.
	 * @return
	 */
	public ItemStack addEnchantment(ItemStack item, CEnchantments enchant, Integer level){
		List<String> newLore = new ArrayList<String>();
		ItemMeta meta = item.getItemMeta();
		if(item.hasItemMeta()){
			if(item.getItemMeta().hasLore()){
				newLore.addAll(item.getItemMeta().getLore());
			}
		}
		newLore.add(color(enchant.getEnchantmentColor()+enchant.getCustomName()+" "+convertPower(level)));
		if(newLore.contains(color(Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName")))){
			newLore.remove(color(Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName")));
			newLore.add(color(Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName")));
		}
		if(newLore.contains(color(Main.settings.getConfig().getString("Settings.ProtectionCrystal.Protected")))){
			newLore.remove(color(Main.settings.getConfig().getString("Settings.ProtectionCrystal.Protected")));
			newLore.add(color(Main.settings.getConfig().getString("Settings.ProtectionCrystal.Protected")));
		}
		meta.setLore(newLore);
		item.setItemMeta(meta);
		return item;
	}
	
	/**
	 * 
	 * @param item Item you want to remove the enchantment from.
	 * @param enchant Enchantment you want removed.
	 * @return Item with out the enchantment.
	 */
	public ItemStack removeEnchantment(ItemStack item, CEnchantments enchant){
		List<String> newLore = new ArrayList<String>();
		ItemMeta meta = item.getItemMeta();
		for(String lore : item.getItemMeta().getLore()){
			if(!lore.contains(enchant.getCustomName())){
				newLore.add(lore);
			}
		}
		meta.setLore(newLore);
		item.setItemMeta(meta);
		return item;
	}
	
	/**
	 * 
	 * @param item Item you want to get the enchantments from.
	 * @return A list of enchantments the item has.
	 */
	public ArrayList<CEnchantments> getItemEnchantments(ItemStack item){
		ArrayList<CEnchantments> enchantments = new ArrayList<CEnchantments>();
		if(item!=null){
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasLore()){
					for(String lore : item.getItemMeta().getLore()){
						for(CEnchantments enchantment : getEnchantments()){
							if(lore.contains(enchantment.getCustomName())){
								enchantments.add(enchantment);
							}
						}
					}
				}
			}
		}
		return enchantments;
	}

	/**
	 * 
	 * @param book The book you are getting the power from.
	 * @param enchant The enchantment you want the power from.
	 * @return The power the enchantment has.
	 */
	public Integer getBookPower(ItemStack book, CEnchantments enchant){
		String line = book.getItemMeta().getDisplayName().replace(enchant.getBookColor() + enchant.getCustomName()+" ", "");
		if(Methods.isInt(line))return Integer.parseInt(line);
		if(line.equalsIgnoreCase("I"))return 1;
		if(line.equalsIgnoreCase("II"))return 2;
		if(line.equalsIgnoreCase("III"))return 3;
		if(line.equalsIgnoreCase("IV"))return 4;
		if(line.equalsIgnoreCase("V"))return 5;
		if(line.equalsIgnoreCase("VI"))return 6;
		if(line.equalsIgnoreCase("VII"))return 7;
		if(line.equalsIgnoreCase("VIII"))return 8;
		if(line.equalsIgnoreCase("IX"))return 9;
		if(line.equalsIgnoreCase("X"))return 10;
		return 1;
	}
	
	/**
	 * 
	 * @param item Item you are getting the power from.
	 * @param enchant The enchantment you want the power from.
	 * @return The power the enchantment has.
	 */
	public Integer getPower(ItemStack item, CEnchantments enchant){
		String line = "";
		for(String lore : item.getItemMeta().getLore()){
			if(lore.contains(enchant.getCustomName())){
				line = lore;
				break;
			}
		}
		line = line.replace(enchant.getEnchantmentColor() + enchant.getCustomName()+" ", "");
		if(Methods.isInt(line))return Integer.parseInt(line);
		if(line.equalsIgnoreCase("I"))return 1;
		if(line.equalsIgnoreCase("II"))return 2;
		if(line.equalsIgnoreCase("III"))return 3;
		if(line.equalsIgnoreCase("IV"))return 4;
		if(line.equalsIgnoreCase("V"))return 5;
		if(line.equalsIgnoreCase("VI"))return 6;
		if(line.equalsIgnoreCase("VII"))return 7;
		if(line.equalsIgnoreCase("VIII"))return 8;
		if(line.equalsIgnoreCase("IX"))return 9;
		if(line.equalsIgnoreCase("X"))return 10;
		return 1;
	}
	
	/**
	 * Loads the block list for blast.
	 */
	public void load(){
		BlockList.clear();
		for(String id : Main.settings.getBlockList().getStringList("Block-List")){
			try{
				BlockList.add(Methods.makeItem(id, 1).getType());
			}catch(Exception e){}
		}
		if(Main.settings.getConfig().contains("Settings.EnchantmentOptions.MaxRageLevel")){
			rageMaxLevel = Main.settings.getConfig().getInt("Settings.EnchantmentOptions.MaxRageLevel");
		}else{
			rageMaxLevel = 4;
		}
	}
	
	/**
	 * 
	 * @return The block list for blast.
	 */
	public ArrayList<Material> getBlockList(){
		return BlockList;
	}
	
	/**
	 * 
	 * @return The max rage stack level.
	 */
	public Integer getMaxRageLevel(){
		return rageMaxLevel;
	}
	
	private String convertPower(Integer i){
		if(i<=0)return "I";
		if(i==1)return "I";
		if(i==2)return "II";
		if(i==3)return "III";
		if(i==4)return "IV";
		if(i==5)return "V";
		if(i==6)return "VI";
		if(i==7)return "VII";
		if(i==8)return "VIII";
		if(i==9)return "IX";
		if(i==10)return "X";
		return i+"";
	}
	
	private String color(String msg){
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
}
package me.BadBones69.CrazyEnchantments.API;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import me.BadBones69.CrazyEnchantments.Api;
import me.BadBones69.CrazyEnchantments.Main;

public class CrazyEnchantments {
	
	public static CrazyEnchantments instance = new CrazyEnchantments();

	public static CrazyEnchantments getInstance() {
		return instance;
	}
	
	/**
	 * This one is useful for getting the custom enchantment colors.
	 * @param enchantment The enchantment you wish to get the color from.
	 * @return Returns the color of the enchantment that goes on books.
	 */
	public String getBookColor(String enchantment){
		if(Main.settings.getEnchs().contains("Enchantments."+enchantment)){
			return Main.settings.getEnchs().getString("Enchantments."+enchantment+".BookColor");
		}else{
			return Main.settings.getCustomEnchs().getString("Enchantments."+enchantment+".BookColor");
		}
	}
	
	/**
	 * 
	 * @param enchantment The enchantment you wish to get the color from.
	 * @return Returns the color of the enchantment that goes on books.
	 */
	public String getBookColor(CEnchantments enchantment){
		return Main.settings.getEnchs().getString("Enchantments."+enchantment.getName()+".BookColor");
	}
	
	/**
	 * This one is useful for getting the custom enchantment colors.
	 * @param enchantment The enchantment you wish to get the color from.
	 * @return Returns the color of the enchantment that goes on an item.
	 */
	public String getEnchantmentColor(String enchantment){
		if(Main.settings.getEnchs().contains("Enchantments."+enchantment)){
			return Main.settings.getEnchs().getString("Enchantments."+enchantment+".Color");
		}else{
			return Main.settings.getCustomEnchs().getString("Enchantments."+enchantment+".Color");
		}
	}
	
	/**
	 * 
	 * @param enchantment The enchantment you wish to get the color from.
	 * @return Returns the color of the enchantment that goes on an item.
	 */
	public String getEnchantmentColor(CEnchantments enchantment){
		return Main.settings.getEnchs().getString("Enchantments."+enchantment.getName()+".Color");
	}
	
	/**
	 * This one is useful for getting the custom enchantment colors.
	 * @param enchantment The enchantment you wish to check.
	 * @return True if enchantment is enabled / False if the enchantment is disabled.
	 */
	public Boolean isEnchantmentEnabled(String enchantment){
		if(Main.settings.getEnchs().contains("Enchantments."+enchantment)){
			return Main.settings.getEnchs().getBoolean("Enchantments."+enchantment+".Enabbled");
		}else{
			return  Main.settings.getCustomEnchs().getBoolean("Enchantments."+enchantment+".Enabbled");
		}
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
	 * @return List of all the enchantments.
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
	 * @return List of all the custom enchantments.
	 */
	public ArrayList<String> getCustomEnchantments(){
		ArrayList<String> enchs = new ArrayList<String>();
		if(Main.settings.getCustomEnchs().contains("Enchantments")){
			for(String en : Main.settings.getCustomEnchs().getConfigurationSection("Enchantments").getKeys(false)){
				enchs.add(en);
			}
		}
		return enchs;
	}
	
	/**
	 * Good for getting Custom Enchantments.
	 * @param enchant The enchantment you want to get the custom name of.
	 * @return Custom enchantment name of the enchantment.
	 */
	public String getCustomName(String enchant){
		if(Main.settings.getEnchs().contains("Enchantments."+enchant)){
			return Main.settings.getEnchs().getString("Enchantments."+enchant+".Name");
		}else{
			return Main.settings.getCustomEnchs().getString("Enchantments."+enchant+".Name");
		}
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
							if(lore.contains(enchantment.getCustomName())){
								return true;
							}
						}
						for(String enchantment : getCustomEnchantments()){
							if(lore.contains(getCustomName(enchantment))){
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
						if(lore.contains(enchantment.getCustomName())){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Best used if needing to use custom enchantments.
	 * @param item Item that you want to check if it has an enchantment.
	 * @param enchantment The enchantment you want to check if the item has.
	 * @return True if the item has the enchantment / False if it doesn't have the enchantment.
	 */
	public Boolean hasEnchantment(ItemStack item, String enchantment){
		if(item!=null){
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasLore()){
					for(String lore : item.getItemMeta().getLore()){
						if(lore.contains(getCustomName(enchantment))){
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
	 * Use if you want to get the custom enchantments also.
	 * @param item Item you want to get the enchantments from.
	 * @return A list of enchantments the item has.
	 */
	public ArrayList<String> getItemEnchantmentsWithCustom(ItemStack item){
		ArrayList<String> enchantments = new ArrayList<String>();
		if(item!=null){
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasLore()){
					for(String lore : item.getItemMeta().getLore()){
						for(CEnchantments enchantment : getEnchantments()){
							if(lore.contains(enchantment.getCustomName())){
								enchantments.add(enchantment.getName());
							}
						}
						for(String enchantment : getCustomEnchantments()){
							if(lore.contains(getCustomName(enchantment))){
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
		line = line.replace(enchant.getCustomName()+" ", "");
		line = Api.removeColor(line);
		if(Api.isInt(line))return Integer.parseInt(line);
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
	 * Best used if you need to get a custom enchantments power.
	 * @param item Item you are getting the power from.
	 * @param enchant The enchantment you want the power from.
	 * @return The power the enchantment has.
	 */
	public Integer getPower(ItemStack item, String enchant){
		String line = "";
		for(String lore : item.getItemMeta().getLore()){
			if(lore.contains(getCustomName(enchant))){
				line = lore;
				break;
			}
		}
		line = line.replace(getCustomName(enchant)+" ", "");
		line = Api.removeColor(line);
		if(Api.isInt(line))return Integer.parseInt(line);
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
}
package me.BadBones69.CrazyEnchantments.API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import me.BadBones69.CrazyEnchantments.Main;
import me.BadBones69.CrazyEnchantments.Methods;

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
		if(item != null){
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
		if(item != null){
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
	 * @param enchantment The enchantment you want to check.
	 * @return All the categories the enchantment is in.
	 */
	public ArrayList<String> getEnchantmentCategories(CEnchantments enchantment){
		ArrayList<String> cats = new ArrayList<String>();
		for(String c : Main.settings.getEnchs().getStringList("Enchantments." + enchantment.getName() + ".Categories")){
			for(String C : Main.settings.getConfig().getConfigurationSection("Categories").getKeys(false)){
				if(c.equalsIgnoreCase(C)){
					cats.add(C);
				}
			}
		}
		return cats;
	}
	
	/**
	 * 
	 * @param category The category you want the rarity from.
	 * @return The level of the category's rarity.
	 */
	public Integer getCategoryRarity(String category){
		int rarity = 0;
		FileConfiguration config = Main.settings.getConfig();
		if(config.contains("Categories." + category)){
			rarity = config.getInt("Categories." + category + ".Rarity");
		}
		return rarity;
	}
	
	/**
	 * 
	 * @param player The player you want to check if they have the enchantment on their armor.
	 * @param include The item you want to include.
	 * @param exclude The item you want to exclude.
	 * @param enchantment The enchantment you are checking.
	 * @return True if a piece of armor has the enchantment and false if not.
	 */
	public Boolean playerHasEnchantmentOn(Player player, ItemStack include, ItemStack exclude, CEnchantments enchantment){
		for(ItemStack armor : player.getEquipment().getArmorContents()){
			if(!armor.isSimilar(exclude)){
				if(hasEnchantment(armor, enchantment)){
					return true;
				}
			}
		}
		if(hasEnchantment(include, enchantment)){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param player The player you want to check if they have the enchantment on their armor.
	 * @param item The item you want to exclude.
	 * @param enchantment The enchantment you are checking.
	 * @return True if a piece of armor has the enchantment and false if not.
	 */
	public Boolean playerHasEnchantmentOnExclude(Player player, ItemStack item, CEnchantments enchantment){
		for(ItemStack armor : player.getEquipment().getArmorContents()){
			if(!armor.isSimilar(item)){
				if(hasEnchantment(armor, enchantment)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param player The player you want to check if they have the enchantment on their armor.
	 * @param item The item you want to include.
	 * @param enchantment The enchantment you are checking.
	 * @return True if a piece of armor has the enchantment and false if not.
	 */
	public Boolean playerHasEnchantmentOnInclude(Player player, ItemStack item, CEnchantments enchantment){
		for(ItemStack armor : player.getEquipment().getArmorContents()){
			if(hasEnchantment(armor, enchantment)){
				return true;
			}
		}
		if(hasEnchantment(item, enchantment)){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param player The player you want to get the highest level of an enchantment from.
	 * @param include The item you want to include.
	 * @param exclude The item you want to exclude.
	 * @param enchantment The enchantment you are checking.
	 * @return The highest level of the enchantment that the player currently has.
	 */
	public Integer getHighestEnchantmentLevel(Player player, ItemStack include, ItemStack exclude, CEnchantments enchantment){
		int highest = 0;
		for(ItemStack armor : player.getEquipment().getArmorContents()){
			if(!armor.isSimilar(exclude)){
				if(hasEnchantment(armor, enchantment)){
					int level = getPower(armor, enchantment);
					if(highest < level){
						highest = level;
					}
				}
			}
		}
		if(hasEnchantment(include, enchantment)){
			int level = getPower(include, enchantment);
			if(highest < level){
				highest = level;
			}
		}
		return highest;
	}
	
	/**
	 * 
	 * @param player The player you want to get the highest level of an enchantment from.
	 * @param item The item you want to exclude.
	 * @param enchantment The enchantment you are checking.
	 * @return The highest level of the enchantment that the player currently has.
	 */
	public Integer getHighestEnchantmentLevelExclude(Player player, ItemStack item, CEnchantments enchantment){
		int highest = 0;
		for(ItemStack armor : player.getEquipment().getArmorContents()){
			if(!armor.isSimilar(item)){
				if(hasEnchantment(armor, enchantment)){
					int level = getPower(armor, enchantment);
					if(highest < level){
						highest = level;
					}
				}
			}
		}
		return highest;
	}
	
	/**
	 * 
	 * @param player The player you want to get the highest level of an enchantment from.
	 * @param item The item you want to include.
	 * @param enchantment The enchantment you are checking.
	 * @return The highest level of the enchantment that the player currently has.
	 */
	public Integer getHighestEnchantmentLevelInclude(Player player, ItemStack item, CEnchantments enchantment){
		int highest = 0;
		for(ItemStack armor : player.getEquipment().getArmorContents()){
			if(hasEnchantment(armor, enchantment)){
				int level = getPower(armor, enchantment);
				if(highest < level){
					highest = level;
				}
			}
		}
		if(hasEnchantment(item, enchantment)){
			int level = getPower(item, enchantment);
			if(highest < level){
				highest = level;
			}
		}
		return highest;
	}
	
	/**
	 * 
	 * @param item Item you want to add the enchantment to.
	 * @param enchant Enchantment you want added.
	 * @param level Tier of the enchantment.
	 * @return
	 */
	public ItemStack addEnchantment(ItemStack item, CEnchantments enchant, Integer level){
		if(hasEnchantment(item, enchant)){
			removeEnchantment(item, enchant);
		}
		List<String> newLore = new ArrayList<String>();
		List<String> lores = new ArrayList<String>();
		HashMap<String, String> enchantments = new HashMap<String, String>();
		for(CEnchantments en : getItemEnchantments(item)){
			enchantments.put(en.getName(), Methods.color(en.getEnchantmentColor() + en.getCustomName() + " " +  convertPower(getPower(item, en))));
			removeEnchantment(item, en);
		}
		for(String en : Main.CustomE.getItemEnchantments(item)){
			enchantments.put(en, Methods.color(Main.CustomE.getEnchantmentColor(en) + Main.CustomE.getCustomName(en) + " " + convertPower(Main.CustomE.getPower(item, en))));
			Main.CustomE.removeEnchantment(item, en);
		}
		ItemMeta meta = item.getItemMeta();
		if(meta != null){
			if(meta.hasLore()){
				for(String l : item.getItemMeta().getLore()){
					lores.add(l);
				}
			}
		}
		enchantments.put(enchant.getName(), Methods.color(enchant.getEnchantmentColor() + enchant.getCustomName() + " " + convertPower(level)));
		for(String en : enchantments.keySet()){
			newLore.add(enchantments.get(en));
		}
		newLore.addAll(lores);
		meta.setLore(newLore);
		item.setItemMeta(meta);
		return Methods.addGlow(item);
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
						for(CEnchantments en : getEnchantments()){
							if(lore.contains(en.getCustomName())){
								enchantments.add(en);
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
	 * @param enchantment The enchantment you want the max level effects from.
	 * @return The list of all the max potion effects based on all the armor on the player.
	 */
	public HashMap<PotionEffectType, Integer> getUpdatedEffects(Player player, ItemStack include, ItemStack exclude, CEnchantments enchantment){
		HashMap<PotionEffectType, Integer> effects = new HashMap<PotionEffectType, Integer>();
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for(ItemStack armor : player.getEquipment().getArmorContents()){
			items.add(armor);
		}
		if(include == null){
			include = new ItemStack(Material.AIR);
		}
		if(exclude == null){
			exclude = new ItemStack(Material.AIR);
		}
		if(exclude.isSimilar(include)){
			exclude = new ItemStack(Material.AIR);
		}
		items.add(include);
		for(CEnchantments ench : getEnchantmentPotions().keySet()){
			for(ItemStack armor : items){
				if(armor != null){
					if(!armor.isSimilar(exclude)){
						if(hasEnchantment(armor, ench)){
							int power = getPower(armor, ench);
							for(PotionEffectType type : getEnchantmentPotions().get(enchantment).keySet()){
								if(getEnchantmentPotions().get(ench).containsKey(type)){
									if(effects.containsKey(type)){
										int updated = effects.get(type);
										if(updated < (power + getEnchantmentPotions().get(ench).get(type))){
											effects.put(type, power + getEnchantmentPotions().get(ench).get(type));
										}
									}else{
										effects.put(type, power + getEnchantmentPotions().get(ench).get(type));
									}
								}
							}
						}
					}
				}
			}
		}
		for(PotionEffectType type : getEnchantmentPotions().get(enchantment).keySet()){
			if(!effects.containsKey(type)){
				effects.put(type, -1);
			}
		}
		return effects;
	}
	
	/**
	 * 
	 * @return All the effects for each enchantment that needs it.
	 */
	public HashMap<CEnchantments, HashMap<PotionEffectType, Integer>> getEnchantmentPotions(){
		HashMap<CEnchantments, HashMap<PotionEffectType, Integer>> enchants = new HashMap<CEnchantments, HashMap<PotionEffectType, Integer>>();
		enchants.put(CEnchantments.BURNSHIELD, new HashMap<PotionEffectType, Integer>());
		enchants.get(CEnchantments.BURNSHIELD).put(PotionEffectType.FIRE_RESISTANCE, -1);
		
		enchants.put(CEnchantments.DRUNK, new HashMap<PotionEffectType, Integer>());
		enchants.get(CEnchantments.DRUNK).put(PotionEffectType.INCREASE_DAMAGE, -1);
		enchants.get(CEnchantments.DRUNK).put(PotionEffectType.SLOW_DIGGING, -1);
		enchants.get(CEnchantments.DRUNK).put(PotionEffectType.SLOW, 0);
		
		enchants.put(CEnchantments.HULK, new HashMap<PotionEffectType, Integer>());
		enchants.get(CEnchantments.HULK).put(PotionEffectType.INCREASE_DAMAGE, -1);
		enchants.get(CEnchantments.HULK).put(PotionEffectType.DAMAGE_RESISTANCE, -1);
		enchants.get(CEnchantments.HULK).put(PotionEffectType.SLOW, 0);
		
		enchants.put(CEnchantments.VALOR, new HashMap<PotionEffectType, Integer>());
		enchants.get(CEnchantments.VALOR).put(PotionEffectType.DAMAGE_RESISTANCE, -1);
		
		enchants.put(CEnchantments.OVERLOAD, new HashMap<PotionEffectType, Integer>());
		enchants.get(CEnchantments.OVERLOAD).put(PotionEffectType.HEALTH_BOOST, 0);
		
		enchants.put(CEnchantments.NINJA, new HashMap<PotionEffectType, Integer>());
		enchants.get(CEnchantments.NINJA).put(PotionEffectType.HEALTH_BOOST, -1);
		enchants.get(CEnchantments.NINJA).put(PotionEffectType.SPEED, -1);
		
		enchants.put(CEnchantments.INSOMNIA, new HashMap<PotionEffectType, Integer>());
		enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.CONFUSION, -1);
		enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.SLOW_DIGGING, -1);
		enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.SLOW,0);
		return enchants;
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
	
	public String convertPower(Integer i){
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
	
}
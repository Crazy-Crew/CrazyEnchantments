package me.BadBones69.CrazyEnchantments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ECControl implements Listener{
	static String Enchants(String cat){
		ArrayList<String> enchants = new ArrayList<String>();
		Random number = new Random();
		for(String en : Main.settings.getEnchs().getConfigurationSection("Enchantments").getKeys(false)){
			for(String C : Main.settings.getEnchs().getStringList("Enchantments."+en+".Categories")){
				if(cat.equalsIgnoreCase(C)){
					String power = powerPicker(en, cat);
					enchants.add(Main.settings.getEnchs().getString("Enchantments."+en+".BookColor")+Main.settings.getEnchs().getString("Enchantments."+en+".Name")+" "+power);
				}
			}
		}
		for(String en : Main.settings.getCustomEnchs().getConfigurationSection("Enchantments").getKeys(false)){
			for(String C : Main.settings.getCustomEnchs().getStringList("Enchantments."+en+".Categories")){
				if(cat.equalsIgnoreCase(C)){
					String power = powerPicker(en, cat);
					enchants.add(Main.settings.getCustomEnchs().getString("Enchantments."+en+".BookColor")+Main.settings.getCustomEnchs().getString("Enchantments."+en+".Name")+" "+power);
				}
			}
		}
		String enchant = enchants.get(number.nextInt(enchants.size()));
		return enchant;
	}
	static HashMap<String, ArrayList<Material>> allEnchantments(){
		HashMap<String, ArrayList<Material>> en = new HashMap<String, ArrayList<Material>>();
		//---------Sword---------//
		en.put("Viper", isSword());
		en.put("SlowMo", isSword());
		en.put("Vampire", isSword());
		en.put("FastTurn", isSword());
		en.put("Blindness", isSword());
		en.put("LifeSteal", isSword());
		en.put("LightWeight", isSword());
		en.put("DoubleDamage", isSword());
		//----------Axes--------//
		en.put("Rekt", isAxe());
		en.put("Dizzy", isAxe());
		en.put("Cursed", isAxe());
		en.put("FeedMe", isAxe());
		en.put("Blessed", isAxe());
		en.put("Berserk", isAxe());
		//----------Bow----------//
		en.put("Boom", isBow());
		en.put("Venom", isBow());
		en.put("Doctor", isBow());
		en.put("Piercing", isBow());
		//---------Armor---------//
		en.put("Hulk", isArmor());
		en.put("Ninja", isArmor());
		en.put("Molten", isArmor());
		en.put("Savior", isArmor());
		en.put("Freeze", isArmor());
		en.put("Nursery", isArmor());
		en.put("Fortify", isArmor());
		en.put("OverLoad", isArmor());
		en.put("PainGiver", isArmor());
		en.put("BurnShield", isArmor());
		en.put("Enlightened", isArmor());
		en.put("SelfDestruct", isArmor());
		//--------Helmets--------//
		en.put("Mermaid", isHelmet());
		en.put("Glowing", isHelmet());
		//---------Boots--------//
		en.put("Gears", isBoots());
		en.put("Springs", isBoots());
		en.put("AntiGravity", isBoots());
		//---------Custom--------//
		for(String ench : CustomEnchantments.getEnchantments()){
			String type = Main.settings.getCustomEnchs().getString("Enchantments."+ench+".EnchantOptions.ItemsEnchantable");
			if(type.equalsIgnoreCase("Armor"))en.put(ench, isArmor());
			if(type.equalsIgnoreCase("Helmets"))en.put(ench, isHelmet());
			if(type.equalsIgnoreCase("Boots"))en.put(ench, isBoots());
			if(type.equalsIgnoreCase("Swords"))en.put(ench, isSword());
			if(type.equalsIgnoreCase("Axes"))en.put(ench, isAxe());
			if(type.equalsIgnoreCase("Weapons"))en.put(ench, isWeapon());
			if(type.equalsIgnoreCase("Bows"))en.put(ench, isBow());
		}
		return en;
	}
	@EventHandler
	public void onClick(PlayerInteractEvent e){
		Player player = e.getPlayer();
		if(e.getItem()!=null){
			ItemStack item = e.getItem();
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasDisplayName()){
					for(String i : allEnchantments().keySet()){
						if(item.getItemMeta().getDisplayName().contains(i)){
							player.sendMessage(Api.getPrefix()+Api.color(Main.settings.getMsg().getString("Messages.Right-Click-Book")));
						}
					}
				}
			}
		}
	}
	static ItemStack pick(String cat){
		int Smax = Main.settings.getConfig().getInt("Categories."+cat+".EnchOptions.SuccessPercent.Max");
		int Smin = Main.settings.getConfig().getInt("Categories."+cat+".EnchOptions.SuccessPercent.Min");
		int Dmax = Main.settings.getConfig().getInt("Categories."+cat+".EnchOptions.DestroyPercent.Max");
		int Dmin = Main.settings.getConfig().getInt("Categories."+cat+".EnchOptions.DestroyPercent.Min");
		ArrayList<String> lore = new ArrayList<String>();
		if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.DestroyChance")){
			lore.add(Api.color("&4"+percentPick(Dmax, Dmin)+"% Destroy Chance"));
		}
		if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.SuccessChance")){
			lore.add(Api.color("&a"+percentPick(Smax, Smin)+"% Success Chance"));
		}
		return Api.makeItem(Material.BOOK, 1, 0, Enchants(cat), Api.addDiscription(), lore);
	}
	static String percentPick(int max, int min){
		Random i = new Random();
		return Integer.toString(min+i.nextInt(max-min));
	}
	static String powerPicker(String en, String C){
		Random r = new Random();
		int ench = 5; //Max set by the enchantment
		if(Main.settings.getEnchs().contains("Enchantments."+en)){
			ench=Main.settings.getEnchs().getInt("Enchantments."+en+".MaxPower");
		}
		if(Main.settings.getCustomEnchs().contains("Enchantments."+en)){
			ench=Main.settings.getCustomEnchs().getInt("Enchantments."+en+".MaxPower");
		}
		int max = Main.settings.getConfig().getInt("Categories."+C+".EnchOptions.LvlRange.Max"); //Max lvl set by the Category
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
	public static ArrayList<Material> isArmor(){
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.DIAMOND_HELMET);
		ma.add(Material.DIAMOND_CHESTPLATE);
		ma.add(Material.DIAMOND_LEGGINGS);
		ma.add(Material.DIAMOND_BOOTS);
		ma.add(Material.CHAINMAIL_HELMET);
		ma.add(Material.CHAINMAIL_CHESTPLATE);
		ma.add(Material.CHAINMAIL_LEGGINGS);
		ma.add(Material.CHAINMAIL_BOOTS);
		ma.add(Material.GOLD_HELMET);
		ma.add(Material.GOLD_CHESTPLATE);
		ma.add(Material.GOLD_LEGGINGS);
		ma.add(Material.GOLD_BOOTS);
		ma.add(Material.IRON_HELMET);
		ma.add(Material.IRON_CHESTPLATE);
		ma.add(Material.IRON_LEGGINGS);
		ma.add(Material.IRON_BOOTS);
		ma.add(Material.DIAMOND_HELMET);
		ma.add(Material.DIAMOND_CHESTPLATE);
		ma.add(Material.DIAMOND_LEGGINGS);
		ma.add(Material.DIAMOND_BOOTS);
		return ma;
	}
	public static ArrayList<Material> isHelmet(){
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.DIAMOND_HELMET);
		ma.add(Material.CHAINMAIL_HELMET);
		ma.add(Material.GOLD_HELMET);
		ma.add(Material.IRON_HELMET);
		ma.add(Material.DIAMOND_HELMET);
		return ma;
	}
	public static ArrayList<Material> isBoots(){
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.DIAMOND_BOOTS);
		ma.add(Material.CHAINMAIL_BOOTS);
		ma.add(Material.GOLD_BOOTS);
		ma.add(Material.IRON_BOOTS);
		ma.add(Material.DIAMOND_BOOTS);
		return ma;
	}
	public static ArrayList<Material> isAxe(){
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.WOOD_AXE);
		ma.add(Material.STONE_AXE);
		ma.add(Material.IRON_AXE);
		ma.add(Material.DIAMOND_AXE);
		return ma;
	}
	public static ArrayList<Material> isBow(){
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.BOW);
		return ma;
	}
	public static ArrayList<Material> isSword(){
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.WOOD_SWORD);
		ma.add(Material.STONE_SWORD);
		ma.add(Material.IRON_SWORD);
		ma.add(Material.DIAMOND_SWORD);
		return ma;
	}
	public static ArrayList<Material> isWeapon(){
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.WOOD_SWORD);
		ma.add(Material.STONE_SWORD);
		ma.add(Material.IRON_SWORD);
		ma.add(Material.DIAMOND_SWORD);
		ma.add(Material.WOOD_AXE);
		ma.add(Material.STONE_AXE);
		ma.add(Material.IRON_AXE);
		ma.add(Material.DIAMOND_AXE);
		return ma;
	}
}
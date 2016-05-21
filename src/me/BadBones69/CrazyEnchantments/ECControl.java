package me.BadBones69.CrazyEnchantments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
					if(Api.isEnchantmentEnabled(en)){
						String power = powerPicker(en, cat);
						enchants.add(Main.settings.getEnchs().getString("Enchantments."+en+".BookColor")+Main.settings.getEnchs().getString("Enchantments."+en+".Name")+" "+power);
					}
				}
			}
		}
		if(Main.settings.getCustomEnchs().contains("Enchantments")){
			for(String en : Main.settings.getCustomEnchs().getConfigurationSection("Enchantments").getKeys(false)){
				for(String C : Main.settings.getCustomEnchs().getStringList("Enchantments."+en+".Categories")){
					if(cat.equalsIgnoreCase(C)){
						String power = powerPicker(en, cat);
						enchants.add(Main.settings.getCustomEnchs().getString("Enchantments."+en+".BookColor")+Main.settings.getCustomEnchs().getString("Enchantments."+en+".Name")+" "+power);
					}
				}
			}
		}
		String enchant = enchants.get(number.nextInt(enchants.size()));
		return enchant;
	}
	static HashMap<String, ArrayList<Material>> allEnchantments(){
		HashMap<String, ArrayList<Material>> en = new HashMap<String, ArrayList<Material>>();
		//---------Sword---------//
		en.put("Trap", isSword());
		en.put("Rage", isSword());
		en.put("Viper", isSword());
		en.put("Snare", isSword());
		en.put("SlowMo", isSword());
		en.put("Wither", isSword());
		en.put("Vampire", isSword());
		en.put("Execute", isSword());
		en.put("FastTurn", isSword());
		en.put("Disarmer", isSword());
		en.put("Headless", isSword());
		en.put("Insomnia", isSword());
		en.put("Paralyze", isSword());
		en.put("Blindness", isSword());
		en.put("LifeSteal", isSword());
		en.put("Confusion", isSword());
		en.put("Nutrition", isSword());
		en.put("SkillSwipe", isSword());
		en.put("Obliterate", isSword());
		en.put("Inquisitive", isSword());
		en.put("LightWeight", isSword());
		en.put("DoubleDamage", isSword());
		//----------Axes--------//
		en.put("Rekt", isAxe());
		en.put("Dizzy", isAxe());
		en.put("Cursed", isAxe());
		en.put("FeedMe", isAxe());
		en.put("Blessed", isAxe());
		en.put("Berserk", isAxe());
		en.put("Decapitation", isAxe());
		//----------Bow----------//
		en.put("Boom", isBow());
		en.put("Venom", isBow());
		en.put("Doctor", isBow());
		en.put("Piercing", isBow());
		en.put("IceFreeze", isBow());
		en.put("Lightning", isBow());
		//---------Armor---------//
		en.put("Hulk", isArmor());
		en.put("Valor", isArmor());
		en.put("Drunk", isArmor());
		en.put("Ninja", isArmor());
		en.put("Voodoo", isArmor());
		en.put("Molten", isArmor());
		en.put("Savior", isArmor());
		en.put("Freeze", isArmor());
		en.put("Nursery", isArmor());
		en.put("Fortify", isArmor());
		en.put("OverLoad", isArmor());
		en.put("SmokeBomb", isArmor());
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
		//---------PickAxes--------//
		en.put("AutoSmelt", isPickAxe());
		en.put("Experience", isPickAxe());
		//---------Tools--------//
		en.put("Haste", isTool());
		en.put("Telepathy", isTool());
		en.put("Oxygenate", isTool());
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
			if(type.equalsIgnoreCase("Pickaxes"))en.put(ench, isPickAxe());
			if(type.equalsIgnoreCase("Tools"))en.put(ench, isTool());
		}
		return en;
	}
	@EventHandler
	public void onBookClean(PlayerInteractEvent e){
		Player player = e.getPlayer();
		if(e.getItem()!=null){
			ItemStack item = e.getItem();
			if(item.getType()!=Material.BOOK)return;
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasDisplayName()){
					if(item.getItemMeta().getDisplayName().equals(Api.color(Main.settings.getConfig().getString("Settings.LostBook.Name")))){
						for(String C : Main.settings.getConfig().getConfigurationSection("Categories").getKeys(false)){
							if(Api.color(Main.settings.getConfig().getString("Categories."+C+".Name")).equalsIgnoreCase(getCategory(item))){
								Api.removeItem(item, player);
								ItemStack book = Api.addGlow(pick(C));
								player.getInventory().addItem(book);
								player.sendMessage(Api.getPrefix()+Api.color(Main.settings.getMsg().getString("Messages.Clean-Lost-Book")
										.replaceAll("%Found%", book.getItemMeta().getDisplayName()).replaceAll("%found%", book.getItemMeta().getDisplayName())));
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
		Player player = e.getPlayer();
		if(e.getItem()!=null){
			ItemStack item = e.getItem();
			if(item.getType()!=Material.BOOK)return;
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasDisplayName()){
					for(String en : allEnchantments().keySet()){
						if(item.getItemMeta().getDisplayName().contains(Api.color(Api.getEnchBookColor(en)+Api.getEnchName(en)))){
							String name = Main.settings.getEnchs().getString("Enchantments."+en+".Info.Name");
							List<String> desc = Main.settings.getEnchs().getStringList("Enchantments."+en+".Info.Description");
							player.sendMessage(Api.color(name));
							for(String msg : desc)player.sendMessage(Api.color(msg));
							return;
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
			lore.add(Api.color("&4"+Api.percentPick(Dmax, Dmin)+"% Destroy Chance"));
		}
		if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.SuccessChance")){
			lore.add(Api.color("&a"+Api.percentPick(Smax, Smin)+"% Success Chance"));
		}
		return Api.makeItem(Material.BOOK, 1, 0, Enchants(cat), Api.addDiscription(), lore);
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
	public static ArrayList<Material> isPickAxe(){
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.WOOD_PICKAXE);
		ma.add(Material.STONE_PICKAXE);
		ma.add(Material.IRON_PICKAXE);
		ma.add(Material.DIAMOND_PICKAXE);
		return ma;
	}
	public static ArrayList<Material> isTool(){
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.WOOD_PICKAXE);
		ma.add(Material.STONE_PICKAXE);
		ma.add(Material.IRON_PICKAXE);
		ma.add(Material.DIAMOND_PICKAXE);
		ma.add(Material.WOOD_AXE);
		ma.add(Material.STONE_AXE);
		ma.add(Material.IRON_AXE);
		ma.add(Material.DIAMOND_AXE);
		ma.add(Material.WOOD_SPADE);
		ma.add(Material.STONE_SPADE);
		ma.add(Material.IRON_SPADE);
		ma.add(Material.DIAMOND_SPADE);
		ma.add(Material.WOOD_HOE);
		ma.add(Material.STONE_HOE);
		ma.add(Material.IRON_HOE);
		ma.add(Material.DIAMOND_HOE);
		return ma;
	}
}
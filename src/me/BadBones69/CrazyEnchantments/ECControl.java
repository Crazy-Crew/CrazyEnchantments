package me.BadBones69.CrazyEnchantments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ECControl implements Listener{
	static String T1(){
		ArrayList<String> enchants = new ArrayList<String>();
		Random number = new Random();
		String power = " I";
		for(String i : Main.settings.getConfig().getStringList("Settings.T1.Enchantments")){
			enchants.add(Api.getBookColor()+i+power);
		}
		String enchant = enchants.get(number.nextInt(enchants.size()));
		return enchant;
	}
	static String T2(){
		ArrayList<String> enchants = new ArrayList<String>();
		Random number = new Random();
		String power = " II";
		for(String i : Main.settings.getConfig().getStringList("Settings.T2.Enchantments")){
			enchants.add(Api.getBookColor()+i+power);
		}
		String enchant = enchants.get(number.nextInt(enchants.size()));
		return enchant;
	}
	static String T3(){
		ArrayList<String> enchants = new ArrayList<String>();
		Random number = new Random();
		String power = " III";
		for(String i : Main.settings.getConfig().getStringList("Settings.T3.Enchantments")){
			enchants.add(Api.getBookColor()+i+power);
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
							player.sendMessage(Api.getPrefix()+Api.color("&7The &bSuccess Chance &7is the chance the enchantment will successfully be added to your item."));
						}
					}
				}
			}
		}
	}
	static ItemStack pickT1(){
		Random number = new Random();
		int chance;
		for(int counter = 1; counter<=1; counter++){
			chance = 1 + number.nextInt(100);
			if(chance >= 1 && chance <= 20){
				return Api.makeItem(Material.BOOK, 1, 0, T2(),
						Api.addDiscription(), Arrays.asList(Api.color("&a"+percentPickT2()+"% Success Chance")));
			}
		}
		return Api.makeItem(Material.BOOK, 1, 0, T1(),
				Api.addDiscription(), Arrays.asList(Api.color("&a"+percentPickT1()+"% Success Chance")));
	}
	static ItemStack pickT2(){
		Random number = new Random();
		int chance;
		for(int counter = 1; counter<=1; counter++){
			chance = 1 + number.nextInt(100);
			if(chance >= 1 && chance <= 20){
				return Api.makeItem(Material.BOOK, 1, 0, T3(),
						Api.addDiscription(), Arrays.asList(Api.color("&a"+percentPickT3()+"% Success Chance")));
			}
		}
		return Api.makeItem(Material.BOOK, 1, 0, T2(),
				Api.addDiscription(), Arrays.asList(Api.color("&a"+percentPickT2()+"% Success Chance")));
	}
	static ItemStack pickT3(){
		Random number = new Random();
		int chance;
		for(int counter = 1; counter<=1; counter++){
			chance = 1 + number.nextInt(100);
			if(chance >= 1 && chance <= 5){
				return Api.makeItem(Material.BOOK, 1, 0, T2(),
						Api.addDiscription(), Arrays.asList(Api.color("&a"+percentPickT2()+"% Success Chance")));
			}
		}
		return Api.makeItem(Material.BOOK, 1, 0, T3(),
				Api.addDiscription(), Arrays.asList(Api.color("&a"+percentPickT3()+"% Success Chance")));
	}
	private static String percentPickT1(){
		Random i = new Random();
		return Integer.toString(40+i.nextInt(50));
	}
	private static String percentPickT2(){
		Random i = new Random();
		return Integer.toString(30+i.nextInt(30));
	}
	private static String percentPickT3(){
		Random i = new Random();
		return Integer.toString(10+i.nextInt(35));
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
}
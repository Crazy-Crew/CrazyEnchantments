package me.BadBones69.CrazyEnchantments.API;

import java.util.ArrayList;

import org.bukkit.Material;


public enum EnchantmentType{
	ARMOR("Armor", isArmor()), AXE("Axe", isAxe()), BOW("Bow", isBow()), BOOTS("Boots", isBoots()),
	HELMET("Helmet", isHelmet()), SWORD("Sword", isSword()), WEAPON("Weapon", isWeapon());
	String name;
	ArrayList<Material> items;
	private EnchantmentType(String name, ArrayList<Material> items){
		this.name=name;
		this.items=items;
	}
	/**
	 * 
	 * @return The Enchantment Type Name.
	 */
	public String getName(){
		return name;
	}
	/**
	 * 
	 * @return List of all items in the Type.
	 */
	public ArrayList<Material> getItems(){
		return items;
	}
	/**
	 * 
	 * @return List of all Enchantments in the Type.
	 */
	public ArrayList<Enchantment> getEnchantmnets(){
		ArrayList<Enchantment> en = new ArrayList<Enchantment>();
		for(Enchantment e : Enchantment.getEnchantments()){
			if(e.getType()==getFromName(name)){
				en.add(e);
			}
		}
		return en;
	}
	/**
	 * 
	 * @return List of all Types.
	 */
	public static EnchantmentType[] getTypes(){
		EnchantmentType[] enchs=EnchantmentType.values();
		return enchs;
	}
	/**
	 * 
	 * @param name Name of the Enchantment Type.
	 * @return The Enchantment Type.
	 */
	public static EnchantmentType getFromName(String name){
		for(EnchantmentType e : getTypes()){
			if(e.getName().equalsIgnoreCase(name)){
				return e;
			}
		}
		return null;
	}
	static ArrayList<Material> isArmor(){
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
	static ArrayList<Material> isHelmet(){
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.DIAMOND_HELMET);
		ma.add(Material.CHAINMAIL_HELMET);
		ma.add(Material.GOLD_HELMET);
		ma.add(Material.IRON_HELMET);
		ma.add(Material.DIAMOND_HELMET);
		return ma;
	}
	static ArrayList<Material> isBoots(){
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.DIAMOND_BOOTS);
		ma.add(Material.CHAINMAIL_BOOTS);
		ma.add(Material.GOLD_BOOTS);
		ma.add(Material.IRON_BOOTS);
		ma.add(Material.DIAMOND_BOOTS);
		return ma;
	}
	static ArrayList<Material> isAxe(){
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.WOOD_AXE);
		ma.add(Material.STONE_AXE);
		ma.add(Material.IRON_AXE);
		ma.add(Material.DIAMOND_AXE);
		return ma;
	}
	static ArrayList<Material> isBow(){
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.BOW);
		return ma;
	}
	static ArrayList<Material> isSword(){
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.WOOD_SWORD);
		ma.add(Material.STONE_SWORD);
		ma.add(Material.IRON_SWORD);
		ma.add(Material.DIAMOND_SWORD);
		return ma;
	}
	static ArrayList<Material> isWeapon(){
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

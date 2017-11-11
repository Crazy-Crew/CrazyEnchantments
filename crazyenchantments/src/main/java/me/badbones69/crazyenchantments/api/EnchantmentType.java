package me.badbones69.crazyenchantments.api;

import me.badbones69.crazyenchantments.Main;
import org.bukkit.Material;

import java.util.ArrayList;

public enum EnchantmentType {

	ARMOR("Armor", isArmor()),
	HELMET("Helmet", isHelmet()),
	CHESTPLATE("Chestplate", isChestplate()),
	LEGGINGS("Leggings", isLeggings()),
	BOOTS("Boots", isBoots()),
	BOW("Bow", isBow()),
	AXE("Axe", isAxe()),
	SWORD("Sword", isSword()),
	WEAPONS("Weapons", isWeapon()),
	PICKAXE("PickAxe", isPickAxe()),
	TOOL("Tool", isTool()),
	ALL("All", isAll());

	private String name;
	private ArrayList<Material> items;

	/**\
	 *
	 * @param name Name of the type.
	 * @param items Items in the enchantment type.
	 */
	private EnchantmentType(String name, ArrayList<Material> items) {
		this.name = name;
		this.items = items;
	}

	/**
	 *
	 * @return The Enchantment Type Name.
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @return List of all items in the Type.
	 */
	public ArrayList<Material> getItems() {
		return items;
	}

	/**
	 *
	 * @return List of all Enchantments in the Type.
	 */
	public ArrayList<CEnchantments> getEnchantmnets() {
		ArrayList<CEnchantments> en = new ArrayList<CEnchantments>();
		for(CEnchantments e : Main.CE.getEnchantments()) {
			if(e.getType() == getFromName(name)) {
				en.add(e);
			}
		}
		return en;
	}

	/**
	 *
	 * @return List of all Types.
	 */
	public static EnchantmentType[] getTypes() {
		EnchantmentType[] enchs = EnchantmentType.values();
		return enchs;
	}

	/**
	 *
	 * @param name Name of the Enchantment Type.
	 * @return The Enchantment Type.
	 */
	public static EnchantmentType getFromName(String name) {
		for(EnchantmentType e : getTypes()) {
			if(e.getName().equalsIgnoreCase(name)) {
				return e;
			}
		}
		return null;
	}

	private static ArrayList<Material> isArmor() {
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.LEATHER_HELMET);
		ma.add(Material.LEATHER_CHESTPLATE);
		ma.add(Material.LEATHER_LEGGINGS);
		ma.add(Material.LEATHER_BOOTS);
		ma.add(Material.IRON_HELMET);
		ma.add(Material.IRON_CHESTPLATE);
		ma.add(Material.IRON_LEGGINGS);
		ma.add(Material.IRON_BOOTS);
		ma.add(Material.CHAINMAIL_HELMET);
		ma.add(Material.CHAINMAIL_CHESTPLATE);
		ma.add(Material.CHAINMAIL_LEGGINGS);
		ma.add(Material.CHAINMAIL_BOOTS);
		ma.add(Material.GOLD_HELMET);
		ma.add(Material.GOLD_CHESTPLATE);
		ma.add(Material.GOLD_LEGGINGS);
		ma.add(Material.GOLD_BOOTS);
		ma.add(Material.DIAMOND_HELMET);
		ma.add(Material.DIAMOND_CHESTPLATE);
		ma.add(Material.DIAMOND_LEGGINGS);
		ma.add(Material.DIAMOND_BOOTS);
		return ma;
	}

	private static ArrayList<Material> isChestplate() {
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.LEATHER_CHESTPLATE);
		ma.add(Material.IRON_CHESTPLATE);
		ma.add(Material.CHAINMAIL_CHESTPLATE);
		ma.add(Material.GOLD_CHESTPLATE);
		ma.add(Material.DIAMOND_CHESTPLATE);
		return ma;
	}

	private static ArrayList<Material> isLeggings() {
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.LEATHER_LEGGINGS);
		ma.add(Material.IRON_LEGGINGS);
		ma.add(Material.CHAINMAIL_LEGGINGS);
		ma.add(Material.GOLD_LEGGINGS);
		ma.add(Material.DIAMOND_LEGGINGS);
		return ma;
	}

	private static ArrayList<Material> isHelmet() {
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.LEATHER_HELMET);
		ma.add(Material.IRON_HELMET);
		ma.add(Material.CHAINMAIL_HELMET);
		ma.add(Material.GOLD_HELMET);
		ma.add(Material.DIAMOND_HELMET);
		return ma;
	}

	private static ArrayList<Material> isBoots() {
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.LEATHER_BOOTS);
		ma.add(Material.IRON_BOOTS);
		ma.add(Material.CHAINMAIL_BOOTS);
		ma.add(Material.GOLD_BOOTS);
		ma.add(Material.DIAMOND_BOOTS);
		return ma;
	}

	private static ArrayList<Material> isAxe() {
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.WOOD_AXE);
		ma.add(Material.STONE_AXE);
		ma.add(Material.IRON_AXE);
		ma.add(Material.GOLD_AXE);
		ma.add(Material.DIAMOND_AXE);
		return ma;
	}

	private static ArrayList<Material> isBow() {
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.BOW);
		return ma;
	}

	private static ArrayList<Material> isSword() {
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.WOOD_SWORD);
		ma.add(Material.STONE_SWORD);
		ma.add(Material.IRON_SWORD);
		ma.add(Material.GOLD_SWORD);
		ma.add(Material.DIAMOND_SWORD);
		return ma;
	}

	private static ArrayList<Material> isWeapon() {
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.WOOD_SWORD);
		ma.add(Material.STONE_SWORD);
		ma.add(Material.IRON_SWORD);
		ma.add(Material.GOLD_SWORD);
		ma.add(Material.DIAMOND_SWORD);
		ma.add(Material.WOOD_AXE);
		ma.add(Material.STONE_AXE);
		ma.add(Material.IRON_AXE);
		ma.add(Material.GOLD_AXE);
		ma.add(Material.DIAMOND_AXE);
		return ma;
	}

	private static ArrayList<Material> isPickAxe() {
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.WOOD_PICKAXE);
		ma.add(Material.STONE_PICKAXE);
		ma.add(Material.IRON_PICKAXE);
		ma.add(Material.GOLD_PICKAXE);
		ma.add(Material.DIAMOND_PICKAXE);
		return ma;
	}

	private static ArrayList<Material> isTool() {
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.add(Material.WOOD_PICKAXE);
		ma.add(Material.STONE_PICKAXE);
		ma.add(Material.IRON_PICKAXE);
		ma.add(Material.GOLD_PICKAXE);
		ma.add(Material.DIAMOND_PICKAXE);
		ma.add(Material.WOOD_AXE);
		ma.add(Material.STONE_AXE);
		ma.add(Material.IRON_AXE);
		ma.add(Material.GOLD_AXE);
		ma.add(Material.DIAMOND_AXE);
		ma.add(Material.WOOD_SPADE);
		ma.add(Material.STONE_SPADE);
		ma.add(Material.IRON_SPADE);
		ma.add(Material.GOLD_SPADE);
		ma.add(Material.DIAMOND_SPADE);
		ma.add(Material.WOOD_HOE);
		ma.add(Material.STONE_HOE);
		ma.add(Material.IRON_HOE);
		ma.add(Material.GOLD_HOE);
		ma.add(Material.DIAMOND_HOE);
		return ma;
	}

	private static ArrayList<Material> isAll() {
		ArrayList<Material> ma = new ArrayList<Material>();
		ma.addAll(isArmor());
		ma.addAll(isTool());
		ma.addAll(isBow());
		ma.addAll(isWeapon());
		return ma;
	}
}
package me.badbones69.crazyenchantments.api.enums;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;

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
	FISHING_ROD("Fishing-Rod", new ArrayList<>(Collections.singletonList(Material.FISHING_ROD))),
	ELYTRA("Elytra", new ArrayList<>(Collections.singletonList(Material.ELYTRA))),
	ALL("All", isAll());
	
	private String name;
	private ArrayList<Material> items;
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	/**
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
	public ArrayList<CEnchantment> getEnchantmnets() {
		ArrayList<CEnchantment> en = new ArrayList<>();
		for(CEnchantment e : ce.getRegisteredEnchantments()) {
			if(e.getEnchantmentType() == getFromName(name)) {
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
		return EnchantmentType.values();
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
		ArrayList<Material> ma = new ArrayList<>();
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
		ma.add(Material.GOLDEN_HELMET);
		ma.add(Material.GOLDEN_CHESTPLATE);
		ma.add(Material.GOLDEN_LEGGINGS);
		ma.add(Material.GOLDEN_BOOTS);
		ma.add(Material.DIAMOND_HELMET);
		ma.add(Material.DIAMOND_CHESTPLATE);
		ma.add(Material.DIAMOND_LEGGINGS);
		ma.add(Material.DIAMOND_BOOTS);
		ma.add(Material.TURTLE_HELMET);
		return ma;
	}
	
	private static ArrayList<Material> isChestplate() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(Material.LEATHER_CHESTPLATE);
		ma.add(Material.IRON_CHESTPLATE);
		ma.add(Material.CHAINMAIL_CHESTPLATE);
		ma.add(Material.GOLDEN_CHESTPLATE);
		ma.add(Material.DIAMOND_CHESTPLATE);
		return ma;
	}
	
	private static ArrayList<Material> isLeggings() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(Material.LEATHER_LEGGINGS);
		ma.add(Material.IRON_LEGGINGS);
		ma.add(Material.CHAINMAIL_LEGGINGS);
		ma.add(Material.GOLDEN_LEGGINGS);
		ma.add(Material.DIAMOND_LEGGINGS);
		return ma;
	}
	
	private static ArrayList<Material> isHelmet() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(Material.LEATHER_HELMET);
		ma.add(Material.IRON_HELMET);
		ma.add(Material.CHAINMAIL_HELMET);
		ma.add(Material.GOLDEN_HELMET);
		ma.add(Material.DIAMOND_HELMET);
		ma.add(Material.TURTLE_HELMET);
		return ma;
	}
	
	private static ArrayList<Material> isBoots() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(Material.LEATHER_BOOTS);
		ma.add(Material.IRON_BOOTS);
		ma.add(Material.CHAINMAIL_BOOTS);
		ma.add(Material.GOLDEN_BOOTS);
		ma.add(Material.DIAMOND_BOOTS);
		return ma;
	}
	
	private static ArrayList<Material> isAxe() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(Material.WOODEN_AXE);
		ma.add(Material.STONE_AXE);
		ma.add(Material.IRON_AXE);
		ma.add(Material.GOLDEN_AXE);
		ma.add(Material.DIAMOND_AXE);
		return ma;
	}
	
	private static ArrayList<Material> isBow() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(Material.BOW);
		return ma;
	}
	
	private static ArrayList<Material> isSword() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(Material.WOODEN_SWORD);
		ma.add(Material.STONE_SWORD);
		ma.add(Material.IRON_SWORD);
		ma.add(Material.GOLDEN_SWORD);
		ma.add(Material.DIAMOND_SWORD);
		return ma;
	}
	
	private static ArrayList<Material> isWeapon() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(Material.WOODEN_SWORD);
		ma.add(Material.STONE_SWORD);
		ma.add(Material.IRON_SWORD);
		ma.add(Material.GOLDEN_SWORD);
		ma.add(Material.DIAMOND_SWORD);
		ma.add(Material.WOODEN_AXE);
		ma.add(Material.STONE_AXE);
		ma.add(Material.IRON_AXE);
		ma.add(Material.GOLDEN_AXE);
		ma.add(Material.DIAMOND_AXE);
		return ma;
	}
	
	private static ArrayList<Material> isPickAxe() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(Material.WOODEN_PICKAXE);
		ma.add(Material.STONE_PICKAXE);
		ma.add(Material.IRON_PICKAXE);
		ma.add(Material.GOLDEN_PICKAXE);
		ma.add(Material.DIAMOND_PICKAXE);
		return ma;
	}
	
	private static ArrayList<Material> isTool() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(Material.WOODEN_PICKAXE);
		ma.add(Material.STONE_PICKAXE);
		ma.add(Material.IRON_PICKAXE);
		ma.add(Material.GOLDEN_PICKAXE);
		ma.add(Material.DIAMOND_PICKAXE);
		ma.add(Material.WOODEN_AXE);
		ma.add(Material.STONE_AXE);
		ma.add(Material.IRON_AXE);
		ma.add(Material.GOLDEN_AXE);
		ma.add(Material.DIAMOND_AXE);
		ma.add(Material.WOODEN_SHOVEL);
		ma.add(Material.STONE_SHOVEL);
		ma.add(Material.IRON_SHOVEL);
		ma.add(Material.GOLDEN_SHOVEL);
		ma.add(Material.DIAMOND_SHOVEL);
		ma.add(Material.WOODEN_HOE);
		ma.add(Material.STONE_HOE);
		ma.add(Material.IRON_HOE);
		ma.add(Material.GOLDEN_HOE);
		ma.add(Material.DIAMOND_HOE);
		ma.add(Material.SHEARS);
		return ma;
	}
	
	private static ArrayList<Material> isAll() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.addAll(isArmor());
		ma.addAll(isTool());
		ma.addAll(isBow());
		ma.addAll(isWeapon());
		ma.add(Material.FISHING_ROD);
		ma.add(Material.ELYTRA);
		return ma;
	}
	
}

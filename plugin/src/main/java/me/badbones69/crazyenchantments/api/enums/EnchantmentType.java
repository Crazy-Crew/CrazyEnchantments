package me.badbones69.crazyenchantments.api.enums;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;

public enum EnchantmentType {
	
	ARMOR("Armor", getArmor()),
	HELMET("Helmet", getHelmets()),
	CHESTPLATE("Chestplate", getChestplates()),
	LEGGINGS("Leggings", getLeggings()),
	BOOTS("Boots", getBoots()),
	BOW("Bow", getBow()),
	AXE("Axe", getAxes()),
	SWORD("Sword", getSwords()),
	WEAPONS("Weapons", getWepons()),
	PICKAXE("PickAxe", getPickAxes()),
	SHOVEL("Shovel", getShovels()),
	TOOL("Tool", getTools()),
	FISHING_ROD("Fishing-Rod", new ArrayList<>(Collections.singletonList(Material.FISHING_ROD))),
	ELYTRA("Elytra", getElytra()),
	ALL("All", getAll());
	
	private String name;
	private ArrayList<Material> items;
	private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
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
	
	private static ArrayList<Material> getArmor() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.addAll(getHelmets());
		ma.addAll(getChestplates());
		ma.addAll(getLeggings());
		ma.addAll(getBoots());
		return ma;
	}
	
	private static ArrayList<Material> getHelmets() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(Material.LEATHER_HELMET);
		ma.add(Material.IRON_HELMET);
		ma.add(Material.CHAINMAIL_HELMET);
		ma.add(ce.getMaterial("GOLDEN_HELMET", "GOLD_HELMET"));
		ma.add(Material.DIAMOND_HELMET);
		if(Version.getCurrentVersion().isNewer(Version.v1_12_R1)) {
			ma.add(Material.matchMaterial("TURTLE_HELMET"));
		}
		return ma;
	}
	
	private static ArrayList<Material> getChestplates() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(Material.LEATHER_CHESTPLATE);
		ma.add(Material.IRON_CHESTPLATE);
		ma.add(Material.CHAINMAIL_CHESTPLATE);
		ma.add(ce.getMaterial("GOLDEN_CHESTPLATE", "GOLD_CHESTPLATE"));
		ma.add(Material.DIAMOND_CHESTPLATE);
		return ma;
	}
	
	private static ArrayList<Material> getLeggings() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(Material.LEATHER_LEGGINGS);
		ma.add(Material.IRON_LEGGINGS);
		ma.add(Material.CHAINMAIL_LEGGINGS);
		ma.add(ce.getMaterial("GOLDEN_LEGGINGS", "GOLD_LEGGINGS"));
		ma.add(Material.DIAMOND_LEGGINGS);
		return ma;
	}
	
	private static ArrayList<Material> getBoots() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(Material.LEATHER_BOOTS);
		ma.add(Material.IRON_BOOTS);
		ma.add(Material.CHAINMAIL_BOOTS);
		ma.add(ce.getMaterial("GOLDEN_BOOTS", "GOLD_BOOTS"));
		ma.add(Material.DIAMOND_BOOTS);
		return ma;
	}
	
	private static ArrayList<Material> getAxes() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(ce.getMaterial("WOODEN_AXE", "WOOD_AXE"));
		ma.add(Material.STONE_AXE);
		ma.add(Material.IRON_AXE);
		ma.add(ce.getMaterial("GOLDEN_AXE", "GOLD_AXE"));
		ma.add(Material.DIAMOND_AXE);
		return ma;
	}
	
	private static ArrayList<Material> getBow() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(Material.BOW);
		return ma;
	}
	
	private static ArrayList<Material> getSwords() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(ce.getMaterial("WOODEN_SWORD", "WOOD_SWORD"));
		ma.add(Material.STONE_SWORD);
		ma.add(Material.IRON_SWORD);
		ma.add(ce.getMaterial("GOLDEN_SWORD", "GOLD_SWORD"));
		ma.add(Material.DIAMOND_SWORD);
		return ma;
	}
	
	private static ArrayList<Material> getWepons() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.addAll(getSwords());
		ma.addAll(getAxes());
		if(Version.getCurrentVersion().isNewer(Version.v1_12_R1)) {
			ma.add(Material.matchMaterial("TRIDENT"));
		}
		return ma;
	}
	
	private static ArrayList<Material> getPickAxes() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(ce.getMaterial("WOODEN_PICKAXE", "WOOD_PICKAXE"));
		ma.add(Material.STONE_PICKAXE);
		ma.add(Material.IRON_PICKAXE);
		ma.add(ce.getMaterial("GOLDEN_PICKAXE", "GOLD_PICKAXE"));
		ma.add(Material.DIAMOND_PICKAXE);
		return ma;
	}
	
	private static ArrayList<Material> getShovels() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.add(ce.getMaterial("WOODEN_SHOVEL", "WOOD_SPADE"));
		ma.add(ce.getMaterial("STONE_SHOVEL", "STONE_SPADE"));
		ma.add(ce.getMaterial("IRON_SHOVEL", "IRON_SPADE"));
		ma.add(ce.getMaterial("GOLDEN_SHOVEL", "GOLD_SPADE"));
		ma.add(ce.getMaterial("DIAMOND_SHOVEL", "DIAMOND_SPADE"));
		return ma;
	}
	
	private static ArrayList<Material> getTools() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.addAll(getPickAxes());
		ma.addAll(getAxes());
		ma.addAll(getShovels());
		ma.add(ce.getMaterial("WOODEN_HOE", "WOOD_HOE"));
		ma.add(Material.STONE_HOE);
		ma.add(Material.IRON_HOE);
		ma.add(ce.getMaterial("GOLDEN_HOE", "GOLD_HOE"));
		ma.add(Material.DIAMOND_HOE);
		ma.add(Material.SHEARS);
		return ma;
	}
	
	private static ArrayList<Material> getElytra() {
		ArrayList<Material> ma = new ArrayList<>();
		if(Version.getCurrentVersion().isNewer(Version.v1_8_R3)) {
			ma.add(Material.matchMaterial("ELYTRA"));
		}
		return ma;
	}
	
	private static ArrayList<Material> getAll() {
		ArrayList<Material> ma = new ArrayList<>();
		ma.addAll(getArmor());
		ma.addAll(getTools());
		ma.addAll(getBow());
		ma.addAll(getWepons());
		ma.addAll(getElytra());
		ma.add(Material.FISHING_ROD);
		return ma;
	}
	
}

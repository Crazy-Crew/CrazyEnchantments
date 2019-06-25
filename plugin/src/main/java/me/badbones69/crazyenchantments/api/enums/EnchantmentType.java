package me.badbones69.crazyenchantments.api.enums;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.Material;

import java.util.*;

public enum EnchantmentType {
	
	ARMOR("Armor"),
	HELMET("Helmet"),
	CHESTPLATE("Chestplate"),
	LEGGINGS("Leggings"),
	BOOTS("Boots"),
	BOW("Bow"),
	AXE("Axe"),
	SWORD("Sword"),
	WEAPONS("Weapons"),
	PICKAXE("PickAxe"),
	SHOVEL("Shovel"),
	HOE("Hoe"),
	TOOL("Tool"),
	FISHING_ROD("Fishing-Rod"),
	ELYTRA("Elytra"),
	ALL("All");
	
	private String name;
	private EnchantmentType instance;
	private static HashMap<EnchantmentType, List<Material>> items;
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	/**
	 *
	 * @param name Name of the type.
	 */
	private EnchantmentType(String name) {
		this.name = name;
		this.instance = this;
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
	public List<Material> getItems() {
		if(items == null) {
			loadItems();
		}
		return items.get(instance);
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
	
	private void loadItems() {
		items = new HashMap<>();
		//Helmets
		items.put(HELMET, new LinkedList<>(Arrays.asList(
		Material.LEATHER_HELMET,
		Material.IRON_HELMET,
		Material.CHAINMAIL_HELMET,
		ce.getMaterial("GOLDEN_HELMET", "GOLD_HELMET"),
		Material.DIAMOND_HELMET)));
		//Chestplates
		items.put(CHESTPLATE, new LinkedList<>(Arrays.asList(
		Material.LEATHER_CHESTPLATE,
		Material.IRON_CHESTPLATE,
		Material.CHAINMAIL_CHESTPLATE,
		ce.getMaterial("GOLDEN_CHESTPLATE", "GOLD_CHESTPLATE"),
		Material.DIAMOND_CHESTPLATE)));
		//Leggings
		items.put(LEGGINGS, new LinkedList<>(Arrays.asList(
		Material.LEATHER_LEGGINGS,
		Material.IRON_LEGGINGS,
		Material.CHAINMAIL_LEGGINGS,
		ce.getMaterial("GOLDEN_LEGGINGS", "GOLD_LEGGINGS"),
		Material.DIAMOND_LEGGINGS)));
		//Boots
		items.put(BOOTS, new LinkedList<>(Arrays.asList(
		Material.LEATHER_BOOTS,
		Material.IRON_BOOTS,
		Material.CHAINMAIL_BOOTS,
		ce.getMaterial("GOLDEN_BOOTS", "GOLD_BOOTS"),
		Material.DIAMOND_BOOTS)));
		//Armor
		items.put(ARMOR, new ArrayList<>());
		items.get(ARMOR).addAll(items.get(HELMET));
		items.get(ARMOR).addAll(items.get(CHESTPLATE));
		items.get(ARMOR).addAll(items.get(LEGGINGS));
		items.get(ARMOR).addAll(items.get(BOOTS));
		//Axes
		items.put(AXE, new LinkedList<>(Arrays.asList(
		ce.getMaterial("WOODEN_AXE", "WOOD_AXE"),
		Material.STONE_AXE,
		Material.IRON_AXE,
		ce.getMaterial("GOLDEN_AXE", "GOLD_AXE"),
		Material.DIAMOND_AXE)));
		//Bow
		items.put(BOW, new LinkedList<>(Arrays.asList(Material.BOW)));
		//Swords
		items.put(SWORD, new LinkedList<>(Arrays.asList(
		ce.getMaterial("WOODEN_SWORD", "WOOD_SWORD"),
		Material.STONE_SWORD,
		Material.IRON_SWORD,
		ce.getMaterial("GOLDEN_SWORD", "GOLD_SWORD"),
		Material.DIAMOND_SWORD)));
		//Weapons
		items.put(WEAPONS, new ArrayList<>());
		items.get(WEAPONS).addAll(items.get(SWORD));
		items.get(WEAPONS).addAll(items.get(AXE));
		//Pickaxes
		items.put(PICKAXE, new LinkedList<>(Arrays.asList(
		ce.getMaterial("WOODEN_PICKAXE", "WOOD_PICKAXE"),
		Material.STONE_PICKAXE,
		Material.IRON_PICKAXE,
		ce.getMaterial("GOLDEN_PICKAXE", "GOLD_PICKAXE"),
		Material.DIAMOND_PICKAXE)));
		//Shovels
		items.put(SHOVEL, new LinkedList<>(Arrays.asList(
		ce.getMaterial("WOODEN_SHOVEL", "WOOD_SPADE"),
		ce.getMaterial("STONE_SHOVEL", "STONE_SPADE"),
		ce.getMaterial("IRON_SHOVEL", "IRON_SPADE"),
		ce.getMaterial("GOLDEN_SHOVEL", "GOLD_SPADE"),
		ce.getMaterial("DIAMOND_SHOVEL", "DIAMOND_SPADE"))));
		//Hoes
		items.put(HOE, new LinkedList<>(Arrays.asList(
		ce.getMaterial("WOODEN_HOE", "WOOD_HOE"),
		Material.STONE_HOE,
		Material.IRON_HOE,
		ce.getMaterial("GOLDEN_HOE", "GOLD_HOE"),
		Material.DIAMOND_HOE)));
		//Tools
		items.put(TOOL, new ArrayList<>());
		items.get(TOOL).addAll(items.get(PICKAXE));
		items.get(TOOL).addAll(items.get(AXE));
		items.get(TOOL).addAll(items.get(SHOVEL));
		items.get(TOOL).addAll(items.get(HOE));
		items.get(TOOL).add(Material.SHEARS);
		//Elytra
		items.put(ELYTRA, new ArrayList<>());
		if(Version.getCurrentVersion().isNewer(Version.v1_8_R3)) {
			items.get(ELYTRA).add(Material.matchMaterial("ELYTRA"));
		}
		//Fishing Pole
		items.put(FISHING_ROD, new LinkedList<>(Arrays.asList(Material.FISHING_ROD)));
		//1.12+ Items
		if(Version.getCurrentVersion().isNewer(Version.v1_12_R1)) {
			items.get(HELMET).add(Material.matchMaterial("TURTLE_HELMET"));
			items.get(WEAPONS).add(Material.matchMaterial("TRIDENT"));
		}
		//All
		items.put(ALL, new ArrayList<>());
		items.get(ALL).addAll(items.get(ARMOR));
		items.get(ALL).addAll(items.get(TOOL));
		items.get(ALL).addAll(items.get(BOW));
		items.get(ALL).addAll(items.get(WEAPONS));
		items.get(ALL).addAll(items.get(ELYTRA));
		items.get(ALL).addAll(items.get(FISHING_ROD));
	}
	
}
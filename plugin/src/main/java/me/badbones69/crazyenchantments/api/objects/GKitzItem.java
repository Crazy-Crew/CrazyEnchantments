package me.badbones69.crazyenchantments.api.objects;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class GKitzItem {
	
	private String type;
	private String name;
	private int amount;
	private String player;
	private boolean unbreakable;
	private ArrayList<String> lore;
	private HashMap<Enchantment, Integer> enchantments;
	private HashMap<CEnchantment, Integer> ceEnchantments;
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	/**
	 * Make an empty gkit item.
	 */
	public GKitzItem() {
		this.name = "";
		this.amount = 1;
		this.type = "299";
		this.player = "";
		this.unbreakable = false;
		this.lore = new ArrayList<>();
		this.enchantments = new HashMap<>();
		this.ceEnchantments = new HashMap<>();
	}
	
	/**
	 *
	 * @param enchant Crazy Enchantment
	 * @param level Level of the enchantment
	 */
	public void addCEEnchantment(CEnchantment enchant, int level) {
		ceEnchantments.put(enchant, level);
	}
	
	/**
	 *
	 * @param enchant Crazy Enchantment
	 */
	public void removeCEEnchantment(CEnchantment enchant) {
		ceEnchantments.remove(enchant);
	}
	
	/**
	 *
	 * @param enchant Vanilla Enchantment
	 * @param level Level of the enchantment
	 */
	public void addEnchantment(Enchantment enchant, int level) {
		enchantments.put(enchant, level);
	}
	
	/**
	 *
	 * @param enchant Vanilla Enchantment
	 */
	public void removeEnchantment(Enchantment enchant) {
		enchantments.remove(enchant);
	}
	
	/**
	 *
	 * @param id Item's ID
	 */
	public void setItem(String id) {
		type = id;
	}
	
	/**
	 *
	 * @param amount Amount of items
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	/**
	 *
	 * @param name Name of the item
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 *
	 * @param lore Lore of the item
	 */
	public void setLore(ArrayList<String> lore) {
		this.lore = lore;
	}
	
	/**
	 *
	 * @param player The player the head will be set to.
	 */
	public void setPlayer(String player) {
		this.player = player;
	}
	
	/**
	 *
	 * @param unbreakable True for the item to be unbreakable and false if it can take damage.
	 */
	public void setUnbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
	}
	
	/**
	 *
	 * @return Returns a fully finished item.
	 */
	public ItemStack build() {
		ItemStack item = new ItemBuilder().setMaterial(type).setAmount(amount).setName(name).setPlayer(player).setUnbreakable(unbreakable).build();
		for(CEnchantment en : ceEnchantments.keySet()) {
			ce.addEnchantment(item, en, ceEnchantments.get(en));
		}
		item.addUnsafeEnchantments(enchantments);
		for(String l : lore) {
			Methods.addLore(item, l);
		}
		return item;
	}
	
}
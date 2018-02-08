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
	private Integer amount;
	private ArrayList<String> lore;
	private HashMap<Enchantment, Integer> enchantments;
	private HashMap<CEnchantment, Integer> ceEnchantments;
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	private HashMap<CEnchantments, Integer> ceEnchantments;
	
	/**
	 * Make an empty gkit item.
	 */
	public GKitzItem() {
		name = "";
		amount = 1;
		type = "299";
		lore = new ArrayList<>();
		enchantments = new HashMap<>();
		ceEnchantments = new HashMap<>();
	}
	
	/**
	 *
	 * @param enchant Crazy Enchantment
	 * @param level Level of the enchantment
	 */
	public void addCEEnchantment(CEnchantment enchant, Integer level) {
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
	public void addEnchantment(Enchantment enchant, Integer level) {
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
	public void setAmount(Integer amount) {
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
	 * @return Returns a fully finished item.
	 */
	public ItemStack build() {
		ItemStack item = new ItemBuilder().setMaterial(type).setAmount(amount).setName(name).build();
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
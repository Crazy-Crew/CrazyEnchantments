package me.badbones69.crazyenchantments.api.enums;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import org.bukkit.inventory.ItemStack;

/**
 * @Author Borlea
 * @Github https://github.com/borlea/
 * @Website http://thederpygolems.ca/
 * Jul 30, 2015 6:46:16 PM
 */
public enum ArmorType {
	
	HELMET(5), CHESTPLATE(6), LEGGINGS(7), BOOTS(8);
	
	private final int slot;
	private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	private ArmorType(int slot) {
		this.slot = slot;
	}
	
	/**
	 * Attempts to match the ArmorType for the specified ItemStack.
	 *
	 * @param itemStack The ItemStack to parse the type of.
	 * @return The parsed ArmorType. (null if none were found.)
	 */
	public static ArmorType matchType(final ItemStack itemStack) {
		if(itemStack == null) {
			return null;
		}
		if(itemStack.getType().name().toLowerCase().contains("skull")) {
			return HELMET;
		}
		if(EnchantmentType.HELMET.getItems().contains(itemStack.getType())) {
			return HELMET;
		}else if(EnchantmentType.CHESTPLATE.getItems().contains(itemStack.getType())) {
			return CHESTPLATE;
		}else if(EnchantmentType.LEGGINGS.getItems().contains(itemStack.getType())) {
			return LEGGINGS;
		}else if(EnchantmentType.BOOTS.getItems().contains(itemStack.getType())) {
			return BOOTS;
		}
		return null;
	}
	
	public int getSlot() {
		return slot;
	}
	
}

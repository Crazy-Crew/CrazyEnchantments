package me.badbones69.crazyenchantments.multisupport;

import java.lang.reflect.Field;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * Got from here 
 * https://github.com/Sothatsit/UsefulSnippets/blob/master/UsefulSnippets/src/me/sothatsit/usefulsnippets/EnchantGlow.java
 * Had to edit it to not error with other plugins.
 *
 */

public class EnchantGlow extends EnchantmentWrapper {

	private static Enchantment glow;

	public EnchantGlow(int id) {
		super(id);
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return true;
	}

	@Override
	public boolean conflictsWith(Enchantment other) {
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ALL;
	}

	@Override
	public int getMaxLevel() {
		return 10;
	}

	@Override
	public String getName() {
		return "Crazy_Glow";
	}

	@Override
	public int getStartLevel() {
		return 1;
	}
	
	public static Enchantment getGlow() {
		if (glow != null){
			return glow;
		}
		for(Enchantment ench : Enchantment.values()){
			if((ench + "").toLowerCase().contains("crazy_glow")){
				glow = ench;
				return ench;
			}
		}
		try{
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
		}catch (Exception e) {
			e.printStackTrace();
		}
		glow = new EnchantGlow(169);
		Enchantment.registerEnchantment(glow);
		return glow;
	}

	public static ItemStack addGlow(ItemStack item) {
		Enchantment glow = getGlow();
		item.addEnchantment(glow, 1);
		return item;
	}

}

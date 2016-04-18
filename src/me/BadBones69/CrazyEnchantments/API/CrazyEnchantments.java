package me.BadBones69.CrazyEnchantments.API;

import java.util.ArrayList;

public class CrazyEnchantments {
	static CrazyEnchantments instance = new CrazyEnchantments();
	public static CrazyEnchantments getInstance(){
		return instance;
	}
	public ArrayList<Enchantment> getEnchantments(){
		ArrayList<Enchantment> enchants = new ArrayList<Enchantment>();
		for(Enchantment en : Enchantment.values())enchants.add(en);
		return enchants;
	}
}
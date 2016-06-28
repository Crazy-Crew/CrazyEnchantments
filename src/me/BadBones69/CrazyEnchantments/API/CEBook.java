package me.BadBones69.CrazyEnchantments.API;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.BadBones69.CrazyEnchantments.Api;
import me.BadBones69.CrazyEnchantments.Main;

public class CEBook {
	
	CEnchantments enchantment;
	int amount;
	int power;
	int destory_rate;
	int success_rate;
	
	
	public CEBook(CEnchantments enchantment, Integer power, Integer amount){
		this.enchantment = enchantment;
		this.amount = amount;
		this.power = power;
		this.destory_rate = 0;
		this.success_rate = 100;
	}
	
	public void setEnchantemnt(CEnchantments enchantment){
		this.enchantment = enchantment;
	}
	
	public void setAmount(Integer amount){
		this.amount = amount;
	}
	
	public void setPower(Integer power){
		this.power = power;
	}
	
	public void setDestoryRate(Integer destory_rate){
		this.destory_rate = destory_rate;
	}
	
	public void setSuccessRate(Integer success_rate){
		this.success_rate = success_rate;
	}
	
	public ItemStack buildBook(){
		String name = enchantment.getBookColor() + enchantment.getCustomName() + " " + convertPower(power);
		List<String> lore = new ArrayList<String>();
		for(String l : Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore")){
			lore.add(Api.color(l)
					.replaceAll("%Destroy_Rate%", destory_rate+"").replaceAll("%destroy_rate%", destory_rate+"")
					.replaceAll("%Success_Rate%", success_rate+"").replaceAll("%success_Rate%", success_rate+""));
		}
		return Api.makeItem(Material.BOOK, amount, 0, name, lore);
	}
	
	/**
	 * 
	 * @param i Number you want to convert.
	 * @return 
	 */
	private String convertPower(Integer i){
		if(i==0)return "I";
		if(i==1)return "I";
		if(i==2)return "II";
		if(i==3)return "III";
		if(i==4)return "IV";
		if(i==5)return "V";
		if(i==6)return "VI";
		if(i==7)return "VII";
		if(i==8)return "VII";
		if(i==9)return "IX";
		if(i==10)return "X";
		return i+"";
	}
}
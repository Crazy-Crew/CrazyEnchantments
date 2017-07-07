package me.badbones69.crazyenchantments.api;

import java.util.ArrayList;
import java.util.List;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;

public class CEnchantment {
	
	private String name;
	private String customName;
	private Boolean activated;
	private String color;
	private String bookColor;
	private Integer maxLevel;
	private String infoName;
	private List<String> infoDescription;
	private List<String> categories;
	private EnchantmentType enchantmentType;
	
	public CEnchantment(String name){
		this.name = name;
		this.customName = name;
		this.activated = true;
		this.color = "&7";
		this.bookColor = "&b&l";
		this.maxLevel = 3;
		this.infoName = "&7" + name;
		this.infoDescription = new ArrayList<String>();
		this.categories = new ArrayList<String>();
		this.enchantmentType = EnchantmentType.ALL;
	}
	
	public CEnchantment setName(String name){
		this.name = name;
		return this;
	}
	
	public String getName(){
		return name;
	}
	
	public CEnchantment setCustomName(String customName){
		this.customName = customName;
		return this;
	}
	
	public String getCustomName(){
		return customName;
	}
	
	public CEnchantment setActivated(Boolean activated){
		this.activated = activated;
		return this;
	}
	
	public Boolean isActivated(){
		return activated;
	}
	
	public CEnchantment setColor(String color){
		this.color = Methods.color(color);
		return this;
	}
	
	public String getColor(){
		return color;
	}
	
	public CEnchantment setBookColor(String bookColor){
		this.bookColor = Methods.color(bookColor);
		return this;
	}
	
	public String getBookColor(){
		return bookColor;
	}
	
	public CEnchantment setMaxLevel(Integer maxLevel){
		this.maxLevel = maxLevel;
		return this;
	}
	
	public Integer getMaxLevel(){
		return maxLevel;
	}
	
	public CEnchantment setInfoName(String infoName){
		this.infoName = Methods.color(infoName);
		return this;
	}
	
	public String getInfoName(){
		return infoName;
	}
	
	public CEnchantment setInfoDescription(List<String> infoDescription){
		ArrayList<String> info = new ArrayList<String>();
		for(String i : infoDescription){
			info.add(Methods.color(i));
		}
		this.infoDescription = info;
		return this;
	}
	
	public List<String> getInfoDescription(){
		return infoDescription;
	}
	
	public CEnchantment setCategories(List<String> categories){
		this.categories = categories;
		return this;
	}
	
	public List<String> getCategories(){
		return categories;
	}
	
	public CEnchantment setEnchantmentType(EnchantmentType enchantmentType){
		this.enchantmentType = enchantmentType;
		return this;
	}
	
	public EnchantmentType getEnchantmentType(){
		return enchantmentType;
	}
	
	public void registerEnchantment(){
		DataStorage.registerEnchantment(this);
	}
	
	public void unregisterEnchantment(){
		DataStorage.unregisterEnchantment(this);
	}
	
	public static CEnchantment getCEnchantmentFromName(String enchantment){
		return Main.CE.getEnchantmentFromName(enchantment);
	}
	
}
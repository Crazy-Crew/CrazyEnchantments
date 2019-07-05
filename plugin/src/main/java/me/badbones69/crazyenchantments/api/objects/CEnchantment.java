package me.badbones69.crazyenchantments.api.objects;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.events.RegisteredCEnchantmentEvent;
import me.badbones69.crazyenchantments.api.events.UnregisterCEnchantmentEvent;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CEnchantment {
	
	private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	private String name;
	private String customName;
	private Boolean activated;
	private String color;
	private String bookColor;
	private Integer maxLevel;
	private String infoName;
	private Integer chance;
	private Integer chanceIncrease;
	private List<String> infoDescription;
	private List<String> categories;
	private EnchantmentType enchantmentType;
	private CEnchantment instance;
	
	public CEnchantment(String name) {
		this.instance = this;
		this.name = name;
		this.customName = name;
		this.activated = true;
		this.color = "&7";
		this.bookColor = "&b&l";
		this.maxLevel = 3;
		this.infoName = "&7" + name;
		this.chance = 0;
		this.chanceIncrease = 0;
		this.infoDescription = new ArrayList<>();
		this.categories = new ArrayList<>();
		this.enchantmentType = null;
	}
	
	public CEnchantment setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public CEnchantment setCustomName(String customName) {
		this.customName = customName;
		return this;
	}
	
	public String getCustomName() {
		return customName;
	}
	
	public CEnchantment setActivated(Boolean activated) {
		this.activated = activated;
		return this;
	}
	
	public Boolean isActivated() {
		return activated;
	}
	
	public CEnchantment setColor(String color) {
		this.color = Methods.color(color);
		return this;
	}
	
	public String getColor() {
		return color;
	}
	
	public CEnchantment setBookColor(String bookColor) {
		if(bookColor.startsWith("&f")) {
			bookColor = bookColor.substring(2);
		}
		this.bookColor = Methods.color(bookColor);
		return this;
	}
	
	public String getBookColor() {
		return bookColor;
	}
	
	public CEnchantment setMaxLevel(Integer maxLevel) {
		this.maxLevel = maxLevel;
		return this;
	}
	
	public Integer getMaxLevel() {
		return maxLevel;
	}
	
	public CEnchantment setInfoName(String infoName) {
		this.infoName = Methods.color(infoName);
		return this;
	}
	
	public String getInfoName() {
		return infoName;
	}
	
	public CEnchantment setChance(Integer chance) {
		this.chance = chance;
		return this;
	}
	
	public Integer getChance() {
		return chance;
	}
	
	public CEnchantment setChanceIncrease(Integer chanceIncrease) {
		this.chanceIncrease = chanceIncrease;
		return this;
	}
	
	public Integer getChanceIncrease() {
		return chanceIncrease;
	}
	
	public Boolean hasChanceSystem() {
		return chance > 0;
	}
	
	public Boolean chanceSuccesful(Integer enchantmentLevel) {
		int newChance = chance + (chanceIncrease * (enchantmentLevel - 1));
		int pickedChance = new Random().nextInt(100) + 1;
		//This is to just chance the chance system.
		//Boolean result = newChance >= 100 || newChance <= 0 || pickedChance <= chance;
		//System.out.print("[CrazyEnchantments] " + name + ", " + newChance + "%, " + pickedChance + "/100, " + result);
		//return result;
		return newChance >= 100 || newChance <= 0 || pickedChance <= chance;
	}
	
	public CEnchantment setInfoDescription(List<String> infoDescription) {
		ArrayList<String> info = new ArrayList<>();
		for(String i : infoDescription) {
			info.add(Methods.color(i));
		}
		this.infoDescription = info;
		return this;
	}
	
	public List<String> getInfoDescription() {
		return infoDescription;
	}
	
	public CEnchantment setCategories(List<String> categories) {
		this.categories = categories;
		return this;
	}
	
	public List<String> getCategories() {
		return categories;
	}
	
	public CEnchantment setEnchantmentType(EnchantmentType enchantmentType) {
		this.enchantmentType = enchantmentType;
		return this;
	}
	
	public EnchantmentType getEnchantmentType() {
		return enchantmentType;
	}
	
	public void registerEnchantment() {
		RegisteredCEnchantmentEvent event = new RegisteredCEnchantmentEvent(instance);
		Bukkit.getPluginManager().callEvent(event);
		ce.registerEnchantment(instance);
		if(enchantmentType != null) {
			enchantmentType.addEnchantment(instance);
		}
	}
	
	public void unregisterEnchantment() {
		UnregisterCEnchantmentEvent event = new UnregisterCEnchantmentEvent(instance);
		Bukkit.getPluginManager().callEvent(event);
		ce.unregisterEnchantment(instance);
		if(enchantmentType != null) {
			enchantmentType.removeEnchantment(instance);
		}
	}
	
	public Integer getPower(ItemStack item) {
		int power;
		String line = "";
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasLore()) {
				for(String lore : item.getItemMeta().getLore()) {
					if(lore.contains(customName)) {
						line = lore;
						break;
					}
				}
			}
		}
		power = ce.convertLevelInteger(line.replace(color + customName + " ", ""));
		if(!Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.UnSafe-Enchantments")) {
			if(power > maxLevel) {
				power = maxLevel;
			}
		}
		return power;
	}
	
	public static CEnchantment getCEnchantmentFromName(String enchantment) {
		return ce.getEnchantmentFromName(enchantment);
	}
	
}
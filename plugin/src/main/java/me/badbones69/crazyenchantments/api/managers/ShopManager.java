package me.badbones69.crazyenchantments.api.managers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.currencyapi.Currency;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.enums.ShopOption;
import me.badbones69.crazyenchantments.api.objects.Category;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.api.objects.LostBook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.HashMap;

public class ShopManager {
	
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	private static ShopManager instance = new ShopManager();
	private String inventoryName;
	private int inventorySize;
	private boolean enchantmentTableShop;
	private HashMap<ItemBuilder, Integer> customizerItems = new HashMap<>();
	private HashMap<ItemBuilder, Integer> shopItems = new HashMap<>();
	
	public static ShopManager getInstance() {
		return instance;
	}
	
	public void load() {
		customizerItems.clear();
		shopItems.clear();
		FileConfiguration config = Files.CONFIG.getFile();
		inventoryName = Methods.color(config.getString("Settings.InvName"));
		inventorySize = config.getInt("Settings.GUISize");
		enchantmentTableShop = config.getBoolean("Settings.EnchantmentOptions.Right-Click-Enchantment-Table");
		for(String custom : config.getStringList("Settings.GUICustomization")) {
			ItemBuilder itemBuilder = new ItemBuilder();
			int slot = 0;
			String[] b = custom.split(", ");
			for(String i : b) {
				if(i.contains("Item:")) {
					itemBuilder.setMaterial(i.replace("Item:", ""));
				}
				if(i.contains("Name:")) {
					i = i.replace("Name:", "");
					itemBuilder.setName(i);
				}
				if(i.contains("Slot:")) {
					slot = Integer.parseInt(i.replace("Slot:", ""));
				}
				if(i.contains("Lore:")) {
					itemBuilder.setLore(Arrays.asList(i.replace("Lore:", "").split(",")));
				}
			}
			if(slot > inventorySize || slot <= 0) {
				continue;
			}
			slot--;
			customizerItems.put(itemBuilder, slot);
		}
		for(Category category : ce.getCategories()) {
			if(category.isInGUI()) {
				if(category.getSlot() > inventorySize) {
					continue;
				}
				shopItems.put(category.getDisplayItem(), category.getSlot());
			}
			LostBook lostBook = category.getLostBook();
			if(lostBook.isInGUI()) {
				if(lostBook.getSlot() > inventorySize) {
					continue;
				}
				shopItems.put(lostBook.getDisplayItem(), lostBook.getSlot());
			}
		}
		for(ShopOption option : ShopOption.values()) {
			if(option.isInGUI()) {
				if(option.getSlot() > inventorySize) {
					continue;
				}
				shopItems.put(option.getItemBuilder(), option.getSlot());
			}
		}
	}
	
	public Inventory getShopInventory(Player player) {
		HashMap<String, String> placeholders = new HashMap<>();
		for(Currency currency : Currency.values()) {
			placeholders.put("%" + currency.getName() + "%", CurrencyAPI.getCurrency(player, currency) + "");
		}
		Inventory inventory = Bukkit.createInventory(null, inventorySize, inventoryName);
		for(ItemBuilder itemBuilder : customizerItems.keySet()) {
			int slot = customizerItems.get(itemBuilder);
			itemBuilder.setNamePlaceholders(placeholders)
			.setLorePlaceholders(placeholders);
			inventory.setItem(slot, itemBuilder.build());
		}
		shopItems.keySet().forEach(itemBuilder -> inventory.setItem(shopItems.get(itemBuilder), itemBuilder.build()));
		return inventory;
	}
	
	public String getInventoryName() {
		return inventoryName;
	}
	
	public int getInventorySize() {
		return inventorySize;
	}
	
	public boolean isEnchantmentTableShop() {
		return enchantmentTableShop;
	}
	
}
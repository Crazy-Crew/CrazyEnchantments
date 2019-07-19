package me.badbones69.crazyenchantments.api.objects;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class InfoMenuManager {
	
	public static InfoMenuManager instance = new InfoMenuManager();
	private Inventory inventoryMenu;
	private String inventoryName;
	private int inventorySize;
	private ItemStack backRight;
	private ItemStack backLeft;
	private List<EnchantmentType> enchantmentTypes = new ArrayList<>();
	
	public static InfoMenuManager getInstance() {
		return instance;
	}
	
	public void load() {
		enchantmentTypes.clear();
		FileConfiguration file = Files.ENCHANTMENT_TYPES.getFile();
		String path = "Info-GUI-Settings";
		inventoryName = Methods.color(file.getString(path + ".Inventory.Name", "&c&lEnchantment Info"));
		inventorySize = file.getInt(path + ".Inventory.Size", 18);
		inventoryMenu = Bukkit.createInventory(null, inventorySize, inventoryName);
		backRight = new ItemBuilder()
		.setMaterial(file.getString(path + ".Back-Item.Right.Item"))
		.setName(file.getString(path + ".Back-Item.Right.Name"))
		.setLore(file.getStringList(path + ".Back-Item.Right.Lore"))
		.build();
		backLeft = new ItemBuilder()
		.setMaterial(file.getString(path + ".Back-Item.Left.Item"))
		.setName(file.getString(path + ".Back-Item.Left.Name"))
		.setLore(file.getStringList(path + ".Back-Item.Left.Lore"))
		.build();
		for(String type : file.getConfigurationSection("Types").getKeys(false)) {
			EnchantmentType enchantmentType = new EnchantmentType(type);
			enchantmentTypes.add(enchantmentType);
			inventoryMenu.setItem(enchantmentType.getSlot(), enchantmentType.getDisplayItem());
		}
	}
	
	public Inventory getInventoryMenu() {
		return inventoryMenu;
	}
	
	public String getInventoryName() {
		return inventoryName;
	}
	
	public int getInventorySize() {
		return inventorySize;
	}
	
	public List<EnchantmentType> getEnchantmentTypes() {
		return enchantmentTypes;
	}
	
	public ItemStack getBackRightButton() {
		return backRight;
	}
	
	public ItemStack getBackLeftButton() {
		return backLeft;
	}
	
	public EnchantmentType getFromName(String name) {
		for(EnchantmentType enchantmentType : enchantmentTypes) {
			if(enchantmentType.getName().equalsIgnoreCase(name)) {
				return enchantmentType;
			}
		}
		return null;
	}
	
	public void openInfoMenu(Player player) {
		player.openInventory(inventoryMenu);
	}
	
	public void openInfoMenu(Player player, EnchantmentType enchantmentType) {
		List<CEnchantment> enchantments = enchantmentType.getEnchantments();
		int slots = 9;
		for(int size = enchantments.size() + 1; size > 9; size -= 9) slots += 9;
		Inventory inventory = Bukkit.createInventory(null, slots, inventoryName);
		for(CEnchantment enchantment : enchantments) {
			if(enchantment.isActivated()) {
				inventory.addItem(new ItemBuilder()
				.setMaterial(Files.CONFIG.getFile().getString("Settings.Enchantment-Book-Item"))
				.setName(enchantment.getInfoName())
				.setLore(enchantment.getInfoDescription())
				.setGlowing(true)
				.build());
			}
		}
		inventory.setItem(slots - 1, backRight);
		player.openInventory(inventory);
	}
	
}
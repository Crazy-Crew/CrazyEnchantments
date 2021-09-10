package me.badbones69.crazyenchantments.api.managers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.EnchantmentType;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InfoMenuManager {
    
    public static final InfoMenuManager instance = new InfoMenuManager();
    private Inventory inventoryMenu;
    private String inventoryName;
    private int inventorySize;
    private ItemStack backRight;
    private ItemStack backLeft;
    private List<EnchantmentType> enchantmentTypes = new ArrayList<>();
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    
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
        .setMaterial(file.getString(path + ".Back-Item.Right.Item", "NETHER_STAR"))
        .setPlayer(file.getString(path + ".Back-Item.Right.Player"))
        .setName(file.getString(path + ".Back-Item.Right.Name", "&7&l<<&b&lBack"))
        .setLore(file.getStringList(path + ".Back-Item.Right.Lore"))
        .build();
        backLeft = new ItemBuilder()
        .setMaterial(file.getString(path + ".Back-Item.Left.Item", "NETHER_STAR"))
        .setPlayer(file.getString(path + ".Back-Item.Left.Player"))
        .setName(file.getString(path + ".Back-Item.Left.Name", "&b&lBack&7&l>>"))
        .setLore(file.getStringList(path + ".Back-Item.Left.Lore"))
        .build();
        for (String type : file.getConfigurationSection("Types").getKeys(false)) {
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
        for (EnchantmentType enchantmentType : enchantmentTypes) {
            if (enchantmentType.getName().equalsIgnoreCase(name)) {
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
        for (int size = enchantments.size() + 1; size > 9; size -= 9) slots += 9;
        Inventory inventory = Bukkit.createInventory(null, slots, inventoryName);
        for (CEnchantment enchantment : enchantments) {
            if (enchantment.isActivated()) {
                inventory.addItem(
                ce.getEnchantmentBook()
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
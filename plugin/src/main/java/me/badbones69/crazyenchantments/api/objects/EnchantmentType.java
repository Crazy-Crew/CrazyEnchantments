package me.badbones69.crazyenchantments.api.objects;

import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.managers.InfoMenuManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentType {
    
    private String name;
    private int slot;
    private ItemStack displayItem;
    private List<CEnchantment> enchantments = new ArrayList<>();
    private List<Material> enchantableMaterials = new ArrayList<>();
    
    public EnchantmentType(String name) {
        FileConfiguration file = Files.ENCHANTMENT_TYPES.getFile();
        String path = "Types." + name;
        this.name = name;
        this.slot = file.getInt(path + ".Display-Item.Slot", 1) - 1;
        this.displayItem = new ItemBuilder()
        .setMaterial(file.getString(path + ".Display-Item.Item"))
        .setName(file.getString(path + ".Display-Item.Name"))
        .setLore(file.getStringList(path + ".Display-Item.Lore")).build();
        for (String type : file.getStringList(path + ".Enchantable-Items")) {
            Material material = new ItemBuilder().setMaterial(type).getMaterial();
            if (material != null) {
                this.enchantableMaterials.add(material);
            }
        }
    }
    
    public static EnchantmentType getFromName(String name) {
        return InfoMenuManager.getInstance().getFromName(name);
    }
    
    public String getName() {
        return name;
    }
    
    public int getSlot() {
        return slot;
    }
    
    public ItemStack getDisplayItem() {
        return displayItem;
    }
    
    public List<Material> getEnchantableMaterials() {
        return enchantableMaterials;
    }
    
    public boolean canEnchantItem(ItemStack item) {
        return enchantableMaterials.contains(item.getType());
    }
    
    public List<CEnchantment> getEnchantments() {
        return enchantments;
    }
    
    public void addEnchantment(CEnchantment enchantment) {
        enchantments.add(enchantment);
    }
    
    public void removeEnchantment(CEnchantment enchantment) {
        enchantments.remove(enchantment);
    }
    
}
package com.badbones69.crazyenchantments.api.objects;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.managers.InfoMenuManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnchantmentType {

    private static final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private static final InfoMenuManager infoMenuManager = plugin.getStarter().getInfoMenuManager();
    
    private static String displayName = null;
    private static int slot = 0;
    private static ItemStack displayItem = null;
    private static final List<CEnchantment> enchantments = new ArrayList<>();
    private static final List<Material> enchantableMaterials = new ArrayList<>();
    
    public EnchantmentType(String name) {
        FileConfiguration file = Files.ENCHANTMENT_TYPES.getFile();
        String path = "Types." + name;
        displayName = name;
        slot = file.getInt(path + ".Display-Item.Slot", 1) - 1;
        displayItem = new ItemBuilder()
        .setMaterial(Objects.requireNonNull(file.getString(path + ".Display-Item.Item")))
        .setName(file.getString(path + ".Display-Item.Name"))
        .setLore(file.getStringList(path + ".Display-Item.Lore")).build();

        for (String type : file.getStringList(path + ".Enchantable-Items")) {
            Material material = new ItemBuilder().setMaterial(type).getMaterial();
            if (material != null) enchantableMaterials.add(material);
        }
    }
    
    public static EnchantmentType getFromName(String name) {
        return infoMenuManager.getFromName(name);
    }
    
    public static String getName() {
        return displayName;
    }
    
    public static int getSlot() {
        return slot;
    }
    
    public static ItemStack getDisplayItem() {
        return displayItem;
    }
    
    public static List<Material> getEnchantableMaterials() {
        return enchantableMaterials;
    }
    
    public static boolean canEnchantItem(ItemStack item) {
        return enchantableMaterials.contains(item.getType());
    }
    
    public static List<CEnchantment> getEnchantments() {
        return enchantments;
    }
    
    public static void addEnchantment(CEnchantment enchantment) {
        enchantments.add(enchantment);
    }
    
    public static void removeEnchantment(CEnchantment enchantment) {
        enchantments.remove(enchantment);
    }
}
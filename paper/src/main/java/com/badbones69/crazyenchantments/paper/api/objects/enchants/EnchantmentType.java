package com.badbones69.crazyenchantments.paper.api.objects.enchants;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnchantmentType {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Methods methods = this.plugin.getStarter().getMethods();

    private final String displayName;
    private final int slot;
    private final ItemStack displayItem;
    private final List<CEnchantment> enchantments = new ArrayList<>();
    private final List<Material> enchantmentMaterials = new ArrayList<>();

    public EnchantmentType(String name) {
        FileConfiguration file = Files.ENCHANTMENT_TYPES.getFile();
        String path = "Types." + name;
        this.displayName = name;
        this.slot = file.getInt(path + ".Display-Item.Slot", 1) - 1;
        this.displayItem = new ItemBuilder()
        .setMaterial(Objects.requireNonNull(file.getString(path + ".Display-Item.Item")))
        .setName(file.getString(path + ".Display-Item.Name"))
        .setLore(file.getStringList(path + ".Display-Item.Lore")).build();

        for (String type : file.getStringList(path + ".Enchantable-Items")) {
            Material material = new ItemBuilder().setMaterial(type).getMaterial();

            if (material != null) this.enchantmentMaterials.add(material);
        }
    }

    public EnchantmentType getFromName(String name) {
        return this.methods.getFromName(name);
    }

    public String getName() {
        return this.displayName;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getDisplayItem() {
        return this.displayItem;
    }

    public List<Material> getEnchantmentMaterials() {
        return this.enchantmentMaterials;
    }

    public boolean canEnchantItem(ItemStack item) {
        return this.enchantmentMaterials.contains(item.getType());
    }

    public List<CEnchantment> getEnchantments() {
        return this.enchantments;
    }

    public void addEnchantment(CEnchantment enchantment) {
        this.enchantments.add(enchantment);
    }

    public void removeEnchantment(CEnchantment enchantment) {
        this.enchantments.remove(enchantment);
    }
}
package com.badbones69.crazyenchantments.paper.api.builders.types.gkitz;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.enums.keys.FileKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class KitsManager {

    private static Component inventoryName;
    private static int inventorySize;

    private static ItemStack backRight, backLeft;

    public static void load() {
        final FileConfiguration file = FileKeys.ENCHANTMENT_TYPES.getConfiguration();

        String path = "Info-GUI-Settings";

        inventoryName = ColorUtils.legacyTranslateColourCodes(file.getString(path + ".Inventory.Name", "&c&lEnchantment Info"));
        inventorySize = file.getInt(path + ".Inventory.Size", 18);

        backRight = new ItemBuilder()
                .setMaterial(file.getString(path + ".Back-Item.Right.Item", "NETHER_STAR"))
                .setPlayerName(file.getString(path + ".Back-Item.Right.Player", ""))
                .setItemModel(file.getString(path + ".Back-Item.Right.Model.Namespace", ""), file.getString(path + ".Back-Item.Right.Item.Model.Key", ""))
                .setName(file.getString(path + ".Back-Item.Right.Name", "&7&l<<&b&lBack"))
                .setLore(file.getStringList(path + ".Back-Item.Right.Lore"))
                .addKey(DataKeys.back_right.getNamespacedKey(), "")
                .build();

        backLeft = new ItemBuilder()
                .setMaterial(file.getString(path + ".Back-Item.Left.Item", "NETHER_STAR"))
                .setPlayerName(file.getString(path + ".Back-Item.Left.Player", ""))
                .setItemModel(file.getString(path + ".Back-Item.Left.Model.Namespace", ""), file.getString(path + ".Back-Item.Left.Item.Model.Key", ""))
                .setName(file.getString(path + ".Back-Item.Left.Name", "&b&lBack&7&l>>"))
                .setLore(file.getStringList(path + ".Back-Item.Left.Lore"))
                .addKey(DataKeys.back_left.getNamespacedKey(), "")
                .build();
    }

    public static Component getInventoryName() {
        return inventoryName;
    }

    public static int getInventorySize() {
        return inventorySize;
    }

    public static ItemStack getBackLeft() {
        return backLeft;
    }

    public static ItemStack getBackRight() {
        return backRight;
    }
}
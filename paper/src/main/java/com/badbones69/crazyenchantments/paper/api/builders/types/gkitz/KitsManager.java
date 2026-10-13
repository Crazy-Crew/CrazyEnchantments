package com.badbones69.crazyenchantments.paper.api.builders.types.gkitz;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.ryderbelserion.common.api.enums.Files;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class KitsManager {

    private static Component inventoryName;
    private static int inventorySize;

    private static ItemStack backRight, backLeft;

    public static void load() {
        final CommentedConfigurationNode configuration = Files.ENCHANTMENT_TYPES.getConfiguration();

        final CommentedConfigurationNode section = configuration.node("Info-GUI-Settings");

        inventoryName = ColorUtils.legacyTranslateColourCodes(section.node("Inventory", "Name").getString("&c&lEnchantment Info"));
        inventorySize = section.node("Inventory", "Size").getInt(18);

        final CommentedConfigurationNode backItemRightSection = section.node("Back-Item", "Right");

        backRight = new ItemBuilder()
                .setMaterial(backItemRightSection.node("Item").getString("NETHER_STAR"))
                .setPlayerName(backItemRightSection.node("Player").getString(""))
                .setItemModel(backItemRightSection.node("Model", "Namespace").getString(""), backItemRightSection.node("Model", "Key").getString(""))
                .setName(backItemRightSection.node("Name").getString("&7&l<<&b&lBack"))
                .setLore(StringUtils.getStringList(backItemRightSection.node("Lore")))
                .addKey(DataKeys.back_right.getNamespacedKey(), "")
                .build();

        final CommentedConfigurationNode backItemLeftSection = section.node("Back-Item", "Left");

        backLeft = new ItemBuilder()
                .setMaterial(backItemLeftSection.node("Item").getString("NETHER_STAR"))
                .setPlayerName(backItemLeftSection.node("Player").getString(""))
                .setItemModel(backItemLeftSection.node("Model", "Namespace").getString(""), backItemRightSection.node("Model", "Key").getString(""))
                .setName(backItemLeftSection.node("Name").getString("&b&lBack&7&l>>"))
                .setLore(StringUtils.getStringList(backItemLeftSection.node("Lore")))
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
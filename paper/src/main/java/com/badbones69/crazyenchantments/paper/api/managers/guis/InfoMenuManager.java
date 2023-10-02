package com.badbones69.crazyenchantments.paper.api.managers.guis;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.utilities.misc.ColorUtils;
import com.ryderbelserion.cluster.bukkit.utils.LegacyUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InfoMenuManager {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final EnchantmentBookSettings enchantmentBookSettings = plugin.getStarter().getEnchantmentBookSettings();

    private Inventory inventoryMenu;
    private String inventoryName;
    private int inventorySize;
    private ItemStack backRight;
    private ItemStack backLeft;
    private final List<EnchantmentType> enchantmentTypes = new ArrayList<>();

    public void load() {
        this.enchantmentTypes.clear();
        FileConfiguration file = Files.ENCHANTMENT_TYPES.getFile();
        String path = "Info-GUI-Settings";
        this.inventoryName = LegacyUtils.color(file.getString(path + ".Inventory.Name", "&c&lEnchantment Info"));
        this.inventorySize = file.getInt(path + ".Inventory.Size", 18);
        this.inventoryMenu = plugin.getServer().createInventory(null, this.inventorySize, this.inventoryName);
        this.backRight = new ItemBuilder()
        .setMaterial(file.getString(path + ".Back-Item.Right.Item", "NETHER_STAR"))
        .setPlayerName(file.getString(path + ".Back-Item.Right.Player"))
        .setName(file.getString(path + ".Back-Item.Right.Name", "&7&l<<&b&lBack"))
        .setLore(file.getStringList(path + ".Back-Item.Right.Lore"))
        .build();
        this.backLeft = new ItemBuilder()
        .setMaterial(file.getString(path + ".Back-Item.Left.Item", "NETHER_STAR"))
        .setPlayerName(file.getString(path + ".Back-Item.Left.Player"))
        .setName(file.getString(path + ".Back-Item.Left.Name", "&b&lBack&7&l>>"))
        .setLore(file.getStringList(path + ".Back-Item.Left.Lore"))
        .build();

        for (String type : file.getConfigurationSection("Types").getKeys(false)) {
            EnchantmentType enchantmentType = new EnchantmentType(type);
            this.enchantmentTypes.add(enchantmentType);
            this.inventoryMenu.setItem(enchantmentType.getSlot(), enchantmentType.getDisplayItem());
        }
    }

    public Inventory getInventoryMenu() {
        return this.inventoryMenu;
    }

    public String getInventoryName() {
        return this.inventoryName;
    }

    public int getInventorySize() {
        return this.inventorySize;
    }

    public List<EnchantmentType> getEnchantmentTypes() {
        return this.enchantmentTypes;
    }

    public ItemStack getBackRightButton() {
        return this.backRight;
    }

    public ItemStack getBackLeftButton() {
        return this.backLeft;
    }

    public void openInfoMenu(Player player) {
        player.openInventory(this.inventoryMenu);
    }

    public void openInfoMenu(Player player, EnchantmentType enchantmentType) {
        List<CEnchantment> enchantments = enchantmentType.getEnchantments();
        int slots = 9;

        for (int size = enchantments.size() + 1; size > 9; size -= 9) slots += 9;

        Inventory inventory = this.plugin.getServer().createInventory(null, slots, this.inventoryName);

        for (CEnchantment enchantment : enchantments) {
            if (enchantment.isActivated()) {
                inventory.addItem(
                        this.enchantmentBookSettings.getNormalBook()
                .setName(enchantment.getInfoName())
                .setLore(enchantment.getInfoDescription())
                .setGlow(true)
                .build());
            }
        }

        inventory.setItem(slots - 1, this.backRight);
        player.openInventory(inventory);
    }
}
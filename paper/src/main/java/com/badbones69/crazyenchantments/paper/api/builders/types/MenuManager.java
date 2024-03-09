package com.badbones69.crazyenchantments.paper.api.builders.types;

import com.badbones69.crazyenchantments.ConfigManager;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsPreviewMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.tinkerer.TinkererMenu;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.platform.TinkerConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class MenuManager {

    private static final List<EnchantmentType> enchantmentTypes = new ArrayList<>();

    public static void load() {
        enchantmentTypes.clear();

        FileConfiguration file = Files.ENCHANTMENT_TYPES.getFile();

        for (String type : file.getConfigurationSection("Types").getKeys(false)) {
            EnchantmentType enchantmentType = new EnchantmentType(type);
            enchantmentTypes.add(enchantmentType);
        }
    }

    public static List<EnchantmentType> getEnchantmentTypes() {
        return enchantmentTypes;
    }

    public static void openKitsMenu(Player player) {
        FileConfiguration gkitz = Files.GKITZ.getFile();

        player.openInventory(new KitsMenu(player, gkitz.getInt("Settings.GUI-Size"), gkitz.getString("Settings.Inventory-Name")).build().getInventory());
    }

    public static void openKitsPreviewMenu(Player player, int slots, GKitz kit) {
        player.openInventory(new KitsPreviewMenu(player, slots, ColorUtils.toLegacy(kit.getDisplayItem().displayName()), kit).build().getInventory());
    }

    public static void openInfoMenu(Player player) {
        player.openInventory(new BaseMenu(player, KitsManager.getInventorySize(), ColorUtils.toLegacy(KitsManager.getInventoryName())).build().getInventory());
    }

    public static void openInfoMenu(Player player, EnchantmentType type) {
        List<CEnchantment> enchantments = type.getEnchantments();
        int slots = 9;

        for (int size = enchantments.size() + 1; size > 9; size -= 9) slots += 9;

        player.openInventory(new BaseMenu(player, slots, ColorUtils.toLegacy(KitsManager.getInventoryName())).setEnchantmentType(type).build().getInventory());
    }

    public static void openBlackSmithMenu(Player player) {
        player.openInventory(new BlackSmithMenu(player, 27, BlackSmithManager.getInventoryName()).build().getInventory());
    }

    public static void openTinkererMenu(Player player) {
        player.openInventory(new TinkererMenu(player, 54, ConfigManager.getTinker().getProperty(TinkerConfig.menu_name)).build().getInventory());
    }

    public static int getInventorySize(FileConfiguration configuration) {
        return configuration.getInt("Settings.GUISize");
    }
}
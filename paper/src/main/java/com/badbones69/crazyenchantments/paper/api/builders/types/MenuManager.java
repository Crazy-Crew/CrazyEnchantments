package com.badbones69.crazyenchantments.paper.api.builders.types;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
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
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class MenuManager {

    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private static final FusionPaper fusion = plugin.getFusion();

    private static final List<EnchantmentType> enchantmentTypes = new ArrayList<>();

    public static void load() {
        enchantmentTypes.clear();

        FileConfiguration file = Files.ENCHANTMENT_TYPES.getFile();

        final ConfigurationSection section = file.getConfigurationSection("Types");

        if (section == null) {
            fusion.log("warn", "The types section cannot be found in enchantment-types.yml, It's possible the file is badly formatted!");

            return;
        }

        for (String type : section.getKeys(false)) {
            enchantmentTypes.add(new EnchantmentType(type));
        }
    }

    public static List<EnchantmentType> getEnchantmentTypes() {
        return enchantmentTypes;
    }

    public static void openKitsMenu(@NotNull final Player player) {
        FileConfiguration gkitz = Files.GKITZ.getFile();

        player.openInventory(new KitsMenu(player, gkitz.getInt("Settings.GUI-Size"), gkitz.getString("Settings.Inventory-Name")).build().getInventory());
    }

    public static void openKitsPreviewMenu(@NotNull final Player player, final int slots, @NotNull final GKitz kit) {
        player.openInventory(new KitsPreviewMenu(player, slots, ColorUtils.toLegacy(kit.getDisplayItem().displayName()), kit).build().getInventory());
    }

    public static void openInfoMenu(@NotNull final Player player) {
        player.openInventory(new BaseMenu(player, KitsManager.getInventorySize(), ColorUtils.toLegacy(KitsManager.getInventoryName())).build().getInventory());
    }

    public static void openInfoMenu(@NotNull final Player player, @NotNull final EnchantmentType type) {
        List<CEnchantment> enchantments = type.getEnchantments();

        int slots = 9;

        for (int size = enchantments.size() + 1; size > 9; size -= 9) slots += 9;

        player.openInventory(new BaseMenu(player, slots, ColorUtils.toLegacy(KitsManager.getInventoryName())).setEnchantmentType(type).build().getInventory());
    }

    public static void openBlackSmithMenu(@NotNull final Player player) {
        player.openInventory(new BlackSmithMenu(player, 27, BlackSmithManager.getInventoryName()).build().getInventory());
    }

    public static void openTinkererMenu(@NotNull final Player player) {
        player.openInventory(new TinkererMenu(player, 54, Files.TINKER.getFile().getString("Settings.GUIName", "&7&lThe &4&lCrazy &c&lTinkerer")).build().getInventory());
    }
}
package com.badbones69.crazyenchantments.paper.api.builders.types;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsPreviewMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.tinkerer.TinkererMenu;
import com.badbones69.crazyenchantments.paper.api.enums.keys.FileKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.ryderbelserion.fusion.api.enums.Level;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.ArrayList;
import java.util.List;

public class MenuManager {

    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private static final CrazyPlatform platform = plugin.getPlatform();

    private static final FusionPaper fusion = platform.getFusion();

    private static final List<EnchantmentType> enchantmentTypes = new ArrayList<>();

    public static void load() {
        enchantmentTypes.clear();

        final CommentedConfigurationNode file = FileKeys.ENCHANTMENT_TYPES.getConfiguration();

        final CommentedConfigurationNode section = file.node("Types");

        if (section == null) {
            fusion.log(Level.warn, "The types section cannot be found in enchantment-types.yml, It's possible the file is badly formatted!");

            return;
        }

        section.childrenMap().forEach((id, node) -> enchantmentTypes.add(new EnchantmentType(id.toString(), node)));
    }

    public static List<EnchantmentType> getEnchantmentTypes() {
        return enchantmentTypes;
    }

    public static void openKitsMenu(Player player) {
        final @NotNull CommentedConfigurationNode gkitz = FileKeys.GKITZ.getConfiguration();

        player.openInventory(new KitsMenu(player, gkitz.node("Settings", "GUI-Size").getInt(27),
                gkitz.node("Settings", "Inventory-Name").getString("&8List of all GKitz")).build().getInventory());
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
        player.openInventory(new TinkererMenu(player, 54, FileKeys.TINKER.getConfiguration().node("Settings", "GUIName").getString("&7&lThe &4&lCrazy &c&lTinkerer")).build().getInventory());
    }
}
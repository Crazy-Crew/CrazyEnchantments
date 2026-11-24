package com.badbones69.crazyenchantments.paper.api;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.ryderbelserion.common.api.enums.Files;
import com.ryderbelserion.common.utils.ConfigUtils;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class CrazyItems {

    private boolean animationToggle;
    private String guiName;

    private ItemBuilder scramblerItem;
    private ItemBuilder pointer;

    public void init() {
        final CommentedConfigurationNode config = Files.CONFIG.getConfiguration();

        loadScrollControl(config);
        loadScrambler(config);
    }

    public void loadScrambler(final CommentedConfigurationNode config) {
        this.scramblerItem = new ItemBuilder()
                .setMaterial(ConfigUtils.getNode(config, "Settings.Scrambler.Item").getString("SUNFLOWER"))
                .setItemModel(ConfigUtils.getNode(config, "Settings.Scrambler.Item.Model.Namespace").getString(""),
                        ConfigUtils.getNode(config, "Settings.Scrambler.Item.Model.Key").getString(""))
                .setName(ConfigUtils.getNode(config, "Settings.Scrambler.Name").getString("&cError getting name for scrambler item."))
                .setLore(StringUtils.getStringList(config.node("Settings", "Scrambler", "Lore")))
                .setGlow(ConfigUtils.getNode(config, "Settings.Scrambler.Glowing").getBoolean(false));

        this.pointer = new ItemBuilder()
                .setMaterial(ConfigUtils.getNode(config, "Settings.Scrambler.GUI.Pointer.Item").getString("REDSTONE_TORCH"))
                .setItemModel(ConfigUtils.getNode(config, "Settings.Scrambler.GUI.Pointer.Item.Model.Namespace").getString(""),
                        ConfigUtils.getNode(config, "Settings.Scrambler.GUI.Pointer.Item.Model.Key").getString(""))
                .setName(ConfigUtils.getNode(config, "Settings.Scrambler.GUI.Pointer.Name").getString("&cError getting name for pointer item."))
                .setLore(StringUtils.getStringList(config.node("Settings", "Scrambler", "GUI", "Pointer", "Lore")));

        this.animationToggle = config.node("Settings", "Scrambler", "GUI", "Toggle").getBoolean(true);
        this.guiName = ColorUtils.color(config.node("Settings", "Scrambler", "GUI", "Name").getString("&cError getting name for scrambler gui name."));
    }

    private String suffix;
    private boolean countVanillaEnchantments;
    private boolean useSuffix;
    private boolean blackScrollChanceToggle;
    private int blackScrollChance;

    public void loadScrollControl(final CommentedConfigurationNode config) {
        this.suffix = config.node("Settings", "TransmogScroll", "Amount-of-Enchantments").getString(" &7[&6&n%amount%&7]");
        this.countVanillaEnchantments = config.node("Settings", "TransmogScroll", "Count-Vanilla-Enchantments").getBoolean(true);
        this.useSuffix = config.node("Settings", "TransmogScroll", "Amount-Toggle").getBoolean(true);
        this.blackScrollChance = config.node("Settings", "BlackScroll", "Chance").getInt(75);
        this.blackScrollChanceToggle = config.node("Settings", "BlackScroll", "Chance-Toggle").getBoolean(false);
    }

    public String getSuffix() {
        return this.suffix;
    }

    public boolean isCountVanillaEnchantments() {
        return this.countVanillaEnchantments;
    }

    public boolean isUseSuffix() {
        return this.useSuffix;
    }

    public int getBlackScrollChance() {
        return this.blackScrollChance;
    }

    public boolean isBlackScrollChanceToggle() {
        return this.blackScrollChanceToggle;
    }

    public ItemStack getScramblers(final Player player) {
        return getScramblers(player, 1);
    }

    public ItemStack getScramblers(final Player player, int amount) {
        final ItemStack item = this.scramblerItem.setAmount(amount).build(player);

        item.editPersistentDataContainer(container -> {
            container.set(DataKeys.scrambler.getNamespacedKey(), PersistentDataType.BOOLEAN, true);
        });

        return item;
    }

    public ItemStack getSlotCrystal(final Player player, final int amount) {
        final CommentedConfigurationNode config = Files.CONFIG.getConfiguration();

        final ItemBuilder builder = new ItemBuilder()
                .setMaterial(ConfigUtils.getNode(config, "Settings.Slot_Crystal.Item").getString("RED_WOOL"))
                .setName(ConfigUtils.getNode(config, "Settings.Slot_Crystal.Name").getString("Error getting slot crystal name."))
                .setItemModel(ConfigUtils.getNode(config, "Settings.Slot_Crystal.Model.Namespace").getString(""),
                        ConfigUtils.getNode(config, "Settings.Slot_Crystal.Model.Key").getString(""))
                .setLore(StringUtils.getStringList(config.node(ConfigUtils.getString("Settings.Slot_Crystal.Lore"))))
                .setGlow(ConfigUtils.getNode(config, "Settings.Slot_Crystal.Glowing").getBoolean(false))
                .addKey(DataKeys.slot_crystal.getNamespacedKey(), "");

        return builder.setAmount(amount).build(player);
    }

    public ItemStack getSlotCrystal(final Player player) {
        return getSlotCrystal(player, 1);
    }

    public void setGlass(Inventory inv) {
        for (int slot = 0; slot < 9; slot++) {
            if (slot != 4) {
                inv.setItem(slot, ColorUtils.getRandomPaneColor().setName(" ").build());
                inv.setItem(slot + 18, ColorUtils.getRandomPaneColor().setName(" ").build());
            } else {
                inv.setItem(slot, this.pointer.build());
                inv.setItem(slot + 18, this.pointer.build());
            }
        }
    }

    public boolean isAnimationToggle() {
        return this.animationToggle;
    }

    public boolean isScrambler(ItemStack item) {
        return item.getPersistentDataContainer().has(DataKeys.scrambler.getNamespacedKey());
    }

    public ItemBuilder getPointer() {
        return this.pointer;
    }

    public String getGuiName() {
        return this.guiName;
    }
}
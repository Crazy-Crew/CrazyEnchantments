package com.badbones69.crazyenchantments.paper.controllers.settings;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.keys.FileKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.*;

public class ProtectionCrystalSettings {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this.starter.getMethods();

    private final Map<UUID, List<ItemStack>> crystalItems = new HashMap<>();

    private String protectionString;
    private ItemBuilder crystal;

    public void loadProtectionCrystal() {
        final CommentedConfigurationNode config = FileKeys.CONFIG.getConfiguration();

        this.protectionString = config
                .node("Settings", "ProtectionCrystal", "Protected")
                .getString("&6Ancient Protection");

        this.crystal = new ItemBuilder()
                .setMaterial(config
                        .node("Settings", "ProtectionCrystal", "Item")
                        .getString("EMERALD"))
                .setItemModel(config
                        .node("Settings", "ProtectionCrystal", "Model", "Namespace")
                        .getString(""),
                        config
                        .node("Settings", "ProtectionCrystal", "Model", "Key")
                        .getString(""))
                .setName(config
                        .node("Settings", "ProtectionCrystal", "Name")
                        .getString("&cError getting name for protection crystals."))
                .setLore(StringUtils.getStringList(config.node("Settings", "ProtectionCrystal", "Lore")))
                .setGlow(config
                        .node("Settings", "ProtectionCrystal", "Glowing")
                        .getBoolean(false));
    }

    public final ItemStack getCrystal(final Audience player) {
        return getCrystal(player, 1);
    }

    public final ItemStack getCrystal(final Audience player, final int amount) {
        final ItemStack item = this.crystal.setAmount(amount).build(player);

        item.editPersistentDataContainer(container -> container.set(DataKeys.protection_crystal.getNamespacedKey(), PersistentDataType.BOOLEAN, true));

        return item;
    }

    /**
     * Add a player to the map to protect items.
     * @param player - The player object.
     * @param items - The items in the player's inventory.
     */
    public void addPlayer(Player player, List<ItemStack> items) {
        this.crystalItems.put(player.getUniqueId(), items);
    }

    /**
     * Remove the player from the map.
     * @param player - The player object.
     */
    public void removePlayer(Player player) {
        this.crystalItems.remove(player.getUniqueId());
    }

    /**
     * Check if the map contains the player.
     * @param player - The player object.
     */
    public boolean containsPlayer(Player player) {
        return this.crystalItems.containsKey(player.getUniqueId());
    }

    /**
     * Get the player from the map.
     * @param player - The player object.
     * @return Get the player's items stored.
     */
    public List<ItemStack> getPlayer(Player player) {
        return this.crystalItems.get(player.getUniqueId());
    }

    /**
     * @return The hash map.
     */
    public Map<UUID, List<ItemStack>> getCrystalItems() {
        return this.crystalItems;
    }

    /**
     * Check if the player has permissions & if the option is enabled.
     * @param player - The player to check.
     */
    public boolean isProtectionSuccessful(Player player) {
        if (player.hasPermission("crazyenchantments.bypass.protectioncrystal")) return true;

        final CommentedConfigurationNode config = FileKeys.CONFIG.getConfiguration();

        if (config.node("Settings", "ProtectionCrystal", "Chance", "Toggle").getBoolean(false)) {
            return this.methods.randomPicker(config.node("Settings", "ProtectionCrystal", "Chance", "Success-Chance").getInt(100), 100);
        }

        return true;
    }

    public static boolean isProtected(PersistentDataContainerView data) {
        return data.has(DataKeys.protected_item.getNamespacedKey());
    }

    /**
     * Check if the item is a protection crystal.
     * @param item - The item to check.
     * @return True if the item is a protection crystal.
     */
    public boolean isProtectionCrystal(ItemStack item) {
        return item.getPersistentDataContainer().has(DataKeys.protection_crystal.getNamespacedKey());
    }

    /**
     * Remove protection from the item.
     * @param item - The item to remove protection from.
     * @return The new item.
     */
    public final ItemStack removeProtection(final ItemStack item) {
        final PersistentDataContainerView view = item.getPersistentDataContainer();

        if (view.has(DataKeys.protected_item.getNamespacedKey())) {
            item.editPersistentDataContainer(container -> {
                container.remove(DataKeys.protected_item.getNamespacedKey());
            });
        }

        final List<Component> lore = item.lore();

        if (lore != null) {
            lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).contains(ColorUtils.stripStringColour(this.protectionString)));

            item.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(lore).build());
        }

        return item;
    }

    /**
     * Add protection to an item.
     * @param item - The item to add protection to.
     * @return The new item.
     */
    public final ItemStack addProtection(final ItemStack item) {
        final List<Component> itemLore = item.lore();

        List<Component> lore = itemLore != null ? itemLore : new ArrayList<>();

        item.editPersistentDataContainer(container -> {
            container.set(DataKeys.protected_item.getNamespacedKey(), PersistentDataType.BOOLEAN, true);
        });

        lore.add(ColorUtils.legacyTranslateColourCodes(this.protectionString));

        item.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(lore).build());

        return item;
    }
}
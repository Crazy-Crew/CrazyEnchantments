package com.badbones69.crazyenchantments.paper.controllers.settings;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ProtectionCrystalSettings {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this.starter.getMethods();

    @NotNull
    private final String protectionString = ColorUtils.color(Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected"));

    private final HashMap<UUID, List<ItemStack>> crystalItems = new HashMap<>();

    private ItemBuilder crystal;

    public void loadProtectionCrystal() {
        FileConfiguration config = Files.CONFIG.getFile();

        this.crystal = new ItemBuilder()
                .setMaterial(config.getString("Settings.ProtectionCrystal.Item", "EMERALD"))
                .setItemModel(config.getString("Settings.ProtectionCrystal.Model.Namespace", ""), config.getString("Settings.ProtectionCrystal.Model.Key", ""))
                .setName(config.getString("Settings.ProtectionCrystal.Name", "Error getting name."))
                .setLore(config.getStringList("Settings.ProtectionCrystal.Lore"))
                .setGlow(config.getBoolean("Settings.ProtectionCrystal.Glowing", false));
    }

    public final ItemStack getCrystal() {
        return getCrystal(1);
    }

    public final ItemStack getCrystal(final int amount) {
        final ItemStack item = this.crystal.setAmount(amount).build();

        item.editPersistentDataContainer(container -> {
            container.set(DataKeys.protection_crystal.getNamespacedKey(), PersistentDataType.BOOLEAN, true);
        });

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
    public HashMap<UUID, List<ItemStack>> getCrystalItems() {
        return this.crystalItems;
    }

    /**
     * Check if the player has permissions & if the option is enabled.
     * @param player - The player to check.
     */
    public boolean isProtectionSuccessful(Player player) {
        if (player.hasPermission("crazyenchantments.bypass.protectioncrystal")) return true;

        FileConfiguration config = Files.CONFIG.getFile();

        if (config.getBoolean("Settings.ProtectionCrystal.Chance.Toggle", false)) return this.methods.randomPicker(config.getInt("Settings.ProtectionCrystal.Chance.Success-Chance", 100), 100);

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
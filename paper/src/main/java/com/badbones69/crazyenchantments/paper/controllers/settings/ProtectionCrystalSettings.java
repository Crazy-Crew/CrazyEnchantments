package com.badbones69.crazyenchantments.paper.controllers.settings;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenchantments.ConfigManager;
import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.platform.impl.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProtectionCrystalSettings {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull Methods methods = this.starter.getMethods();

    private final @NotNull String protectionString = ColorUtils.color(ConfigManager.getConfig().getProperty(Config.protection_crystal_protected));

    private final HashMap<UUID, List<ItemStack>> crystalItems = new HashMap<>();

    private ItemBuilder crystal;

    /**
     * Check if the item is protected or not.
     *
     * @param item - The item to check.
     * @return True if yes otherwise false.
     */
    public static boolean isProtected(ItemStack item) {
        return item.hasItemMeta() && isProtected(item.getItemMeta());
    }

    public static boolean isProtected(ItemMeta meta) {
        return meta != null && isProtected(meta.getPersistentDataContainer());
    }

    public static boolean isProtected(PersistentDataContainer data) {
        return data != null && data.has(DataKeys.protected_item.getNamespacedKey());
    }

    public void loadProtectionCrystal() {
        SettingsManager config = ConfigManager.getConfig();

        this.crystal = new ItemBuilder()
                .setMaterial(config.getProperty(Config.protection_crystal_item))
                .setName(config.getProperty(Config.protection_crystal_name))
                .setLore(config.getProperty(Config.protection_crystal_lore))
                .setGlow(config.getProperty(Config.protection_crystal_glowing));
    }

    public ItemStack getCrystals() {
        return getCrystals(1);
    }

    public ItemStack getCrystals(int amount) {
        ItemStack item = this.crystal.setAmount(amount).build();
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(DataKeys.protection_crystal.getNamespacedKey(), PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Add a player to the map to protect items.
     *
     * @param player - The player object.
     * @param items  - The items in the player's inventory.
     */
    public void addPlayer(Player player, List<ItemStack> items) {
        this.crystalItems.put(player.getUniqueId(), items);
    }

    /**
     * Remove the player from the map.
     *
     * @param player - The player object.
     */
    public void removePlayer(Player player) {
        this.crystalItems.remove(player.getUniqueId());
    }

    /**
     * Check if the map contains the player.
     *
     * @param player - The player object.
     */
    public boolean containsPlayer(Player player) {
        return this.crystalItems.containsKey(player.getUniqueId());
    }

    /**
     * Get the player from the map.
     *
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
     *
     * @param player - The player to check.
     */
    public boolean isProtectionSuccessful(Player player) {
        if (player.hasPermission("crazyenchantments.bypass.protectioncrystal")) return true;

        SettingsManager config = ConfigManager.getConfig();

        if (config.getProperty(Config.protection_crystal_chance_toggle))
            return this.methods.randomPicker(config.getProperty(Config.protection_crystal_chance), 100);

        return true;
    }

    /**
     * Check if the item is a protection crystal.
     *
     * @param item - The item to check.
     * @return True if the item is a protection crystal.
     */
    public boolean isProtectionCrystal(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(DataKeys.protection_crystal.getNamespacedKey());
    }

    /**
     * Remove protection from the item.
     *
     * @param item - The item to remove protection from.
     * @return The new item.
     */
    public ItemStack removeProtection(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta.getPersistentDataContainer().has(DataKeys.protected_item.getNamespacedKey()))
            meta.getPersistentDataContainer().remove(DataKeys.protected_item.getNamespacedKey());

        if (!(item.lore() == null)) {
            List<Component> lore = item.lore();

            assert lore != null;
            lore.removeIf(loreComponent -> PlainTextComponentSerializer.plainText().serialize(loreComponent).replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "").contains(this.protectionString.replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "")));

            meta.lore(lore);
        }

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Add protection to an item.
     *
     * @param item - The item to add protection to.
     * @return The new item.
     */
    public ItemStack addProtection(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = item.lore() != null ? item.lore() : new ArrayList<>();

        meta.getPersistentDataContainer().set(DataKeys.protected_item.getNamespacedKey(), PersistentDataType.BOOLEAN, true);

        assert lore != null;
        lore.add(ColorUtils.legacyTranslateColourCodes(this.protectionString));

        meta.lore(lore);
        item.setItemMeta(meta);

        return item;
    }
}
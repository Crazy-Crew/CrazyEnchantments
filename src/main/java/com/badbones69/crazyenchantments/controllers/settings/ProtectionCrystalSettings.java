package com.badbones69.crazyenchantments.controllers.settings;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.FileManager;
import com.badbones69.crazyenchantments.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.utilities.misc.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ProtectionCrystalSettings {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final String protectionString = ColorUtils.color(FileManager.Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected"));

    private final HashMap<UUID, List<ItemStack>> crystalItems = new HashMap<>();

    private ItemBuilder crystal;

    public void loadProtectionCrystal() {
        FileConfiguration config = FileManager.Files.CONFIG.getFile();
        crystal = new ItemBuilder()
                .setMaterial(Objects.requireNonNull(config.getString("Settings.ProtectionCrystal.Item")))
                .setName(config.getString("Settings.ProtectionCrystal.Name"))
                .setLore(config.getStringList("Settings.ProtectionCrystal.Lore"))
                .setGlow(config.getBoolean("Settings.ProtectionCrystal.Glowing"));
    }

    public ItemStack getCrystals() {
        return getCrystals(1);
    }

    public ItemStack getCrystals(int amount) {

        ItemStack item = crystal.setAmount(amount).build();
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(DataKeys.PROTECTION_CRYSTAL.getKey(), PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Add a player to the map to protect items.
     * @param player - The player object.
     * @param items - The items in the player's inventory.
     */
    public void addPlayer(Player player, List<ItemStack> items) {
        crystalItems.put(player.getUniqueId(), items);
    }

    /**
     * Remove the player from the map.
     * @param player - The player object.
     */
    public void removePlayer(Player player) {
        crystalItems.remove(player.getUniqueId());
    }

    /**
     * Check if the map contains the player.
     * @param player - The player object.
     */
    public boolean containsPlayer(Player player) {
        return crystalItems.containsKey(player.getUniqueId());
    }

    /**
     * Get the player from the map.
     * @param player - The player object.
     * @return Get the player's items stored.
     */
    public List<ItemStack> getPlayer(Player player) {
        return crystalItems.get(player.getUniqueId());
    }

    /**
     * @return The hash map.
     */
    public HashMap<UUID, List<ItemStack>> getCrystalItems() {
        return crystalItems;
    }

    /**
     * Check if the player has permissions & if the option is enabled.
     * @param player - The player to check.
     */
    public boolean isProtectionSuccessful(Player player) {
        if (player.hasPermission("crazyenchantments.bypass.protectioncrystal")) return true;

        FileConfiguration config = FileManager.Files.CONFIG.getFile();

        if (config.getBoolean("Settings.ProtectionCrystal.Chance.Toggle")) return methods.randomPicker(config.getInt("Settings.ProtectionCrystal.Chance.Success-Chance", 100), 100);

        return true;
    }

    /**
     * Check if the item is protected or not.
     * @param item - The item to check.
     * @return True if yes otherwise false.
     */
    public boolean isProtected(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(DataKeys.PROTECTED_ITEM.getKey());
    }

    /**
     * Check if the item is a protection crystal.
     * @param item - The item to check.
     * @return True if the item is a protection crystal.
     */
    public boolean isProtectionCrystal(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(DataKeys.PROTECTION_CRYSTAL.getKey());
    }

    /**
     * Remove protection from the item.
     * @param item - The item to remove protection from.
     * @return The new item.
     */
    public ItemStack removeProtection(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta.getPersistentDataContainer().has(DataKeys.PROTECTED_ITEM.getKey())) meta.getPersistentDataContainer().remove(DataKeys.PROTECTED_ITEM.getKey());

        if (!(item.lore() == null)) {
            List<Component> lore = item.lore();

            assert lore != null;
            lore.removeIf(loreComponent -> PlainTextComponentSerializer.plainText().serialize(loreComponent).replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "")
                    .contains(protectionString.replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "")));

            meta.lore(lore);
        }
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Add protection to an item.
     * @param item - The item to add protection to.
     * @return The new item.
     */
    public ItemStack addProtection(ItemStack item) {

        ItemMeta meta = item.getItemMeta();
        List<Component> lore = item.lore() != null ? item.lore() : new ArrayList<>();

        meta.getPersistentDataContainer().set(DataKeys.PROTECTED_ITEM.getKey(), PersistentDataType.BOOLEAN, true);

        assert lore != null;
        lore.add(ColorUtils.legacyTranslateColourCodes(protectionString));

        meta.lore(lore);
        item.setItemMeta(meta);

        return item;
    }

}
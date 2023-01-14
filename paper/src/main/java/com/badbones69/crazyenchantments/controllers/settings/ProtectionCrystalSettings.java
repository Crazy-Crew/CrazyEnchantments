package com.badbones69.crazyenchantments.controllers.settings;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.FileManager;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ProtectionCrystalSettings {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final String protectionString = starter.color(FileManager.Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected"));

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
        return crystal.setAmount(amount).build();
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
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            for (String lore : item.getItemMeta().getLore()) {
                if (lore.contains(protectionString)) return true;
            }
        }

        return false;
    }

    /**
     * Remove protection from the item.
     * @param item - The item to remove protection from.
     * @return The new item.
     */
    public ItemStack removeProtection(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();

        ArrayList<String> lore = new ArrayList<>(itemMeta.getLore());

        lore.remove(protectionString);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }
}
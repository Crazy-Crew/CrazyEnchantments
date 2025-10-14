package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.enums.v2.Messages;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.paper.managers.items.ItemManager;
import com.badbones69.crazyenchantments.paper.managers.items.interfaces.CustomItem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProtectionCrystalListener implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final ItemManager itemManager = this.plugin.getItemManager();

    @NotNull
    private final Methods methods = null;

    @NotNull
    private final ProtectionCrystalSettings protectionCrystalSettings = null;

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        final ItemStack crystalItem = event.getCursor();

        if (crystalItem.isEmpty()) return;

        final ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null || itemStack.isEmpty()) return;

        final Optional<CustomItem> customItem = this.itemManager.getItem("protection_crystal_item");

        if (customItem.isEmpty()) return;

        final CustomItem item = customItem.get();

        if (!item.isItem(crystalItem) | item.isItem(itemStack)) return;

        if (item.hasKey(itemStack, DataKeys.protected_item.getNamespacedKey())) return;

        if (itemStack.getAmount() > 1 || crystalItem.getAmount() > 1) {
            Messages.NEED_TO_UNSTACK_ITEM.sendMessage(player);

            return;
        }

        event.setCancelled(true);

        player.setItemOnCursor(this.methods.removeItem(crystalItem));

        item.addKey(itemStack, DataKeys.protected_item.getNamespacedKey(), "true");

        event.setCurrentItem(itemStack);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getKeepInventory()) return;

        Player player = event.getEntity();
        List<ItemStack> savedItems = new ArrayList<>();

        for (ItemStack item : event.getDrops()) {
            if (item == null || item.isEmpty()) continue;

            if (ProtectionCrystalSettings.isProtected(item.getPersistentDataContainer()) && this.protectionCrystalSettings.isProtectionSuccessful(player)) savedItems.add(item);
        }

        savedItems.forEach(item -> event.getDrops().remove(item));

        this.protectionCrystalSettings.addPlayer(player, savedItems);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (this.protectionCrystalSettings.containsPlayer(player)) {
            final PlayerInventory inventory = player.getInventory();

            final YamlConfiguration config = FileKeys.config.getYamlConfiguration();

            // If the config does not have the option then it will lose the protection by default.
            if (config.getBoolean("Settings.ProtectionCrystal.Lose-Protection-On-Death", true)) {
                for (ItemStack item : this.protectionCrystalSettings.getCrystalItems().get(player.getUniqueId())) {
                    if (item == null || item.isEmpty()) continue;

                    inventory.addItem(this.protectionCrystalSettings.removeProtection(item));
                }
            } else {
                for (ItemStack item : this.protectionCrystalSettings.getPlayer(player)) {
                    if (item == null || item.isEmpty()) continue;

                    inventory.addItem(item);
                }
            }

            this.protectionCrystalSettings.removePlayer(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCrystalClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();

        final ItemStack itemStack = inventory.getItemInMainHand();

        if (itemStack.isEmpty()) return;

        this.itemManager.getItem("protection_crystal_item").ifPresent(action -> {
            if (action.hasKey(itemStack, DataKeys.protection_crystal.getNamespacedKey())) event.setCancelled(true);
        });
    }
}
package com.badbones69.crazyenchantments.listeners;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.FileManager;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.controllers.settings.ProtectionCrystalSettings;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class ProtectionCrystalListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    // Settings.
    private final ProtectionCrystalSettings protectionCrystalSettings = starter.getProtectionCrystalSettings();

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (e.getInventory() != null) {
            ItemStack crystalItem = e.getCursor() != null ? e.getCursor() : new ItemStack(Material.AIR); // The Crystal.
            ItemStack item = e.getCurrentItem() != null ? e.getCurrentItem() : new ItemStack(Material.AIR); // The item your adding the protection to.
            if (item.getType() != Material.AIR && crystalItem.getType() != Material.AIR &&
            // The item getting protected is not stacked.
            item.getAmount() == 1 &&
            // Making sure they are not dropping crystals on top of other crystals.
            !protectionCrystalSettings.getCrystals().isSimilar(item) && crystalItem.isSimilar(protectionCrystalSettings.getCrystals()) &&
            // The item does not have protection on it.
            !protectionCrystalSettings.isProtected(item)) {
                // The crystal is not stacked.

                if (crystalItem.getAmount() > 1) {
                    player.sendMessage(Messages.NEED_TO_UNSTACK_ITEM.getMessage());
                    return;
                }

                e.setCancelled(true);
                player.setItemOnCursor(methods.removeItem(crystalItem));
                e.setCurrentItem(methods.addLore(item, starter.color(FileManager.Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected"))));
                player.updateInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent e) {

        if (e.getKeepInventory()) return;

        Player player = e.getEntity();
        List<ItemStack> savedItems = new ArrayList<>();
        List<ItemStack> droppedItems = new ArrayList<>();

        for (ItemStack item : e.getDrops()) {

            if (item != null) {

                if (protectionCrystalSettings.isProtected(item) && protectionCrystalSettings.isProtectionSuccessful(player)) {
                    savedItems.add(item);
                    continue;
                }

                droppedItems.add(item);
            }
        }

        e.getDrops().clear();
        e.getDrops().addAll(droppedItems);

        protectionCrystalSettings.addPlayer(player, savedItems);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();

        if (protectionCrystalSettings.containsPlayer(player)) {

            // If the config does not have the option then it will lose the protection by default.
            if (Files.CONFIG.getFile().getBoolean("Settings.ProtectionCrystal.Lose-Protection-On-Death", true)) {
                for (ItemStack item : protectionCrystalSettings.getCrystalItems().get(player.getUniqueId())) {
                    player.getInventory().addItem(protectionCrystalSettings.removeProtection(item));
                }
            } else {
                for (ItemStack item : protectionCrystalSettings.getPlayer(player)) {
                    player.getInventory().addItem(item);
                }
            }

            protectionCrystalSettings.removePlayer(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCrystalClick(PlayerInteractEvent e) {
        ItemStack item = methods.getItemInHand(e.getPlayer());

        if (item.isSimilar(protectionCrystalSettings.getCrystals())) e.setCancelled(true);
    }
}
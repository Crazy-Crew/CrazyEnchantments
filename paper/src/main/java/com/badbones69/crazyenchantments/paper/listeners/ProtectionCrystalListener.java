package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.ConfigManager;
import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.platform.impl.Config;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProtectionCrystalListener implements Listener {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull Methods methods = this.starter.getMethods();

    private final @NotNull ProtectionCrystalSettings protectionCrystalSettings = this.starter.getProtectionCrystalSettings();

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        ItemStack crystalItem = event.getCursor();

        ItemStack item = event.getCurrentItem() != null ? event.getCurrentItem() : new ItemStack(Material.AIR);

        if (item.getType() == Material.AIR || crystalItem.getType() == Material.AIR) return;

        if (!this.protectionCrystalSettings.isProtectionCrystal(crystalItem)) return;

        if (this.protectionCrystalSettings.isProtectionCrystal(item)) return;

        if (ProtectionCrystalSettings.isProtected(item)) return;

        if (item.getAmount() > 1 || crystalItem.getAmount() > 1) {
            player.sendMessage(Messages.NEED_TO_UNSTACK_ITEM.getMessage());
            return;
        }

        event.setCancelled(true);

        player.setItemOnCursor(this.methods.removeItem(crystalItem));

        event.setCurrentItem(this.protectionCrystalSettings.addProtection(item));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getKeepInventory()) return;

        Player player = event.getEntity();
        List<ItemStack> savedItems = new ArrayList<>();
        List<ItemStack> droppedItems = new ArrayList<>();

        for (ItemStack item : event.getDrops()) {

            if (item != null) {

                if (ProtectionCrystalSettings.isProtected(item) && this.protectionCrystalSettings.isProtectionSuccessful(player)) {
                    savedItems.add(item);
                    continue;
                }

                droppedItems.add(item);
            }
        }

        event.getDrops().clear();
        event.getDrops().addAll(droppedItems);

        this.protectionCrystalSettings.addPlayer(player, savedItems);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (this.protectionCrystalSettings.containsPlayer(player)) {
            // If the config does not have the option then it will lose the protection by default.
            if (ConfigManager.getConfig().getProperty(Config.protection_crystal_lose_protection_on_death)) {
                for (ItemStack item : this.protectionCrystalSettings.getCrystalItems().get(player.getUniqueId())) {
                    player.getInventory().addItem(this.protectionCrystalSettings.removeProtection(item));
                }
            } else {
                for (ItemStack item : this.protectionCrystalSettings.getPlayer(player)) {
                    player.getInventory().addItem(item);
                }
            }

            this.protectionCrystalSettings.removePlayer(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCrystalClick(PlayerInteractEvent event) {
        ItemStack item = this.methods.getItemInHand(event.getPlayer());

        if (!item.hasItemMeta()) return;

        if (this.protectionCrystalSettings.isProtectionCrystal(item)) event.setCancelled(true);
    }
}
package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
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

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this.starter.getMethods();

    @NotNull
    private final ProtectionCrystalSettings protectionCrystalSettings = this.starter.getProtectionCrystalSettings();

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

        for (ItemStack item : event.getDrops()) {
            if (ProtectionCrystalSettings.isProtected(item) && this.protectionCrystalSettings.isProtectionSuccessful(player)) savedItems.add(item);
        }

        savedItems.forEach(item -> event.getDrops().remove(item));

        this.protectionCrystalSettings.addPlayer(player, savedItems);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (this.protectionCrystalSettings.containsPlayer(player)) {
            // If the config does not have the option then it will lose the protection by default.
            if (Files.CONFIG.getFile().getBoolean("Settings.ProtectionCrystal.Lose-Protection-On-Death", true)) {
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
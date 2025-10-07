package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.managers.WingsManager;
import com.badbones69.crazyenchantments.paper.api.utils.WingsUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BootEnchantments implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    // Plugin Managers.
    @NotNull
    private final WingsManager wingsManager = this.starter.getWingsManager();

    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerEquip(PlayerArmorChangeEvent event) {
        if (!this.wingsManager.isWingsEnabled()) return;

        Player player = event.getPlayer();

        // Check the new armor piece.
        WingsUtils.checkArmor(event.getNewItem(), true, ItemStack.empty(), player);

        // Check the old armor piece.
        WingsUtils.checkArmor(ItemStack.empty(), false, event.getOldItem(), player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerFly(PlayerToggleFlightEvent event) {
        if (!this.wingsManager.isWingsEnabled()) return;

        Player player = event.getPlayer();

        if (player.getEquipment().getBoots() == null) return;
        if (!this.enchantmentBookSettings.getEnchantments(player.getEquipment().getBoots()).containsKey(CEnchantments.WINGS.getEnchantment())) return;

        if (WingsUtils.checkRegion(player) || WingsUtils.isEnemiesNearby(player)) return;

        if (event.isFlying()) {
            if (player.getAllowFlight()) {
                event.setCancelled(true);
                player.setFlying(true);
                this.wingsManager.addFlyingPlayer(player);
            }
        } else {
            this.wingsManager.removeFlyingPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom() == event.getTo()) return;

        Player player = event.getPlayer();
        boolean isFlying = player.isFlying(); // TODO implement single method for all enchantment checks. #EnchantUtils

        if (this.wingsManager.isWingsEnabled() && this.enchantmentBookSettings.getEnchantments(player.getEquipment().getBoots()).containsKey(CEnchantments.WINGS.getEnchantment())) {
            if (WingsUtils.checkRegion(player)) {
                if (!WingsUtils.isEnemiesNearby(player)) {
                    player.setAllowFlight(true);
                } else {
                    if (isFlying && WingsUtils.checkGameMode(player)) {
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        this.wingsManager.removeFlyingPlayer(player);
                    }
                }
            } else {
                if (isFlying && WingsUtils.checkGameMode(player)) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    this.wingsManager.removeFlyingPlayer(player);
                }
            }

            if (isFlying) this.wingsManager.addFlyingPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!this.wingsManager.isWingsEnabled()) return;

        if (!this.enchantmentBookSettings.getEnchantments(player.getEquipment().getBoots()).containsKey(CEnchantments.WINGS.getEnchantment())) return;

        if (WingsUtils.checkRegion(player) || WingsUtils.isEnemiesNearby(player)) return;

        player.setAllowFlight(true);
        this.wingsManager.addFlyingPlayer(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.wingsManager.isWingsEnabled() || !this.wingsManager.isFlyingPlayer(player)) return;

        player.setFlying(false);
        player.setAllowFlight(false);
        this.wingsManager.removeFlyingPlayer(player);
    }
}
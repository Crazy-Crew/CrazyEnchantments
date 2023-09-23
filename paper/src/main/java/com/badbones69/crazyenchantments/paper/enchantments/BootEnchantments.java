package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.managers.WingsManager;
import com.badbones69.crazyenchantments.paper.api.support.anticheats.SpartanSupport;
import com.badbones69.crazyenchantments.paper.utilities.WingsUtils;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BootEnchantments implements Listener {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Starter starter = this.plugin.getStarter();

    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    // Plugin Support.
    private final SpartanSupport spartanSupport = this.starter.getSpartanSupport();

    // Plugin Managers.
    private final WingsManager wingsManager = this.starter.getWingsManager();

    // Utils

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEquip(PlayerArmorChangeEvent event) {
        if (!this.wingsManager.isWingsEnabled()) return;

        Player player = event.getPlayer();

        // Check the new armor piece.
        WingsUtils.checkArmor(event.getNewItem(), true, null, player);

        // Check the old armor piece.
        WingsUtils.checkArmor(null, false, event.getOldItem(), player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFly(PlayerToggleFlightEvent event) {
        if (!this.wingsManager.isWingsEnabled()) return;

        Player player = event.getPlayer();

        if (player.getEquipment().getBoots() == null) return;
        if (!this.crazyManager.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS)) return;

        if (WingsUtils.checkRegion(player) || WingsUtils.isEnemiesNearby(player)) return;

        if (SupportedPlugins.SPARTAN.isPluginLoaded()) spartanSupport.cancelNormalMovements(player);

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
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom() == event.getTo()) return;

        Player player = event.getPlayer();
        boolean isFlying = player.isFlying();

        if (this.wingsManager.isWingsEnabled() && this.crazyManager.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS)) {
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
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!this.wingsManager.isWingsEnabled()) return;

        if (!this.crazyManager.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS)) return;

        if (WingsUtils.checkRegion(player) || WingsUtils.isEnemiesNearby(player)) return;

        if (SupportedPlugins.SPARTAN.isPluginLoaded()) this.spartanSupport.cancelNormalMovements(player);

        player.setAllowFlight(true);
        this.wingsManager.addFlyingPlayer(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.wingsManager.isWingsEnabled() || !this.wingsManager.isFlyingPlayer(player)) return;

        player.setFlying(false);
        player.setAllowFlight(false);
        this.wingsManager.removeFlyingPlayer(player);
    }
}
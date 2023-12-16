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

public class BootEnchantments implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    // Plugin Support.
    private final SpartanSupport spartanSupport = starter.getSpartanSupport();

    // Plugin Managers.
    private final WingsManager wingsManager = starter.getWingsManager();

    // Utils

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEquip(PlayerArmorChangeEvent event) {
        if (!wingsManager.isWingsEnabled()) return;

        Player player = event.getPlayer();

        // Check the new armor piece.
        WingsUtils.checkArmor(event.getNewItem(), true, null, player);

        // Check the old armor piece.
        WingsUtils.checkArmor(null, false, event.getOldItem(), player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFly(PlayerToggleFlightEvent event) {
        if (!wingsManager.isWingsEnabled()) return; // TODO implement single method for all enchantment checks. #EnchantUtils

        Player player = event.getPlayer();

        if (player.getEquipment().getBoots() == null) return;
        if (!crazyManager.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS)) return;

        if (WingsUtils.checkRegion(player) || WingsUtils.isEnemiesNearby(player)) return;

        if (SupportedPlugins.SPARTAN.isPluginLoaded()) spartanSupport.cancelNormalMovements(player);

        if (event.isFlying()) {
            if (player.getAllowFlight()) {
                event.setCancelled(true);
                player.setFlying(true);
                wingsManager.addFlyingPlayer(player);
            }
        } else {
            wingsManager.removeFlyingPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom() == event.getTo()) return;

        Player player = event.getPlayer();
        boolean isFlying = player.isFlying(); // TODO implement single method for all enchantment checks. #EnchantUtils

        if (wingsManager.isWingsEnabled() && crazyManager.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS)) {
            if (WingsUtils.checkRegion(player)) {
                if (!WingsUtils.isEnemiesNearby(player)) {
                    player.setAllowFlight(true);
                } else {
                    if (isFlying && WingsUtils.checkGameMode(player)) {
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        wingsManager.removeFlyingPlayer(player);
                    }
                }
            } else {
                if (isFlying && WingsUtils.checkGameMode(player)) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    wingsManager.removeFlyingPlayer(player);
                }
            }

            if (isFlying) wingsManager.addFlyingPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!wingsManager.isWingsEnabled()) return;

        if (!crazyManager.hasEnchantment(player.getEquipment().getBoots(), CEnchantments.WINGS)) return;

        if (WingsUtils.checkRegion(player) || WingsUtils.isEnemiesNearby(player)) return;

        if (SupportedPlugins.SPARTAN.isPluginLoaded()) spartanSupport.cancelNormalMovements(player);

        player.setAllowFlight(true);
        wingsManager.addFlyingPlayer(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!wingsManager.isWingsEnabled() || !wingsManager.isFlyingPlayer(player)) return;

        player.setFlying(false);
        player.setAllowFlight(false);
        wingsManager.removeFlyingPlayer(player);
    }
}
package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.managers.WingsManager;
import com.badbones69.crazyenchantments.paper.api.utils.WingsUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.kyori.adventure.util.TriState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class BootEnchantments implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyPlatform platform = this.plugin.getPlatform();

    private final EnchantmentBookSettings bookSettings = this.platform.getBookSettings();

    private final CrazyManager crazyManager = this.plugin.getCrazyManager();
    private final WingsManager wingsManager = this.crazyManager.getWingsManager();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerEquip(PlayerArmorChangeEvent event) {
        if (!this.wingsManager.isWingsEnabled()) return;

        Player player = event.getPlayer();

        // Check the new armor piece.
        WingsUtils.checkArmor(event.getNewItem(), true, null, player);

        // Check the old armor piece.
        WingsUtils.checkArmor(null, false, event.getOldItem(), player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerFly(PlayerToggleFlightEvent event) {
        if (!this.wingsManager.isWingsEnabled()) return;

        final Player player = event.getPlayer();
        final EntityEquipment equipment = player.getEquipment();

        final ItemStack itemStack = equipment.getBoots();

        if (itemStack.isEmpty()) return;

        if (!this.bookSettings.getEnchantments(itemStack).containsKey(CEnchantments.WINGS.getEnchantment())) return;

        if (!WingsUtils.canFly(player)) return;

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

        final Player player = event.getPlayer();
        final EntityEquipment equipment = player.getEquipment();

        final ItemStack itemStack = equipment.getBoots();

        boolean isFlying = player.isFlying(); // TODO implement single method for all enchantment checks. #EnchantUtils

        if (this.wingsManager.isWingsEnabled() && this.bookSettings.getEnchantments(itemStack).containsKey(CEnchantments.WINGS.getEnchantment())) {
            if (WingsUtils.canFly(player)) {
                player.setAllowFlight(true);
            } else {
                if (isFlying && WingsUtils.checkGameMode(player)) {
                    player.setFlyingFallDamage(TriState.FALSE);
                    player.setAllowFlight(false);
                    player.setFlying(false);

                    this.wingsManager.removeFlyingPlayer(player);
                }
            }

            if (isFlying) this.wingsManager.addFlyingPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (!this.wingsManager.isWingsEnabled()) return;

        if (!this.bookSettings.getEnchantments(player.getEquipment().getBoots()).containsKey(CEnchantments.WINGS.getEnchantment())) return;

        if (!WingsUtils.canFly(player)) return;

        player.setAllowFlight(true);

        this.wingsManager.addFlyingPlayer(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (!this.wingsManager.isWingsEnabled() || !this.wingsManager.isFlyingPlayer(player)) return;

        player.setFlyingFallDamage(TriState.FALSE);
        player.setAllowFlight(false);
        player.setFlying(false);

        this.wingsManager.removeFlyingPlayer(player);
    }
}
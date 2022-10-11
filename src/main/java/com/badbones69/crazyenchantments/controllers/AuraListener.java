package com.badbones69.crazyenchantments.controllers;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.events.AuraActiveEvent;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AuraListener implements Listener {
    
    private final CEnchantments[] AURA_ENCHANTMENTS = {
    CEnchantments.BLIZZARD,
    CEnchantments.ACIDRAIN,
    CEnchantments.SANDSTORM,
    CEnchantments.RADIANT,
    CEnchantments.INTIMIDATE
    };

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null ||
        from.getBlockX() == to.getBlockX()
        && from.getBlockY() == to.getBlockY()
        && from.getBlockZ() == to.getBlockZ()) return;

        List<Player> players = getNearbyPlayers(player);

        if (players.isEmpty()) return;

        EntityEquipment playerEquipment = player.getEquipment();

        if (playerEquipment.getHolder().isEmpty()) return;

        for (ItemStack item : playerEquipment.getArmorContents()) { // The player that moves.
            Map<CEnchantment, Integer> itemEnchantments = crazyManager.getEnchantments(item);
            itemEnchantments.forEach((enchantment, level) -> {
                CEnchantments enchantmentEnum = getAuraEnchantmentEnum(enchantment);

                if (enchantmentEnum == null) return;

                for (Player other : players) {
                    AuraActiveEvent auraEvent = new AuraActiveEvent(player, other, enchantmentEnum, level);
                    plugin.getServer().getPluginManager().callEvent(auraEvent);
                }
            });
        }

        for (Player other : players) {
            EntityEquipment otherEquipment = other.getEquipment();

            if (otherEquipment.getHolder().isEmpty()) continue;

            for (ItemStack item : otherEquipment.getArmorContents()) { // The other players moving.
                Map<CEnchantment, Integer> itemEnchantments = crazyManager.getEnchantments(item);
                itemEnchantments.forEach((enchantment, level) -> {
                    CEnchantments enchantmentEnum = getAuraEnchantmentEnum(enchantment);

                    if (enchantmentEnum == null) return;

                    AuraActiveEvent auraEvent = new AuraActiveEvent(other, player, enchantmentEnum, level);
                    plugin.getServer().getPluginManager().callEvent(auraEvent);
                });
            }
        }
    }
    
    private CEnchantments getAuraEnchantmentEnum(CEnchantment enchantment) {
        return Arrays.stream(AURA_ENCHANTMENTS).filter(enchantmentEnum -> enchantmentEnum.getName().equals(enchantment.getName())).findFirst().orElse(null);
    }

    private static List<Player> getNearbyPlayers(Player player) {
        List<Player> players = new ArrayList<>();

        for (Entity entity : player.getNearbyEntities(3, 3, 3)) {
            if (!(entity instanceof Player) || entity.getUniqueId().toString().equals(player.getUniqueId().toString())) continue;

            players.add((Player) entity);
        }

        return players;
    }
}
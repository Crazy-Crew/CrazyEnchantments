package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.AuraActiveEvent;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import org.bukkit.Bukkit;
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
    
    private final static CEnchantments[] AURA_ENCHANTMENTS = {
    CEnchantments.BLIZZARD,
    CEnchantments.ACIDRAIN,
    CEnchantments.SANDSTORM,
    CEnchantments.RADIANT,
    CEnchantments.INTIMIDATE
    };
    
    private final CrazyEnchantments ce = CrazyEnchantments.getInstance();
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null ||
        from.getBlockX() == to.getBlockX()
        && from.getBlockY() == to.getBlockY()
        && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        List<Player> players = getNearbyPlayers(player, 3);
        if (players.isEmpty()) {
            return;
        }
        EntityEquipment playerEquipment = player.getEquipment();
        if (playerEquipment == null) {
            return; // Should never happen
        }
        for (ItemStack item : playerEquipment.getArmorContents()) { // The player that moves.
            Map<CEnchantment, Integer> itemEnchantments = ce.getEnchantments(item);
            itemEnchantments.forEach((enchantment, level) -> {
                CEnchantments enchantmentEnum = getAuraEnchantmentEnum(enchantment);
                if (enchantmentEnum == null) {
                    return; // Not an aura enchantment
                }
                for (Player other : players) {
                    AuraActiveEvent auraEvent = new AuraActiveEvent(player, other, enchantmentEnum, level);
                    Bukkit.getPluginManager().callEvent(auraEvent);
                }
            });
        }
        for (Player other : players) {
            EntityEquipment otherEquipment = other.getEquipment();
            if (otherEquipment == null) {
                continue; // Should never happen
            }
            for (ItemStack item : otherEquipment.getArmorContents()) {// The other players moving.
                Map<CEnchantment, Integer> itemEnchantments = ce.getEnchantments(item);
                itemEnchantments.forEach((enchantment, level) -> {
                    CEnchantments enchantmentEnum = getAuraEnchantmentEnum(enchantment);
                    if (enchantmentEnum == null) {
                        return; // Not an aura enchantment
                    }
                    AuraActiveEvent auraEvent = new AuraActiveEvent(other, player, enchantmentEnum, level);
                    Bukkit.getPluginManager().callEvent(auraEvent);
                });
            }
        }
    }
    
    private static CEnchantments getAuraEnchantmentEnum(CEnchantment enchantment) {
        return Arrays.stream(AURA_ENCHANTMENTS).filter(enchantmentEnum -> enchantmentEnum.getName().equals(enchantment.getName())).findFirst().orElse(null);
    }
    
    // TODO: move into utils?
    private static List<Player> getNearbyPlayers(Player player, int radius) {
        List<Player> players = new ArrayList<>();
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (!(entity instanceof Player) || entity.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                continue;
            }
            players.add((Player) entity);
        }
        return players;
    }
    
}
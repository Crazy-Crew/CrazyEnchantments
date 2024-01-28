package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.AuraActiveEvent;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class AuraListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    private final CEnchantments[] AURA_ENCHANTMENTS = {
            CEnchantments.BLIZZARD,
            CEnchantments.ACIDRAIN,
            CEnchantments.SANDSTORM,
            CEnchantments.RADIANT,
            CEnchantments.INTIMIDATE
    };

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX()
        && from.getBlockY() == to.getBlockY()
        && from.getBlockZ() == to.getBlockZ()) return;

        List<Player> players = getNearbyPlayers(player);

        if (players.isEmpty()) return;

        EntityEquipment playerEquipment = player.getEquipment();

        for (ItemStack item : playerEquipment.getArmorContents()) { // The player that moves.
            Map<CEnchantment, Integer> itemEnchantments = enchantmentBookSettings.getEnchantments(item);
            itemEnchantments.forEach((enchantment, level) -> {
                CEnchantments enchantmentEnum = getAuraEnchantmentEnum(enchantment);

                if (enchantmentEnum != null) players.forEach((other) -> plugin.getServer().getPluginManager().callEvent(new AuraActiveEvent(player, other, enchantmentEnum, level)));

            });
        }

        for (Player other : players) {
            EntityEquipment otherEquipment = other.getEquipment();

            for (ItemStack item : otherEquipment.getArmorContents()) { // The other players moving.
                Map<CEnchantment, Integer> itemEnchantments = enchantmentBookSettings.getEnchantments(item);
                itemEnchantments.forEach((enchantment, level) -> {
                    CEnchantments enchantmentEnum = getAuraEnchantmentEnum(enchantment);

                    if (enchantmentEnum != null) plugin.getServer().getPluginManager().callEvent(new AuraActiveEvent(other, player, enchantmentEnum, level));

                });
            }
        }
    }

    private CEnchantments getAuraEnchantmentEnum(CEnchantment enchantment) {
        return Arrays.stream(AURA_ENCHANTMENTS).filter(enchantmentEnum -> enchantmentEnum.getName().equals(enchantment.getName())).findFirst().orElse(null);
    }

    private List<Player> getNearbyPlayers(Player player) {

        return player.getNearbyEntities(3, 3, 3).stream().filter((entity) ->
                entity instanceof Player && !entity.getUniqueId().equals(player.getUniqueId())).map(entity -> (Player) entity).collect(Collectors.toList());

    }
}
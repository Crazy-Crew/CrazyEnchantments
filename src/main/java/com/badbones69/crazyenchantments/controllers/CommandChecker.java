package com.badbones69.crazyenchantments.controllers;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Arrays;
import java.util.List;

public class CommandChecker implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    private final List<String> clearInventoryCommands = Arrays.asList("/ci", "/clear", "/clearinventory");
    private final ItemStack air = new ItemStack(Material.AIR);

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClear(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();

        if (clearInventoryCommands.contains(e.getMessage().toLowerCase())) {
            for (CEnchantments enchantment : crazyManager.getEnchantmentPotions().keySet()) {
                if (enchantment.isActivated()) {
                    for (ItemStack armor : player.getEquipment().getArmorContents()) {
                        if (armor != null) crazyManager.getUpdatedEffects(player, air, air, enchantment).keySet().forEach(player :: removePotionEffect);
                    }
                }
            }

            updateEffects(player);
        } else if (e.getMessage().equalsIgnoreCase("/heal")) {
            updateEffects(player);
        }
    }

    private void updateEffects(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                crazyManager.updatePlayerEffects(player);
            }
        }.runTaskLater(plugin, 5);
    }
}
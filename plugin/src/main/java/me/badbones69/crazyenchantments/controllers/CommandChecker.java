package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
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
    
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private List<String> clearInventoryCommands = Arrays.asList("/ci", "/clear", "/clearinventory");
    private ItemStack air = new ItemStack(Material.AIR);
    
    @EventHandler
    public void onInventoryClear(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (clearInventoryCommands.contains(e.getMessage().toLowerCase())) {
            for (CEnchantments enchantment : ce.getEnchantmentPotions().keySet()) {
                if (enchantment.isActivated()) {
                    for (ItemStack armor : player.getEquipment().getArmorContents()) {
                        if (armor != null) {
                            ce.getUpdatedEffects(player, air, air, enchantment).keySet().forEach(player :: removePotionEffect);
                        }
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
                ce.updatePlayerEffects(player);
            }
        }.runTaskLater(ce.getPlugin(), 5);
    }
    
}
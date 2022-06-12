package com.badbones69.crazyenchantments.enchantments;

import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.ArrayList;

public class Helmets implements Listener {
    
    private CrazyManager ce = CrazyManager.getInstance();
    private PluginSupport pluginSupport = PluginSupport.INSTANCE;
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMovement(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (CEnchantments.COMMANDER.isActivated()) {
            for (ItemStack armor : player.getEquipment().getArmorContents()) {
                if (ce.hasEnchantment(armor, CEnchantments.COMMANDER)) {
                    int radius = 4 + ce.getLevel(armor, CEnchantments.COMMANDER);
                    ArrayList<Player> players = new ArrayList<>();

                    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                        if (entity instanceof Player other) {
                            if (pluginSupport.isFriendly(player, other)) {
                                players.add(other);
                            }
                        }
                    }

                    if (!players.isEmpty()) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.COMMANDER, armor);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            for (Player other : players) {
                                other.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 3 * 20, 1));
                            }
                        }
                    }
                }
            }
        }
    }
}
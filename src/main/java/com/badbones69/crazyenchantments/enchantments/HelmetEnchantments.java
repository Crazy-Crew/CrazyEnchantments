package com.badbones69.crazyenchantments.enchantments;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
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

public class HelmetEnchantments implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    private final PluginSupport pluginSupport = plugin.getStarter().getPluginSupport();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMovement(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (CEnchantments.COMMANDER.isActivated() && player.getEquipment() != null) {
            for (ItemStack armor : player.getEquipment().getArmorContents()) {
                if (crazyManager.hasEnchantment(armor, CEnchantments.COMMANDER)) {
                    int radius = 4 + crazyManager.getLevel(armor, CEnchantments.COMMANDER);
                    ArrayList<Player> players = new ArrayList<>();

                    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                        if (entity instanceof Player other) {
                            if (pluginSupport.isFriendly(player, other)) players.add(other);
                        }
                    }

                    if (!players.isEmpty()) {
                        EnchantmentUseEvent useEvent = new EnchantmentUseEvent(player, CEnchantments.COMMANDER, armor);

                        plugin.getServer().getPluginManager().callEvent(useEvent);

                        if (!useEvent.isCancelled()) {
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
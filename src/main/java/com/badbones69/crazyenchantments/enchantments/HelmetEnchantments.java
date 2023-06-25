package com.badbones69.crazyenchantments.enchantments;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import org.bukkit.Material;
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

    private final Starter starter = plugin.getStarter();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    // Plugin Support.
    private final PluginSupport pluginSupport = starter.getPluginSupport();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMovement(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!CEnchantments.COMMANDER.isActivated()) return;

        for (ItemStack armor : player.getEquipment().getArmorContents()) {
            if (armor.getType() == Material.AIR || !crazyManager.hasEnchantment(armor, CEnchantments.COMMANDER)) continue;

            int radius = 4 + crazyManager.getLevel(armor, CEnchantments.COMMANDER);

            ArrayList<Player> players = new ArrayList<>();

            for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                if (entity instanceof Player other && pluginSupport.isFriendly(player, other)) players.add(other);
            }

            if (players.isEmpty()) return;
            
            EnchantmentUseEvent useEvent = new EnchantmentUseEvent(player, CEnchantments.COMMANDER, armor);
            plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) return;
            players.forEach(otherPlayer -> otherPlayer.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 3 * 20, 1)));

        }
    }
}
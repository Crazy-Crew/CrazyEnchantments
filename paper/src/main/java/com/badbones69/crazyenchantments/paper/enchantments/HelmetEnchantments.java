package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.PluginSupport;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            if (armor.isEmpty()) continue;
            Map<CEnchantment, Integer> enchantments = starter.getEnchantmentBookSettings().getEnchantments(armor);

            if (!enchantments.containsKey(CEnchantments.COMMANDER.getEnchantment())) continue;

            int radius = 4 + enchantments.get(CEnchantments.COMMANDER.getEnchantment());

            List<Player> players = player.getNearbyEntities(radius, radius, radius).stream().filter(e ->
                    e instanceof Player && pluginSupport.isFriendly(player, e)).map(e -> (Player) e).toList();

            if (players.isEmpty()) return;

            EnchantmentUseEvent useEvent = new EnchantmentUseEvent(player, CEnchantments.COMMANDER, armor); // TODO add to isActive check.
            plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) return;
            players.forEach(otherPlayer -> otherPlayer.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 3 * 20, 1)));
        }
    }
}
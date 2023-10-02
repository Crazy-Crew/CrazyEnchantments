package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.PluginSupport;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.EnchantmentUseEvent;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class HelmetEnchantments implements Listener {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Starter starter = this.plugin.getStarter();

    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    // Plugin Support.
    private final PluginSupport pluginSupport = this.starter.getPluginSupport();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMovement(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!CEnchantments.COMMANDER.isActivated()) return;

        for (ItemStack armor : player.getEquipment().getArmorContents()) {
            if (armor == null || armor.getType() == Material.AIR || !this.crazyManager.hasEnchantment(armor, CEnchantments.COMMANDER)) continue;

            int radius = 4 + this.crazyManager.getLevel(armor, CEnchantments.COMMANDER);

            ArrayList<Player> players = new ArrayList<>();

            for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                if (entity instanceof Player other && this.pluginSupport.isFriendly(player, other)) players.add(other);
            }

            if (players.isEmpty()) return;

            EnchantmentUseEvent useEvent = new EnchantmentUseEvent(player, CEnchantments.COMMANDER, armor);
            this.plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) return;
            players.forEach(otherPlayer -> otherPlayer.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 3 * 20, 1)));
        }
    }
}
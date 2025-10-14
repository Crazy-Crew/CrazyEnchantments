package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.managers.TinkerManager;
import com.badbones69.crazyenchantments.paper.managers.configs.ConfigManager;
import com.badbones69.crazyenchantments.paper.managers.configs.types.TinkerConfig;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class MiscListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final ConfigManager configManager = this.plugin.getOptions();

    private final TinkerConfig config = this.configManager.getTinkerConfig();

    private final TinkerManager tinkerManager = this.plugin.geTinkerManager();

    @NotNull
    private final CrazyManager crazyManager = null;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.crazyManager.loadCEPlayer(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.crazyManager.unloadCEPlayer(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onFireworkDamage(EntityDamageEvent event) {
        final Entity directEntity = event.getDamageSource().getDirectEntity();

        if (directEntity instanceof Firework firework) {
            PersistentDataContainer container = firework.getPersistentDataContainer();

            if (container.has(DataKeys.no_firework_damage.getNamespacedKey())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onExperienceUse(PlayerInteractEvent event) {
        final Action action = event.getAction();

        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) return;

        final EquipmentSlot equipmentSlot = event.getHand();

        if (equipmentSlot == null) return;

        final Player player = event.getPlayer();

        if (this.tinkerManager.takeExperience(player, this.config.getCurrency(), equipmentSlot)) {
            event.setCancelled(true);
        }
    }
}
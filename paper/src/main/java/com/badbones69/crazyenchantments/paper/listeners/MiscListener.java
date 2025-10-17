package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.enums.DataKeys;
import com.badbones69.crazyenchantments.paper.managers.PlayerManager;
import com.badbones69.crazyenchantments.paper.managers.TinkerManager;
import com.badbones69.crazyenchantments.paper.managers.configs.ConfigManager;
import com.badbones69.crazyenchantments.paper.managers.configs.types.guis.TinkerConfig;
import com.badbones69.crazyenchantments.registry.UserRegistry;
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

public class MiscListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyInstance instance = this.plugin.getInstance();

    private final PlayerManager playerManager = this.instance.getPlayerManager();

    private final UserRegistry userRegistry = this.instance.getUserRegistry();

    private final ConfigManager configManager = this.plugin.getConfigManager();

    private final TinkerConfig config = this.configManager.getTinkerConfig();

    private final TinkerManager tinkerManager = this.plugin.geTinkerManager();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        this.playerManager.loadPlayer(player);

        this.userRegistry.addUser(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        this.playerManager.unloadPlayer(player, false);

        this.userRegistry.removeUser(player);
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
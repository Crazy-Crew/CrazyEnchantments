package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.managers.AllyManager;
import com.badbones69.crazyenchantments.paper.api.objects.AllyMob;
import com.badbones69.crazyenchantments.paper.api.objects.AllyMob.AllyType;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentSettings;
import com.badbones69.crazyenchantments.paper.utilities.misc.EventUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import java.util.Calendar;

public class AllyEnchantments implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    // Settings.
    private final EnchantmentSettings enchantmentSettings = starter.getEnchantmentSettings();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    // Plugin Managers.
    private final AllyManager allyManager = starter.getAllyManager();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAllySpawn(EntityDamageByEntityEvent event) {
        if (EventUtils.isIgnoredEvent(event)) return;

        if (event.getEntity() instanceof Player player && event.getDamager() instanceof LivingEntity enemy) { // Player gets attacked
            if (!inCoolDown(player)) {
                for (ItemStack item : player.getEquipment().getArmorContents()) {
                    // Spawn allies when getting attacked.
                    if (enchantmentBookSettings.hasEnchantments(item) && enemy instanceof Player) {

                        checkAllyType(enemy, player, item);

                        if (crazyManager.hasEnchantment(item, CEnchantments.BEEKEEPER)) {
                            int power = crazyManager.getLevel(item, CEnchantments.BEEKEEPER);
                            spawnAllies(player, enemy, AllyType.BEE, power);
                        }

                        checkEnchantment(enemy, player, item);
                    }
                }
            } else {
                allyManager.setEnemy(player, enemy);
            }
        }

        if (event.getEntity() instanceof LivingEntity enemy && event.getDamager() instanceof Player player) { // Player attacks
            // If the player is trying to hurt their own ally stop the damage.
            if (allyManager.isAlly(player, enemy)) {
                event.setCancelled(true);
                return;
            }

            if (inCoolDown(player)) {
                allyManager.setEnemy(player, enemy);
                return;
            }

            for (ItemStack item : player.getEquipment().getArmorContents()) {
                // Spawn allies when attacking
                if (enchantmentBookSettings.hasEnchantments(item)) {
                    checkAllyType(enemy, player, item);

                    if (enemy instanceof Player) checkEnchantment(enemy, player, item);
                }
            }
        }
    }

    private void checkAllyType(LivingEntity enemy, Player player, ItemStack item) {
        if (crazyManager.hasEnchantment(item, CEnchantments.TAMER)) {
            int power = crazyManager.getLevel(item, CEnchantments.TAMER);
            spawnAllies(player, enemy, AllyType.WOLF, power);
        }

        if (crazyManager.hasEnchantment(item, CEnchantments.GUARDS)) {
            int power = crazyManager.getLevel(item, CEnchantments.GUARDS);
            spawnAllies(player, enemy, AllyType.IRON_GOLEM, power);
        }
    }

    private void checkEnchantment(LivingEntity enemy, Player player, ItemStack item) {
        if (crazyManager.hasEnchantment(item, CEnchantments.NECROMANCER)) {
            int power = crazyManager.getLevel(item, CEnchantments.NECROMANCER);

            spawnAllies(player, enemy, AllyType.ZOMBIE, power * 2);
        }

        if (crazyManager.hasEnchantment(item, CEnchantments.INFESTATION)) {
            int power = crazyManager.getLevel(item, CEnchantments.INFESTATION);

            spawnAllies(player, enemy, AllyType.ENDERMITE, power * 3);
            spawnAllies(player, enemy, AllyType.SILVERFISH, power * 3);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAllyTarget(EntityTargetEvent event) {
        if (!(event.getTarget() instanceof Player player)) return;

        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        if (!allyManager.isAlly(player, entity)) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAllyDeath(EntityDeathEvent event) {
        if (!allyManager.isAllyMob(event.getEntity())) return;

        event.setDroppedExp(0);
        event.getDrops().clear();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAllyDespawn(ChunkUnloadEvent event) {
        if (event.getChunk().getEntities().length < 1) return;

        for (Entity entity : event.getChunk().getEntities()) {
            if (!(entity instanceof LivingEntity livingEntity)) continue;

            if (allyManager.isAllyMob(livingEntity)) allyManager.getAllyMob(livingEntity).forceRemoveAlly();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLeave(PlayerQuitEvent event) {
        allyManager.forceRemoveAllies(event.getPlayer());
    }

    private void spawnAllies(Player player, LivingEntity enemy, AllyType allyType, int amount) {
        Calendar coolDown = Calendar.getInstance();
        coolDown.add(Calendar.MINUTE, 2);

        enchantmentSettings.addAllyCoolDown(player, coolDown);

        for (int i = 0; i < amount; i++) {
            AllyMob ally = new AllyMob(player, allyType);
            ally.spawnAlly(60);
            ally.attackEnemy(enemy);
        }
    }

    private boolean inCoolDown(Player player) {
        if (enchantmentSettings.containsAllyPlayer(player)) {
            // Right now is before the player's cool-down ends.
            if (Calendar.getInstance().before(enchantmentSettings.getAllyPlayer(player))) return true;

            // Remove the player because their cool-down is over.
            enchantmentSettings.removeAllyCoolDown(player);
        }

        return false;
    }
}
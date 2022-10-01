package com.badbones69.crazyenchantments.enchantments;

import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.managers.AllyManager;
import com.badbones69.crazyenchantments.api.objects.AllyMob;
import com.badbones69.crazyenchantments.api.objects.AllyMob.AllyType;
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
import java.util.HashMap;
import java.util.UUID;

public class AllyEnchantments implements Listener {

    private static final AllyManager allyManager = AllyManager.getInstance();
    private final CrazyManager crazyManager = CrazyManager.getInstance();
    private final HashMap<UUID, Calendar> allyCoolDown = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAllySpawn(EntityDamageByEntityEvent e) {
        if (crazyManager.isIgnoredEvent(e)) return;

        if (e.getEntity() instanceof Player player && e.getDamager() instanceof LivingEntity enemy) { // Player gets attacked
            if (!inCoolDown(player)) {
                for (ItemStack item : player.getEquipment().getArmorContents()) {
                    // Spawn allies when getting attacked
                    if (crazyManager.hasEnchantments(item) && enemy instanceof Player) {

                        if (crazyManager.hasEnchantment(item, CEnchantments.TAMER)) {
                            int power = crazyManager.getLevel(item, CEnchantments.TAMER);
                            spawnAllies(player, enemy, AllyType.WOLF, power);
                        }

                        if (crazyManager.hasEnchantment(item, CEnchantments.GUARDS)) {
                            int power = crazyManager.getLevel(item, CEnchantments.GUARDS);
                            spawnAllies(player, enemy, AllyType.IRON_GOLEM, power);
                        }

                        if (crazyManager.hasEnchantment(item, CEnchantments.BEEKEEPER)) {
                            int power = crazyManager.getLevel(item, CEnchantments.BEEKEEPER);
                            spawnAllies(player, enemy, AllyType.BEE, power);
                        }

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
                }
            } else {
                allyManager.setEnemy(player, enemy);
            }
        }

        if (e.getEntity() instanceof LivingEntity enemy && e.getDamager() instanceof Player player) { // Player attacks
            // If the player is trying to hurt their own ally stop the damage.
            if (allyManager.isAlly(player, enemy)) {
                e.setCancelled(true);
                return;
            }
            if (inCoolDown(player)) {
                allyManager.setEnemy(player, enemy);
                return;
            }
            for (ItemStack item : player.getEquipment().getArmorContents()) {
                // Spawn allies when attacking
                if (crazyManager.hasEnchantments(item)) {
                    if (crazyManager.hasEnchantment(item, CEnchantments.TAMER)) {
                        int power = crazyManager.getLevel(item, CEnchantments.TAMER);
                        spawnAllies(player, enemy, AllyType.WOLF, power);
                    }

                    if (crazyManager.hasEnchantment(item, CEnchantments.GUARDS)) {
                        int power = crazyManager.getLevel(item, CEnchantments.GUARDS);
                        spawnAllies(player, enemy, AllyType.IRON_GOLEM, power);
                    }

                    if (enemy instanceof Player) {
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
                }
            }
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
            if (allyManager.isAllyMob(livingEntity)) {
                allyManager.getAllyMob(livingEntity).forceRemoveAlly();
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLeave(PlayerQuitEvent e) {
        allyManager.forceRemoveAllies(e.getPlayer());
    }

    private void spawnAllies(Player player, LivingEntity enemy, AllyType allyType, int amount) {
        Calendar coolDown = Calendar.getInstance();
        coolDown.add(Calendar.MINUTE, 2);
        allyCoolDown.put(player.getUniqueId(), coolDown);

        for (int i = 0; i < amount; i++) {
            AllyMob ally = new AllyMob(player, allyType);
            ally.spawnAlly(60);
            ally.attackEnemy(enemy);
        }
    }

    private boolean inCoolDown(Player player) {
        if (allyCoolDown.containsKey(player.getUniqueId())) {
            // Right now is before the player's cooldown ends.
            if (Calendar.getInstance().before(allyCoolDown.get(player.getUniqueId()))) {
                return true;
            }

            // Remove the player because their cooldown is over.
            allyCoolDown.remove(player.getUniqueId());
        }
        return false;
    }
}
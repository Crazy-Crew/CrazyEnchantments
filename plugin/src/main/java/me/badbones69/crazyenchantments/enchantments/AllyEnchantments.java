package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.managers.AllyManager;
import me.badbones69.crazyenchantments.api.objects.AllyMob;
import me.badbones69.crazyenchantments.api.objects.AllyMob.AllyType;
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
    
    private static AllyManager allyManager = AllyManager.getInstance();
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private HashMap<UUID, Calendar> allyCoolDown = new HashMap<>();
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAllySpawn(EntityDamageByEntityEvent e) {
        if (!e.isCancelled() && !ce.isIgnoredEvent(e)) {
            Calendar rightNow = Calendar.getInstance();
            if (e.getEntity() instanceof Player && e.getDamager() instanceof LivingEntity) {// Player gets attacked
                Player player = (Player) e.getEntity();
                LivingEntity enemy = (LivingEntity) e.getDamager();
                if (!inCoolDown(player)) {
                    for (ItemStack item : player.getEquipment().getArmorContents()) {
                        // Spawn allies when getting attacked
                        if (ce.hasEnchantments(item)) {
                            if (ce.hasEnchantment(item, CEnchantments.TAMER)) {
                                int power = ce.getLevel(item, CEnchantments.TAMER);
                                spawnAllies(player, enemy, AllyType.WOLF, power);
                            }
                            if (ce.hasEnchantment(item, CEnchantments.GUARDS)) {
                                int power = ce.getLevel(item, CEnchantments.GUARDS);
                                spawnAllies(player, enemy, AllyType.IRON_GOLEM, power);
                            }
                            if (ce.hasEnchantment(item, CEnchantments.BEEKEEPER)) {
                                int power = ce.getLevel(item, CEnchantments.BEEKEEPER);
                                spawnAllies(player, enemy, AllyType.BEE, power);
                            }
                            if (enemy instanceof Player) {
                                if (ce.hasEnchantment(item, CEnchantments.NECROMANCER)) {
                                    int power = ce.getLevel(item, CEnchantments.NECROMANCER);
                                    spawnAllies(player, enemy, AllyType.ZOMBIE, power * 2);
                                }
                                if (ce.hasEnchantment(item, CEnchantments.INFESTATION)) {
                                    int power = ce.getLevel(item, CEnchantments.INFESTATION);
                                    spawnAllies(player, enemy, AllyType.ENDERMITE, power * 3);
                                    spawnAllies(player, enemy, AllyType.SILVERFISH, power * 3);
                                }
                            }
                        }
                    }
                } else {
                    allyManager.setEnemy(player, enemy);
                }
            }
            if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Player) {// Player attacks
                Player player = (Player) e.getDamager();
                LivingEntity enemy = (LivingEntity) e.getEntity();
                //If the player is trying to hurt their own ally stop the damage.
                if (allyManager.isAlly(player, enemy)) {
                    e.setCancelled(true);
                    return;
                }
                if (!inCoolDown(player)) {
                    for (ItemStack item : player.getEquipment().getArmorContents()) {
                        // Spawn allies when attacking
                        if (ce.hasEnchantments(item)) {
                            if (ce.hasEnchantment(item, CEnchantments.TAMER)) {
                                int power = ce.getLevel(item, CEnchantments.TAMER);
                                spawnAllies(player, enemy, AllyType.WOLF, power);
                            }
                            if (ce.hasEnchantment(item, CEnchantments.GUARDS)) {
                                int power = ce.getLevel(item, CEnchantments.GUARDS);
                                spawnAllies(player, enemy, AllyType.IRON_GOLEM, power);
                            }
                            if (enemy instanceof Player) {
                                if (ce.hasEnchantment(item, CEnchantments.NECROMANCER)) {
                                    int power = ce.getLevel(item, CEnchantments.NECROMANCER);
                                    spawnAllies(player, enemy, AllyType.ZOMBIE, power * 2);
                                }
                                if (ce.hasEnchantment(item, CEnchantments.INFESTATION)) {
                                    int power = ce.getLevel(item, CEnchantments.INFESTATION);
                                    spawnAllies(player, enemy, AllyType.ENDERMITE, power * 3);
                                    spawnAllies(player, enemy, AllyType.SILVERFISH, power * 3);
                                }
                            }
                        }
                    }
                } else {
                    allyManager.setEnemy(player, enemy);
                }
            }
        }
    }
    
    @EventHandler
    public void onAllyTarget(EntityTargetEvent e) {
        if (e.getTarget() instanceof Player && e.getEntity() instanceof LivingEntity) {
            if (allyManager.isAlly((Player) e.getTarget(), (LivingEntity) e.getEntity())) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onAllyDeath(EntityDeathEvent e) {
        if (allyManager.isAllyMob(e.getEntity())) {
            e.setDroppedExp(0);
            e.getDrops().clear();
        }
    }
    
    @EventHandler
    public void onAllyDespawn(ChunkUnloadEvent e) {
        if (e.getChunk().getEntities().length > 0) {
            for (Entity entity : e.getChunk().getEntities()) {
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    if (allyManager.isAllyMob(livingEntity)) {
                        allyManager.getAllyMob(livingEntity).forceRemoveAlly();
                    }
                }
            }
        }
    }
    
    @EventHandler
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
            //Right now is before the player's cooldown ends.
            if (Calendar.getInstance().before(allyCoolDown.get(player.getUniqueId()))) {
                return true;
            }
            //Remove the player because their cooldown is over.
            allyCoolDown.remove(player.getUniqueId());
        }
        return false;
    }
}
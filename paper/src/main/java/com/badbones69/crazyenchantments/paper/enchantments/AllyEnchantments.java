package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.PluginSupport;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.managers.AllyManager;
import com.badbones69.crazyenchantments.paper.api.objects.AllyMob;
import com.badbones69.crazyenchantments.paper.api.objects.AllyMob.AllyType;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.api.utils.EventUtils;
import com.gmail.nossr50.party.PartyManager;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AllyEnchantments implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = CrazyEnchantments.get();

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    // Settings.
    @NotNull
    private final EnchantmentBookSettings bookSettings = this.starter.getEnchantmentBookSettings();

    // Plugin Managers.
    @NotNull
    private final AllyManager allyManager = this.starter.getAllyManager();

    private final HashMap<UUID, Calendar> allyCoolDown = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAllySpawn(EntityDamageByEntityEvent event) {
        if (EventUtils.isIgnoredEvent(event)) return;

        if (event.getEntity() instanceof Player player && event.getDamager() instanceof LivingEntity enemy) { // Player gets attacked
            if (!inCoolDown(player)) {
                if (enemy instanceof Player) { // Checks armor and spawn allies when getting attacked.
                    Arrays.stream(player.getEquipment().getArmorContents()).map(this.bookSettings::getEnchantments).filter(enchants -> !enchants.isEmpty()).forEach(enchants -> checkAllyType(enemy, player, enchants));
                }
            } else {
                this.allyManager.setEnemy(player, enemy);
            }
        }

        if (event.getEntity() instanceof LivingEntity enemy && event.getDamager() instanceof Player player) { // Player attacks
            // If the player is trying to hurt their own ally stop the damage.
            if (this.allyManager.isAlly(player, enemy)) {
                event.setCancelled(true);
                return;
            }
            if (inCoolDown(player)) {
                this.allyManager.setEnemy(player, enemy);
                return;
            }

            // Checks armor and spawns allies when attacking
            Arrays.stream(player.getEquipment().getArmorContents()).map(this.bookSettings::getEnchantments).filter(enchants -> !enchants.isEmpty()).forEach(enchants -> checkAllyType(enemy, player, enchants));
        }
    }

    private void checkAllyType(LivingEntity enemy, Player player, Map<CEnchantment, Integer> enchants) {
        if (enchants.containsKey(CEnchantments.TAMER.getEnchantment())) {
            int power = enchants.get(CEnchantments.TAMER.getEnchantment());
            spawnAllies(player, enemy, AllyType.WOLF, power);
        }

        if (enchants.containsKey(CEnchantments.GUARDS.getEnchantment())) {
            int power = enchants.get(CEnchantments.GUARDS.getEnchantment());
            spawnAllies(player, enemy, AllyType.IRON_GOLEM, power);
        }

        if (enchants.containsKey(CEnchantments.BEEKEEPER.getEnchantment())) {
            int power = enchants.get(CEnchantments.BEEKEEPER.getEnchantment());
            spawnAllies(player, enemy, AllyType.BEE, power);
        }

        if (enchants.containsKey(CEnchantments.NECROMANCER.getEnchantment())) {
            int power = enchants.get(CEnchantments.NECROMANCER.getEnchantment());

            spawnAllies(player, enemy, AllyType.ZOMBIE, power * 2);
        }

        if (enchants.containsKey(CEnchantments.INFESTATION.getEnchantment())) {
            int power = enchants.get(CEnchantments.INFESTATION.getEnchantment());

            spawnAllies(player, enemy, AllyType.ENDERMITE, power * 3);
            spawnAllies(player, enemy, AllyType.SILVERFISH, power * 3);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAllyTarget(EntityTargetEvent event) {
        if (event.getTarget() == null) return; // For when the entity forgets.
        AllyMob allyMob = this.allyManager.getAllyMob(event.getEntity());
        AllyMob target = this.allyManager.getAllyMob(event.getTarget());

        // Stop ally mob from attacking other mobs owned by the player.
        if (allyMob != null && target != null && allyMob.getOwner().getUniqueId() == target.getOwner().getUniqueId()) {
            event.setCancelled(true);
            return;
        }

        // Stop ally from targeting party members.
        if (allyMob != null && target instanceof Player && PluginSupport.SupportedPlugins.MCMMO.isPluginLoaded()) {
            PartyManager.inSameParty(allyMob.getOwner(), (Player) target);
        }

        // Stop your pets from targeting you.
        if (event.getTarget() instanceof Player player && allyMob != null) {
            if (player.getUniqueId() == allyMob.getOwner().getUniqueId()) event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAllyDeath(EntityDeathEvent event) {
        if (!this.allyManager.isAllyMob(event.getEntity())) return;

        event.setDroppedExp(0);
        event.getDrops().clear();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAllyDespawn(ChunkUnloadEvent event) {
        if (event.getChunk().getEntities().length < 1) return;

        for (Entity entity : event.getChunk().getEntities()) {
            if (!(entity instanceof LivingEntity livingEntity)) continue;

            if (this.allyManager.isAllyMob(livingEntity)) this.allyManager.getAllyMob(livingEntity).forceRemoveAlly();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLeave(PlayerQuitEvent event) {
        this.allyManager.forceRemoveAllies(event.getPlayer());
    }

    private void spawnAllies(Player player, LivingEntity enemy, AllyType allyType, int amount) {
        Calendar coolDown = Calendar.getInstance();
        coolDown.add(Calendar.MINUTE, 2);

        this.allyCoolDown.put(player.getUniqueId(), coolDown);

        for (int i = 0; i < amount; i++) {
            AllyMob ally = new AllyMob(player, allyType);
            ally.spawnAlly(60);
            ally.attackEnemy(enemy);
        }
    }

    private boolean inCoolDown(Player player) {
        if (this.allyCoolDown.containsKey(player.getUniqueId())) {
            // Right now is before the player's cool-down ends.
            if (Calendar.getInstance().before(this.allyCoolDown.get(player.getUniqueId()))) return true;

            // Remove the player because their cool-down is over.
            this.allyCoolDown.remove(player.getUniqueId());
        }

        return false;
    }
}
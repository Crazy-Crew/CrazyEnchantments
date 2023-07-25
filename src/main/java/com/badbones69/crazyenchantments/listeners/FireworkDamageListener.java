package com.badbones69.crazyenchantments.listeners;

import com.badbones69.crazyenchantments.api.enums.pdc.DataKeys;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class FireworkDamageListener implements Listener {

    /**
     * @param firework The firework you want to add.
     */
    public void addFirework(Entity firework) {
        PersistentDataContainer container = firework.getPersistentDataContainer();

        container.set(DataKeys.NO_FIREWORK_DAMAGE.getKey(), PersistentDataType.BOOLEAN, true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Firework firework) {

            PersistentDataContainer container = firework.getPersistentDataContainer();

            if (container.has(DataKeys.NO_FIREWORK_DAMAGE.getKey(), PersistentDataType.BOOLEAN)) e.setCancelled(true);
        }
    }
}
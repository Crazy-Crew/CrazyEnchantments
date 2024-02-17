package com.badbones69.crazyenchantments.paper.api.utils;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EventUtils {
    
    private static final Set<Event> ignoredEvents = new HashSet<>();
    private static final Set<UUID> ignoredUUIDs = new HashSet<>();
    
    public static Set<Event> getIgnoredEvents() {
        return ignoredEvents;
    }

    public static boolean isIgnoredEvent(Event event) {
        return ignoredEvents.contains(event);
    }

    public static void addIgnoredEvent(Event event) {
        ignoredEvents.add(event);
    }

    public static void removeIgnoredUUID(UUID uuid) {
        ignoredUUIDs.remove(uuid);
    }

    public static Set<UUID> getIgnoredUUIDs() {
        return ignoredUUIDs;
    }

    public static boolean isIgnoredUUID(UUID uuid) {
        return ignoredUUIDs.contains(uuid);
    }

    public static void addIgnoredUUID(UUID uuid) {
        ignoredUUIDs.add(uuid);
    }

    public static void removeIgnoredEvent(Event event) {
        ignoredEvents.remove(event);
    }

    public static boolean containsDrop(EntityDeathEvent event, Material material) {
        boolean hasDroppedMat = true;

        if (material != null && !material.isAir()) {
            hasDroppedMat = false;

            for (ItemStack drop : event.getDrops()) {
                if (drop.getType() == material) {
                    hasDroppedMat = true;
                    break;
                }
            }
        }

        return hasDroppedMat;
    }
}
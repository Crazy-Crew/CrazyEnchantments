package com.badbones69.crazyenchantments.paper.api.utils;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EventUtils {
    
    private static final Set<Event> ignoredEvents = new HashSet<>();
    private static final Set<UUID> ignoredUUIDs = new HashSet<>();
    
    public static Set<Event> getIgnoredEvents() {
        return ignoredEvents;
    }

    public static boolean isIgnoredEvent(@NotNull final Event event) {
        return ignoredEvents.contains(event);
    }

    public static void addIgnoredEvent(@NotNull final Event event) {
        ignoredEvents.add(event);
    }

    public static void removeIgnoredUUID(@NotNull final UUID uuid) {
        ignoredUUIDs.remove(uuid);
    }

    public static Set<UUID> getIgnoredUUIDs() {
        return ignoredUUIDs;
    }

    public static boolean isIgnoredUUID(@NotNull final UUID uuid) {
        return ignoredUUIDs.contains(uuid);
    }

    public static void addIgnoredUUID(@NotNull final UUID uuid) {
        ignoredUUIDs.add(uuid);
    }

    public static void removeIgnoredEvent(@NotNull final Event event) {
        ignoredEvents.remove(event);
    }

    public static boolean containsDrop(@NotNull final EntityDeathEvent event, @NotNull final Material material) {
        boolean hasDroppedMat = true;

        if (!material.isAir()) {
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
package com.badbones69.crazyenchantments.paper.utilities.misc;

import org.bukkit.event.Event;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventUtils {
    
    private static final List<Event> ignoredEvents = new ArrayList<>();
    private static final List<UUID> ignoredUUIDs = new ArrayList<>();
    
    public static List<Event> getIgnoredEvents() {
        return ignoredEvents;
    }

    public static boolean isIgnoredEvent(Event event) {
        return ignoredEvents.contains(event);
    }

    public static void addIgnoredEvent(Event event) {
        if (!ignoredEvents.contains(event)) ignoredEvents.add(event);
    }

    public static void removeIgnoredUUID(UUID uuid) {
        ignoredUUIDs.remove(uuid);
    }

    public static List<UUID> getIgnoredUUIDs() {
        return ignoredUUIDs;
    }

    public static boolean isIgnoredUUID(UUID uuid) {
        return ignoredUUIDs.contains(uuid);
    }

    public static void addIgnoredUUID(UUID uuid) {
        if (!ignoredUUIDs.contains(uuid)) ignoredUUIDs.add(uuid);
    }

    public static void removeIgnoredEvent(Event event) {
        ignoredEvents.remove(event);
    }
}
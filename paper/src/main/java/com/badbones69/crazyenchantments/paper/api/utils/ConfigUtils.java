package com.badbones69.crazyenchantments.paper.api.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class ConfigUtils {

    public static List<String> getStringList(@NotNull final ConfigurationSection configuration, @NotNull final String path) {
        return getStringList(configuration, List.of(), path);
    }

    public static List<String> getStringList(@NotNull final ConfigurationSection configuration, @NotNull final List<String> defaultValue, @NotNull final String path) {
        if (configuration.contains(path)) {
            return configuration.getStringList(path);
        }

        return defaultValue;
    }
}
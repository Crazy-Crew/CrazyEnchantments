package com.badbones69.crazyenchantments.paper.api.utils;

import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import java.util.List;

public class ConfigUtils {

    public static List<String> getStringList(@NotNull final BasicConfigurationNode configurationNode, @NotNull final Object... path) {
        return getStringList(configurationNode, List.of(), path);
    }

    public static List<String> getStringList(@NotNull final BasicConfigurationNode configurationNode, @NotNull final List<String> defaultValue, @NotNull final Object... path) {
        final BasicConfigurationNode node = configurationNode.node(path);

        try {
            final List<String> list = node.getList(String.class);

            if (list != null) {
                return list;
            }

            return defaultValue;
        } catch (final SerializationException exception) {
            throw new FusionException(String.format("Failed to serialize %s!", node.path()), exception);
        }
    }

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
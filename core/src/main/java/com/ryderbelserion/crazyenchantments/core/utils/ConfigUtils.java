package com.ryderbelserion.crazyenchantments.core.utils;

import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import java.util.List;

public class ConfigUtils {

    public static List<String> getStringList(@NotNull final CommentedConfigurationNode configurationNode, @NotNull final Object... path) {
        return getStringList(configurationNode, List.of(), path);
    }

    public static List<String> getStringList(@NotNull final CommentedConfigurationNode configurationNode, @NotNull final List<String> defaultValue, @NotNull final Object... path) {
        final CommentedConfigurationNode node = configurationNode.node(path);

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
}
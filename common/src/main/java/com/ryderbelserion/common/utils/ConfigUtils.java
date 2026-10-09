package com.ryderbelserion.common.utils;

import com.ryderbelserion.fusion.api.FusionProvider;
import com.ryderbelserion.fusion.api.enums.Level;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class ConfigUtils {

    private static final FusionKyori fusion = (FusionKyori) FusionProvider.api();

    public static CommentedConfigurationNode getNode(final CommentedConfigurationNode parent, final String path) {
        return parent.node(getString(path));
    }

    public static void setNode(final CommentedConfigurationNode parent, final String path, final Class<?> type, final Object value) {
        try {
            parent.node(getString(path)).set(type, value);
        } catch (final SerializationException exception) {
            fusion.log(Level.error, "Failed to set node @ %s path using class type %s with value %s", exception, path, type, value);
        }
    }

    public static void setNode(final CommentedConfigurationNode parent, final Class<?> type, final Object value, final Object... path) {
        try {
            parent.node(path).set(type, value);
        } catch (final SerializationException exception) {
            fusion.log(Level.error, "Failed to set node @ %s path using class type %s with value %s", exception, path, type, value);
        }
    }

    public static Object[] getString(final String path) {
        return path.split("\\.");
    }
}
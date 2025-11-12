package com.ryderbelserion.crazyenchantments.api;

import com.ryderbelserion.crazyenchantments.api.interfaces.platform.ICrazyEnchantments;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class CrazyEnchantmentsProvider {

    @ApiStatus.Internal
    private CrazyEnchantmentsProvider() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static ICrazyEnchantments instance;

    @ApiStatus.Internal
    public static ICrazyEnchantments getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ChatManager API has not been initialized yet");
        }

        return instance;
    }

    @ApiStatus.Internal
    public static void register(@NotNull final ICrazyEnchantments instance) {
        CrazyEnchantmentsProvider.instance = instance;
    }

    @ApiStatus.Internal
    public static void unregister() {
        CrazyEnchantmentsProvider.instance = null;
    }
}
package us.crazycrew.crazyenchantments;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ICrazyProvider {

    private static @Nullable ICrazyEnchantments instance;

    @ApiStatus.Internal
    public static void register(@NotNull final ICrazyEnchantments instance) {
        ICrazyProvider.instance = instance;
    }

    @ApiStatus.Internal
    public static void unregister() {
        ICrazyProvider.instance = null;
    }

    public static @NotNull ICrazyEnchantments getInstance() {
        if (instance == null) throw new IllegalStateException("CrazyEnchantments API is not yet initialized.");

        return instance;
    }
    
}
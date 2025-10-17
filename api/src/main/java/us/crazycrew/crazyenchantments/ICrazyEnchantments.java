package us.crazycrew.crazyenchantments;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazyenchantments.enums.Mode;
import us.crazycrew.crazyenchantments.exceptions.CrazyException;
import us.crazycrew.crazyenchantments.interfaces.registry.IMessageRegistry;
import us.crazycrew.crazyenchantments.interfaces.registry.IUserRegistry;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class ICrazyEnchantments {

    public static final String namespace = "crazyenchantments";

    public static <T extends ICrazyEnchantments> T getInstance(@NotNull final Class<T> classObject) {
        if (!ICrazyEnchantments.class.isAssignableFrom(classObject)) {
            throw new CrazyException("This class is not assignable, as it's not a superclass, or things similar.");
        }

        return classObject.cast(ICrazyEnchantments.class);
    }

    public abstract boolean hasPermission(@NotNull final Audience audience, @NotNull final String permission);

    public abstract void registerPermission(@NotNull final Mode mode, @NotNull final String permission, @NotNull final String description, @NotNull final Map<String, Boolean> children);

    public void registerPermission(@NotNull final Mode mode, @NotNull final String permission, @NotNull final String description) {
        registerPermission(mode, permission, description, new HashMap<>());
    }

    public abstract void broadcast(@NotNull final Component component, @NotNull final String permission);

    public void broadcast(@NotNull final Component component) {
        broadcast(component, "");
    }

    public abstract boolean isConsoleSender(@NotNull final Audience audience);

    public abstract IMessageRegistry getMessageRegistry();

    public abstract IUserRegistry getUserRegistry();

    public abstract String getMessageType();

    public abstract Path getDataPath();

    public abstract String getPrefix();

    public abstract void reload(@Nullable final Audience audience);

    public void reload() {
        reload(null);
    }

    public abstract void init();
}
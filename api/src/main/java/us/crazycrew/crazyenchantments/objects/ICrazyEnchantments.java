package us.crazycrew.crazyenchantments.objects;

import com.ryderbelserion.fusion.core.FusionCore;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazyenchantments.enums.Mode;
import us.crazycrew.crazyenchantments.exceptions.CrazyException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class ICrazyEnchantments {

    public static <T extends ICrazyEnchantments> T getInstance(@NotNull final Class<T> classObject) {
        if (!ICrazyEnchantments.class.isAssignableFrom(classObject)) {
            throw new CrazyException("This class is not assignable, as it's not a superclass, or things similar.");
        }

        return classObject.cast(ICrazyEnchantments.class);
    }

    public abstract void registerPermission(@NotNull final Mode mode, @NotNull final String permission, @NotNull final String description, @NotNull final Map<String, Boolean> children);

    public void registerPermission(@NotNull final Mode mode, @NotNull final String permission, @NotNull final String description) {
        registerPermission(mode, permission, description, new HashMap<>());
    }

    public abstract FusionCore getFusion();

    public abstract Path getDataPath();

    public abstract void reload(@Nullable final Audience audience);

    public void reload() {
        reload(null);
    }

    public abstract void init();
}
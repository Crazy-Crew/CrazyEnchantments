package us.crazycrew.crazyenchantments.objects;

import com.ryderbelserion.fusion.core.FusionCore;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenchantments.exceptions.CrazyException;
import java.nio.file.Path;

public abstract class ICrazyEnchantments {

    public static <T extends ICrazyEnchantments> T getInstance(@NotNull final Class<T> classObject) {
        if (!ICrazyEnchantments.class.isAssignableFrom(classObject)) {
            throw new CrazyException("This class is not assignable, as it's not a superclass, or things similar.");
        }

        return classObject.cast(ICrazyEnchantments.class);
    }

    public abstract FusionCore getFusion();

    public abstract Path getDataPath();

}
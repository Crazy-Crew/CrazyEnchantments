package us.crazycrew.crazyenchantments.interfaces;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class IUser {

    public abstract void sendMessage(@NotNull final Key key, @NotNull final Map<String, String> placeholders);

    public void sendMessage(@NotNull final Key key) {
        sendMessage(key, new HashMap<>());
    }

    public abstract boolean hasPermission(@NotNull final String permission);

    public abstract void setLocale(@NotNull final Locale locale);

    public abstract Audience getAudience();

    public abstract String getLocale();
}
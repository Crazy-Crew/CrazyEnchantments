package us.crazycrew.crazyenchantments.interfaces;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public interface IMessage {

    void broadcast(@NotNull final Audience audience, @NotNull final Map<String, String> placeholders);

    default void broadcast(@NotNull final Audience audience) {
        broadcast(audience, new HashMap<>());
    }

    void send(@NotNull final Audience audience, @NotNull final Map<String, String> placeholders);

    default void send(@NotNull final Audience audience) {
        send(audience, new HashMap<>());
    }

    Component getComponent(@NotNull final Audience audience, @NotNull final Map<String, String> placeholders);

    default Component getComponent(@NotNull final Audience audience) {
        return getComponent(audience, new HashMap<>());
    }

    void migrate();
}
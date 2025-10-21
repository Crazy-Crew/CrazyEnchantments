package us.crazycrew.crazyenchantments.interfaces.registry;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public interface IUserRegistry<U> {

    void addUser(@NotNull final Audience audience);

    void removeUser(@NotNull final Audience audience);

    boolean hasUser(@NotNull final UUID uuid);

    @NotNull U getUser(@NotNull final Audience audience);

    @NotNull Map<UUID, U> getUsers();

}
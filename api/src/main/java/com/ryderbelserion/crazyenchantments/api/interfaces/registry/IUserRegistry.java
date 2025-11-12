package com.ryderbelserion.crazyenchantments.api.interfaces.registry;

import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.UUID;

public interface IUserRegistry<P, U> {

    void addUser(@NotNull final P audience);

    void removeUser(@NotNull final P audience);

    boolean hasUser(@NotNull final UUID uuid);

    @NotNull U getUser(@NotNull final P audience);

    @NotNull Map<UUID, U> getUsers();

}
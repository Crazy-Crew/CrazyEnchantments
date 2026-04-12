package com.ryderbelserion.crazyenchantments.api.interfaces.registry;

import com.ryderbelserion.crazyenchantments.api.interfaces.IUser;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.UUID;

public interface IUserRegistry<S> {

    void init();

    IUser addUser(@NotNull final S player);

    IUser removeUser(@NotNull final UUID uuid);

    Optional<? extends IUser> getUser(@NotNull final UUID uuid);

    IUser getConsole();

}
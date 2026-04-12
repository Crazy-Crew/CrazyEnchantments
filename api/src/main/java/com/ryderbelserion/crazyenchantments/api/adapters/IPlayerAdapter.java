package com.ryderbelserion.crazyenchantments.api.adapters;

import com.ryderbelserion.crazyenchantments.api.interfaces.IUser;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

public interface IPlayerAdapter<T> {

    @NotNull Optional<? extends IUser> getUser(@NotNull final T player);

}
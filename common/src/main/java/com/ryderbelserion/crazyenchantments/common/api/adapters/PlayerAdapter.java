package com.ryderbelserion.crazyenchantments.common.api.adapters;

import com.ryderbelserion.crazyenchantments.api.adapters.IPlayerAdapter;
import com.ryderbelserion.crazyenchantments.api.interfaces.IUser;
import com.ryderbelserion.crazyenchantments.api.interfaces.registry.IContextRegistry;
import com.ryderbelserion.crazyenchantments.api.interfaces.registry.IUserRegistry;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

public class PlayerAdapter<P> implements IPlayerAdapter<P> {

    private final IUserRegistry<?> userRegistry;
    private final IContextRegistry<P> contextManager;

    public PlayerAdapter(@NotNull final IUserRegistry<?> userRegistry, @NotNull final IContextRegistry<P> contextManager) {
        this.userRegistry = userRegistry;
        this.contextManager = contextManager;
    }

    @Override
    public @NotNull final Optional<? extends IUser> getUser(@NotNull final P player) {
        return this.userRegistry.getUser(this.contextManager.getUUID(player));
    }
}
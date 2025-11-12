package com.ryderbelserion.crazyenchantments.core.commands.player;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

public abstract class ISource {

    private final Audience audience;

    public ISource(@NotNull final Audience audience) {
        this.audience = audience;
    }

    public abstract boolean hasPermission(@NotNull final String value);

    public @NotNull Audience getAudience() {
        return this.audience;
    }
}
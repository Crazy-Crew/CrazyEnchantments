package com.badbones69.crazyenchantments.commands.player;

import com.badbones69.crazyenchantments.CrazyPlugin;
import com.badbones69.crazyenchantments.registry.UserRegistry;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenchantments.ICrazyProvider;
import us.crazycrew.crazyenchantments.interfaces.IUser;

public abstract class ISource {

    protected final CrazyPlugin plugin = (CrazyPlugin) ICrazyProvider.getInstance();

    protected final UserRegistry userRegistry = this.plugin.getUserRegistry();

    private final Audience audience;

    public ISource(@NotNull final Audience audience) {
        this.audience = audience;
    }

    public abstract boolean hasPermission(@NotNull final String value);

    public @NotNull Audience getAudience() {
        return this.audience;
    }

    public IUser getSender() {
        return this.userRegistry.getUser(this.audience);
    }
}
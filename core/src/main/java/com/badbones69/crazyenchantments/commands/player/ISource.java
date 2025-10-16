package com.badbones69.crazyenchantments.commands.player;

import com.badbones69.crazyenchantments.CrazyPlugin;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenchantments.objects.ICrazyEnchantments;

public abstract class ISource {

    protected final CrazyPlugin plugin = ICrazyEnchantments.getInstance(CrazyPlugin.class);

    private final Audience audience;

    public ISource(@NotNull final Audience audience) {
        this.audience = audience;
    }

    public abstract boolean hasPermission(@NotNull final String value);

    public @NotNull Audience getAudience() {
        return this.audience;
    }

    /*public IUser getSender() {
        return this.userRegistry.getUser(this.audience);
    }*/
}
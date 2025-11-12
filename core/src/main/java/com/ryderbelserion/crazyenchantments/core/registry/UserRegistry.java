package com.ryderbelserion.crazyenchantments.core.registry;

import com.ryderbelserion.crazyenchantments.api.CrazyEnchantmentsProvider;
import com.ryderbelserion.crazyenchantments.api.interfaces.registry.IUserRegistry;
import com.ryderbelserion.crazyenchantments.core.CrazyEnchantments;
import com.ryderbelserion.crazyenchantments.core.objects.User;
import com.ryderbelserion.fusion.core.FusionProvider;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;
import java.util.*;

public class UserRegistry implements IUserRegistry<User> {

    private final CrazyEnchantments plugin = (CrazyEnchantments) CrazyEnchantmentsProvider.getInstance();
    private final FusionKyori fusion = (FusionKyori) FusionProvider.getInstance();

    private final Map<UUID, User> users = new HashMap<>();

    public void init(@NotNull final Audience audience) {
        if (this.plugin.isConsoleSender(audience)) {
            this.users.put(CrazyEnchantments.console, new User(audience));
        }
    }

    @Override
    public void addUser(@NotNull final Audience audience) {
        if (this.plugin.isConsoleSender(audience)) {
            return;
        }

        final Optional<UUID> uuid = audience.get(Identity.UUID);

        uuid.ifPresent(value -> {
            final User user = new User(audience);

            final Optional<Locale> locale = audience.get(Identity.LOCALE);

            locale.ifPresent(user::setLocale);

            this.users.put(value, user);
        });
    }

    @Override
    public void removeUser(@NotNull final Audience audience) {
        if (this.plugin.isConsoleSender(audience)) {
            return;
        }

        final Optional<UUID> uuid = audience.get(Identity.UUID);

        uuid.ifPresent(this.users::remove);
    }

    @Override
    public final boolean hasUser(@NotNull final UUID uuid) {
        return this.users.containsKey(uuid);
    }

    @Override
    public @NotNull final User getUser(@NotNull final Audience audience) {
        if (this.plugin.isConsoleSender(audience)) {
            return this.users.get(CrazyEnchantments.console);
        }

        final Optional<UUID> optional = audience.get(Identity.UUID);

        //noinspection OptionalGetWithoutIsPresent
        return this.users.get(optional.get());
    }

    @Override
    public @NotNull final Map<UUID, User> getUsers() {
        return this.users;
    }
}
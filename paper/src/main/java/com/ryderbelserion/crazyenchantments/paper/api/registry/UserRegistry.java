package com.ryderbelserion.crazyenchantments.paper.api.registry;

import com.ryderbelserion.crazyenchantments.paper.api.objects.User;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UserRegistry {

    private final Map<UUID, User> users = new HashMap<>();

    public void addUser(@NotNull final Audience audience) {
        final Optional<UUID> uuid = audience.get(Identity.UUID);

        uuid.ifPresent(value -> {
            final User user = new User(audience);

            final Optional<Locale> locale = audience.get(Identity.LOCALE);

            locale.ifPresent(user::setLocale);

            this.users.put(value, user);
        });
    }

    public void removeUser(@NotNull final Audience audience) {
        final Optional<UUID> uuid = audience.get(Identity.UUID);

        uuid.ifPresent(this.users::remove);
    }

    public Optional<User> getUser(@NotNull final Audience audience) {
        final Optional<UUID> uuid = audience.get(Identity.UUID);

        return uuid.map(this.users::get);
    }

    public final Map<UUID, User> getUsers() {
        return Collections.unmodifiableMap(this.users);
    }

    @ApiStatus.Internal
    public void purgeUsers() {
        this.users.clear();
    }
}
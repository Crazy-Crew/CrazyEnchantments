package com.ryderbelserion.crazyenchantments.paper.api.registry.adapters;

import com.ryderbelserion.crazyenchantments.api.constants.Messages;
import com.ryderbelserion.crazyenchantments.api.interfaces.IUser;
import com.ryderbelserion.crazyenchantments.common.CEPlugin;
import net.kyori.adventure.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

public class PaperUserAdapter implements IUser {

    protected Player player;
    protected Key locale;

    public PaperUserAdapter(@Nullable final CommandSender sender) {
        if (sender instanceof Player reference) {
            this.player = reference;

            setLocale(reference.locale().toString());
        }
    }

    public PaperUserAdapter() {
        this(null);
    }

    @Override
    public @NotNull final UUID getUniqueId() {
        return this.player == null ? CEPlugin.CONSOLE_UUID : this.player.getUniqueId();
    }

    @Override
    public @NotNull final String getUsername() {
        return this.player == null ? CEPlugin.CONSOLE_NAME : this.player.getName();
    }

    @Override
    public @NotNull final Key getLocaleKey() {
        return this.player == null ? Messages.default_locale : this.locale;
    }

    @Override
    public void setLocale(@NotNull final String locale) {
        final String[] splitter = locale.contains("-") ? locale.split("-") : locale.split("_");

        final String language = splitter[0];
        final String country = splitter[1];

        final String value = "%s_%s.yml".formatted(language, country).toLowerCase();

        if (!value.equalsIgnoreCase("en_us.yml")) {
            this.locale = Key.key(CEPlugin.namespace, value);
        }
    }
}
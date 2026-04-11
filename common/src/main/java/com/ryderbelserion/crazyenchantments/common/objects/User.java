package com.ryderbelserion.crazyenchantments.common.objects;

import com.ryderbelserion.crazyenchantments.api.CrazyEnchantmentsProvider;
import com.ryderbelserion.crazyenchantments.api.interfaces.IUser;
import com.ryderbelserion.crazyenchantments.common.CrazyEnchantments;
import com.ryderbelserion.crazyenchantments.common.enums.constants.Messages;
import com.ryderbelserion.crazyenchantments.common.registry.MessageRegistry;
import com.ryderbelserion.fusion.core.api.FusionProvider;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import java.util.Locale;
import java.util.Map;

public class User extends IUser {

    private final CrazyEnchantments plugin = (CrazyEnchantments) CrazyEnchantmentsProvider.getInstance();
    private final FusionKyori fusion = (FusionKyori) FusionProvider.getInstance();

    private final MessageRegistry messageRegistry = this.plugin.getMessageRegistry();

    private final Audience audience;

    public User(@NotNull final Audience audience) {
        this.audience = audience;
    }

    private Key locale = Messages.default_locale;

    @Override
    public Component getComponent(@NotNull final Key key, @NotNull final Map<String, String> placeholders) {
        return this.messageRegistry.getMessage(getLocale(), key).getComponent(getAudience(), placeholders);
    }

    @Override
    public void sendMessage(@NotNull final Key key, @NotNull final Map<String, String> placeholders) {
        this.messageRegistry.getMessage(getLocale(), key).send(getAudience(), placeholders);
    }

    @Override
    public final boolean hasPermission(@NotNull final String permission) {
        return this.plugin.hasPermission(getAudience(), permission);
    }

    @Override
    public void setLocale(@NotNull final Locale locale) {
        final String country = locale.getCountry();
        final String language = locale.getLanguage();

        final String value = "%s_%s.yml".formatted(language, country).toLowerCase();

        if (!value.equalsIgnoreCase("en_us.yml")) {
            this.locale = Key.key(CrazyEnchantments.namespace, value);
        }

        this.fusion.log(Level.WARNING, "Locale Debug: Country: %s, Language: %s", country, language);
    }

    @Override
    public @NotNull final Audience getAudience() {
        return this.audience;
    }

    @Override
    public @NotNull final Key getLocale() {
        return this.locale;
    }
}
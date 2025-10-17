package com.badbones69.crazyenchantments.objects;

import com.badbones69.crazyenchantments.CrazyPlugin;
import com.badbones69.crazyenchantments.registry.MessageRegistry;
import com.ryderbelserion.fusion.core.FusionCore;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenchantments.constants.MessageKeys;
import us.crazycrew.crazyenchantments.interfaces.IUser;
import us.crazycrew.crazyenchantments.ICrazyEnchantments;
import java.util.Locale;
import java.util.Map;

public class User extends IUser {

    private final CrazyPlugin plugin = ICrazyEnchantments.getInstance(CrazyPlugin.class);

    private final MessageRegistry messageRegistry = this.plugin.getMessageRegistry();

    private final FusionCore fusion = this.plugin.getFusion();

    private final Audience audience;

    public User(@NotNull final Audience audience) {
        this.audience = audience;
    }

    private Key locale = MessageKeys.default_locale;

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

        this.locale = Key.key(ICrazyEnchantments.namespace, String.format("%s-%s.yml", language, country));

        this.fusion.log("warn", "Locale Debug: Country: {}, Language: {}", country, language);
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
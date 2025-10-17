package com.badbones69.crazyenchantments.objects;

import com.badbones69.crazyenchantments.CrazyPlugin;
import com.badbones69.crazyenchantments.registry.MessageRegistry;
import com.ryderbelserion.fusion.core.FusionCore;
import com.ryderbelserion.fusion.core.files.FileManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenchantments.interfaces.IUser;
import us.crazycrew.crazyenchantments.ICrazyEnchantments;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

public class User extends IUser {

    private final CrazyPlugin plugin = ICrazyEnchantments.getInstance(CrazyPlugin.class);

    private final MessageRegistry messageRegistry = this.plugin.getMessageRegistry();

    private final FusionCore fusion = this.plugin.getFusion();

    private final FileManager fileManager = this.fusion.getFileManager();

    private final Path path = this.fusion.getDataPath();

    private final Audience audience;

    public User(@NotNull final Audience audience) {
        this.audience = audience;
    }

    private String locale = "en-US";

    @Override
    public void sendMessage(@NotNull final Key key, @NotNull final Map<String, String> placeholders) {
        this.messageRegistry.getMessage(getLocaleKey(), key).send(getAudience(), placeholders);
    }

    @Override
    public final boolean hasPermission(@NotNull final String permission) {
        return this.plugin.hasPermission(getAudience(), permission);
    }

    @Override
    public void setLocale(@NotNull final Locale locale) {
        final String country = locale.getCountry();
        final String language = locale.getLanguage();

        this.locale = String.format("%s-%s", language, country);

        this.fusion.log("warn", "Locale Debug: Country: {}, Language: {}", country, language);
    }

    @Override
    public @NotNull final Audience getAudience() {
        return this.audience;
    }

    public @NotNull final Key getLocaleKey() {
        final String locale = "%s.yml".formatted(getLocale());

        return Key.key(ICrazyEnchantments.namespace, locale);
    }

    @Override
    public @NotNull final String getLocale() {
        return this.locale;
    }
}
package com.ryderbelserion.crazyenchantments.api.interfaces;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.Locale;

public interface IUser {

    void setLocale(@NotNull final Locale locale);

    Audience getAudience();

    CommentedConfigurationNode locale();

    String getLocale();

}
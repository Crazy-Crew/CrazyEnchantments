package com.ryderbelserion.crazyenchantments.paper.api.interfaces;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

public interface IMessage {

    void broadcast(@NotNull final Audience audience, @NotNull final Map<String, String> placeholders);

    void broadcast(@NotNull final Audience audience);

    void send(@NotNull final Audience audience, @NotNull final Map<String, String> placeholders);

    void send(@NotNull final Audience audience);

}
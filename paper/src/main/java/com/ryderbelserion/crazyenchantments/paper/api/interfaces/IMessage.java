package com.ryderbelserion.crazyenchantments.paper.api.interfaces;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

public interface IMessage {

    void broadcast(@NotNull Audience audience);

    void send(@NotNull final Audience audience);

}
package com.ryderbelserion.crazyenchantments.api.interfaces.platform;

import com.ryderbelserion.crazyenchantments.api.interfaces.registry.IMessageRegistry;
import com.ryderbelserion.crazyenchantments.api.interfaces.registry.IUserRegistry;
import com.ryderbelserion.fusion.core.files.FileManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface ICrazyEnchantments {

    @NotNull <P, U> IUserRegistry<P, U> getUserRegistry();

    @NotNull IMessageRegistry getMessageRegistry();

    @NotNull FileManager getFileManager();

    boolean isConsoleSender(@NotNull final Audience audience);

    void broadcast(@NotNull final Component component, @NotNull final String permission);

    void broadcast(@NotNull final Component component);

    void start();

    void reload();

    void stop();

}
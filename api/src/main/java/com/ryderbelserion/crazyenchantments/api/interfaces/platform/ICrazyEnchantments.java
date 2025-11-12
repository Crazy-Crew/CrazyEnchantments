package com.ryderbelserion.crazyenchantments.api.interfaces.platform;

import com.ryderbelserion.crazyenchantments.api.interfaces.registry.IMessageRegistry;
import com.ryderbelserion.crazyenchantments.api.interfaces.registry.IUserRegistry;
import com.ryderbelserion.fusion.files.FileManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface ICrazyEnchantments {

    @NotNull <U> IUserRegistry<U> getUserRegistry();

    @NotNull IMessageRegistry getMessageRegistry();

    @NotNull FileManager getFileManager();

    boolean isConsoleSender(@NotNull final Audience audience);

    void broadcast(@NotNull final Component component, @NotNull final String permission);

    default void broadcast(@NotNull Component component) {
        broadcast(component, "");
    }

    void registerCommands();

    void reload();

    void start(@NotNull final Audience audience);

    void stop();

}
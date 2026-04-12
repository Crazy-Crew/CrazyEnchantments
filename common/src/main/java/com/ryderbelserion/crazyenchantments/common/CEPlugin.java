package com.ryderbelserion.crazyenchantments.common;

import com.ryderbelserion.crazyenchantments.api.CrazyEnchantments;
import com.ryderbelserion.crazyenchantments.api.adapters.IPlayerAdapter;
import com.ryderbelserion.crazyenchantments.common.api.adapters.PlayerAdapter;
import com.ryderbelserion.crazyenchantments.common.api.adapters.sender.ISenderAdapter;
import com.ryderbelserion.crazyenchantments.common.api.enums.Mode;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class CEPlugin<S> extends CrazyEnchantments<S> {

    public static final UUID CONSOLE_UUID = new UUID(0, 0);
    public static final String CONSOLE_NAME = "Console";

    private IPlayerAdapter<?> adapter;

    public CEPlugin(@NotNull final FusionKyori fusion) {
        super(fusion);
    }

    public abstract boolean hasPermission(@NotNull final Audience audience, @NotNull final String permission);

    public abstract void registerPermission(@NotNull final Mode mode, @NotNull final String permission, @NotNull final String description, @NotNull final Map<String, Boolean> children);

    public void registerPermission(@NotNull final Mode mode, @NotNull final String permission, @NotNull final String description) {
        registerPermission(mode, permission, description, new HashMap<>());
    }

    public abstract void broadcast(@NotNull final Component component, @NotNull final String permission);

    public void broadcast(@NotNull Component component) {
        broadcast(component, "");
    }

    public abstract ISenderAdapter getSenderAdapter();

    @Override
    public void init() {
        this.fusion.init();

        Provider.register(this);

        try {
            Files.createDirectories(this.dataPath);
        } catch (final IOException ignored) {}

        this.fileManager.addFolder(this.dataPath.resolve("locale"), FileType.YAML)
                .addFile(this.dataPath.resolve("messages.yml"), FileType.YAML)
                .addFile(this.dataPath.resolve("config.yml"), FileType.YAML);
    }

    @Override
    public void post() {
        this.adapter = new PlayerAdapter<>(getUserRegistry(), getContextRegistry());

        registerCommands();
    }

    @Override
    public void reload() {
        this.fileManager.refresh(false).addFolder(this.dataPath.resolve("locale"), FileType.YAML);

        this.fusion.reload();

        getMessageRegistry().init();
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public @NotNull <C> IPlayerAdapter<C> getPlayerAdapter(@NotNull final Class<C> object) {
        return (IPlayerAdapter<C>) this.adapter;
    }
}
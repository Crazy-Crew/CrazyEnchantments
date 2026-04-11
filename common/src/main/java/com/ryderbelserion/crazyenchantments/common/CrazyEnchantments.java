package com.ryderbelserion.crazyenchantments.common;

import com.ryderbelserion.crazyenchantments.api.CrazyEnchantmentsProvider;
import com.ryderbelserion.crazyenchantments.api.interfaces.platform.ICrazyEnchantments;
import com.ryderbelserion.crazyenchantments.common.enums.Mode;
import com.ryderbelserion.crazyenchantments.common.registry.MessageRegistry;
import com.ryderbelserion.crazyenchantments.common.registry.UserRegistry;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class CrazyEnchantments implements ICrazyEnchantments {

    private final FusionKyori<Audience> fusion;

    public static final UUID console = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final String namespace = "crazyenchantments";

    private final FileManager fileManager;
    private final Path path;

    public CrazyEnchantments(@NotNull final FusionKyori<Audience> fusion) {
        this.fusion = fusion;
        this.fileManager = this.fusion.getFileManager();
        this.path = this.fusion.getDataPath();
    }

    private MessageRegistry messageRegistry;
    private UserRegistry userRegistry;

    @Override
    public void start(@NotNull final Audience audience) {
        CrazyEnchantmentsProvider.register(this);

        this.fileManager.addFolder(this.path.resolve("locale"), FileType.YAML)
                .addFile(this.path.resolve("messages.yml"), FileType.YAML)
                .addFile(this.path.resolve("config.yml"), FileType.YAML);

        this.messageRegistry = new MessageRegistry();
        this.messageRegistry.init();

        this.userRegistry = new UserRegistry();
        this.userRegistry.init(audience);

        registerCommands();
    }

    @Override
    public void reload() {
        this.fusion.reload();

        this.fileManager.refresh(false);

        this.messageRegistry.init();
    }

    @Override
    public void stop() {

    }

    public abstract boolean hasPermission(@NotNull final Audience audience, @NotNull final String permission);

    public abstract void registerPermission(@NotNull final Mode mode, @NotNull final String permission, @NotNull final String description, @NotNull final Map<String, Boolean> children);

    public void registerPermission(@NotNull final Mode mode, @NotNull final String permission, @NotNull final String description) {
        registerPermission(mode, permission, description, new HashMap<>());
    }

    @Override
    public @NotNull final FileManager getFileManager() {
        return this.fileManager;
    }

    @Override
    public @NotNull final UserRegistry getUserRegistry() {
        return this.userRegistry;
    }

    @Override
    public @NotNull final MessageRegistry getMessageRegistry() {
        return this.messageRegistry;
    }

    public @NotNull FusionKyori<Audience> getFusion() {
        return this.fusion;
    }
}
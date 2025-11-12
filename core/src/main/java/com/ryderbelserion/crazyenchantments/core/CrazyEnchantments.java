package com.ryderbelserion.crazyenchantments.core;

import com.ryderbelserion.crazyenchantments.api.CrazyEnchantmentsProvider;
import com.ryderbelserion.crazyenchantments.api.interfaces.platform.ICrazyEnchantments;
import com.ryderbelserion.crazyenchantments.core.registry.MessageRegistry;
import com.ryderbelserion.crazyenchantments.core.registry.UserRegistry;
import com.ryderbelserion.fusion.core.api.enums.FileType;
import com.ryderbelserion.fusion.core.files.FileManager;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.ArrayList;

public abstract class CrazyEnchantments implements ICrazyEnchantments {

    private final FileManager fileManager;
    private final Path path;

    public CrazyEnchantments(@NotNull final Path path, @NotNull final FileManager fileManager) {
        this.fileManager = fileManager;
        this.path = path;
    }

    private MessageRegistry messageRegistry;
    private UserRegistry userRegistry;

    @Override
    public void start() {
        CrazyEnchantmentsProvider.register(this);

        this.fileManager.addFolder(this.path.resolve("locale"), FileType.YAML, new ArrayList<>(), null)
                .addFile(this.path.resolve("messages.yml"), new ArrayList<>(), null)
                .addFile(this.path.resolve("config.yml"), new ArrayList<>(), null);

        this.messageRegistry = new MessageRegistry(this.userRegistry = new UserRegistry());
        this.messageRegistry.init();
    }

    @Override
    public void reload() {
        this.fileManager.refresh(false);

        this.messageRegistry.init();
    }

    @Override
    public void stop() {

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
}
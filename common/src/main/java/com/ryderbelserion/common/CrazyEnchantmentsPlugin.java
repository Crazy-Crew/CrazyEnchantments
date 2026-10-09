package com.ryderbelserion.common;

import com.ryderbelserion.common.api.EnchantmentProvider;
import com.ryderbelserion.common.api.enums.messages.Messages;
import com.ryderbelserion.common.api.sender.ISenderAdapter;
import com.ryderbelserion.fusion.api.enums.Level;
import com.ryderbelserion.fusion.api.objects.FusionKey;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public abstract class CrazyEnchantmentsPlugin<C, S, F extends FusionKyori<S>> {

    public static final UUID CONSOLE_UUID = new UUID(0, 0);
    public static final String CONSOLE_NAME = "Console";
    public static final String namespace = "crazyenchantments";

    protected final Path path;
    protected final F fusion;

    public CrazyEnchantmentsPlugin(@NonNull final F fusion, @NonNull final Path path) {
        this.fusion = fusion;
        this.path = path;
    }

    public abstract ISenderAdapter<C, S> getSenderAdapter();

    protected FileManager fileManager;

    public void loadMessages() {
        final List<Path> paths = this.fileManager.getFilesByPath(this.path.resolve("locale"), ".yml");

        paths.add(this.path.resolve("messages.yml")); // add to list

        this.fusion.getMessageRegistry().init(action -> {
            for (final Path path : paths) {
                this.fileManager.getYamlFile(path).ifPresentOrElse(customFile -> {
                    final String fileName = customFile.getFileName();

                    final FusionKey key = FusionKey.key(namespace, fileName.equalsIgnoreCase("messages.yml") ? "default" : fileName.toLowerCase());

                    final CommentedConfigurationNode configuration = customFile.getConfiguration();

                    for (final Messages message : Messages.values()) {
                        message.addKey(action, configuration, key);
                    }
                }, () -> this.fusion.log(Level.info, "Path %s not found in cache.".formatted(path)));
            }
        });
    }

    public void init() {
        this.fusion.init().post();

        this.fileManager = this.fusion.getFileManager();

        EnchantmentProvider.register(this);
    }

    public void reload() {
        this.fileManager.refresh(false);
    }

    public @NonNull final FileManager getFileManager() {
        return this.fileManager;
    }

    public @NonNull final Path getDataPath() {
        return this.path;
    }

    public @NonNull F getFusion() {
        return this.fusion;
    }
}
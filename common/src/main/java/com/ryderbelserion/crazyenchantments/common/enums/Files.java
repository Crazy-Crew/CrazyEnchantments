package com.ryderbelserion.crazyenchantments.common.enums;

import com.ryderbelserion.fusion.core.FusionCore;
import com.ryderbelserion.fusion.core.files.FileManager;
import com.ryderbelserion.fusion.core.files.types.YamlCustomFile;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.Objects;

public enum Files {

    messages("messages.yml"),
    config("config.yml");

    private final FusionKyori kyori = (FusionKyori) FusionCore.Provider.get();
    private final FileManager fileManager = this.kyori.getFileManager();
    private final Path path = this.kyori.getPath();

    private final Path relativePath;

    Files(@NotNull final String fileName) {
        this.relativePath = this.path.resolve(fileName);
    }

    public @NotNull final CommentedConfigurationNode getConfig() {
        return Objects.requireNonNull(getCustomFile()).getConfiguration();
    }

    public @Nullable final YamlCustomFile getCustomFile() {
        return this.fileManager.getYamlFile(this.relativePath);
    }

    public @NotNull final Path getPath() {
        return this.relativePath;
    }
}
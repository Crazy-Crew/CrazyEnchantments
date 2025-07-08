package com.ryderbelserion.crazyenchantments.enums;

import com.ryderbelserion.fusion.core.FusionProvider;
import com.ryderbelserion.fusion.core.api.FusionCore;
import com.ryderbelserion.fusion.core.files.types.YamlCustomFile;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.Objects;

public enum FileKeys {

    config("config.yml");

    private final FusionCore fusion = FusionProvider.get();

    private final Path relativePath;

    FileKeys(@NotNull final String fileName) {
        this.relativePath = this.fusion.getPath().resolve(fileName);
    }

    public @NotNull
    final CommentedConfigurationNode getConfig() {
        return getCustomFile().getConfiguration();
    }

    public @NotNull final YamlCustomFile getCustomFile() {
        return Objects.requireNonNull(this.fusion.getFileManager().getYamlFile(this.relativePath));
    }

    public @NotNull final Path getRelativePath() {
        return this.relativePath;
    }
}
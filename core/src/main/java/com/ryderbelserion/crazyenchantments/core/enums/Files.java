package com.ryderbelserion.crazyenchantments.core.enums;

import com.ryderbelserion.fusion.core.FusionProvider;
import com.ryderbelserion.fusion.core.api.FusionCore;
import com.ryderbelserion.fusion.core.files.types.YamlCustomFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.Objects;

public enum Files {

    messages("messages.yml"),
    config("config.yml");

    private final FusionCore fusion = FusionProvider.get();

    private final Path path;

    Files(@NotNull final String fileName) {
        this.path = this.fusion.getPath().resolve(fileName);
    }

    public @NotNull final CommentedConfigurationNode getConfig() {
        return Objects.requireNonNull(getCustomFile()).getConfiguration();
    }

    public @Nullable final YamlCustomFile getCustomFile() {
        return this.fusion.getFileManager().getYamlFile(this.path);
    }

    public @NotNull final Path getPath() {
        return this.path;
    }
}
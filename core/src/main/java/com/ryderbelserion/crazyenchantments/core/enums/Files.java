package com.ryderbelserion.crazyenchantments.core.enums;

import com.ryderbelserion.fusion.core.FusionCore;
import com.ryderbelserion.fusion.core.FusionProvider;
import com.ryderbelserion.fusion.core.exceptions.FusionException;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.Optional;

public enum Files {

    messages("messages.yml"),
    config("config.yml");

    private final FusionCore fusion = FusionProvider.getInstance();

    private final FileManager fileManager = this.fusion.getFileManager();

    private final Path path;

    Files(@NotNull final String fileName) {
        this.path = this.fusion.getDataPath().resolve(fileName);
    }

    public @NotNull final CommentedConfigurationNode getConfig() {
        return getCustomFile().getConfiguration();
    }

    public @NotNull final YamlCustomFile getCustomFile() {
        @NotNull final Optional<YamlCustomFile> customFile = this.fileManager.getYamlFile(this.path);

        if (customFile.isEmpty()) {
            throw new FusionException("Could not find custom file for " + this.path);
        }

        return customFile.get();
    }

    public @NotNull final Path getPath() {
        return this.path;
    }
}
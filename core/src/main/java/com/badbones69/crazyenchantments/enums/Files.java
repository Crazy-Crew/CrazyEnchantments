package com.badbones69.crazyenchantments.enums;

import com.badbones69.crazyenchantments.CrazyPlugin;
import com.ryderbelserion.fusion.core.FusionCore;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.ryderbelserion.fusion.core.files.FileManager;
import com.ryderbelserion.fusion.core.files.enums.FileType;
import com.ryderbelserion.fusion.core.files.types.JsonCustomFile;
import com.ryderbelserion.fusion.core.files.types.YamlCustomFile;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.crazyenchantments.ICrazyProvider;
import java.nio.file.Path;
import java.util.Optional;

public enum Files {

    messages("Messages.yml", FileType.YAML),

    blocks("blocks.json", FileType.JSON),
    heads("heads.json", FileType.JSON);

    private final CrazyPlugin plugin = (CrazyPlugin) ICrazyProvider.getInstance();

    private final FusionCore fusion = this.plugin.getFusion();

    private final FileManager fileManager = this.fusion.getFileManager();

    private final FileType fileType;
    private final Path location;

    Files(@NotNull final String location, @NotNull final FileType fileType) {
        this.location = this.fusion.getDataPath().resolve(location);
        this.fileType = fileType;
    }

    public @NotNull final BasicConfigurationNode getJsonConfiguration() {
        return getJsonCustomFile().getConfiguration();
    }

    public @NotNull final JsonCustomFile getJsonCustomFile() {
        @NotNull final Optional<JsonCustomFile> customFile = this.fileManager.getJsonFile(this.location);

        if (customFile.isEmpty()) {
            throw new FusionException("Could not find custom file for " + this.location);
        }

        return customFile.get();
    }

    public @NotNull final CommentedConfigurationNode getYamlConfiguration() {
        return getYamlCustomFile().getConfiguration();
    }

    public @NotNull final YamlCustomFile getYamlCustomFile() {
        @NotNull final Optional<YamlCustomFile> customFile = this.fileManager.getYamlFile(this.location);

        if (customFile.isEmpty()) {
            throw new FusionException("Could not find custom file for " + this.location);
        }

        return customFile.get();
    }

    public @NotNull final FileType getFileType() {
        return this.fileType;
    }

    public @NotNull final Path getPath() {
        return this.location;
    }

    public void save() {
        this.fileManager.saveFile(this.location);
    }
}
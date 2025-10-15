package com.badbones69.crazyenchantments.paper.api.enums.files;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.ryderbelserion.fusion.core.files.enums.FileType;
import com.ryderbelserion.fusion.core.files.types.JsonCustomFile;
import com.ryderbelserion.fusion.core.files.types.YamlCustomFile;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import com.ryderbelserion.fusion.paper.files.types.PaperCustomFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.Optional;

public enum FileKeys {

    config(FileType.PAPER, "config.yml"),

    blocks(FileType.JSON, "blocks.json"),
    currency(FileType.YAML, "currency.yml"),

    data(FileType.PAPER, "Data.yml"),
    enchantment_types(FileType.PAPER, "Enchantment-Types.yml"),
    enchantments(FileType.PAPER, "Enchantments.yml"),
    gkitz(FileType.PAPER, "GKitz.yml"),
    head_map(FileType.PAPER, "HeadMap.yml"),
    messages(FileType.PAPER, "Messages.yml"),
    tinker(FileType.PAPER, "Tinker.yml");

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);
    private final PaperFileManager fileManager = this.plugin.getFileManager();
    private final Path path = this.plugin.getDataPath();

    private final FileType fileType;
    private final Path location; // the file location
    private final Path folder; // the folder which defaults to the data path

    FileKeys(@NotNull final FileType fileType, @NotNull final String fileName, @NotNull final String folder) {
        this.folder = this.path.resolve(folder);
        this.location = this.folder.resolve(fileName);
        this.fileType = fileType;
    }

    FileKeys(@NotNull final FileType fileType, @NotNull final String fileName) {
        this.folder = this.path;
        this.location = this.folder.resolve(fileName);
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

    public @NotNull final YamlConfiguration getPaperConfiguration() {
        return getPaperCustomFile().getConfiguration();
    }

    public @NotNull final PaperCustomFile getPaperCustomFile() {
        @NotNull final Optional<PaperCustomFile> customFile = this.fileManager.getPaperFile(this.location);

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
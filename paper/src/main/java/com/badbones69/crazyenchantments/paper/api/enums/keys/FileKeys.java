package com.badbones69.crazyenchantments.paper.api.enums.keys;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.ryderbelserion.fusion.api.exceptions.FusionException;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.Optional;

public enum FileKeys {

    CONFIG(FileType.YAML, "config.yml"),
    BLOCKLIST(FileType.YAML, "BlockList.yml"),
    HEADMAP(FileType.YAML, "HeadMap.yml"),
    DATA(FileType.YAML, "Data.yml"),
    ENCHANTMENTS(FileType.YAML, "Enchantments.yml"),
    GKITZ(FileType.YAML, "GKitz.yml"),
    MESSAGES(FileType.YAML, "Messages.yml"),
    ENCHANTMENT_TYPES(FileType.YAML, "Enchantment-Types.yml"),
    TINKER(FileType.YAML, "Tinker.yml"),

    SUPPORT(FileType.YAML, "support.yml");

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);
    private final CrazyPlatform platform = this.plugin.getPlatform();
    private final FileManager fileManager = this.platform.getFileManager();
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

    public @NotNull final CommentedConfigurationNode getConfigurationNode() {
        return getYamlCustomFile().getConfiguration();
    }

    public @NotNull final YamlCustomFile getYamlCustomFile() {
        final Optional<YamlCustomFile> customFile = this.fileManager.getYamlFile(this.location);

        if (customFile.isEmpty()) {
            throw new FusionException("Could not find custom file for " + this.location);
        }

        return customFile.get();
    }

    public @NotNull final CommentedConfigurationNode getConfiguration() {
        return getConfigurationNode();
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

    public void addFile() {
        this.fileManager.addFile(this.location, this.fileType);
    }
}
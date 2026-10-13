package com.ryderbelserion.common.api.enums;

import com.ryderbelserion.common.CrazyEnchantmentsPlugin;
import com.ryderbelserion.common.api.EnchantmentProvider;
import com.ryderbelserion.fusion.api.exceptions.FusionException;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.Optional;

public enum Files {

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

    private final CrazyEnchantmentsPlugin plugin = EnchantmentProvider.api();
    private final FileManager fileManager = this.plugin.getFileManager();
    private final Path path = this.plugin.getDataPath();

    private final FileType fileType;
    private final Path location; // the file location
    private final Path folder; // the folder which defaults to the data path

    Files(@NonNull final FileType fileType, @NonNull final String fileName, @NonNull final String folder) {
        this.folder = this.path.resolve(folder);
        this.location = this.folder.resolve(fileName);
        this.fileType = fileType;
    }

    Files(@NonNull final FileType fileType, @NonNull final String fileName) {
        this.folder = this.path;
        this.location = this.folder.resolve(fileName);
        this.fileType = fileType;
    }

    public @NonNull final YamlCustomFile getYamlCustomFile() {
        final Optional<YamlCustomFile> customFile = this.fileManager.getYamlFile(this.location);

        if (customFile.isEmpty()) {
            throw new FusionException("Could not find custom file for " + this.location);
        }

        return customFile.get();
    }

    public @NonNull final CommentedConfigurationNode getConfiguration() {
        return getYamlCustomFile().getConfiguration();
    }

    public @NonNull final FileType getFileType() {
        return this.fileType;
    }

    public @NonNull final Path getPath() {
        return this.location;
    }

    public void save() {
        this.fileManager.saveFile(this.location);
    }

    public void addFile() {
        this.fileManager.addFile(this.location, this.fileType);
    }
}
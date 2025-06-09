package com.ryderbelserion.crazyenchantments.paper.api.enums;

import com.ryderbelserion.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.fusion.core.files.FileManager;
import com.ryderbelserion.fusion.core.files.types.YamlCustomFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.Objects;

public enum Files {

    messages("messages.yml"),
    config("config.yml");

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);
    private final FileManager fileManager = this.plugin.getFileManager();

    private final Path path;

    Files(@NotNull final String fileName) {
        this.path = this.plugin.getDataPath().resolve(fileName);
    }

    public @NotNull final CommentedConfigurationNode getConfig() {
        return Objects.requireNonNull(getCustomFile()).getConfiguration();
    }

    public @Nullable final YamlCustomFile getCustomFile() {
        return this.fileManager.getYamlFile(this.path);
    }

    public @NotNull final Path getPath() {
        return this.path;
    }
}
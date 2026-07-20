package com.badbones69.crazyenchantments.paper.api;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.keys.FileKeys;
import com.badbones69.crazyenchantments.paper.support.SupportUtils;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class CrazyPlatform {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Server server = this.plugin.getServer();

    private final Path dataPath = this.plugin.getDataPath();

    private PaperFileManager fileManager;
    private SupportUtils support;

    private FusionPaper fusion;

    public void init() {
        this.fusion = new FusionPaper(this.plugin);
        this.fusion.init();

        this.fileManager = this.fusion.getFileManager();

        this.fileManager.addFile(this.dataPath.resolve("support.yml"), FileType.YAML);

        List.of(
                FileKeys.CONFIG,
                FileKeys.BLOCKLIST,
                FileKeys.HEADMAP,
                FileKeys.DATA,
                FileKeys.ENCHANTMENTS,
                FileKeys.GKITZ,
                FileKeys.MESSAGES,
                FileKeys.ENCHANTMENT_TYPES,
                FileKeys.TINKER,
                FileKeys.BLOCKS
        ).forEach(FileKeys::addFile);

        this.support = new SupportUtils();
        this.support.init();

        loadExamples();
    }

    public void reload() {
        this.fileManager.addFile(this.dataPath.resolve("support.yml"), FileType.YAML)
                .refresh(false);

        loadExamples();
    }

    public void loadExamples() {
        final Path examples = this.dataPath.resolve("examples");

        if (Files.exists(examples)) {
            try (final Stream<Path> values = Files.walk(examples)) {
                values.sorted(Comparator.reverseOrder()).forEach(path -> { // sorted in reverse order, to ensure the directories are empty first.
                    try {
                        this.fusion.log(Level.WARNING, "Successfully deleted path %s, re-generating the examples later.", path);

                        Files.delete(path);
                    } catch (final IOException exception) {
                        this.fusion.log(Level.WARNING, "Failed to delete %s in loop.", exception, path);
                    }
                });
            } catch (final Exception exception) {
                this.fusion.log(Level.WARNING, "Failed to delete %s.", exception, examples);
            }
        }

        try {
            Files.createDirectory(examples);
        } catch (IOException exception) {
            this.fusion.log(Level.WARNING, "Failed to create directory %s.", exception, examples);
        }

        List.of(
                "BlockList.yml",
                "config.yml",
                "Data.yml",
                "Enchantment-Types.yml",
                "Enchantments.yml",
                "GKitz.yml",
                "HeadMap.yml",
                "Messages.yml",
                "Tinker.yml",
                "Blocks.yml"
        ).forEach(file -> this.fileManager.extractFile(file, examples.resolve(file)));
    }

    public @NonNull final Optional<Player> getPlayer(@NonNull final String name) {
        return Optional.ofNullable(this.server.getPlayer(name));
    }

    public @NonNull final PaperFileManager getFileManager() {
        return this.fileManager;
    }

    public @NonNull final SupportUtils getSupport() {
        return this.support;
    }

    public @NonNull final FusionPaper getFusion() {
        return this.fusion;
    }
}
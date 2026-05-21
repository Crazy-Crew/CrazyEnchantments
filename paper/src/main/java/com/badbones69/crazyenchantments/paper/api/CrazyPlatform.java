package com.badbones69.crazyenchantments.paper.api;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.fusion.core.api.enums.Level;
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

    private final FusionPaper fusion = this.plugin.getFusion();

    private final PaperFileManager fileManager = this.fusion.getFileManager();

    private final Path dataPath = this.plugin.getDataPath();

    public void init() {
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
                "Tinker.yml"
        ).forEach(file -> this.fileManager.extractFile(file, examples.resolve(file)));
    }

    public Optional<Player> getPlayer(@NonNull final String name) {
        return Optional.ofNullable(this.server.getPlayer(name));
    }
}
package com.badbones69.crazyenchantments.paper.api;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.config.ConfigOptions;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class CrazyInstance {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final PaperFileManager fileManager = this.plugin.getFileManager();

    private final ConfigOptions options = this.plugin.getOptions();

    private final FusionPaper fusion = this.plugin.getFusion();

    private final Path path = this.plugin.getDataPath();

    public void init() {
        loadExamples();
    }

    public void reload() {
        loadExamples();
    }

    public void loadExamples() {
        if (this.options.isUpdateExamplesFolder()) {
            try (final Stream<Path> values = Files.walk(this.path.resolve("examples"))) {
                values.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        this.fusion.log("info", "Successfully deleted path {}, re-generating the examples later.", path);

                        Files.delete(path);
                    } catch (final IOException exception) {
                        this.fusion.log("warn", "Failed to delete {} in loop, Reason: {}", path, exception.getMessage());
                    }
                });
            } catch (final Exception exception) {
                this.fusion.log("warn", "Failed to delete {}, Reason: {}", this.path.resolve("examples"), exception.getMessage());
            }

            List.of(
                    "config.yml",
                    "BlockList.yml",
                    "Data.yml",
                    "Enchantment-Types.yml",
                    "Enchantments.yml",
                    "GKitz.yml",
                    "HeadMap.yml",
                    "Messages.yml",
                    "Tinker.yml"
            ).forEach(file -> this.fileManager.extractFile(this.path.resolve("examples").resolve(file)));
        }
    }
}
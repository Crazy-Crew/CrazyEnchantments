package com.badbones69.crazyenchantments.paper.api;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.adapters.PaperSenderAdapter;
import com.ryderbelserion.common.api.enums.Files;
import com.badbones69.crazyenchantments.paper.support.SupportUtils;
import com.ryderbelserion.common.CrazyEnchantmentsPlugin;
import com.ryderbelserion.fusion.api.enums.Level;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class CrazyPlatform extends CrazyEnchantmentsPlugin<Component, Audience, FusionPaper> {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Server server = this.plugin.getServer();

    private PaperSenderAdapter senderAdapter;
    private SupportUtils support;

    public CrazyPlatform(@NonNull final FusionPaper fusion, @NonNull final Path path) {
        super(fusion, path);
    }

    @Override
    public void init() {
        super.init();

        for (final Files key : Files.values()) {
            key.addFile();
        }

        this.senderAdapter = new PaperSenderAdapter();

        loadMessages();

        this.support = new SupportUtils();
        this.support.init();

        loadExamples();
    }

    @Override
    public void reload() {
        super.reload();

        loadExamples();
    }

    @Override
    public @NonNull final PaperSenderAdapter getSenderAdapter() {
        return this.senderAdapter;
    }

    public void loadExamples() {
        final Path examples = this.path.resolve("examples");

        if (java.nio.file.Files.exists(examples)) {
            try (final Stream<Path> values = java.nio.file.Files.walk(examples)) {
                values.sorted(Comparator.reverseOrder()).forEach(path -> { // sorted in reverse order, to ensure the directories are empty first.
                    try {
                        this.fusion.log(Level.warn, "Successfully deleted path %s, re-generating the examples later.", path);

                        java.nio.file.Files.delete(path);
                    } catch (final IOException exception) {
                        this.fusion.log(Level.warn, "Failed to delete %s in loop.", exception, path);
                    }
                });
            } catch (final Exception exception) {
                this.fusion.log(Level.warn, "Failed to delete %s.", exception, examples);
            }
        }

        try {
            java.nio.file.Files.createDirectory(examples);
        } catch (IOException exception) {
            this.fusion.log(Level.warn, "Failed to create directory %s.", exception, examples);
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

    public @NonNull final Optional<Player> getPlayer(@NonNull final String name) {
        return Optional.ofNullable(this.server.getPlayer(name));
    }

    public @NonNull final SupportUtils getSupport() {
        return this.support;
    }

    @Override
    public @NonNull final FusionPaper getFusion() {
        return this.fusion;
    }
}
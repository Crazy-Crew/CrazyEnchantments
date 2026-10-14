package com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types;

import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.enums.MigrationType;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.interfaces.IMigrator;
import com.ryderbelserion.common.api.enums.Files;
import com.ryderbelserion.common.api.enums.messages.Messages;
import com.ryderbelserion.common.utils.ConfigUtils;
import com.ryderbelserion.fusion.api.enums.Level;
import com.ryderbelserion.fusion.api.objects.FusionKey;
import com.ryderbelserion.fusion.kyori.utils.AdvUtils;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.ryderbelserion.common.CrazyEnchantmentsPlugin.namespace;

public class LegacyMigrator extends IMigrator {

    public LegacyMigrator(@NonNull final CommandSender sender) {
        super(sender, MigrationType.LEGACY_TO_MINIMESSAGE);
    }

    @Override
    public void init() {
        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        final List<Path> paths = this.fileManager.getFilesByPath(this.path.resolve("locale"), ".yml");

        paths.add(this.path.resolve("Messages.yml")); // add to list

        this.messageRegistry.init(action -> {
            for (final Path path : paths) {
                this.fileManager.getYamlFile(path).ifPresentOrElse(customFile -> {
                    final String fileName = customFile.getFileName();

                    final FusionKey key = FusionKey.key(namespace, fileName.equalsIgnoreCase("Messages.yml") ? "default" : fileName.toLowerCase());

                    final CommentedConfigurationNode configuration = customFile.getConfiguration();

                    for (final Messages message : Messages.values()) {
                        message.migrateKey(action, configuration, key);
                    }

                    customFile.save();

                    success.add("<green>⤷ " + fileName);
                }, () -> {
                    this.fusion.log(Level.info, "Path %s not found in cache.".formatted(path));

                    failed.add("<red>⤷ " + path);
                });
            }
        });

        final CommentedConfigurationNode configuration = Files.CONFIG.getConfiguration();

        if (configuration.hasChild("Settings", "Prefix")) {
            ConfigUtils.setNode(
                    configuration,
                    String.class,
                    AdvUtils.convert(configuration.node("Settings", "Prefix").getString("<dark_gray>[<green>CrazyEnchantments<dark_gray>]: "), true),
                    "Settings", "Prefix"
            );

            Files.CONFIG.save();

            success.add("<green>⤷ config.yml");
        } else {
            failed.add("<red>⤷ config.yml");
        }

        final int convertedFiles = success.size();
        final int failedFiles = failed.size();

        final List<String> files = new ArrayList<>(failedFiles + convertedFiles);

        files.addAll(failed);
        files.addAll(success);

        sendMessage(files, convertedFiles, failedFiles, true);
    }
}
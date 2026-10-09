package com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types;

import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.enums.MigrationType;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.interfaces.IMigrator;
import com.ryderbelserion.common.api.enums.Files;
import com.ryderbelserion.common.api.enums.messages.Messages;
import com.ryderbelserion.fusion.api.enums.Level;
import com.ryderbelserion.fusion.api.objects.FusionKey;
import kotlin.io.FilesKt;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.nio.file.Path;
import java.util.List;

import static com.ryderbelserion.common.CrazyEnchantmentsPlugin.namespace;

public class LegacyMigrator extends IMigrator {

    public LegacyMigrator(@NonNull final CommandSender sender) {
        super(sender, MigrationType.LEGACY_TO_MINIMESSAGE);
    }

    @Override
    public void init() {
        final List<Path> paths = this.fileManager.getFilesByPath(this.path.resolve("locale"), ".yml");

        paths.add(this.path.resolve("messages.yml")); // add to list

        this.messageRegistry.init(action -> {
            for (final Path path : paths) {
                this.fileManager.getYamlFile(path).ifPresentOrElse(customFile -> {
                    final String fileName = customFile.getFileName();

                    final FusionKey key = FusionKey.key(namespace, fileName.equalsIgnoreCase("messages.yml") ? "default" : fileName.toLowerCase());

                    final CommentedConfigurationNode configuration = customFile.getConfiguration();

                    for (final Messages message : Messages.values()) {
                        message.migrateKey(action, configuration, key);
                    }
                }, () -> this.fusion.log(Level.info, "Path %s not found in cache.".formatted(path)));
            }
        });
    }
}
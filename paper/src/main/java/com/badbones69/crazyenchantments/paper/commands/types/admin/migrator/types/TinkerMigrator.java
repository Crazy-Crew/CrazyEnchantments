package com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types;

import com.ryderbelserion.common.api.enums.Files;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.enums.MigrationType;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.interfaces.IMigrator;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.ArrayList;
import java.util.List;

public class TinkerMigrator extends IMigrator {

    public TinkerMigrator(@NonNull final CommandSender sender) {
        super(sender, MigrationType.TINKER);
    }

    @Override
    public void init() {
        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        final CommentedConfigurationNode configuration = Files.TINKER.getConfiguration().node("Tinker");

        if (configuration.hasChild("Vanilla-Enchantments")) {
            final CommentedConfigurationNode section = configuration.node("Vanilla-Enchantments");

            section.childrenMap().forEach((_, action) -> {
                final String index = action.getString("10, 1");

                if (!index.contains(",")) {
                    try {
                        action.set(String.class, "%s, 1".formatted(index));
                    } catch (final Exception exception) {
                        exception.printStackTrace();
                    }
                }
            });

            success.add("<green>⤷ Tinker.yml");
        } else {
            failed.add("<red>⤷ Tinker.yml");
        }

        if (configuration.hasChild("Crazy-Enchantments")) {
            final CommentedConfigurationNode section = configuration.node("Crazy-Enchantments");

            section.childrenMap().forEach((_, action) -> {
                if (action.hasChild("Items")) {
                    final CommentedConfigurationNode items = action.node("Items");
                    final String index = items.getString("20, 1");

                    if (!index.contains(",")) {
                        try {
                             items.set(String.class, "%s, 1".formatted(index));
                        } catch (final Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }

                if (action.hasChild("Book")) {
                    final CommentedConfigurationNode books = action.node("Book");
                    final String index = books.getString("5, 1");

                    if (!index.contains(",")) {
                        try {
                            books.set(String.class, "%s, 1".formatted(index));
                        } catch (final Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            });

            Files.TINKER.save();

            if (failed.isEmpty()) {
                success.add("<green>⤷ Tinker.yml");
            }
        } {
            if (success.isEmpty()) {
                failed.add("<red>⤷ Tinker.yml");
            }
        }

        final int convertedFiles = success.size();
        final int failedFiles = failed.size();

        final List<String> files = new ArrayList<>(failedFiles + convertedFiles);

        files.addAll(failed);
        files.addAll(success);

        sendMessage(files, convertedFiles, failedFiles, false);
    }
}
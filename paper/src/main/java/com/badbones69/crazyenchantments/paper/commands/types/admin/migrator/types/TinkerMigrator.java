package com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types;

import com.ryderbelserion.common.api.enums.Files;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.enums.MigrationType;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.interfaces.IMigrator;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class TinkerMigrator extends IMigrator {

    public TinkerMigrator(@NonNull final CommandSender sender) {
        super(sender, MigrationType.TINKER);
    }

    @Override
    public void init() {
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
        }
    }
}
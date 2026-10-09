package com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.config;

import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.enums.MigrationType;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.interfaces.IMigrator;
import com.ryderbelserion.common.api.enums.Files;
import com.ryderbelserion.common.utils.ConfigUtils;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class MissingOptionsMigrator extends IMigrator {

    public MissingOptionsMigrator(@NonNull final CommandSender sender) {
        super(sender, MigrationType.MISSING_OPTIONS);
    }

    @Override
    public void init() {
        boolean isSave = false;

        final CommentedConfigurationNode configuration = Files.CONFIG.getConfiguration();

        if (!configuration.hasChild("Settings", "CESuccessOverride")) {
            ConfigUtils.setNode(configuration, "Settings.CESuccessOverride", Integer.class, -1);

            isSave = true;
        }

        if (!configuration.hasChild("Settings", "CEFailureOverride")) {
            ConfigUtils.setNode(configuration, "Settings.CEFailureOverride", Integer.class, -1);

            isSave = true;
        }

        if (!configuration.hasChild("Settings", "Toggle-Metrics")) {
            ConfigUtils.setNode(configuration, "Settings.Toggle-Metrics", Boolean.class, false);

            isSave = true;
        }

        if (!configuration.hasChild("Settings", "Refresh-Potion-Effects-On-World-Change")) {
            ConfigUtils.setNode(configuration, "Settings.Refresh-Potion-Effects-On-World-Change", Boolean.class, false);

            isSave = true;
        }

        if (isSave) {
            Files.CONFIG.save();
        }
    }
}

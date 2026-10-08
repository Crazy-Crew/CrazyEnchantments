package com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types;

import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.enums.MigrationType;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.interfaces.IMigrator;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

public class LegacyMigrator extends IMigrator {

    public LegacyMigrator(@NonNull final CommandSender sender) {
        super(sender, MigrationType.LEGACY_TO_MINIMESSAGE);
    }

    @Override
    public void init() {

    }
}
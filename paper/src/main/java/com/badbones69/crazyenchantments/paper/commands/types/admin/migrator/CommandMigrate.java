package com.badbones69.crazyenchantments.paper.commands.types.admin.migrator;

import com.badbones69.crazyenchantments.paper.commands.BaseEnchantCommand;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.enums.MigrationType;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.LegacyMigrator;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.TinkerMigrator;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.config.BrokenEnchantmentsMigrator;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.config.MissingOptionsMigrator;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.interfaces.IMigrator;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class CommandMigrate extends BaseEnchantCommand {

    @Command("migrate")
    @Permission(value = "crazyenchantments.migrate", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments migrate <migration-type>")
    public void execute(final CommandSender sender, final MigrationType migrationType) {
        IMigrator migrator = null;

        switch (migrationType) {
            case BROKEN_ENCHANTMENTS -> migrator = new BrokenEnchantmentsMigrator(sender);
            case LEGACY_TO_MINIMESSAGE -> migrator = new LegacyMigrator(sender);
            case MISSING_OPTIONS -> migrator = new MissingOptionsMigrator(sender);
            case TINKER -> migrator = new TinkerMigrator(sender);
        }

        if (migrator == null) {
            return;
        }

        migrator.init();
    }
}
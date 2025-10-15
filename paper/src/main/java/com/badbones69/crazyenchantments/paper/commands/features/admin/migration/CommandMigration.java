package com.badbones69.crazyenchantments.paper.commands.features.admin.migration;

import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import com.badbones69.crazyenchantments.paper.commands.features.admin.migration.enums.MigrationType;
import com.badbones69.crazyenchantments.paper.commands.features.admin.migration.types.LegacyMigration;
import com.badbones69.crazyenchantments.paper.commands.features.admin.migration.types.TinkerMigration;
import com.badbones69.crazyenchantments.paper.commands.features.admin.migration.types.EnchantMigration;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Flag;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import java.util.Optional;

public class CommandMigration extends BaseCommand {

    @Command("migrate")
    @Permission(value = "crazyenchantments.migrate", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments migrate -t [type]")
    @Flag(flag = "t", longFlag = "type", argument = MigrationType.class)
    @Flag(flag = "p", longFlag = "player", argument = Player.class)
    public void migrate(final CommandSender sender, final Flags flags) {
        final Optional<MigrationType> key = flags.getFlagValue("mt", MigrationType.class);

        key.ifPresent(type -> {
            switch (type) {
                case tinker_migration -> new TinkerMigration(sender).run();
                case legacy_migration -> new LegacyMigration(sender).run();
                case enchant_migration -> {
                    if (!(sender instanceof Player player)) {
                        Messages.PLAYERS_ONLY.sendMessage(sender);

                        return;
                    }

                    if (!player.hasPermission("crazyenchantments.migrate-enchants")) {
                        Messages.NO_PERMISSION.sendMessage(player);

                        return;
                    }

                    Player argument = player;

                    if (flags.hasFlag("p")) {
                        final Optional<Player> optional = flags.getFlagValue("p", Player.class);

                        if (optional.isEmpty()) {
                            Messages.NOT_ONLINE.sendMessage(sender);
                        } else {
                            argument = optional.get();
                        }
                    }

                    new EnchantMigration(argument).run();
                }
            }
        });
    }
}
package com.badbones69.crazyenchantments.paper.commands.v2.features.admin;

import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.commands.v2.features.BaseCommand;
import com.badbones69.crazyenchantments.paper.commands.v2.features.admin.validation.enums.ValidationType;
import com.badbones69.crazyenchantments.paper.commands.v2.features.admin.validation.types.ConfigValidator;
import com.badbones69.crazyenchantments.paper.commands.v2.features.admin.validation.types.EnchantValidator;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Flag;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import java.util.Optional;

public class CommandValidate extends BaseCommand {

    @Command("validate")
    @Permission(value = "crazyenchantments.validate", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments validate -t [type]")
    @Flag(flag = "t", longFlag = "type", argument = String.class, suggestion = "migrators")
    @Flag(flag = "p", longFlag = "player", argument = Player.class)
    public void validate(final CommandSender sender, final Flags flags) {
        final boolean hasFlag = flags.hasFlag("t");

        if (!hasFlag) {
            //todo() send message

            return;
        }

        final Optional<String> key = flags.getFlagValue("mt");

        if (key.isEmpty()) {
            //todo() send message

            return;
        }

        final ValidationType type = ValidationType.fromName(key.get());

        if (type == null) {
            //todo() send message

            return;
        }

        switch (type) {
            case config_validator -> new ConfigValidator(sender).run();
            case enchant_validator -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());

                    return;
                }

                if (!player.hasPermission("crazyenchantments.validate-enchants")) {
                    player.sendMessage(Messages.NO_PERMISSION.getMessage());

                    return;
                }

                Player argument = player;

                if (flags.hasFlag("p")) {
                    final Optional<Player> optional = flags.getFlagValue("p", Player.class);

                    if (optional.isEmpty()) {
                        sender.sendMessage(Messages.NOT_ONLINE.getMessage());
                    } else {
                        argument = optional.get();
                    }
                }

                new EnchantValidator(argument).run();
            }
        }
    }
}
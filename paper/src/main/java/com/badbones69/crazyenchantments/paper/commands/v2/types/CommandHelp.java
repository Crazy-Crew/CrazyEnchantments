package com.badbones69.crazyenchantments.paper.commands.v2.types;

import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.commands.v2.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class CommandHelp extends BaseCommand {

    @Command("help")
    @Permission(value = "crazyenchantments.help", def = PermissionDefault.TRUE)
    public void help(CommandSender sender) {
        sender.sendMessage(Messages.HELP.getMessage());
    }
}
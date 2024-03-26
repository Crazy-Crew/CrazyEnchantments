package com.badbones69.crazyenchantments.paper.commands.types;

import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.commands.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandHelp extends BaseCommand {

    @Command
    @Permission("crazyenchantments.gui")
    public void gui(Player player) {
        //this.listener.openGUI(player);
    }

    @Command("help")
    @Permission(value = "crazyenchantments.help", def = PermissionDefault.TRUE)
    public void help(CommandSender sender) {
        sender.sendMessage(Messages.HELP.getMessage());
    }
}
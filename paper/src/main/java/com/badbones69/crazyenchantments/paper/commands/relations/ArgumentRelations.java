package com.badbones69.crazyenchantments.paper.commands.relations;

import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.commands.MessageManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;

public class ArgumentRelations extends MessageManager {

    @Override
    public void build() {
        //this.commandManager.registerMessage(BukkitMessageKey.UNKNOWN_COMMAND, (sender, context) -> Messages.unknown_command.sendMessage(sender, "{command}", context.getInvalidInput()));

        //this.commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> Messages.correct_usage.sendMessage(sender, "{usage}", context.getSyntax()));

        //this.commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> Messages.correct_usage.sendMessage(sender, "{usage}", context.getSyntax()));

        //this.commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> Messages.correct_usage.sendMessage(sender, "{usage}", context.getSyntax()));

        this.commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> sender.sendMessage(Messages.NO_PERMISSION.getMessage("%permission%", context.getPermission().toString())));

        this.commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> sender.sendMessage(Messages.PLAYERS_ONLY.getMessage()));

        //this.commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> Messages.PLAYERS_ONLY);
    }
}
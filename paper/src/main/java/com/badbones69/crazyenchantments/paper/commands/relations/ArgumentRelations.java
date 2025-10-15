package com.badbones69.crazyenchantments.paper.commands.relations;

import com.badbones69.crazyenchantments.paper.api.enums.files.MessageKeys;
import com.badbones69.crazyenchantments.paper.commands.MessageManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;

public class ArgumentRelations extends MessageManager {

    @Override
    public void build() {
        this.commandManager.registerMessage(BukkitMessageKey.UNKNOWN_COMMAND, (sender, context) -> MessageKeys.UNKNOWN_COMMAND.sendMessage(sender, "{command}", context.getInvalidInput()));

        this.commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> MessageKeys.CORRECT_USAGE.sendMessage(sender, "{usage}", context.getSyntax()));

        this.commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> MessageKeys.CORRECT_USAGE.sendMessage(sender, "{usage}", context.getSyntax()));

        this.commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> MessageKeys.CORRECT_USAGE.sendMessage(sender, "{usage}", context.getSyntax()));

        this.commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> MessageKeys.NO_PERMISSION.sendMessage(sender, "{permission}", context.getPermission().toString()));

        this.commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> MessageKeys.PLAYERS_ONLY.sendMessage(sender));

        this.commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> MessageKeys.CONSOLE_ONLY.sendMessage(sender));
    }
}
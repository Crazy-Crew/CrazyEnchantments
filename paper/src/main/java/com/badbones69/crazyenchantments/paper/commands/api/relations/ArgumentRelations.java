
package com.badbones69.crazyenchantments.paper.commands.api.relations;

import com.ryderbelserion.common.api.enums.messages.Messages;
import com.badbones69.crazyenchantments.paper.commands.api.MessageManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;

import java.util.Map;

public class ArgumentRelations extends MessageManager {

    @Override
    public void build() {
        this.commandManager.registerMessage(BukkitMessageKey.UNKNOWN_COMMAND, (sender, context) -> Messages.command_not_found.sendMessage(sender, Map.of("%command%", context.getInvalidInput())));

        this.commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> Messages.correct_usage.sendMessage(sender, Map.of("%usage%", context.getSyntax())));

        this.commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> Messages.correct_usage.sendMessage(sender, Map.of("%usage%", context.getSyntax())));

        this.commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> Messages.correct_usage.sendMessage(sender, Map.of("%usage%", context.getSyntax())));

        this.commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> Messages.no_permission.sendMessage(sender, Map.of("%permission%", context.getPermission().toString())));

        this.commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, _) -> Messages.players_only.sendMessage(sender));

        this.commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, _) -> Messages.must_be_console_sender.sendMessage(sender));
    }
}
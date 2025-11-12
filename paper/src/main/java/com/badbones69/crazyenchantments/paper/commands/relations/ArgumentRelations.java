package com.badbones69.crazyenchantments.paper.commands.relations;

import com.badbones69.crazyenchantments.paper.commands.MessageManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import us.crazycrew.crazyenchantments.constants.MessageKeys;
import java.util.HashMap;

public class ArgumentRelations extends MessageManager {

    @Override
    public void build() {
        this.commandManager.registerMessage(BukkitMessageKey.UNKNOWN_COMMAND, (sender, context) -> {
            this.userRegistry.getUser(sender).sendMessage(MessageKeys.unknown_command, new HashMap<>() {{
                put("{command}", context.getInvalidInput());
            }});
        });

        this.commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> {
            this.userRegistry.getUser(sender).sendMessage(MessageKeys.correct_usage, new HashMap<>() {{
                put("{usage}", context.getSyntax());
            }});
        });

        this.commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> {
            this.userRegistry.getUser(sender).sendMessage(MessageKeys.correct_usage, new HashMap<>() {{
                put("{usage}", context.getSyntax());
            }});
        });

        this.commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> {
            this.userRegistry.getUser(sender).sendMessage(MessageKeys.correct_usage, new HashMap<>() {{
                put("{usage}", context.getSyntax());
            }});
        });

        this.commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> {
            this.userRegistry.getUser(sender).sendMessage(MessageKeys.no_permission, new HashMap<>() {{
                put("{permission}", context.getPermission().toString());
            }});
        });

        this.commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> {
            this.userRegistry.getUser(sender).sendMessage(MessageKeys.players_only);
        });

        this.commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> {
            this.userRegistry.getUser(sender).sendMessage(MessageKeys.console_only);
        });
    }
}
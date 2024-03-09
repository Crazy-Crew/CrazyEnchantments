package com.badbones69.crazyenchantments.paper.commands;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.commands.types.CommandHelp;
import com.badbones69.crazyenchantments.paper.commands.types.CommandInfo;
import com.badbones69.crazyenchantments.paper.commands.types.CommandLimit;
import com.badbones69.crazyenchantments.paper.commands.types.admin.CommandBottle;
import com.badbones69.crazyenchantments.paper.commands.types.admin.CommandConvert;
import com.badbones69.crazyenchantments.paper.commands.types.admin.CommandDebug;
import com.badbones69.crazyenchantments.paper.commands.types.admin.CommandFix;
import com.badbones69.crazyenchantments.paper.commands.types.admin.CommandReload;
import com.badbones69.crazyenchantments.paper.commands.types.admin.CommandUpdate;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandManager {

    @NotNull
    private final static CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final static BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(plugin);

    public static void load() {
        Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();

        getCommandManager().registerSuggestion(SuggestionKey.of("players"), (sender, context) -> players.stream().map(Player::getName).toList());

        getCommandManager().registerSuggestion(SuggestionKey.of("numbers"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            for (int i = 1; i <= 64; i++) numbers.add(String.valueOf(i));

            return numbers;
        });

        getCommandManager().registerMessage(BukkitMessageKey.INVALID_ARGUMENT, (sender, context) -> {
            //todo() add message
        });

        getCommandManager().registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> sender.sendMessage(Messages.NO_PERMISSION.getMessage()));

        getCommandManager().registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> sender.sendMessage(Messages.PLAYERS_ONLY.getMessage()));

        getCommandManager().registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> {
            //todo() add message
        });

        getCommandManager().registerMessage(BukkitMessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> {
            //todo() add message
        });

        getCommandManager().registerMessage(BukkitMessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> {
            //todo() add message
        });

        List.of(
                new TinkerCommand(),
                new SmithCommand(),

                new CommandUpdate(),
                new CommandLimit(),
                new CommandInfo(),
                new CommandHelp(),

                new CommandConvert(),
                new CommandBottle(),
                new CommandReload(),
                new CommandDebug(),
                new CommandFix()
        ).forEach(getCommandManager()::registerCommand);
    }

    @NotNull
    public static BukkitCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }
}
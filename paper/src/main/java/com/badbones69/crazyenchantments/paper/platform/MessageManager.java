package com.badbones69.crazyenchantments.paper.platform;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class MessageManager {

    protected final @NotNull BukkitCommandManager<CommandSender> commandManager = CommandManager.getCommandManager();

    public abstract void build();

    public abstract void send(@NotNull CommandSender sender, @NotNull String component);

    public abstract String parse(@NotNull String message);

}
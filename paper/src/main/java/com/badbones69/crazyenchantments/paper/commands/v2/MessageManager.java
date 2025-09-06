package com.badbones69.crazyenchantments.paper.commands.v2;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;

public abstract class MessageManager {

    protected final BukkitCommandManager<CommandSender> commandManager = CommandManager.getCommandManager();

    public abstract void build();

}
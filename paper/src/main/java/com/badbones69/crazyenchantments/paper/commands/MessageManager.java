package com.badbones69.crazyenchantments.paper.commands;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.registry.UserRegistry;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;

public abstract class MessageManager {

    protected final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    protected final CrazyInstance instance = this.plugin.getInstance();

    protected final UserRegistry userRegistry = this.instance.getUserRegistry();

    protected final BukkitCommandManager<CommandSender> commandManager = CommandManager.getCommandManager();

    public abstract void build();

}
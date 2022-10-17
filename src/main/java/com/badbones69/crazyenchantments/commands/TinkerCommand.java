package com.badbones69.crazyenchantments.commands;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.controllers.Tinkerer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TinkerCommand implements CommandExecutor {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
            return true;
        }

        // if (hasPermission(sender)) Tinkerer.openTinker((Player) sender);

        return true;
    }
    
    private boolean hasPermission(CommandSender sender) {
        return methods.hasPermission(sender, "tinker", true);
    }
}
package com.badbones69.crazyenchantments.commands;

import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.controllers.Tinkerer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TinkerCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
            return true;
        }

        if (hasPermission(sender)) {
            Tinkerer.openTinker((Player) sender);
        }

        return true;
    }
    
    private boolean hasPermission(CommandSender sender) {
        return Methods.hasPermission(sender, "tinker", true);
    }
}
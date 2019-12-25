package me.badbones69.crazyenchantments.commands;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.controllers.Tinkerer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TinkerCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
            return true;
        }
        if (hasPermission(sender, "tinker")) {
            Tinkerer.openTinker((Player) sender);
        }
        return true;
    }
    
    private boolean hasPermission(CommandSender sender, String permission) {
        return Methods.hasPermission(sender, permission, true);
    }
    
}
package com.badbones69.crazyenchantments.commands;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.controllers.BlackSmith;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BlackSmithCommand implements CommandExecutor {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Methods methods = plugin.getStarter().getMethods();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
            return true;
        }

        if (hasPermission(sender)) BlackSmith.openBlackSmith((Player) sender);

        return true;
    }
    
    private boolean hasPermission(CommandSender sender) {
        return methods.hasPermission(sender, "blacksmith", true);
    }
}
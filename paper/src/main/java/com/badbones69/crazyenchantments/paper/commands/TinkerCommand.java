package com.badbones69.crazyenchantments.paper.commands;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TinkerCommand implements CommandExecutor {

    @NotNull
    private final CrazyEnchantments plugin = CrazyEnchantments.get();

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this. starter.getMethods();
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
            return true;
        }

        if (hasPermission(sender)) this.plugin.getTinkerer().openTinker(player);

        return true;
    }
    
    private boolean hasPermission(CommandSender sender) {
        return this.methods.hasPermission(sender, "tinker", true);
    }
}
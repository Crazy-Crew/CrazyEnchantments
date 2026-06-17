package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.commands.EnchantCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class ReloadCommand extends EnchantCommand {

    @Command("reload")
    @Permission(value = "crazyenchantments.reload", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments reload")
    public void execute(final CommandSender sender) {
        final Server server = this.plugin.getServer();

        for (final Player player : server.getOnlinePlayers()) {
            this.crazyManager.backupCEPlayer(player);
        }

        this.platform.reload();

        MenuManager.load(); // Load crazyManager after as it will set the enchants in each category.

        this.crazyManager.load();

        BlackSmithManager.load();

        KitsManager.load();

        sender.sendMessage(Messages.CONFIG_RELOAD.getMessage());
    }
}
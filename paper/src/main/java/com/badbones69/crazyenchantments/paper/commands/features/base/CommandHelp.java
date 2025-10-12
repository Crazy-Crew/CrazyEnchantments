package com.badbones69.crazyenchantments.paper.commands.features.base;

import com.badbones69.crazyenchantments.paper.api.builders.types.ShopMenu;
import com.badbones69.crazyenchantments.paper.api.enums.v2.Messages;
import com.badbones69.crazyenchantments.paper.api.managers.ShopManager;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandHelp extends BaseCommand {

    @Command
    @Permission(value = "crazyenchantments.gui", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments")
    public void gui(final Player player) {
        final ShopManager shopManager = this.starter.getShopManager();

        new ShopMenu(player, shopManager.getInventoryName(), shopManager.getInventorySize()).open();
    }

    @Command("help")
    @Permission(value = "crazyenchantments.help", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments help")
    public void help(final CommandSender sender) {
        Messages.HELP.sendMessage(sender);
    }
}
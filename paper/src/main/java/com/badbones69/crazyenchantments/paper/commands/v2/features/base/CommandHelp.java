package com.badbones69.crazyenchantments.paper.commands.v2.features.base;

import com.badbones69.crazyenchantments.paper.api.builders.types.ShopMenu;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.managers.ShopManager;
import com.badbones69.crazyenchantments.paper.commands.v2.features.BaseCommand;
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

        player.openInventory(new ShopMenu(player, shopManager.getInventorySize(), shopManager.getInventoryName()).build().getInventory());
    }

    @Command("help")
    @Permission(value = "crazyenchantments.help", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments help")
    public void help(final CommandSender sender) {
        sender.sendMessage(Messages.HELP.getMessage());
    }
}
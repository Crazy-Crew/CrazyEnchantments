package com.badbones69.crazyenchantments.paper.commands.types.player;

import com.badbones69.crazyenchantments.paper.api.builders.types.ShopMenu;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.managers.ShopManager;
import com.badbones69.crazyenchantments.paper.commands.EnchantCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class HelpCommand extends EnchantCommand {

    @Command
    @Permission(value = "crazyenchantments.gui", def = PermissionDefault.TRUE)
    @Syntax("/crazyenchantments")
    public void gui(final Player player) {
        ShopManager shopManager = this.starter.getShopManager();

        player.openInventory(new ShopMenu(player, shopManager.getInventorySize(), shopManager.getInventoryName()).build().getInventory());
    }

    @Command("help")
    @Permission(value = "crazyenchantments.help", def = PermissionDefault.TRUE)
    @Syntax("/crazyenchantments help")
    public void execute(final CommandSender sender) {
        sender.sendMessage(Messages.HELP.getMessage());
    }
}
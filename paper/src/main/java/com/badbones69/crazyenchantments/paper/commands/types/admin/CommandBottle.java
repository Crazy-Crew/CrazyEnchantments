package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.builders.types.tinkerer.TinkererManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.commands.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

public class CommandBottle extends BaseCommand {

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @Command("bottle")
    @Permission(value = "crazyenchantments.bottle", def = PermissionDefault.OP)
    public void bottle(CommandSender sender, @Suggestion("players") Player target, @Suggestion("numbers") int total, @Suggestion("numbers") int amount) {
        ItemStack item = TinkererManager.getXPBottle(String.valueOf(total));
        item.setAmount(amount);

        if (target == null) {
            sender.sendMessage(Messages.NOT_ONLINE.getMessage());
            return;
        }

        if (item.isEmpty()) return;

        this.starter.getMethods().addItemToInventory(target, item);
    }
}
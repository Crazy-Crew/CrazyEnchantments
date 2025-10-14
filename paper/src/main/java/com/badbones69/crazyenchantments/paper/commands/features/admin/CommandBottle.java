package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.paper.api.enums.v2.Messages;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandBottle extends BaseCommand {

    @Command("bottle")
    @Permission(value = "crazyenchantments.bottle", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments bottle <player> <xp> <amount>")
    public void bottle(final CommandSender sender, final int xp, final int amount, @Optional final Player target) {
        Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        if (safePlayer == null) {
            Messages.NOT_ONLINE.sendMessage(sender);

            return;
        }

//        final ItemStack itemStack = TinkererManager.getXPBottle(xp, FileKeys.tinker.getYamlConfiguration());
//
//        if (itemStack == null) {
//            return;
//        }
//
//        itemStack.setAmount(amount <= 0 ? 1 : amount);
//
//        this.methods.addItemToInventory(safePlayer, itemStack);
    }
}
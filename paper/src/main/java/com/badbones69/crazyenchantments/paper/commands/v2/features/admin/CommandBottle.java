package com.badbones69.crazyenchantments.paper.commands.v2.features.admin;

import com.badbones69.crazyenchantments.paper.api.FileManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.tinkerer.TinkererManager;
import com.badbones69.crazyenchantments.paper.commands.v2.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;

public class CommandBottle extends BaseCommand {

    @Command("bottle")
    @Permission(value = "crazyenchantments.bottle", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments bottle <player> <xp> <amount>")
    public void bottle(final CommandSender sender, final Player player, final int xp, final int amount) {
        final ItemStack itemStack = TinkererManager.getXPBottle(xp, FileManager.Files.TINKER.getFile());

        if (itemStack == null) {
            return;
        }

        itemStack.setAmount(amount <= 0 ? 1 : amount);

        this.methods.addItemToInventory(player, itemStack);
    }
}
package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.Nullable;

public class CommandGive extends BaseCommand {

    @Command("give")
    @Permission(value = "crazyenchantments.give", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments give <item> [target]")
    public void give(final CommandSender sender, @Suggestion("items") final String item, @Nullable @Optional final Player target) {
        final Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        if (safePlayer == null) {
            Messages.NOT_ONLINE.sendMessage(sender);

            return;
        }

        final ItemStack itemStack = ItemBuilder.convertString(item).build();

        if (itemStack == null) {
            Messages.INVALID_ITEM_STRING.sendMessage(sender);

            return;
        }

        Methods.addItemToInventory(safePlayer, itemStack);
    }
}
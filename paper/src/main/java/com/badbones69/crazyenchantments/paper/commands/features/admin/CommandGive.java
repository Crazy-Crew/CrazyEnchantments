package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;

public class CommandGive extends BaseCommand {

    @Command("give")
    @Permission(value = "crazyenchantments.give", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments give <player> <string>")
    //completions.add("Item:DIAMOND_HELMET, Amount:1, Name:&6&lHat, Protection:4, Overload:1-5, Hulk:2-5, Lore:&aLine 1.,&aLine 2.");
    public void give(final CommandSender sender, final Player target, final String item) {
        final ItemStack itemStack = ItemBuilder.convertString(item).build();

        if (itemStack == null) {
            sender.sendMessage(Messages.INVALID_ITEM_STRING.getMessage());

            return;
        }

        this.methods.addItemToInventory(target, itemStack);
    }
}
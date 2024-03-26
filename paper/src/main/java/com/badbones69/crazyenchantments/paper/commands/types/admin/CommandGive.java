package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.paper.commands.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

public class CommandGive extends BaseCommand {

    private final @NotNull Starter starter = this.plugin.getStarter();

    @Command("give")
    @Permission(value = "crazyenchantments.give", def = PermissionDefault.OP)
    public void give(CommandSender sender, @Suggestion("players") Player target, String data) {
        ItemStack itemStack = ItemBuilder.convertString(data).build();

        if (itemStack == null) {
            //todo() send message to sender if item is invalid.
            return;
        }

        this.starter.getMethods().addItemToInventory(target, itemStack);

        //todo() send message to sender that the item was added.
    }
}
package com.badbones69.crazyenchantments.paper.platform.commands.types;

import com.badbones69.crazyenchantments.paper.platform.commands.BaseCommand;
import com.badbones69.crazyenchantments.paper.platform.items.CustomItem;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissionDefault;

public class CommandGive extends BaseCommand {

    @Command("give")
    @Permission(value = "crazyenchantments.give", def = PermissionDefault.OP)
    public void give(CommandSender sender, @Suggestion("players") Player target, CustomItem item) {
        PlayerInventory inventory = target.getInventory();

        if (!inventory.isEmpty()) {
            //todo() inventory is not empty.

            return;
        }

        //todo() send message to person who got the key. If the sender is the player, don't send a "receive" message.

        inventory.setItem(inventory.firstEmpty(), item.getItem());
    }
}
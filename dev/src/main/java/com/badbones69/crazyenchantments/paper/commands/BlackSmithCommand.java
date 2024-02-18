package com.badbones69.crazyenchantments.paper.commands;

import com.badbones69.crazyenchantments.paper.api.builders.types.BlackSmithMenu;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

@Command("blacksmith")
@Permission(value = "crazyenchantments.blacksmith", def = PermissionDefault.TRUE)
public class BlackSmithCommand {

    @Command
    public void run(Player player) {
        BlackSmithMenu inventory = new BlackSmithMenu(player, 27, "&b&lThe Black Smith");

        player.openInventory(inventory.build().getInventory());
    }
}
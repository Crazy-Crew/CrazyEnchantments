package com.badbones69.crazyenchantments.paper.commands.v2;

import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Description;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

@Command("blacksmith")
@Description("Opens the black smith gui.")
@Permission(value = "crazyenchantments.blacksmith", def = PermissionDefault.TRUE)
public class SmithCommand {

    @Command
    public void blacksmith(Player player) {
        MenuManager.openBlackSmithMenu(player);
    }
}
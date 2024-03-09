package com.badbones69.crazyenchantments.paper.commands;

import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Description;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

@Command(value = "tinkerer", alias = "tinker")
@Description("Opens the tinkerer gui.")
@Permission(value = "crazyenchantments.tinker", def = PermissionDefault.TRUE)
public class TinkerCommand {

    @Command
    public void tinkerer(Player player) {
        MenuManager.openTinkererMenu(player);
    }
}
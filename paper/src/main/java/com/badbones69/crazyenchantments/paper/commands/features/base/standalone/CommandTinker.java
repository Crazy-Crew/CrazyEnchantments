package com.badbones69.crazyenchantments.paper.commands.features.base.standalone;

import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

@Command(value = "tinker", alias = "tinkerer")
@Permission(value = "crazyenchantments.tinker", def = PermissionDefault.OP)
@Syntax("/tinker")
public class CommandTinker extends BaseCommand {

    @Command
    public void gui(final Player player) {
        MenuManager.openTinkererMenu(player);
    }
}
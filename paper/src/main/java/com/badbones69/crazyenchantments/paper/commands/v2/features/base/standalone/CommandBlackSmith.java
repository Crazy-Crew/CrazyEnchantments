package com.badbones69.crazyenchantments.paper.commands.v2.features.base.standalone;

import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.commands.v2.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

@Command(value = "blacksmith")
@Permission(value = "crazyenchantments.blacksmith", def = PermissionDefault.OP)
@Syntax("/blacksmith")
public class CommandBlackSmith extends BaseCommand {

    @Command
    public void gui(final Player player) {
        MenuManager.openBlackSmithMenu(player);
    }
}
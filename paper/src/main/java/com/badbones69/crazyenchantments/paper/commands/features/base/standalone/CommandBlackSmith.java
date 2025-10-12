package com.badbones69.crazyenchantments.paper.commands.features.base.standalone;

import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithMenu;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import com.badbones69.crazyenchantments.paper.managers.configs.types.BlackSmithConfig;
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
        final BlackSmithConfig config = this.options.getBlackSmithConfig();

        new BlackSmithMenu(player, config.getGuiName(), config.getGuiSize());
    }
}
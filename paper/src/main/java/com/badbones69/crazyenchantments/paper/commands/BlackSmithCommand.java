package com.badbones69.crazyenchantments.paper.commands;

import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Command(value = "blacksmith", alias = {"bsmith", "bs"})
@Syntax("/blacksmith [player]")
public class BlackSmithCommand {

    @Command
    @Permission(value = "crazyenchantments.blacksmith", def = PermissionDefault.TRUE)
    public void blacksmith(@NotNull final CommandSender sender, @Optional @Nullable final Player target) {
        if (target == null) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());

                return;
            }

            MenuManager.openBlackSmithMenu(player);

            return;
        }

        if (sender.hasPermission("crazyenchantments.blacksmith.others")) {
            MenuManager.openBlackSmithMenu(target);
        }
    }
}
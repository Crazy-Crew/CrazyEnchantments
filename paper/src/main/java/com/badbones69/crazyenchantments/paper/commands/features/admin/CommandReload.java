package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.paper.api.enums.files.MessageKeys;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class CommandReload extends BaseCommand {

    @Command("reload")
    @Permission(value = "crazyenchantments.reload", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments reload")
    public void reload(final CommandSender sender) {
        this.instance.reload(); // reload the plugin.

        this.crazyManager.getCEPlayers().forEach(name -> this.crazyManager.backupCEPlayer(name.getPlayer()));

        this.crazyManager.load();

        MessageKeys.CONFIG_RELOAD.sendMessage(sender);
    }
}
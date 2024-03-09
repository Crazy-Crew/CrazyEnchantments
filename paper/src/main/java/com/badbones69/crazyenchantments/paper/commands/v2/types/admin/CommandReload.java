package com.badbones69.crazyenchantments.paper.commands.v2.types.admin;

import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.FileManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.commands.v2.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

public class CommandReload extends BaseCommand {

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final FileManager fileManager = this.starter.getFileManager();

    @NotNull
    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    @Command("reload")
    @Permission(value = "crazyenchantments.reload", def = PermissionDefault.OP)
    public void reload(CommandSender sender) {
        // Back up players
        this.crazyManager.getCEPlayers().forEach(player -> this.crazyManager.backupCEPlayer(player.getPlayer()));

        // Reload files
        this.fileManager.setup();

        // Reload crazy manager
        this.crazyManager.load();

        // Update menu buttons.
        BlackSmithManager.load();
        KitsManager.load();
        MenuManager.load();

        // Update hooks.
        this.starter.getPluginSupport().updateHooks();

        // Send message
        sender.sendMessage(Messages.CONFIG_RELOAD.getMessage());
    }
}
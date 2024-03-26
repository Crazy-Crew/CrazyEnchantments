package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.ConfigManager;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.FileManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.platform.commands.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

public class CommandReload extends BaseCommand {

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull FileManager fileManager = this.starter.getFileManager();

    private final @NotNull CrazyManager crazyManager = this.starter.getCrazyManager();

    @Command("reload")
    @Permission(value = "crazyenchantments.reload", def = PermissionDefault.OP)
    public void reload(CommandSender sender) {
        // Reload the new configuration
        ConfigManager.reload();

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

        // Send message
        sender.sendRichMessage(Messages.CONFIG_RELOAD.getMessage());
    }
}
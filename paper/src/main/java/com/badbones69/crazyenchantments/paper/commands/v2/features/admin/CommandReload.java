package com.badbones69.crazyenchantments.paper.commands.v2.features.admin;

import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.utils.FileUtils;
import com.badbones69.crazyenchantments.paper.commands.v2.features.BaseCommand;
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
        this.crazyManager.getCEPlayers().forEach(name -> this.crazyManager.backupCEPlayer(name.getPlayer()));

        this.fileManager.setup();

        MenuManager.load(); // Load crazyManager after as it will set the enchants in each category.

        this.crazyManager.load();

        BlackSmithManager.load();
        KitsManager.load();

        sender.sendMessage(Messages.CONFIG_RELOAD.getMessage());

        FileUtils.loadFiles();
    }
}
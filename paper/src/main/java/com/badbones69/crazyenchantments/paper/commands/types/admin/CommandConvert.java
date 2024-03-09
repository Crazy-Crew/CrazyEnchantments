package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.api.MigrateManager;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.commands.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import java.util.List;

public class CommandConvert extends BaseCommand {

    @Command("convert")
    @Permission(value = "crazyenchantments.convert", def = PermissionDefault.OP)
    public void convert(CommandSender sender) {
        List.of(
                "&8&m=====================================================",
                "&eTrying to update config files.",
                "&eIf you have any issues, Please contact Discord Support.",
                "&f&nhttps://discord.gg/crazycrew&r",
                "&eMake sure to check console for more information.",
                "&8&m====================================================="
        ).forEach(line -> ColorUtils.sendMessage(sender, line, true));

        MigrateManager.convert();
    }
}
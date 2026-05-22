package com.badbones69.crazyenchantments.paper.commands.types.admin.debug;

import com.badbones69.crazyenchantments.paper.api.MigrateManager;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.commands.EnchantCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class ConvertCommand extends EnchantCommand {

    @Command("convert")
    @Permission(value = "crazyenchantments.convert", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments convert")
    public void execute(final CommandSender sender) {
        sender.sendMessage(ColorUtils.color("""
                            \n&8&m=======================================================
                            &eTrying to update config FileKeys.
                            &eIf you have any issues, Please contact Discord Support.
                            &f&nhttps://discord.gg/crazycrew&r
                            &eMake sure to check console for more information.
                            &8&m=======================================================
                            """));

        MigrateManager.convert();
    }
}
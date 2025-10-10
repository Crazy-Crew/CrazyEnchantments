package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.v2.Messages;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CommandDust extends BaseCommand {

    @Command("dust")
    @Permission(value = "crazyenchantments.dust", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments dust <Success/Destroy/Mystery> <amount> <percent> [player]")
    public void dust(final CommandSender sender, final Dust dust, final int amount, final int percent, @Optional @Nullable final Player target) {
        Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        if (safePlayer == null) {
            Messages.NOT_ONLINE.sendMessage(sender);

            return;
        }

        if (dust == null) {
            //todo() tell dust is null.

            return;
        }

        this.methods.addItemToInventory(safePlayer, dust.getDust(percent, amount));

        Map<String, String> placeholders = new HashMap<>() {{
            put("%Amount%", String.valueOf(amount));
            put("%Player%", safePlayer.getName());
        }};

        switch (dust) {
            case SUCCESS_DUST -> {
                Messages.GET_SUCCESS_DUST.sendMessage(safePlayer, placeholders);

                if (!sender.getName().equalsIgnoreCase(safePlayer.getName())) {
                    Messages.GIVE_SUCCESS_DUST.sendMessage(sender, placeholders);
                }
            }

            case DESTROY_DUST -> {
                Messages.GET_DESTROY_DUST.sendMessage(safePlayer, placeholders);

                if (!sender.getName().equalsIgnoreCase(safePlayer.getName())) {
                    Messages.GIVE_DESTROY_DUST.sendMessage(sender, placeholders);
                }
            }

            case MYSTERY_DUST -> {
                Messages.GET_MYSTERY_DUST.sendMessage(safePlayer, placeholders);

                if (!sender.getName().equalsIgnoreCase(safePlayer.getName())) {
                    Messages.GIVE_MYSTERY_DUST.sendMessage(sender, placeholders);
                }
            }
        }
    }
}
package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.objects.User;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.enums.shop.Dust;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazyenchantments.constants.MessageKeys;
import java.util.HashMap;
import java.util.Map;

public class CommandDust extends BaseCommand {

    @Command("dust")
    @Permission(value = "crazyenchantments.dust", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments dust <Success/Destroy/Mystery> <amount> <percent> [player]")
    public void dust(final CommandSender sender, final Dust dust, final int amount, final int percent, @Optional @Nullable final Player target) {
        Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        if (safePlayer == null) {
            this.userRegistry.getUser(sender).sendMessage(MessageKeys.not_online);

            return;
        }

        if (dust == null) {
            //todo() tell dust is null.

            return;
        }

        Methods.addItemToInventory(safePlayer, dust.getDust(percent, amount));

        Map<String, String> placeholders = new HashMap<>() {{
            put("%Amount%", String.valueOf(amount));
            put("%Player%", safePlayer.getName());
        }};

        final User user = this.userRegistry.getUser(safePlayer);

        final String playerName = safePlayer.getName();
        final String senderName = sender.getName();

        switch (dust) {
            case SUCCESS_DUST -> {
                user.sendMessage(MessageKeys.get_success_dust, placeholders);

                if (!sender.getName().equalsIgnoreCase(playerName)) {
                    this.userRegistry.getUser(sender).sendMessage(MessageKeys.give_success_dust, placeholders);
                }
            }

            case DESTROY_DUST -> {
                user.sendMessage(MessageKeys.get_destroy_dust, placeholders);

                if (!senderName.equalsIgnoreCase(playerName)) {
                    this.userRegistry.getUser(sender).sendMessage(MessageKeys.give_destroy_dust, placeholders);
                }
            }

            case MYSTERY_DUST -> {
                user.sendMessage(MessageKeys.get_mystery_dust, placeholders);

                if (!senderName.equalsIgnoreCase(playerName)) {
                    this.userRegistry.getUser(sender).sendMessage(MessageKeys.give_mystery_dust, placeholders);
                }
            }
        }
    }
}
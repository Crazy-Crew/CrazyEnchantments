package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.objects.User;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
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

public class CommandBook extends BaseCommand {

    @Command("book")
    @Permission(value = "crazyenchantments.book", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments book <enchantment> <amount> [player]")
    public void book(final CommandSender sender, final CEnchantment enchantment, final int level, final int amount, @Optional @Nullable final Player target) {
        final Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        final User user = this.userRegistry.getUser(sender);

        if (safePlayer == null) {
            user.sendMessage(MessageKeys.not_online);

            return;
        }

        user.sendMessage(MessageKeys.send_enchantment_book, new HashMap<>() {{
            put("{player}", safePlayer.getName());
        }});

        Methods.addItemToInventory(safePlayer, new CEBook(enchantment, level, amount).buildBook());
    }
}
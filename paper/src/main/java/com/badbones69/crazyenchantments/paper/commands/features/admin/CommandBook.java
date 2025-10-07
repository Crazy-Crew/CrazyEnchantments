package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.paper.api.enums.Messages;
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
import java.util.HashMap;

public class CommandBook extends BaseCommand {

    @Command("book")
    @Permission(value = "crazyenchantments.book", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments book <enchantment> <amount> [player]")
    public void book(final CommandSender sender, final CEnchantment enchantment, final int level, final int amount, @Optional @Nullable final Player target) {
        Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        if (safePlayer == null) {
            sender.sendMessage(Messages.NOT_ONLINE.getMessage());

            return;
        }

        safePlayer.sendMessage(Messages.SEND_ENCHANTMENT_BOOK.getMessage(new HashMap<>() {{
            put("%Player%", safePlayer.getName());
        }}));

        this.methods.addItemToInventory(safePlayer, new CEBook(enchantment, level, amount).buildBook());
    }
}
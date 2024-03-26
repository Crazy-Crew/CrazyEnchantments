package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.commands.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class CommandBook extends BaseCommand {

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull Methods methods = this.starter.getMethods();

    private final @NotNull CrazyManager crazyManager = this.starter.getCrazyManager();

    @Command("book")
    @Permission(value = "crazyenchantments.book", def = PermissionDefault.OP)
    public void book(CommandSender sender, @Suggestion("enchants") String name, @Suggestion("numbers") int level, @Suggestion("numbers") int amount, @Suggestion("players") Player target) {
        CEnchantment enchantment = this.crazyManager.getEnchantmentFromName(name);

        if (enchantment == null) {
            sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
            return;
        }

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%Player%", target.getName());

        sender.sendMessage(Messages.SEND_ENCHANTMENT_BOOK.getMessage(placeholders));

        this.methods.addItemToInventory(target, new CEBook(enchantment, level, amount).buildBook());
    }
}
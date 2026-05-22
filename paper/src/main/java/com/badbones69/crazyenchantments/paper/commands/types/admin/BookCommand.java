package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BookCommand extends com.badbones69.crazyenchantments.paper.commands.EnchantCommand {

    @Command(value = "book")
    @Permission(value = "crazyenchantments.book", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments book <enchantment> <level> <amount> [player]")
    public void execute(final CommandSender sender, @ArgName("custom_enchantment") @Suggestion("custom_enchantments") final String name, @Suggestion("ce_enchantment_numbers") final int level, @Suggestion("numbers") final int amount, final Player player) {
        final PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            player.sendMessage("Inventory must be empty.");

            return;
        }
        
        Optional.ofNullable(this.crazyManager.getEnchantmentFromName(name)).ifPresentOrElse(book -> {
            final ItemStack itemStack = new CEBook(book, level, amount).buildBook(); //todo() random support

            if (itemStack.isEmpty()) {
                player.sendMessage("Item can't be empty.");

                return;
            }
            
            inventory.addItem(itemStack);

            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("%Player%", player.getName());

            sender.sendMessage(Messages.SEND_ENCHANTMENT_BOOK.getMessage(placeholders));
        }, () -> {
            //todo() send message

            //HashMap<String, String> placeholders = new HashMap<>();
            
            //placeholders.put("%Category%", name);
            
            //sender.sendMessage(Messages.NOT_A_CATEGORY.getMessage(placeholders));
        });
    }
}
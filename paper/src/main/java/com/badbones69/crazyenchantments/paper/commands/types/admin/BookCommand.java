package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.ryderbelserion.common.api.enums.messages.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.commands.BaseEnchantCommand;
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

public class BookCommand extends BaseEnchantCommand {

    @Command(value = "book")
    @Permission(value = "crazyenchantments.book", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments book <enchantment> <level> <amount> [player]")
    public void execute(final CommandSender sender, @ArgName("custom_enchantment") @Suggestion("custom_enchantments") final String name, @Suggestion("ce_enchantment_numbers") final int level, @Suggestion("numbers") final int amount, final Player player) {
        final PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            Messages.inventory_full.sendMessage(player); //todo() send message to sender as well, and make this message more verbose.

            return;
        }
        
        Optional.ofNullable(this.crazyManager.getEnchantmentFromName(name)).ifPresentOrElse(book -> {
            final ItemStack itemStack = new CEBook(book, level, amount).buildBook(player); //todo() random support

            if (itemStack.isEmpty()) {
                Messages.item_cannot_be_empty.sendMessage(sender, Map.of(
                        "%command%",
                        "book"
                ));

                return;
            }
            
            inventory.addItem(itemStack);

            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("%Player%", player.getName());

            Messages.send_enchantment_book.sendMessage(sender, placeholders);
        }, () -> {
            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("%Category%", name);

            Messages.not_a_category.sendMessage(sender, placeholders);
        });
    }
}
package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;
import java.util.Optional;

public class LostBookCommand extends com.badbones69.crazyenchantments.paper.commands.EnchantCommand {

    @Command(value = "lostbook", alias = {"lb"})
    @Permission(value = "crazyenchantments.lostbook", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments lostbook <category> <amount> [player]")
    public void execute(final CommandSender sender, @Suggestion("categories") final String name, @Suggestion("numbers") final int amount, final Player player) {
        final PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            player.sendMessage("Inventory must be empty.");

            return;
        }

        Optional.ofNullable(this.bookSettings.getCategory(name)).ifPresentOrElse(category -> {
            final ItemStack itemStack = category.getLostBook().getLostBook(category, amount).build();

            if (itemStack.isEmpty()) {
                player.sendMessage("Item can't be empty.");

                return;
            }

            inventory.addItem(itemStack);

            //todo() send message
        }, () -> {
            HashMap<String, String> placeholders = new HashMap<>();

            placeholders.put("%Category%", name);

            sender.sendMessage(Messages.NOT_A_CATEGORY.getMessage(placeholders));
        });
    }
}
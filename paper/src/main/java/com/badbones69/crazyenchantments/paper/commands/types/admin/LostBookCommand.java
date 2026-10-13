package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.ryderbelserion.common.api.enums.messages.Messages;
import com.badbones69.crazyenchantments.paper.commands.BaseEnchantCommand;
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
import java.util.Map;
import java.util.Optional;

public class LostBookCommand extends BaseEnchantCommand {

    @Command(value = "lostbook", alias = {"lb"})
    @Permission(value = "crazyenchantments.lostbook", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments lostbook <category> <amount> [player]")
    public void execute(final CommandSender sender, @Suggestion("categories") final String name, @Suggestion("numbers") final int amount, final Player player) {
        final PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            Messages.inventory_full.sendMessage(player);

            return;
        }

        Optional.ofNullable(this.bookSettings.getCategory(name)).ifPresentOrElse(category -> {
            final ItemStack itemStack = category.getLostBook().getLostBook(category, amount).build(player);

            if (itemStack.isEmpty()) {
                Messages.item_cannot_be_empty.sendMessage(sender, Map.of(
                        "%command%",
                        "lostbook"
                ));

                return;
            }

            inventory.addItem(itemStack);

            final Map<String, String> placeholders = new HashMap<>();

            placeholders.putIfAbsent("%category%", category.getName());

            Messages.get_lostbook.sendMessage(player, placeholders);

            placeholders.putIfAbsent("%player%", player.getName());

            Messages.give_lostbook.sendMessage(sender, placeholders);
        }, () -> {
            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("%Category%", name);

            Messages.not_a_category.sendMessage(sender, placeholders);
        });
    }
}
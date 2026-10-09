package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.api.enums.Dust;
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

public class DustCommand extends BaseEnchantCommand {

    @Command(value = "dust")
    @Permission(value = "crazyenchantments.dust", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments dust <destroy,failed,mystery,success> <amount> [player]")
    public void execute(final CommandSender sender, @Suggestion("dust") final String name, @Suggestion("numbers") final int amount, final Player player, @Suggestion("numbers") final int percent) {
        final PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            Messages.inventory_full.sendMessage(player); //todo() add an extra message, update default message

            return;
        }

        Optional.ofNullable(Dust.getFromName(name)).ifPresentOrElse(dust -> {
            final ItemStack itemStack = dust.getDust(player, percent, amount); //todo() random support

            if (itemStack.isEmpty()) {
                Messages.item_cannot_be_empty.sendMessage(sender, Map.of(
                        "%command%",
                        "dust"
                ));

                return;
            }

            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("%Amount%", String.valueOf(amount));
            placeholders.put("%Player%", player.getName());

            inventory.addItem(itemStack);

            switch (dust) {
                case SUCCESS_DUST -> {
                    Messages.get_success_dust.sendMessage(player, placeholders);
                    Messages.give_success_dust.sendMessage(sender, placeholders);
                }

                case DESTROY_DUST -> {
                    Messages.get_destroy_dust.sendMessage(player, placeholders);
                    Messages.give_destroy_dust.sendMessage(sender, placeholders);
                }

                case MYSTERY_DUST -> {
                    Messages.get_mystery_dust.sendMessage(player, placeholders);
                    Messages.give_mystery_dust.sendMessage(sender, placeholders);
                }
            }
        }, () -> {
            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("%Category%", name);

            Messages.not_a_category.sendMessage(player, placeholders);
        });
    }
}
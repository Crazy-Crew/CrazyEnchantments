package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
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

public class DustCommand extends com.badbones69.crazyenchantments.paper.commands.EnchantCommand {

    @Command(value = "dust")
    @Permission(value = "crazyenchantments.dust", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments dust <destroy,failed,mystery,success> <amount> [player]")
    public void execute(final CommandSender sender, @Suggestion("dust") final String name, @Suggestion("numbers") final int amount, final Player player, @Suggestion("numbers") final int percent) {
        final PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            player.sendMessage(Messages.INVENTORY_FULL.getMessage());

            return;
        }

        Optional.ofNullable(Dust.getFromName(name)).ifPresentOrElse(dust -> {
            final ItemStack itemStack = dust.getDust(percent, amount); //todo() random support

            if (itemStack.isEmpty()) {
                sender.sendMessage(Messages.ITEM_CANNOT_BE_EMPTY.getMessage(Map.of(
                        "%command%",
                        "dust"
                )));

                return;
            }

            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("%Amount%", String.valueOf(amount));
            placeholders.put("%Player%", player.getName());

            inventory.addItem(itemStack);

            switch (dust) {
                case SUCCESS_DUST -> {
                    player.sendMessage(Messages.GET_SUCCESS_DUST.getMessage(placeholders));
                    sender.sendMessage(Messages.GIVE_SUCCESS_DUST.getMessage(placeholders));
                }

                case DESTROY_DUST -> {
                    player.sendMessage(Messages.GET_DESTROY_DUST.getMessage(placeholders));
                    sender.sendMessage(Messages.GIVE_DESTROY_DUST.getMessage(placeholders));
                }

                case MYSTERY_DUST -> {
                    player.sendMessage(Messages.GET_MYSTERY_DUST.getMessage(placeholders));
                    sender.sendMessage(Messages.GIVE_MYSTERY_DUST.getMessage(placeholders));
                }
            }
        }, () -> {
            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("%Category%", name);

            sender.sendMessage(Messages.NOT_A_CATEGORY.getMessage(placeholders));
        });
    }
}
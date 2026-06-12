package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.api.builders.types.tinkerer.TinkererManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.keys.FileKeys;
import com.badbones69.crazyenchantments.paper.commands.EnchantCommand;
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

public class BottleCommand extends EnchantCommand {

    @Command("bottle")
    @Permission(value = "crazyenchantments.bottle", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments bottle [player] [xp-amount] [amount]")
    public void execute(final CommandSender sender, final Player player, @Suggestion("numbers") final int xp, @Suggestion("numbers") final int amount) {
        final ItemStack itemStack = TinkererManager.getXPBottle(String.valueOf(xp), FileKeys.TINKER.getConfiguration());

        itemStack.setAmount(amount);

        if (itemStack.isEmpty()) {
            sender.sendMessage(Messages.ITEM_CANNOT_BE_EMPTY.getMessage(Map.of(
                    "%command%",
                    "bottle"
            )));

            return;
        }

        final PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            player.sendMessage(Messages.INVENTORY_FULL.getMessage());

            return;
        }

        inventory.addItem(itemStack);

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.putIfAbsent("%amount%", String.valueOf(amount));

        player.sendMessage(Messages.GET_BOTTLE.getMessage(placeholders));

        placeholders.putIfAbsent("%player%", player.getName());

        sender.sendMessage(Messages.GIVE_BOTTLE.getMessage(placeholders));
    }
}
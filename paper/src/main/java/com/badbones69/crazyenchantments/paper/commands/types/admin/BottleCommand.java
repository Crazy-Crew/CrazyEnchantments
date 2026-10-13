package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.api.builders.types.tinkerer.TinkererManager;
import com.ryderbelserion.common.api.enums.messages.Messages;
import com.ryderbelserion.common.api.enums.Files;
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

public class BottleCommand extends BaseEnchantCommand {

    @Command("bottle")
    @Permission(value = "crazyenchantments.bottle", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments bottle [player] [xp-amount] [amount]")
    public void execute(final CommandSender sender, final Player player, @Suggestion("numbers") final int xp, @Suggestion("numbers") final int amount) {
        final ItemStack itemStack = TinkererManager.getXPBottle(player, String.valueOf(xp), Files.TINKER.getConfiguration());

        itemStack.setAmount(amount);

        if (itemStack.isEmpty()) {
            Messages.item_cannot_be_empty.sendMessage(sender, Map.of(
                    "%command%",
                    "bottle"
            ));

            return;
        }

        final PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            Messages.inventory_full.sendMessage(player); //todo() send message to sender as well, and make this message more verbose.

            return;
        }

        inventory.addItem(itemStack);

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.putIfAbsent("%amount%", String.valueOf(amount));

        Messages.get_bottle.sendMessage(player, placeholders);

        placeholders.putIfAbsent("%player%", player.getName());

        Messages.give_bottle.sendMessage(sender, placeholders);
    }
}
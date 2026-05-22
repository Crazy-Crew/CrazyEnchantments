package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.api.builders.types.tinkerer.TinkererManager;
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

public class BottleCommand extends EnchantCommand {

    @Command("bottle")
    @Permission(value = "crazyenchantments.bottle", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments bottle [player] [xp-amount] [amount]")
    public void execute(final CommandSender sender, final Player player, @Suggestion("numbers") final int xp, @Suggestion("numbers") final int amount) {
        final ItemStack itemStack = TinkererManager.getXPBottle(String.valueOf(xp), FileKeys.TINKER.getConfiguration());

        itemStack.setAmount(amount);

        if (itemStack.isEmpty()) {
            player.sendMessage("Item can't be empty.");

            return;
        }

        final PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            player.sendMessage("Inventory must be empty.");

            return;
        }

        inventory.addItem(itemStack);
    }
}
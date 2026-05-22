package com.badbones69.crazyenchantments.paper.commands.types.admin.crystal;

import com.badbones69.crazyenchantments.paper.api.enums.Messages;
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

public class ScramblerCommand extends EnchantCommand {

    @Command("scrambler")
    @Permission(value = "crazyenchantments.scrambler", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments scrambler [amount] [player]")
    public void execute(final CommandSender sender, @Suggestion("numbers") final int amount, final Player player) {
        final ItemStack itemStack = this.scrambler.getScramblers(amount);

        if (itemStack.isEmpty()) {
            player.sendMessage("Item can't be empty.");

            return;
        }

        final PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            player.sendMessage("Inventory must be empty.");

            return;
        }

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%Amount%", String.valueOf(amount));
        placeholders.put("%Player%", player.getName());

        inventory.addItem(itemStack);

        sender.sendMessage(Messages.GIVE_SCRAMBLER_CRYSTAL.getMessage(placeholders));
        player.sendMessage(Messages.GET_SCRAMBLER.getMessage(placeholders));
    }
}
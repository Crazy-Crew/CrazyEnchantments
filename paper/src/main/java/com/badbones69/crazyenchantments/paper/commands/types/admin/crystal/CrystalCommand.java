package com.badbones69.crazyenchantments.paper.commands.types.admin.crystal;

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

public class CrystalCommand extends BaseEnchantCommand {

    @Command("crystal")
    @Permission(value = "crazyenchantments.crystal", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments crystal [amount] [player]")
    public void execute(final CommandSender sender, @Suggestion("numbers") final int amount, final Player player) {
        final ItemStack itemStack = this.crystal.getCrystal(player, amount);
        
        if (itemStack.isEmpty()) {
            Messages.item_cannot_be_empty.sendMessage(sender, Map.of(
                    "%command%",
                    "crystal"
            ));
            
            return;
        }

        final PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            Messages.inventory_full.sendMessage(player); //todo() send message to sender as well, and make this message more verbose.

            return;
        }

        final Map<String, String> placeholders = new HashMap<>();
        
        placeholders.put("%Amount%", String.valueOf(amount));
        placeholders.put("%Player%", player.getName());
        
        inventory.addItem(itemStack);

        Messages.give_protection_crystal.sendMessage(sender, placeholders);
        Messages.get_protection_crystal.sendMessage(player, placeholders);
    }
}
package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.objects.User;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.enums.DataKeys;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.persistence.PersistentDataType;
import us.crazycrew.crazyenchantments.constants.MessageKeys;

public class CommandBottle extends BaseCommand {

    @Command("bottle")
    @Permission(value = "crazyenchantments.bottle", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments bottle <player> <xp> <amount>")
    public void bottle(final CommandSender sender, final int xp, final int amount, @Optional final Player target) {
        final Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        final User user = this.userRegistry.getUser(sender);

        if (safePlayer == null) {
            user.sendMessage(MessageKeys.not_online);

            return;
        }

        this.itemManager.getItem("tinker_exp_bottle").ifPresent(item -> {
            final ItemStack itemStack = item.getItemStack(safePlayer);

            if (itemStack == null) return;

            itemStack.editPersistentDataContainer(container -> container.set(DataKeys.experience.getNamespacedKey(), PersistentDataType.INTEGER, xp));

            itemStack.setAmount(Math.max(amount, 1));

            Methods.addItemToInventory(safePlayer, itemStack);
        });
    }
}
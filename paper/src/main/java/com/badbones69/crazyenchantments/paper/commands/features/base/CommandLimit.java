package com.badbones69.crazyenchantments.paper.commands.features.base;

import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;
import java.util.Map;

public class CommandLimit extends BaseCommand {

    @Command("limit")
    @Permission(value = "crazyenchantments.limit", def = PermissionDefault.TRUE)
    @Syntax("/crazyenchantments limit")
    public void limit(final Player player) {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%bypass%", String.valueOf(player.hasPermission("crazyenchantments.bypass.limit")));

        final ItemStack item = player.getInventory().getItemInMainHand();

        int limit = this.crazyManager.getPlayerMaxEnchantments(player);
        int baseLimit = this.crazyManager.getPlayerBaseEnchantments(player);
        int slotModifier = item.isEmpty() ? 0 : this.crazyManager.getEnchantmentLimiter(item);
        int enchantAmount = item.isEmpty() ? 0 : this.enchantmentBookSettings.getEnchantmentAmount(item, this.crazyManager.checkVanillaLimit());

        int canAdd = Math.min(baseLimit - slotModifier, limit);

        placeholders.put("%limit%", String.valueOf(limit));
        placeholders.put("%baseLimit%", String.valueOf(baseLimit));
        placeholders.put("%vanilla%", String.valueOf(this.crazyManager.checkVanillaLimit()));
        placeholders.put("%item%", String.valueOf(enchantAmount));
        placeholders.put("%slotCrystal%", String.valueOf(-slotModifier));
        placeholders.put("%space%", String.valueOf(canAdd - enchantAmount));
        placeholders.put("%canHave%", String.valueOf(canAdd));
        placeholders.put("%limitSetInConfig%", String.valueOf(this.crazyManager.useConfigLimit()));

        player.sendMessage(Messages.LIMIT_COMMAND.getMessage(placeholders));
    }
}
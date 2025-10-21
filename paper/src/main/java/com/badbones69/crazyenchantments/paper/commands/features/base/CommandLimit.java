package com.badbones69.crazyenchantments.paper.commands.features.base;

import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazyenchantments.constants.MessageKeys;
import java.util.HashMap;
import java.util.Map;

public class CommandLimit extends BaseCommand {

    @Command("limit")
    @Permission(value = "crazyenchantments.limit", def = PermissionDefault.TRUE)
    @Syntax("/crazyenchantments limit")
    public void limit(final Player player) {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{bypass}", String.valueOf(player.hasPermission("crazyenchantments.bypass.limit")));

        final ItemStack item = player.getInventory().getItemInMainHand();

        //int limit = this.crazyManager.getPlayerMaxEnchantments(player);
        int limit = 0;
        //int baseLimit = this.crazyManager.getPlayerBaseEnchantments(player);
        int baseLimit = 0;
        //int slotModifier = item.isEmpty() ? 0 : this.crazyManager.getEnchantmentLimiter(item);
        int slotModifier = 0;
        int enchantAmount = item.isEmpty() ? 0 : this.instance.getEnchantmentAmount(item, this.options.isCheckVanillaLimit());

        int canAdd = Math.min(baseLimit - slotModifier, limit);

        placeholders.put("{limit}", String.valueOf(limit));
        placeholders.put("{base_limit}", String.valueOf(baseLimit));
        placeholders.put("{vanilla}", String.valueOf(this.options.isCheckVanillaLimit()));
        placeholders.put("{item}", String.valueOf(enchantAmount));
        placeholders.put("{slot_crystal}", String.valueOf(-slotModifier));
        placeholders.put("{space}", String.valueOf(canAdd - enchantAmount));
        placeholders.put("{max_enchants}", String.valueOf(canAdd));
        placeholders.put("{config_limit}", String.valueOf(this.options.isUseConfigLimits()));

        this.userRegistry.getUser(player).sendMessage(MessageKeys.limit_command, placeholders);
    }
}
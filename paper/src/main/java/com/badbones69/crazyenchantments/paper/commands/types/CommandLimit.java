package com.badbones69.crazyenchantments.paper.commands.types;

import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.commands.BaseCommand;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class CommandLimit extends BaseCommand {

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull CrazyManager crazyManager = this.starter.getCrazyManager();

    private final @NotNull EnchantmentBookSettings bookSettings = this.starter.getEnchantmentBookSettings();

    @Command("limit")
    @Permission(value = "crazyenchantments.limit", def = PermissionDefault.OP)
    public void limit(Player player) {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%bypass%", String.valueOf(player.hasPermission("crazyenchantments.bypass.limit")));

        ItemStack item = player.getInventory().getItemInMainHand();

        int limit = this.crazyManager.getPlayerMaxEnchantments(player);
        int baseLimit = this.crazyManager.getPlayerBaseEnchantments(player);
        boolean vanillaLimit = this.crazyManager.checkVanillaLimit();
        int modifier = item.isEmpty() ? 0 : this.crazyManager.getEnchantmentLimiter(item);

        int math = Math.min(baseLimit - modifier, limit);

        placeholders.put("%limit%", String.valueOf(limit));
        placeholders.put("%baseLimit%", String.valueOf(baseLimit));
        placeholders.put("%vanilla%", String.valueOf(vanillaLimit));
        placeholders.put("%item%", String.valueOf(item.isEmpty() ? 0 : this.bookSettings.getEnchantmentAmount(item, vanillaLimit)));
        placeholders.put("%slotCrystal%", String.valueOf(-modifier));
        placeholders.put("%space%", String.valueOf(math));

        player.sendRichMessage(Messages.LIMIT_COMMAND.getMessage(placeholders));
    }
}
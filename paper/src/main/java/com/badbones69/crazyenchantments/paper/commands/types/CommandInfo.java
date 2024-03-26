package com.badbones69.crazyenchantments.paper.commands.types;

import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.platform.commands.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

public class CommandInfo extends BaseCommand {

    private final Starter starter = this.plugin.getStarter();

    private final @NotNull CrazyManager crazyManager = this.starter.getCrazyManager();

    @Command("info")
    @Permission(value = "crazyenchantments.info", def = PermissionDefault.TRUE)
    public void info(Player player) {
        MenuManager.openInfoMenu(player);
    }

    @Command("info")
    @Permission(value = "crazyenchantments.info", def = PermissionDefault.TRUE)
    public void info(CommandSender sender, @Suggestion("enchants") String enchantmentName) {
        if (sender instanceof Player player) {
            EnchantmentType type = this.starter.getMethods().getFromName(enchantmentName);

            if (type != null) {
                MenuManager.openInfoMenu(player, type);

                return;
            }

            return;
        }

        CEnchantment enchantment = this.crazyManager.getEnchantmentFromName(enchantmentName);

        if (enchantment != null) {
            sender.sendRichMessage(enchantment.getInfoName());
            enchantment.getInfoDescription().forEach(sender::sendRichMessage);

            return;
        }

        sender.sendRichMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
    }
}
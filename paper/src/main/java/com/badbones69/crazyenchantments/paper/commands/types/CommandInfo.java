package com.badbones69.crazyenchantments.paper.commands.types;

import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.commands.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

public class CommandInfo extends BaseCommand {

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    @Command("info")
    @Permission(value = "crazyenchantments.info", def = PermissionDefault.TRUE)
    public void info(Player player) {
        MenuManager.openInfoMenu(player);
    }

    @Command("info")
    @Permission(value = "crazyenchantments.info", def = PermissionDefault.TRUE)
    public void info(CommandSender sender, String enchantmentName) {
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
            sender.sendMessage(enchantment.getInfoName());
            enchantment.getInfoDescription().forEach(sender::sendMessage);

            return;
        }

        sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
    }
}
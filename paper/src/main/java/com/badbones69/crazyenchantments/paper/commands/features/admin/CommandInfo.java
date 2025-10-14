package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.paper.api.builders.types.BaseMenu;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Flag;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandInfo extends BaseCommand {

    @SuppressWarnings("ConstantValue")
    @Command("info")
    @Permission(value = "crazyenchantments.info", def = PermissionDefault.OP)
    @Flag(flag = "t", longFlag = "type", argument = EnchantmentType.class)
    @Flag(flag = "ce", longFlag = "custom", argument = CEnchantment.class)
    @Syntax("/crazyenchantments info [-t/--type] [-ce/--custom]")
    public void info(final CommandSender sender, final Flags flags) {
        final boolean hasEnchantType = flags.hasFlag("t");
        final boolean hasCustomEnchant = flags.hasFlag("ce");

        if (sender instanceof Player player) {
            final YamlConfiguration configuration = FileKeys.enchantment_types.getPaperConfiguration();

            final String title = configuration.getString("Info-GUI-Settings.Inventory.Name", "<red><bold>Enchantment Info");
            final int size = configuration.getInt("Info-GUI-Settings.Inventory.Size", 18);

            if (!hasEnchantType && !hasCustomEnchant) {
                new BaseMenu(player, title, size).open();

                return;
            }

            if (hasEnchantType) {
                flags.getFlagValue("t", EnchantmentType.class).ifPresent(action -> new BaseMenu(player, title, size).setEnchantmentType(action).open());

                return;
            }

            if (hasCustomEnchant) {
                flags.getFlagValue("ce", CEnchantment.class).ifPresent(action -> {
                    sender.sendMessage(action.getInfoName());

                    action.getInfoDescription().forEach(sender::sendMessage);
                });
            }

            return;
        }

        if (hasCustomEnchant) {
            flags.getFlagValue("ce", CEnchantment.class).ifPresent(action -> {
                sender.sendMessage(action.getInfoName());

                action.getInfoDescription().forEach(sender::sendMessage);
            });
        }
    }
}
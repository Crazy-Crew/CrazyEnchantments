package com.badbones69.crazyenchantments.paper.commands.types.admin.debug;

import com.ryderbelserion.common.api.enums.messages.Messages;
import com.badbones69.crazyenchantments.paper.commands.BaseEnchantCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CheckCommand extends BaseEnchantCommand {

    @Command(value = "check", alias = "checkenchants")
    @Permission(value = "crazyenchantments.check", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments check [player]")
    public void execute(final CommandSender sender, @Suggestion("players") final Player player) {
        Arrays.stream(player.getEquipment().getArmorContents()).filter(Objects::nonNull).forEach(item -> {
            final StringBuilder enchantmentsString = new StringBuilder();

            final String main = Messages.main_update_enchants.getMessage(player, Map.of(
                    "%item%", item.getType().toString()
            ));

            this.bookSettings.getEnchantments(item).forEach((enchantment, level) -> {
                final Map<String, String> placeholders = new HashMap<>();

                placeholders.put("%enchant%", enchantment.getName());
                placeholders.put("%level%", String.valueOf(level));

                enchantmentsString.append(Messages.base_update_enchants.getMessage(player, placeholders));
            });

            sender.sendMessage(this.fusion.replacePlaceholders(main, Map.of(
                    "%itemEnchants%", enchantmentsString.toString()
            )));
        });
    }
}
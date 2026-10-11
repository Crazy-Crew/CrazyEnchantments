package com.badbones69.crazyenchantments.paper.commands.types.admin.debug;

import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.ryderbelserion.common.api.enums.Files;
import com.badbones69.crazyenchantments.paper.commands.BaseEnchantCommand;
import com.ryderbelserion.common.utils.ConfigUtils;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.ArrayList;
import java.util.List;

public class DebugCommand extends BaseEnchantCommand {

    @Command("debug")
    @Permission(value = "crazyenchantments.debug", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments debug")
    public void execute(final CommandSender sender) {
        final List<String> brokenEnchantments = new ArrayList<>();
        final List<String> brokenEnchantmentTypes = new ArrayList<>();

        final CommentedConfigurationNode configuration = Files.ENCHANTMENTS.getConfiguration();

        for (CEnchantments enchantment : CEnchantments.values()) {
            if (!configuration.hasChild("Enchantments", enchantment.getName())) brokenEnchantments.add(enchantment.getName());

            if (enchantment.getType() == null) brokenEnchantmentTypes.add(enchantment.getName());
        }

        final StringBuilder builder = new StringBuilder();

        final String prefix = ConfigUtils.getPrefix();

        if (brokenEnchantments.isEmpty() && brokenEnchantmentTypes.isEmpty()) {
            builder.append("%s<green>All enchantments have loaded".formatted(prefix)).append("\n");
        } else {
            if (!brokenEnchantments.isEmpty()) {
                int amount = 1;

                List.of(
                        "%s<red>Missing Enchantments:",
                        "%s<gray>These enchantments are broken due to one of the following reasons:"
                ).forEach(line -> builder.append(line.formatted(prefix)).append("\n"));

                for (final String broke : brokenEnchantments) {
                    builder.append("%s<red>#%s: <gold>%s".formatted(prefix, amount, broke)).append("\n");

                    amount++;
                }

                List.of(
                        "<gray>- <red>Missing from the Enchantments.yml",
                        "<gray>- <red>[Enchantment Name]: option was changed.",
                        "<gray>- <red>Yaml format is broken."
                ).forEach(line -> builder.append(line).append("\n"));
            }

            if (!brokenEnchantmentTypes.isEmpty()) {
                int i = 1;

                List.of(
                        "%s<red>Enchantments with null types:",
                        "%s<gray>These enchantments are broken due to the enchantment type being null."
                ).forEach(line -> builder.append(line.formatted(prefix)).append("\n"));

                for (String broke : brokenEnchantmentTypes) {
                    builder.append("<red>#%s: <gold>%s".formatted(i, broke)).append("\n");

                    i++;
                }
            }
        }

        builder.append("%s<red>Enchantment Types and amount of items in each:".formatted(prefix)).append("\n");

        MenuManager.getEnchantmentTypes().forEach(type -> builder.append("<red>%s: <gold>%s".formatted(type.getName(), type.getEnchantableMaterials().size())));

        for (final String line : builder.toString().split("\n")) {
            sender.sendRichMessage(line);
        }
    }
}
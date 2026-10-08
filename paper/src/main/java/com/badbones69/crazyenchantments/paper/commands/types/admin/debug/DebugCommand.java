package com.badbones69.crazyenchantments.paper.commands.types.admin.debug;

import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.keys.FileKeys;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.commands.BaseEnchantCommand;
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

        final CommentedConfigurationNode configuration = FileKeys.ENCHANTMENTS.getConfiguration();

        for (CEnchantments enchantment : CEnchantments.values()) {
            if (!configuration.hasChild("Enchantments", enchantment.getName())) brokenEnchantments.add(enchantment.getName());

            if (enchantment.getType() == null) brokenEnchantmentTypes.add(enchantment.getName());
        }

        if (brokenEnchantments.isEmpty() && brokenEnchantmentTypes.isEmpty()) {
            sender.sendMessage(ColorUtils.getPrefix("&aAll enchantments are loaded."));
        } else {

            if (!brokenEnchantments.isEmpty()) {
                int amount = 1;
                sender.sendMessage(ColorUtils.getPrefix("&cMissing Enchantments:"));
                sender.sendMessage(ColorUtils.getPrefix("&7These enchantments are broken due to one of the following reasons:"));

                for (String broke : brokenEnchantments) {
                    sender.sendMessage(ColorUtils.color("&c#" + amount + ": &6" + broke));
                    amount++;
                }

                sender.sendMessage(ColorUtils.color("&7- &cMissing from the Enchantments.yml"));
                sender.sendMessage(ColorUtils.color("&7- &c<Enchantment Name>: option was changed"));
                sender.sendMessage(ColorUtils.color("&7- &cYaml format has been broken."));
            }

            if (!brokenEnchantmentTypes.isEmpty()) {
                int i = 1;
                sender.sendMessage(ColorUtils.getPrefix("&cEnchantments with null types:"));
                sender.sendMessage(ColorUtils.getPrefix("&7These enchantments are broken due to the enchantment type being null."));

                for (String broke : brokenEnchantmentTypes) {
                    sender.sendMessage(ColorUtils.color("&c#" + i + ": &6" + broke));
                    i++;
                }
            }
        }

        sender.sendMessage(ColorUtils.getPrefix("&cEnchantment Types and amount of items in each:"));

        MenuManager.getEnchantmentTypes().forEach(type -> sender.sendMessage(ColorUtils.color("&c" + type.getName() + ": &6" + type.getEnchantableMaterials().size())));
    }
}
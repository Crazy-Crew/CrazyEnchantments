package com.badbones69.crazyenchantments.paper.commands.v2.types.admin;

import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.commands.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;

public class CommandDebug extends BaseCommand {

    @Command("debug")
    @Permission(value = "crazyenchantments.debug", def = PermissionDefault.OP)
    public void debug(CommandSender sender) {
        List<String> brokenEnchantments = new ArrayList<>();
        List<String> brokenEnchantmentTypes = new ArrayList<>();

        FileConfiguration configuration = Files.ENCHANTMENTS.getFile();

        for (CEnchantments enchantment : CEnchantments.values()) {
            if (!configuration.contains("Enchantments." + enchantment.getName())) {
                brokenEnchantments.add(enchantment.getCustomName());
            }

            if (enchantment.getType() == null) brokenEnchantmentTypes.add(enchantment.getName());
        }

        if (brokenEnchantments.isEmpty() && brokenEnchantmentTypes.isEmpty()) {
            sender.sendMessage(ColorUtils.getPrefix("&aAll enchantments are loaded."));
            return;
        }

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
            int amount = 1;

            sender.sendMessage(ColorUtils.getPrefix("&cEnchantments with null types:"));
            sender.sendMessage(ColorUtils.getPrefix("&7These enchantments are broken due to the enchantment type being null."));

            for (String broke : brokenEnchantmentTypes) {
                sender.sendMessage(ColorUtils.color("&c#" + amount + ": &6" + broke));

                amount++;
            }
        }

        sender.sendMessage(ColorUtils.getPrefix("&cEnchantment Types and amount of items in each:"));

        MenuManager.getEnchantmentTypes().forEach(type -> sender.sendMessage(ColorUtils.color("&c" + type.getName() + ": &6" + type.getEnchantableMaterials().size())));
    }
}
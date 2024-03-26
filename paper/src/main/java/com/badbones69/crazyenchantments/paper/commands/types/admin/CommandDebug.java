package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.platform.commands.BaseCommand;
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
            //ColorUtils.sendMessage(sender, "%prefix%&aAll enchantments are loaded.", true);
            return;
        }

        if (!brokenEnchantments.isEmpty()) {
            int amount = 1;

            //ColorUtils.sendMessage(sender, "&8===============================================", false);
            //ColorUtils.sendMessage(sender, "&cMissing Enchantments:", false);
            //ColorUtils.sendMessage(sender, "&7These enchantments are broken due to one of the following reasons:", false);

            for (String broke : brokenEnchantments) {
                //ColorUtils.sendMessage(sender, " &c#" + amount + ": &6" + broke, false);

                amount++;
            }

            //ColorUtils.sendMessage(sender, "&7- &cMissing from the Enchantments.yml", false);
            //ColorUtils.sendMessage(sender, "&7- &c<Enchantment Name>: option was changed", false);
            //ColorUtils.sendMessage(sender, "&7- &cYaml format has been broken.", false);
        }

        if (!brokenEnchantmentTypes.isEmpty()) {
            int amount = 1;

            //ColorUtils.sendMessage(sender, "&cEnchantments with null types:", false);
            //ColorUtils.sendMessage(sender, "&7These enchantments are broken due to the enchantment type being null.", false);

            for (String broke : brokenEnchantmentTypes) {
                //ColorUtils.sendMessage(sender, " &c#" + amount + ": &6" + broke, false);

                amount++;
            }
        }

        //ColorUtils.sendMessage(sender, "&cEnchantment Types and amount of items in each:", false);

        //MenuManager.getEnchantmentTypes().forEach(type -> ColorUtils.sendMessage(sender, "&c" + type.getName() + ": &6" + type.getEnchantableMaterials().size(), false));

        //ColorUtils.sendMessage(sender, "&8===============================================", false);
    }
}
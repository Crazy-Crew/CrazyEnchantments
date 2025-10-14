package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.PermissionDefault;
import java.util.ArrayList;
import java.util.List;

public class CommandDebug extends BaseCommand { //todo() legacy trash

    @Command("debug")
    @Permission(value = "crazyenchantments.debug", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments debug")
    public void debug(final CommandSender sender) {
        List<String> brokenEnchantments = new ArrayList<>();
        List<String> brokenEnchantmentTypes = new ArrayList<>();

        final YamlConfiguration configuration = FileKeys.enchantments.getYamlConfiguration();

        for (CEnchantments enchantment : CEnchantments.values()) {
            if (!configuration.contains("Enchantments." + enchantment.getName())) brokenEnchantments.add(enchantment.getName());

            if (enchantment.getType() == null) brokenEnchantmentTypes.add(enchantment.getName());
        }

        if (brokenEnchantments.isEmpty() && brokenEnchantmentTypes.isEmpty()) {
            sender.sendMessage(ColorUtils.getPrefix("<green>All enchantments are loaded."));
        } else {
            if (!brokenEnchantments.isEmpty()) {
                int amount = 1;
                sender.sendMessage(ColorUtils.getPrefix("<red>Missing Enchantments:"));
                sender.sendMessage(ColorUtils.getPrefix("<gray>These enchantments are broken due to one of the following reasons:"));

                for (String broke : brokenEnchantments) {
                    //sender.sendMessage(ColorUtils.color("<red>#" + amount + ": <gold>" + broke));
                    amount++;
                }

                //sender.sendMessage(ColorUtils.color("<gray>- <red>Missing from the Enchantments.yml"));
                //sender.sendMessage(ColorUtils.color("<gray>- <red><Enchantment Name>: option was changed"));
                //sender.sendMessage(ColorUtils.color("<gray>- <red>Yaml format has been broken."));
            }

            if (!brokenEnchantmentTypes.isEmpty()) {
                int i = 1;
                sender.sendMessage(ColorUtils.getPrefix("<red>Enchantments with null types:"));
                sender.sendMessage(ColorUtils.getPrefix("<gray>These enchantments are broken due to the enchantment type being null."));

                for (String broke : brokenEnchantmentTypes) {
                    //sender.sendMessage(ColorUtils.color("<red>#" + i + ": <gold>" + broke));
                    i++;
                }
            }
        }

        sender.sendMessage(ColorUtils.getPrefix("<red>Enchantment Types and amount of items in each:"));

        //MenuManager.getEnchantmentTypes().forEach(type -> sender.sendMessage(ColorUtils.color("<red>" + type.getName() + ": <gold>" + type.getEnchantableMaterials().size())));
    }
}
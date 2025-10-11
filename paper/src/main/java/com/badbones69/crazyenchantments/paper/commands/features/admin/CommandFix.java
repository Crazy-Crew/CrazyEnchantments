package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.PermissionDefault;
import java.util.ArrayList;
import java.util.List;

public class CommandFix extends BaseCommand {

    @Command("fix")
    @Permission(value = "crazyenchantments.fix", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments fix")
    public void fix(final CommandSender sender) {
        List<CEnchantments> brokenEnchantments = new ArrayList<>();

        final YamlConfiguration configuration = FileKeys.enchantments.getYamlConfiguration();

        for (CEnchantments enchantment : CEnchantments.values()) {
            if (!configuration.contains("Enchantments." + enchantment.getName()))
                brokenEnchantments.add(enchantment);
        }

        //sender.sendMessage(ColorUtils.color("<gray>Fixed a total of " + brokenEnchantments.size() + " enchantments."));

        for (CEnchantments enchantment : brokenEnchantments) {
            final String path = "Enchantments." + enchantment.getName();

            configuration.set(path + ".Enabled", true);
            configuration.set(path + ".Name", enchantment.getName());
            configuration.set(path + ".Color", "<gray>");
            configuration.set(path + ".BookColor", "<aqua><b>");
            configuration.set(path + ".MaxPower", 1);
            configuration.set(path + ".Enchantment-Type", enchantment.getType().getName());
            configuration.set(path + ".Info.Name", "<yellow><b>" + enchantment.getName() + " <gray>(<aqua>I<gray>)");
            configuration.set(path + ".Info.Description", enchantment.getDescription());

            final List<String> categories = new ArrayList<>();

            this.categoryManager.getCategories().values().forEach(category -> categories.add(category.getName()));

            configuration.set(path + ".Categories", categories);

            FileKeys.enchantments.save();
        }
    }
}
package com.badbones69.crazyenchantments.paper.commands.types.admin.debug;

import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.keys.FileKeys;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.commands.EnchantCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.PermissionDefault;
import java.util.ArrayList;
import java.util.List;

public class FixCommand extends EnchantCommand {

    @Command("fix")
    @Permission(value = "crazyenchantments.fix", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments fix")
    public void execute(final CommandSender sender) {
        final List<CEnchantments> brokenEnchantments = new ArrayList<>();

        final FileConfiguration file = FileKeys.ENCHANTMENTS.getConfiguration();

        for (CEnchantments enchantment : CEnchantments.values()) {
            if (!file.contains("Enchantments." + enchantment.getName())) brokenEnchantments.add(enchantment);
        }

        sender.sendMessage(ColorUtils.color("&7Fixed a total of " + brokenEnchantments.size() + " enchantments."));

        for (CEnchantments enchantment : brokenEnchantments) {
            final String path = "Enchantments." + enchantment.getName();

            file.set(path + ".Enabled", true);
            file.set(path + ".Name", enchantment.getName());
            file.set(path + ".Color", "&7");
            file.set(path + ".BookColor", "&b&l");
            file.set(path + ".MaxPower", 1);
            file.set(path + ".Enchantment-Type", enchantment.getType().getName());
            file.set(path + ".Info.Name", "&e&l" + enchantment.getName() + " &7(&bI&7)");
            file.set(path + ".Info.Description", enchantment.getDescription());

            final List<String> categories = new ArrayList<>();

            this.bookSettings.getCategories().forEach(category -> categories.add(category.getName()));

            file.set(path + ".Categories", categories);

            FileKeys.ENCHANTMENTS.save();
        }}
}
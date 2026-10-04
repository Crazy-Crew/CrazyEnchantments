package com.badbones69.crazyenchantments.paper.commands.types.admin.debug;

import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.keys.FileKeys;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.commands.EnchantCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.ArrayList;
import java.util.List;

public class FixCommand extends EnchantCommand {

    @Command("fix")
    @Permission(value = "crazyenchantments.fix", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments fix")
    public void execute(final CommandSender sender) {
        final List<CEnchantments> brokenEnchantments = new ArrayList<>();

        final CommentedConfigurationNode config = FileKeys.ENCHANTMENTS.getConfiguration();

        final CommentedConfigurationNode enchantments = config.node("Enchantments");

        for (final CEnchantments enchantment : CEnchantments.values()) {
            if (!config.hasChild(enchantment.getName())) brokenEnchantments.add(enchantment);
        }

        sender.sendMessage(ColorUtils.color("&7Fixed a total of " + brokenEnchantments.size() + " enchantments."));

        for (CEnchantments enchantment : brokenEnchantments) {
            final String name = enchantment.getName();

            final CommentedConfigurationNode section = enchantments.node(name);

            Methods.setNode(section, "Enabled", Boolean.class, true);
            Methods.setNode(section, "Name", String.class, name);
            Methods.setNode(section, "Color", String.class, "&7");
            Methods.setNode(section, "BookColor", String.class, "&b&l");
            Methods.setNode(section, "MaxPower", Integer.class, 1);
            Methods.setNode(section, "Enchantment-Type", String.class, enchantment.getType().getName());
            Methods.setNode(section, "Info.Name", String.class, "&e&l%s &7(&bI&7)".formatted(name));
            Methods.setNode(section, "Info.Description", String.class, enchantment.getDescription());

            final String path = "Enchantments." + enchantment.getName();

            final List<String> categories = new ArrayList<>();

            this.bookSettings.getCategories().forEach(category -> categories.add(category.getName()));

            Methods.setNode(section, "Categories", List.class, categories);

            FileKeys.ENCHANTMENTS.save();
        }}
}
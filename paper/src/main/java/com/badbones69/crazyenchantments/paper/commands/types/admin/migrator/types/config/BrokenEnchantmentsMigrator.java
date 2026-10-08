package com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.config;

import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.keys.FileKeys;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.enums.MigrationType;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.interfaces.IMigrator;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.ArrayList;
import java.util.List;

public class BrokenEnchantmentsMigrator extends IMigrator {

    public BrokenEnchantmentsMigrator(@NonNull final CommandSender sender) {
        super(sender, MigrationType.BROKEN_ENCHANTMENTS);
    }

    @Override
    public void init() {
        final List<CEnchantments> brokenEnchantments = new ArrayList<>();

        final CommentedConfigurationNode config = FileKeys.ENCHANTMENTS.getConfiguration();

        final CommentedConfigurationNode enchantments = config.node("Enchantments");

        for (final CEnchantments enchantment : CEnchantments.values()) {
            if (!config.hasChild(enchantment.getName())) brokenEnchantments.add(enchantment);
        }

        this.sender.sendMessage(ColorUtils.color("&7Fixed a total of " + brokenEnchantments.size() + " enchantments."));

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

            final List<String> categories = new ArrayList<>();

            this.bookSettings.getCategories().forEach(category -> categories.add(category.getName()));

            Methods.setNode(section, "Categories", List.class, categories);

            FileKeys.ENCHANTMENTS.save();
        }
    }
}
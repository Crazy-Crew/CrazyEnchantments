package com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.config;

import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.ryderbelserion.common.api.enums.Files;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.enums.MigrationType;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.interfaces.IMigrator;
import com.ryderbelserion.common.utils.ConfigUtils;
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

        final CommentedConfigurationNode config = Files.ENCHANTMENTS.getConfiguration();

        final CommentedConfigurationNode enchantments = config.node("Enchantments");

        for (final CEnchantments enchantment : CEnchantments.values()) {
            if (!config.hasChild(enchantment.getName())) brokenEnchantments.add(enchantment);
        }

        this.sender.sendMessage(ColorUtils.color("&7Fixed a total of " + brokenEnchantments.size() + " enchantments."));

        for (CEnchantments enchantment : brokenEnchantments) {
            final String name = enchantment.getName();

            final CommentedConfigurationNode section = enchantments.node(name);

            ConfigUtils.setNode(section, "Enabled", Boolean.class, true);
            ConfigUtils.setNode(section, "Name", String.class, name);
            ConfigUtils.setNode(section, "Color", String.class, "&7");
            ConfigUtils.setNode(section, "BookColor", String.class, "&b&l");
            ConfigUtils.setNode(section, "MaxPower", Integer.class, 1);
            ConfigUtils.setNode(section, "Enchantment-Type", String.class, enchantment.getType().getName());
            ConfigUtils.setNode(section, "Info.Name", String.class, "&e&l%s &7(&bI&7)".formatted(name));
            ConfigUtils.setNode(section, "Info.Description", String.class, enchantment.getDescription());

            final List<String> categories = new ArrayList<>();

            this.bookSettings.getCategories().forEach(category -> categories.add(category.getName()));

            ConfigUtils.setNode(section, "Categories", List.class, categories);

            Files.ENCHANTMENTS.save();
        }
    }
}
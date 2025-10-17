package com.badbones69.crazyenchantments.paper.commands.features.admin.migration.types;

import com.badbones69.crazyenchantments.paper.api.enums.files.FileKeys;
import com.badbones69.crazyenchantments.paper.commands.features.admin.migration.interfaces.IEnchantMigration;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class TinkerMigration extends IEnchantMigration {

    public TinkerMigration(@NotNull final CommandSender sender) {
        super(sender);
    }

    @Override
    public void run() {
        final YamlConfiguration configuration = FileKeys.tinker.getPaperConfiguration();

        final ConfigurationSection tinkerSection = configuration.getConfigurationSection("Tinker");

        if (tinkerSection == null) return;

        final ConfigurationSection enchantSection = configuration.getConfigurationSection("Vanilla-Enchantments");

        if (enchantSection != null) {
            for (final String enchantName : enchantSection.getKeys(false)) {
                final ConfigurationSection enchant = enchantSection.getConfigurationSection(enchantName);

                if (enchant == null) continue;

                for (final String item : enchant.getKeys(false)) {
                    final int value = enchant.getInt(item);

                    enchant.set(item, "%s, %s".formatted(value, 1));
                }
            }
        }

        final ConfigurationSection customEnchantSection = configuration.getConfigurationSection("Crazy-Enchantments");

        if (customEnchantSection != null) {
            for (final String enchantName : customEnchantSection.getKeys(false)) {
                final ConfigurationSection enchant = customEnchantSection.getConfigurationSection(enchantName);

                if (enchant == null) continue;

                for (final String item : enchant.getKeys(false)) {
                    final int value = enchant.getInt(item);

                    enchant.set(item, "%s, %s".formatted(value, 1));
                }
            }
        }
    }
}
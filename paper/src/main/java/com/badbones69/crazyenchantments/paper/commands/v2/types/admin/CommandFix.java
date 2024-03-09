package com.badbones69.crazyenchantments.paper.commands.v2.types.admin;

import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.commands.v2.BaseCommand;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CommandFix extends BaseCommand {

    @NotNull
    private final EnchantmentBookSettings bookSettings = this.plugin.getStarter().getEnchantmentBookSettings();

    @Command("fix")
    @Permission(value = "crazyenchantments.fix", def = PermissionDefault.OP)
    public void fix(CommandSender sender) {
        List<CEnchantments> brokenEnchantments = new ArrayList<>();
        FileConfiguration configuration = Files.ENCHANTMENTS.getFile();

        for (CEnchantments enchantment : CEnchantments.values()) {
            if (!configuration.contains("Enchantments." + enchantment.getName())) brokenEnchantments.add(enchantment);
        }

        sender.sendMessage(ColorUtils.color("&7Fixed a total of " + brokenEnchantments.size() + " enchantments."));

        for (CEnchantments enchantment : brokenEnchantments) {
            String path = "Enchantments." + enchantment.getName();

            configuration.set(path + ".Enabled", true);
            configuration.set(path + ".Name", enchantment.getName());
            configuration.set(path + ".Color", "&7");
            configuration.set(path + ".BookColor", "&b&l");
            configuration.set(path + ".MaxPower", 1);
            configuration.set(path + ".Enchantment-Type", enchantment.getType().getName());
            configuration.set(path + ".Info.Name", "&e&l" + enchantment.getName() + " &7(&bI&7)");
            configuration.set(path + ".Info.Description", enchantment.getDescription());

            List<String> categories = new ArrayList<>();

            this.bookSettings.getCategories().forEach(category -> categories.add(category.getName()));

            configuration.set(path + ".Categories", categories);

            Files.ENCHANTMENTS.saveFile();
        }
    }
}
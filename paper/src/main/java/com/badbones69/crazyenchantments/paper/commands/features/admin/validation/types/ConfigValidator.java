package com.badbones69.crazyenchantments.paper.commands.features.admin.validation.types;

import com.badbones69.crazyenchantments.paper.api.MigrateManager;
import com.badbones69.crazyenchantments.paper.commands.features.admin.validation.IEnchantValidator;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class ConfigValidator extends IEnchantValidator {

    public ConfigValidator(@NotNull final CommandSender sender) {
        super(sender);
    }

    @Override
    public void run() {
        MigrateManager.convert();

        sender.sendMessage(this.fusion.parse(this.sender, this.utils.toString(List.of(
                "<gold>=======================================================",
                "",
                "<red>We have attempted to update the configuration files!",
                "<yellow>Please read console to check if everything went smoothly..",
                "",
                "<gold>======================================================="
        ))));
    }
}
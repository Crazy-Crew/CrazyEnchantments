package com.badbones69.crazyenchantments.paper.commands.types.admin.items;

import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.platform.commands.BaseCommand;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class CommandLostBook extends BaseCommand {

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull EnchantmentBookSettings settings = this.starter.getEnchantmentBookSettings();

    @Command("lostbook")
    @Permission(value = "crazyenchantments.lostbook", def = PermissionDefault.OP)
    public void lostbook(CommandSender sender, @Suggestion("categories") String name, int amount, @Suggestion("players") Player target) {
        Category category = this.settings.getCategory(name);

        if (category == null) {
            return;
        }

        this.starter.getMethods().addItemToInventory(target, category.getLostBook().getLostBook(category, amount).build());

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%Category%", name);

        sender.sendRichMessage(Messages.NOT_A_CATEGORY.getMessage(placeholders));
    }
}
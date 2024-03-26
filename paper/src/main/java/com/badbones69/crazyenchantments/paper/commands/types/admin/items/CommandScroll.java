package com.badbones69.crazyenchantments.paper.commands.types.admin.items;

import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.platform.commands.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

public class CommandScroll extends BaseCommand {

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull Methods methods = this.starter.getMethods();

    @Command("scroll")
    @Permission(value = "crazyenchantments.scroll", def = PermissionDefault.OP)
    public void scroll(CommandSender sender, Scrolls scroll, @Suggestion("numbers") int amount, @Suggestion("players") Player target) {
        this.methods.addItemToInventory(target, scroll.getScroll(amount));

        //todo() add sender message.
    }
}
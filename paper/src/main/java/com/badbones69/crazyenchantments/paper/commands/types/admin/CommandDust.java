package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.commands.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class CommandDust extends BaseCommand {

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull Methods methods = this.starter.getMethods();

    @Command("dust")
    @Permission(value = "crazyenchantments.dust", def = PermissionDefault.OP)
    public void dust(CommandSender sender, Dust dust, @Suggestion("numbers") int amount, @Suggestion("players") Player target, @Suggestion("numbers") int percent) {
        if (dust == null) return;

        this.methods.addItemToInventory(target, dust.getDust(percent, amount));

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%Amount%", String.valueOf(amount));
        placeholders.put("%Player%", target.getName());

        switch (dust) {
            case SUCCESS_DUST -> {
                //target.sendMessage(Messages.GET_SUCCESS_DUST.getMessage(placeholders));
                //sender.sendMessage(Messages.GIVE_SUCCESS_DUST.getMessage(placeholders));
            }

            case DESTROY_DUST -> {
                //target.sendMessage(Messages.GET_DESTROY_DUST.getMessage(placeholders));
                //sender.sendMessage(Messages.GIVE_DESTROY_DUST.getMessage(placeholders));
            }

            case MYSTERY_DUST -> {
                //target.sendMessage(Messages.GET_MYSTERY_DUST.getMessage(placeholders));
                //sender.sendMessage(Messages.GIVE_MYSTERY_DUST.getMessage(placeholders));
            }
        }
    }
}
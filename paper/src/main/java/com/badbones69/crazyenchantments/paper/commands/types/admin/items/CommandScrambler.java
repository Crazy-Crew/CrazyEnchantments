package com.badbones69.crazyenchantments.paper.commands.types.admin.items;

import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.commands.BaseCommand;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.listeners.ScramblerListener;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CommandScrambler extends BaseCommand {

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull Methods methods = this.starter.getMethods();

    private final @NotNull ScramblerListener scramblerListener = this.starter.getScramblerListener();

    @Command("scrambler")
    @Permission(value = "crazyenchantments.scrambler", def = PermissionDefault.OP)
    public void scrambler(CommandSender sender, @Suggestion("numbers") int amount, @Suggestion("players") Player target) {
        if (this.methods.isInventoryFull(target)) return;

        this.methods.addItemToInventory(target, this.scramblerListener.getScramblers(amount));

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%Amount%", String.valueOf(amount));
        placeholders.put("%Player%", target.getName());

        sender.sendMessage(Messages.GIVE_SCRAMBLER_CRYSTAL.getMessage(placeholders));

        target.sendMessage(Messages.GET_SCRAMBLER.getMessage(placeholders));
    }
}
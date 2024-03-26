package com.badbones69.crazyenchantments.paper.commands.types.admin.items;

import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.commands.BaseCommand;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.paper.listeners.SlotCrystalListener;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CommandSlotCrystal extends BaseCommand {

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull Methods methods = this.starter.getMethods();

    private final SlotCrystalListener slotCrystalListener = this.starter.getSlotCrystalListener();

    @Command(value = "slotcrystal", alias = {"sc"})
    @Permission(value = "crazyenchantments.slotcrystal", def = PermissionDefault.OP)
    public void give(CommandSender sender, int amount, @Suggestion("players") Player target) {
        if (this.methods.isInventoryFull(target)) return;

        this.methods.addItemToInventory(target, this.slotCrystalListener.getSlotCrystal(amount));

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%Amount%", String.valueOf(amount));
        placeholders.put("%Player%", target.getName());

        sender.sendMessage(Messages.GIVE_SLOT_CRYSTAL.getMessage(placeholders));

        target.sendMessage(Messages.GET_SLOT_CRYSTAL.getMessage(placeholders));
    }
}
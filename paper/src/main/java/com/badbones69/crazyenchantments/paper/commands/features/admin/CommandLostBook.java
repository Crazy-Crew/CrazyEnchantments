package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CommandLostBook extends BaseCommand {

    @Command("lostbook")
    @Permission(value = "crazyenchantments.lostbook", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments lostbook <category> <amount> [player]")
    public void lostbook(final CommandSender sender, final Category category, final int amount, @Optional @Nullable final Player target) {
        if (category == null) {
            sender.sendMessage(Messages.NOT_A_CATEGORY.getMessage());

            return;
        }

        final Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        if (safePlayer == null) {
            sender.sendMessage(Messages.NOT_ONLINE.getMessage());

            return;
        }

        this.methods.addItemToInventory(safePlayer, category.getLostBook().getLostBook(category, amount).build());
    }

    @Command("scroll")
    @Permission(value = "crazyenchantments.scroll", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments scroll <amount> [player]")
    public void scroll(final CommandSender sender, final Scrolls scroll, final int amount, @Optional @Nullable final Player target) {
        final Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        if (safePlayer == null) {
            sender.sendMessage(Messages.NOT_ONLINE.getMessage());

            return;
        }

        if (scroll == null) {
            //todo() add scroll message

            return;
        }

        this.methods.addItemToInventory(safePlayer, scroll.getScroll(amount));
    }

    @Command("scrambler")
    @Permission(value = "crazyenchantments.scrambler", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments scrambler <amount> [player]")
    public void scrambler(final CommandSender sender, final int amount, @Optional @Nullable final Player target) {
        final Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        if (safePlayer == null) {
            sender.sendMessage(Messages.NOT_ONLINE.getMessage());

            return;
        }

        if (this.methods.isInventoryFull(safePlayer)) {
            sender.sendMessage(Messages.INVENTORY_FULL.getMessage());

            return;
        }

        this.itemManager.getItem("scrambler_item").ifPresent(action -> this.methods.addItemToInventory(safePlayer, action.getItemStack(amount)));

        Map<String, String> placeholders = new HashMap<>() {{
            put("%Amount%", String.valueOf(amount));
            put("%Player%", safePlayer.getName());
        }};

        if (target != null) {
            sender.sendMessage(Messages.GIVE_SCRAMBLER_CRYSTAL.getMessage(placeholders));
        }

        safePlayer.sendMessage(Messages.GET_SCRAMBLER.getMessage(placeholders));
    }

    @Command("crystal")
    @Permission(value = "crazyenchantments.crystal", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments crystal <amount> [player]")
    public void crystal(final CommandSender sender, final int amount, @Optional @Nullable final Player target) {
        final Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        if (safePlayer == null) {
            sender.sendMessage(Messages.NOT_ONLINE.getMessage());

            return;
        }

        if (this.methods.isInventoryFull(safePlayer)) {
            sender.sendMessage(Messages.INVENTORY_FULL.getMessage());

            return;
        }

        this.methods.addItemToInventory(safePlayer, this.protectionCrystalSettings.getCrystal(amount));

        Map<String, String> placeholders = new HashMap<>() {{
            put("%Amount%", String.valueOf(amount));
            put("%Player%", safePlayer.getName());
        }};

        if (target != null) {
            sender.sendMessage(Messages.GIVE_PROTECTION_CRYSTAL.getMessage(placeholders));
        }

        safePlayer.sendMessage(Messages.GET_PROTECTION_CRYSTAL.getMessage(placeholders));
    }

    @Command("slotcrystal")
    @Permission(value = "crazyenchantments.slotcrystal", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments slotcrystal <amount> [player]")
    public void slotcrystal(final CommandSender sender, final int amount, @Optional @Nullable final Player target) {
        final Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        if (safePlayer == null) {
            sender.sendMessage(Messages.NOT_ONLINE.getMessage());

            return;
        }

        if (this.methods.isInventoryFull(safePlayer)) {
            sender.sendMessage(Messages.INVENTORY_FULL.getMessage());

            return;
        }

        final ItemStack itemStack = this.starter.getSlotCrystalListener().getSlotCrystal();

        itemStack.setAmount(amount);

        this.methods.addItemToInventory(safePlayer, itemStack);

        Map<String, String> placeholders = new HashMap<>() {{
            put("%Amount%", String.valueOf(amount));
            put("%Player%", safePlayer.getName());
        }};

        if (target != null) {
            sender.sendMessage(Messages.GIVE_SLOT_CRYSTAL.getMessage(placeholders));
        }

        safePlayer.sendMessage(Messages.GET_SLOT_CRYSTAL.getMessage(placeholders));
    }
}
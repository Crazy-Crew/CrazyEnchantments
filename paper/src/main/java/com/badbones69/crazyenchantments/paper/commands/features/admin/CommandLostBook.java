package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.enums.shop.Scrolls;
import com.badbones69.crazyenchantments.paper.api.enums.files.MessageKeys;
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
            MessageKeys.NOT_A_CATEGORY.sendMessage(sender);

            return;
        }

        final Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        if (safePlayer == null) {
            MessageKeys.NOT_ONLINE.sendMessage(sender);

            return;
        }

        Methods.addItemToInventory(safePlayer, category.getLostBook().getLostBook(category, amount).build());
    }

    @Command("scroll")
    @Permission(value = "crazyenchantments.scroll", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments scroll <amount> [player]")
    public void scroll(final CommandSender sender, final Scrolls scroll, final int amount, @Optional @Nullable final Player target) {
        final Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        if (safePlayer == null) {
            MessageKeys.NOT_ONLINE.sendMessage(sender);

            return;
        }

        if (scroll == null) {
            //todo() add scroll message

            return;
        }

        Methods.addItemToInventory(safePlayer, scroll.getScroll(amount));
    }

    @Command("scrambler")
    @Permission(value = "crazyenchantments.scrambler", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments scrambler <amount> [player]")
    public void scrambler(final CommandSender sender, final int amount, @Optional @Nullable final Player target) {
        final Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        if (safePlayer == null) {
            MessageKeys.NOT_ONLINE.sendMessage(sender);

            return;
        }

        if (Methods.isInventoryFull(safePlayer)) {
            MessageKeys.INVENTORY_FULL.sendMessage(sender);

            return;
        }

        this.itemManager.getItem("scrambler_item").ifPresent(action -> Methods.addItemToInventory(safePlayer, action.getItemStack(amount)));

        final Map<String, String> placeholders = new HashMap<>() {{
            put("{amount}", String.valueOf(amount));
            put("{player}", safePlayer.getName());
        }};

        if (target != null) {
            MessageKeys.GIVE_SCRAMBLER_CRYSTAL.sendMessage(sender, placeholders);
        }

        MessageKeys.GET_SCRAMBLER.sendMessage(safePlayer, placeholders);
    }

    @Command("crystal")
    @Permission(value = "crazyenchantments.crystal", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments crystal <amount> [player]")
    public void crystal(final CommandSender sender, final int amount, @Optional @Nullable final Player target) {
        final Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        if (safePlayer == null) {
            MessageKeys.NOT_ONLINE.sendMessage(sender);

            return;
        }

        if (Methods.isInventoryFull(safePlayer)) {
            MessageKeys.INVENTORY_FULL.sendMessage(sender);

            return;
        }

        this.itemManager.getItem("protection_crystal_item").ifPresent(action -> {
            final ItemStack itemStack = action.getItemStack(amount);

            Methods.addItemToInventory(safePlayer, itemStack);

            final Map<String, String> placeholders = new HashMap<>() {{
                put("{amount}", String.valueOf(amount));
                put("{player}", safePlayer.getName());
            }};

            if (target != null) {
                MessageKeys.GIVE_PROTECTION_CRYSTAL.sendMessage(sender, placeholders);
            }

            MessageKeys.GET_PROTECTION_CRYSTAL.sendMessage(safePlayer, placeholders);
        });
    }

    @Command("slotcrystal")
    @Permission(value = "crazyenchantments.slotcrystal", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments slotcrystal <amount> [player]")
    public void slotcrystal(final CommandSender sender, final int amount, @Optional @Nullable final Player target) {
        final Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        if (safePlayer == null) {
            MessageKeys.NOT_ONLINE.sendMessage(sender);

            return;
        }

        if (Methods.isInventoryFull(safePlayer)) {
            MessageKeys.INVENTORY_FULL.sendMessage(sender);

            return;
        }

        this.itemManager.getItem("slot_crystal_item").ifPresent(action -> {
            final ItemStack itemStack = action.getItemStack(amount);

            Methods.addItemToInventory(safePlayer, itemStack);

            final Map<String, String> placeholders = new HashMap<>() {{
                put("{amount}", String.valueOf(amount));
                put("{player}", safePlayer.getName());
            }};

            if (target != null) {
                MessageKeys.GIVE_SLOT_CRYSTAL.sendMessage(sender, placeholders);
            }

            MessageKeys.GET_SLOT_CRYSTAL.sendMessage(safePlayer, placeholders);
        });
    }
}
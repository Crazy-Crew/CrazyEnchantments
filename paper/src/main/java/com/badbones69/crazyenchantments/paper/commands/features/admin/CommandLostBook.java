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
            sender.sendMessage(Messages.NOT_A_CATEGORY.getMessage(new HashMap<>() {{
                put("%Category%", ""); //todo() yes
            }}));

            return;
        }

        final Player safePlayer = target == null ? sender instanceof Player player ? player : null : target;

        if (safePlayer == null) {
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
            return;
        }

        if (scroll == null) {
            return;
        }

        this.methods.addItemToInventory(safePlayer, scroll.getScroll(amount));
    }

    @Command("scrambler")
    @Permission(value = "crazyenchantments.scrambler", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments scrambler <amount> [player]")
    public void scrambler(final CommandSender sender, final int amount, @Optional @Nullable final Player target) {
        if (target == null) {
            if (sender instanceof Player player) {
                if (!this.methods.isInventoryFull(player)) {
                    this.methods.addItemToInventory(player, this.scramblerListener.getScramblers(amount));

                    sender.sendMessage(Messages.GET_SCRAMBLER.getMessage(new HashMap<>() {{
                        put("%Amount%", String.valueOf(amount));
                    }}));
                }
            }

            return;
        }

        if (!this.methods.isInventoryFull(target)) {
            this.methods.addItemToInventory(target, this.scramblerListener.getScramblers(amount));

            Map<String, String> placeholders = new HashMap<>() {{
                put("%Amount%", String.valueOf(amount));
                put("%Player%", target.getName());
            }};

            sender.sendMessage(Messages.GIVE_SCRAMBLER_CRYSTAL.getMessage(placeholders));
            target.sendMessage(Messages.GET_SCRAMBLER.getMessage(placeholders));
        }
    }

    @Command("crystal")
    @Permission(value = "crazyenchantments.crystal", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments crystal <amount> [player]")
    public void crystal(final CommandSender sender, final int amount, @Optional @Nullable final Player target) {
        if (target == null) {
            if (sender instanceof Player player) {
                if (!this.methods.isInventoryFull(player)) {
                    this.methods.addItemToInventory(player, this.protectionCrystalSettings.getCrystal(amount));

                    sender.sendMessage(Messages.GET_PROTECTION_CRYSTAL.getMessage(new HashMap<>() {{
                        put("%Amount%", String.valueOf(amount));
                    }}));
                }
            }

            return;
        }

        if (!this.methods.isInventoryFull(target)) {
            this.methods.addItemToInventory(target, this.protectionCrystalSettings.getCrystal(amount));

            Map<String, String> placeholders = new HashMap<>() {{
                put("%Amount%", String.valueOf(amount));
                put("%Player%", target.getName());
            }};

            sender.sendMessage(Messages.GIVE_PROTECTION_CRYSTAL.getMessage(placeholders));
            target.sendMessage(Messages.GET_PROTECTION_CRYSTAL.getMessage(placeholders));
        }
    }

    @Command("slotcrystal")
    @Permission(value = "crazyenchantments.slotcrystal", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments slotcrystal <amount> [player]")
    public void slotcrystal(final CommandSender sender, final int amount, @Optional @Nullable final Player target) {
        if (target == null) {
            if (sender instanceof Player player) {
                if (!this.methods.isInventoryFull(player)) {
                    final ItemStack itemStack = this.starter.getSlotCrystalListener().getSlotCrystal();

                    itemStack.setAmount(amount);

                    this.methods.addItemToInventory(player, itemStack);

                    sender.sendMessage(Messages.GET_SLOT_CRYSTAL.getMessage(new HashMap<>() {{
                        put("%Amount%", String.valueOf(amount));
                    }}));
                }
            }

            return;
        }

        if (!this.methods.isInventoryFull(target)) {
            final ItemStack itemStack = this.starter.getSlotCrystalListener().getSlotCrystal();

            itemStack.setAmount(amount);

            this.methods.addItemToInventory(target, itemStack);

            Map<String, String> placeholders = new HashMap<>() {{
                put("%Amount%", String.valueOf(amount));
                put("%Player%", target.getName());
            }};

            sender.sendMessage(Messages.GIVE_SLOT_CRYSTAL.getMessage(placeholders));
            target.sendMessage(Messages.GET_SLOT_CRYSTAL.getMessage(placeholders));
        }
    }
}
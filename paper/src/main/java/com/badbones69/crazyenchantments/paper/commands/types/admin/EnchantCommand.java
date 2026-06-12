package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Flag;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class EnchantCommand extends com.badbones69.crazyenchantments.paper.commands.EnchantCommand {

    @Command(value = "add")
    @Permission(value = "crazyenchantments.add", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments add <enchantment> <level>")
    public void add(final Player player, @ArgName("custom_enchantment") @Suggestion("enchantments") final String enchantment, @Suggestion("enchantment_numbers") final int level) {
        final PlayerInventory inventory = player.getInventory();

        final ItemStack itemStack = inventory.getItemInMainHand();

        if (itemStack.isEmpty()) {
            player.sendMessage(Messages.DOESNT_HAVE_ITEM_IN_HAND.getMessage());

            return;
        }

        final Map<String, String> placeholders = new HashMap<>();

        Optional.ofNullable(this.crazyManager.getEnchantmentFromName(enchantment)).ifPresentOrElse(context -> {
            this.crazyManager.addEnchantment(itemStack, context, level);
        }, () -> Optional.ofNullable(this.methods.getEnchantment(enchantment)).ifPresentOrElse(context -> {
            itemStack.addUnsafeEnchantment(context, level);
        }, () -> {
            placeholders.put("%Enchantment%", enchantment);

            player.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage(placeholders));
        }));
    }

    @Command(value = "spawn")
    @Permission(value = "crazyenchantments.spawn", def = PermissionDefault.OP)
    @Flag(flag = "enchantment", suggestion = "custom_enchantments", argument = String.class)
    @Flag(flag = "category", suggestion = "categories", argument = String.class)
    @Flag(flag = "world", suggestion = "worlds", argument = String.class)
    @Flag(flag = "level", suggestion = "numbers", argument = String.class)
    @Flag(flag = "x", argument = Integer.class)
    @Flag(flag = "y", argument = Integer.class)
    @Flag(flag = "z", argument = Integer.class)
    @Syntax("/crazyenchantments spawn [-enchantment/-category] [-world,-level,-x,-y,-z]")
    public void spawn(final CommandSender sender, final Flags flags) {
        if (!flags.hasFlags()) {
            sender.sendRichMessage("<red>You are lacking command flags for this command!");

            return;
        }

        final Location location = sender instanceof Player player ? player.getLocation() : new Location(this.server.getWorlds().getFirst(), 0, 0, 0);

        final AtomicInteger level = new AtomicInteger(1);

        if (flags.hasFlag("w")) {
            flags.getFlagValue("w").ifPresent(name -> {
                final NamespacedKey key = NamespacedKey.fromString(name);

                if (key != null) {
                    final World world = this.server.getWorld(key);

                    if (world != null) {
                        location.setWorld(world);
                    }
                }
            });
        }

        if (flags.hasFlag("x")) {
            flags.getFlagValue("x").flatMap(StringUtils::tryParseInt).ifPresent(context -> location.setX(context.intValue()));
        }

        if (flags.hasFlag("y")) {
            flags.getFlagValue("y").flatMap(StringUtils::tryParseInt).ifPresent(context -> location.setY(context.intValue()));
        }

        if (flags.hasFlag("z")) {
            flags.getFlagValue("z").flatMap(StringUtils::tryParseInt).ifPresent(context -> location.setZ(context.intValue()));
        }

        if (flags.hasFlag("level")) {
            flags.getFlagValue("level").ifPresent(value -> {
                if (value.contains("-")) {
                    level.set(this.methods.getRandomNumber(value));

                    return;
                }

                level.set(StringUtils.tryParseInt(value).orElse(1).intValue());
            });
        }

        final Map<String, String> placeholders = new HashMap<>();

        final World world = location.getWorld();

        final AtomicReference<ItemStack> reference = new AtomicReference<>(ItemStack.empty());

        final AtomicReference<String> enchantReference = new AtomicReference<>();

        if (flags.hasFlag("e")) {
            flags.getFlagValue("e").flatMap(context -> {
                enchantReference.set(context);

                return Optional.ofNullable(this.crazyManager.getEnchantmentFromName(context));
            }).ifPresent(enchantment -> reference.set(new CEBook(enchantment, level.get()).buildBook()));
        } else if (flags.hasFlag("c")) {
            flags.getFlagValue("c").flatMap(context -> {
                enchantReference.set(context);

                return Optional.ofNullable(this.bookSettings.getCategory(context));
            }).ifPresent(category -> reference.set(category.getLostBook().getLostBook(category).build()));
        }

        final ItemStack itemStack = reference.get();

        if (itemStack.isEmpty()) {
            placeholders.put("%Enchantment%", enchantReference.get());

            sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage(placeholders));

            return;
        }

        world.dropItemNaturally(location, itemStack);

        placeholders.put("%World%", location.getWorld().getName());
        placeholders.put("%X%", String.valueOf(location.getBlockX()));
        placeholders.put("%Y%", String.valueOf(location.getBlockY()));
        placeholders.put("%Z%", String.valueOf(location.getBlockZ()));

        sender.sendMessage(Messages.SPAWNED_BOOK.getMessage(placeholders));
    }

    @Command(value = "info")
    @Permission(value = "crazyenchantments.info", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments info <enchantment>")
    public void info(final Player player, @dev.triumphteam.cmd.core.annotations.Optional @Suggestion("enchantments_info") final String enchantment) {
        if (enchantment.isBlank()) {
            MenuManager.openInfoMenu(player);

            return;
        }

        final Map<String, String> placeholders = new HashMap<>();

        Optional.ofNullable(this.methods.getFromName(enchantment)).ifPresentOrElse(context -> MenuManager.openInfoMenu(player, context), () ->
                Optional.ofNullable(this.crazyManager.getEnchantmentFromName(enchantment)).ifPresentOrElse(context -> {
                    player.sendMessage(context.getInfoName());
                    context.getInfoDescription().forEach(player::sendMessage);
                }, () -> {
                    placeholders.put("%Enchantment%", enchantment);

                    player.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage(placeholders));
                }));
    }

    @Command(value = "remove")
    @Permission(value = "crazyenchantments.remove", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments remove <enchantment>")
    public void remove(final Player player, @Suggestion("player_enchantments") final String enchantment) {
        final PlayerInventory inventory = player.getInventory();

        final ItemStack itemStack = inventory.getItemInMainHand();

        if (itemStack.isEmpty()) {
            player.sendMessage(Messages.DOESNT_HAVE_ITEM_IN_HAND.getMessage());

            return;
        }

        final Map<String, String> placeholders = new HashMap<>();

        Optional.ofNullable(this.crazyManager.getEnchantmentFromName(enchantment)).ifPresentOrElse(context -> {
            if (this.bookSettings.getEnchantments(itemStack).containsKey(context)) {
                this.methods.setItemInHand(player, this.bookSettings.removeEnchantment(itemStack, context));

                placeholders.put("%Enchantment%", context.getCustomName());

                player.sendMessage(Messages.REMOVED_ENCHANTMENT.getMessage(placeholders).replace("&", ""));
            }
        }, () -> Optional.ofNullable(this.methods.getEnchantment(enchantment)).ifPresentOrElse(itemStack::removeEnchantment, () -> {
            placeholders.put("%Enchantment%", enchantment);

            player.sendMessage(Messages.DOESNT_HAVE_ENCHANTMENT.getMessage(placeholders));
        }));
    }
}
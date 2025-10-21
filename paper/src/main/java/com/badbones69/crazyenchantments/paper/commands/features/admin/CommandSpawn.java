package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.objects.User;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Flag;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazyenchantments.constants.MessageKeys;
import java.util.HashMap;
import java.util.Optional;

public class CommandSpawn extends BaseCommand {

    @Command("spawn")
    @Permission(value = "crazyenchantments.spawn", def = PermissionDefault.OP)
    @Flag(flag = "l", longFlag = "level", argument = String.class)
    @Flag(flag = "x", argument = int.class)
    @Flag(flag = "y", argument = int.class)
    @Flag(flag = "z", argument = int.class)
    @Flag(flag = "w", longFlag = "world", argument = World.class)
    @Flag(flag = "ce", longFlag = "enchantment", argument = CEnchantment.class)
    @Flag(flag = "cat", longFlag = "category", argument = Category.class)
    @Syntax("/crazyenchantments spawn [-ce/--enchantment] [-c/--category] [-l/--level 1 or 1-5] [-x] [-y] [-z] [-w/--world]")
    public void spawn(final CommandSender sender, final Flags flags) {
        final User user = this.userRegistry.getUser(sender);

        if (!flags.hasFlag("ce") && !flags.hasFlag("cat")) {
            user.sendMessage(MessageKeys.not_an_enchantment);

            return;
        }

        final Location location = sender instanceof Player player ? player.getLocation() : new Location(this.plugin.getServer().getWorlds().getFirst(), 0, 0, 0);

        if (flags.hasFlag("x")) {
            final Optional<Integer> x = flags.getFlagValue("x", int.class);

            x.ifPresent(location::setX);
        }

        if (flags.hasFlag("y")) {
            final Optional<Integer> y = flags.getFlagValue("y", int.class);

            y.ifPresent(location::setY);
        }

        if (flags.hasFlag("z")) {
            final Optional<Integer> z = flags.getFlagValue("z", int.class);

            z.ifPresent(location::setZ);
        }

        if (flags.hasFlag("w")) {
            final Optional<World> world = flags.getFlagValue("w", World.class);

            world.ifPresent(location::setWorld);
        }

        int value = 1;

        if (flags.hasFlag("l")) {
            final Optional<String> level = flags.getFlagValue("l", String.class);

            if (level.isPresent()) {
                final String getter = level.get();

                if (getter.contains("-")) {
                    value = Methods.getRandomNumber(getter);
                } else {
                    value = Integer.parseInt(getter);
                }
            }
        }

        final World world = location.getWorld();
        final int safeValue = value;

        flags.getFlagValue("ce", CEnchantment.class).ifPresent(action -> world.dropItemNaturally(location, new CEBook(action, safeValue).buildBook()));

        flags.getFlagValue("cat", Category.class).ifPresent(action -> world.dropItemNaturally(location, action.getLostBook().getLostBook(action).build()));

        user.sendMessage(MessageKeys.spawned_book, new HashMap<>() {{
            put("{world}", location.getWorld().getName());
            put("{x}", String.valueOf(location.getBlockX()));
            put("{y}", String.valueOf(location.getBlockY()));
            put("{z}", String.valueOf(location.getBlockZ()));
        }});
    }
}
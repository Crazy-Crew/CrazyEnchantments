package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.platform.commands.BaseCommand;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class CommandSpawn extends BaseCommand {

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull CrazyManager crazyManager = this.starter.getCrazyManager();

    private final @NotNull EnchantmentBookSettings settings = this.starter.getEnchantmentBookSettings();

    @Command("spawn")
    @Permission(value = "crazyenchantments.spawn", def = PermissionDefault.OP)
    public void spawn(CommandSender sender, @Suggestion("enchants_categories") String enchantment, String world, @Suggestion("numbers") int level, @Suggestion("numbers") int x, @Suggestion("numbers") int y, @Suggestion("numbers") int z) {
        CEnchantment value = this.crazyManager.getEnchantmentFromName(enchantment);
        Category category = this.settings.getCategory(enchantment);

        if (value == null && category == null) {
            //todo() send not an enchantment message.
            return;
        }

        Location location = sender instanceof Player player ? player.getLocation() : new Location(this.plugin.getServer().getWorld(world), x, y, z);

        location.getWorld().dropItemNaturally(location, category == null ? new CEBook(value, level).buildBook() : category.getLostBook().getLostBook(category).build());

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%World%", location.getWorld().getName());
        placeholders.put("%X%", String.valueOf(location.getBlockX()));
        placeholders.put("%Y%", String.valueOf(location.getBlockY()));
        placeholders.put("%Z%", String.valueOf(location.getBlockZ()));

        sender.sendMessage(Messages.SPAWNED_BOOK.getMessage(placeholders));
    }
}
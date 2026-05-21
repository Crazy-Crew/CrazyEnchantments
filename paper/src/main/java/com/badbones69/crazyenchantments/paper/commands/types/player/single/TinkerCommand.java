package com.badbones69.crazyenchantments.paper.commands.types.player.single;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Flag;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Command(value = "tinkerer", alias = {"tinker"})
@Syntax("/tinkerer -p [player]")
public class TinkerCommand {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CrazyPlatform platform = this.plugin.getPlatform();

    @Command
    @Permission(value = "crazyenchantments.tinker", def = PermissionDefault.TRUE)
    @Flag(flag = "p", longFlag = "player", suggestion = "players")
    public void tinker(@NotNull final CommandSender sender, final Flags flags) {
        if (flags.hasFlag("p") && sender.hasPermission("crazyenchantments.tinker.others")) {
            flags.getFlagValue("p").flatMap(this.platform::getPlayer).ifPresent(MenuManager::openTinkererMenu);

            return;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());

            return;
        }

        MenuManager.openTinkererMenu(player);
    }
}
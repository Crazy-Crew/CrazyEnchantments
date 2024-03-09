package com.badbones69.crazyenchantments.paper.commands.v2;

import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.entity.Player;

@Command("tinkerer")
@Permission("crazyenchantments.tinker")
public class TinkerCommand {

    @Command
    public void tinkerer(Player player) {
        MenuManager.openTinkererMenu(player);
    }
}
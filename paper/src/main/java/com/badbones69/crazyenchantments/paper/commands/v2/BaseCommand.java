package com.badbones69.crazyenchantments.paper.commands.v2;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.listeners.ShopListener;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Command("crazyenchantments")
public abstract class BaseCommand {

    @NotNull
    protected final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final ShopListener listener = this.plugin.getShopListener();

    @Command
    @Permission("crazyenchantments.gui")
    public void gui(Player player) {
        this.listener.openGUI(player);
    }
}
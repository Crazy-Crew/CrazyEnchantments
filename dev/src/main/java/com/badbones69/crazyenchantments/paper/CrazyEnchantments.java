package com.badbones69.crazyenchantments.paper;

import com.badbones69.crazyenchantments.paper.api.builders.types.BlackSmithMenu;
import com.badbones69.crazyenchantments.paper.commands.BlackSmithCommand;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CrazyEnchantments extends JavaPlugin {

    @NotNull
    public static CrazyEnchantments get() {
        return JavaPlugin.getPlugin(CrazyEnchantments.class);
    }

    @NotNull
    private final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this);

    @Override
    public void onEnable() {
        // Register command.
        this.commandManager.registerCommand(new BlackSmithCommand());

        // Register listener.
        getServer().getPluginManager().registerEvents(new BlackSmithMenu.BlackSmithListener(), this);
    }

    @Override
    public void onDisable() {

    }
}
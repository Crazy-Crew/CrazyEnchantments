package com.badbones69.crazyenchantments.paper.commands;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.listeners.ShopListener;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Description;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Command(value = "crazyenchantments", alias = {"ce", "enchanter"})
@Description("The base command for CrazyEnchantments")
public abstract class BaseCommand {

    protected final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    //@NotNull
    //private final ShopListener listener = this.plugin.getShopListener();
}
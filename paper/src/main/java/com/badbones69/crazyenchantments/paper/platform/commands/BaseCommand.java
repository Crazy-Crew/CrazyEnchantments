package com.badbones69.crazyenchantments.paper.platform.commands;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Description;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Command(value = "crazyenchantments", alias = {"ce", "enchanter"})
@Description("The base command for CrazyEnchantments")
public abstract class BaseCommand {

    protected final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

}
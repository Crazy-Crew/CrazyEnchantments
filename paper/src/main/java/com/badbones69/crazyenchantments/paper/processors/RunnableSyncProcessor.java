package com.badbones69.crazyenchantments.paper.processors;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class RunnableSyncProcessor extends Processor<Runnable> {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);
    
    void process(final Runnable process) {
        this.plugin.getServer().getScheduler().runTask(this.plugin, process);
    }
}
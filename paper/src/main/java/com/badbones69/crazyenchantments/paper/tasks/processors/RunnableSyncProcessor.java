package com.badbones69.crazyenchantments.paper.tasks.processors;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class RunnableSyncProcessor extends Processor<Runnable> {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);
    
    void process(final Runnable process) {
        this.plugin.getServer().getGlobalRegionScheduler().run(this.plugin, task -> process.run());
    }
}
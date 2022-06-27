package com.badbones69.crazyenchantments.processors;

import org.bukkit.plugin.Plugin;

public class RunnableSyncProcessor extends Processor<Runnable> {
    
    private final Plugin plugin;
    
    public RunnableSyncProcessor(final Plugin plugin) {
        this.plugin = plugin;
    }
    
    void process(final Runnable process) {
        plugin.getServer().getScheduler().runTask(this.plugin, process);
    }
}
package me.badbones69.crazyenchantments.processors;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class RunnableSyncProcessor extends Processor<Runnable> {
    
    private final Plugin plugin;
    
    public RunnableSyncProcessor(final Plugin plugin) {
        this.plugin = plugin;
    }
    
    void process(final Runnable process) {
        Bukkit.getScheduler().runTask(this.plugin, process);
    }
    
}

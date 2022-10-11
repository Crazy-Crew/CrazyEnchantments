package com.badbones69.crazyenchantments.processors;

import com.badbones69.crazyenchantments.CrazyEnchantments;

public class RunnableSyncProcessor extends Processor<Runnable> {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();
    
    void process(final Runnable process) {
        plugin.getServer().getScheduler().runTask(plugin, process);
    }
}
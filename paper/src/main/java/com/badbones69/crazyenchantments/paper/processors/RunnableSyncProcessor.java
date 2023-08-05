package com.badbones69.crazyenchantments.paper.processors;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;

public class RunnableSyncProcessor extends Processor<Runnable> {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();
    
    void process(final Runnable process) {
        plugin.getServer().getScheduler().runTask(plugin, process);
    }
}
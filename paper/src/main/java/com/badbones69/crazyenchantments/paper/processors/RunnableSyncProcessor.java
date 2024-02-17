package com.badbones69.crazyenchantments.paper.processors;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import org.jetbrains.annotations.NotNull;

public class RunnableSyncProcessor extends Processor<Runnable> {

    @NotNull
    private final CrazyEnchantments plugin = CrazyEnchantments.get();
    
    void process(final Runnable process) {
        this.plugin.getServer().getScheduler().runTask(this.plugin, process);
    }
}
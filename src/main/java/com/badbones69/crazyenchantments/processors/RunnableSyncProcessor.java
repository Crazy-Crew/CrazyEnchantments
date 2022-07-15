package com.badbones69.crazyenchantments.processors;

import com.badbones69.crazyenchantments.api.CrazyManager;

public class RunnableSyncProcessor extends Processor<Runnable> {

    private final CrazyManager crazyManager = CrazyManager.getInstance();
    
    void process(final Runnable process) {
        crazyManager.getPlugin().getServer().getScheduler().runTask(crazyManager.getPlugin(), process);
    }
}
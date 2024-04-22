package com.badbones69.crazyenchantments.paper.tasks.processors;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class PoolProcessor {

    private final CrazyEnchantments plugin =  JavaPlugin.getPlugin(CrazyEnchantments.class);
    private final int maxQueueSize = 10000;
    private ThreadPoolExecutor executor = null;
    private int taskId;

    public PoolProcessor() {
        start();
    }

    public void add(final Runnable process) {
        executor.submit(process);
    }

    public void start() {
        if (executor == null) executor = new ThreadPoolExecutor(1, 10, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(maxQueueSize));
        executor.allowCoreThreadTimeOut(true);
        resizeChecker();
    }

    public void stop() {
        plugin.getServer().getScheduler().cancelTask(taskId);
        executor.shutdown();
        executor = null;
    }

    private void resizeChecker() {
        taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

            if ((executor.getQueue().size() / executor.getCorePoolSize() > maxQueueSize / 5) && !(executor.getMaximumPoolSize() <= executor.getCorePoolSize() + 1)) {
                executor.setCorePoolSize(executor.getCorePoolSize() + 1);
            }

        }, 20, 100);
    }

}

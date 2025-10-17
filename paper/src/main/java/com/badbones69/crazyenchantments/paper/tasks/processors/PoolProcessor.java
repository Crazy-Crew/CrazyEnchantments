package com.badbones69.crazyenchantments.paper.tasks.processors;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.fusion.paper.scheduler.Scheduler;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class PoolProcessor { //todo() nah, I, I don't think we need this.

    private final CrazyEnchantments plugin =  JavaPlugin.getPlugin(CrazyEnchantments.class);

    private ThreadPoolExecutor executor = null;

    private final int maxQueueSize = 10000;

    private ScheduledTask taskId;

    public PoolProcessor() {
        start();
    }

    /**
     * Adds the task into the thread pool to be processed.
     * @param process The {@link Runnable} to process.
     */
    public void add(@NotNull final Runnable process) {
        this.executor.submit(process);
    }

    /**
     * Creates the thread pool used to process tasks.
     */
    public void start() {
        if (this.executor == null) this.executor = new ThreadPoolExecutor(1, 10, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(this.maxQueueSize));

        this.executor.allowCoreThreadTimeOut(true);

        resizeChecker();
    }

    /**
     * Terminates the thread pool.
     */
    public void stop() {
        this.taskId.cancel();

        this.executor.shutdown();

        this.executor = null;
    }

    /**
     * Used to increase the default workers in the thread pool.
     * This should ensure that with a higher player count, that all tasks are processed.
     */
    private void resizeChecker() {
        this.taskId = new FoliaScheduler(this.plugin, Scheduler.async_scheduler, TimeUnit.SECONDS) {
            @Override
            public void run() {
                if ((executor.getQueue().size() / executor.getCorePoolSize() > maxQueueSize / 5) && !(executor.getMaximumPoolSize() <= executor.getCorePoolSize() + 1)) {
                    executor.setCorePoolSize(executor.getCorePoolSize() + 1);
                }
            }
        }.runAtFixedRate(20, 100);
    }
}
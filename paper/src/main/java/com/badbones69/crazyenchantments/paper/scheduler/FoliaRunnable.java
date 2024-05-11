package com.badbones69.crazyenchantments.paper.scheduler;

import io.papermc.paper.threadedregions.scheduler.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public abstract class FoliaRunnable implements Runnable {

    private ScheduledTask task;
    private @Nullable AsyncScheduler asyncScheduler;
    private @Nullable TimeUnit timeUnit;
    private @Nullable EntityScheduler entityScheduler;
    private @Nullable Runnable entityRetired;
    private @Nullable GlobalRegionScheduler globalRegionScheduler;
    private @Nullable RegionScheduler regionScheduler;
    private @Nullable Location location;
    private @Nullable World world;
    private int chunkX;
    private int chunkZ;


    public FoliaRunnable(@NotNull AsyncScheduler scheduler, @Nullable TimeUnit timeUnit) {
        this.asyncScheduler = scheduler;
        this.timeUnit = timeUnit;
    }

    public FoliaRunnable(@NotNull EntityScheduler scheduler, @Nullable Runnable retired) {
        this.entityScheduler = scheduler;
        this.entityRetired = retired;
    }

    public FoliaRunnable(@NotNull GlobalRegionScheduler scheduler) {
        this.globalRegionScheduler = scheduler;
    }

    public FoliaRunnable(@NotNull RegionScheduler scheduler, @Nullable Location location) {
        this.regionScheduler = scheduler;
        this.location = location;
    }

    public FoliaRunnable(@NotNull RegionScheduler scheduler, World world, int chunkX, int chunkZ) {
        this.regionScheduler = scheduler;
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public boolean isCancelled() throws IllegalStateException {
        checkScheduled();
        return task.isCancelled();
    }

    public void cancel() throws IllegalStateException {
        task.cancel();
    }

    @NotNull
    public ScheduledTask run(@NotNull Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        if (this.globalRegionScheduler != null) {
            return setupTask(this.globalRegionScheduler.run(plugin, scheduledTask -> this.run()));
        } else if (this.entityScheduler != null) {
            return setupTask(this.entityScheduler.run(plugin, scheduledTask -> this.run(), entityRetired));
        } else if (this.regionScheduler != null) {
            if (this.location != null) {
                return setupTask(this.regionScheduler.run(plugin, location, scheduledTask -> this.run()));
            } else if (world != null) {
                return setupTask(this.regionScheduler.run(plugin, world, chunkX, chunkZ, scheduledTask -> this.run()));
            } else {
                throw new UnsupportedOperationException("The region type is not supported.");
            }
        } else if (this.asyncScheduler != null){
            return setupTask(this.asyncScheduler.runNow(plugin, scheduledTask -> this.run()));
        } else {
            throw new UnsupportedOperationException("The task type is not supported.");
        }
    }

    /**
     * Schedules this to run after the specified number of server ticks.
     *
     * @param plugin the reference to the plugin scheduling task
     * @param delay the ticks to wait before running the task
     * @return a ScheduledTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException if this was already scheduled
     */
    @NotNull
    public ScheduledTask runDelayed(@NotNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        delay = Math.max(1, delay);
        if (this.globalRegionScheduler != null) {
            return setupTask(this.globalRegionScheduler.runDelayed(plugin, scheduledTask -> this.run(), delay));
        } else if (this.entityScheduler != null) {
            return setupTask(this.entityScheduler.runDelayed(plugin, scheduledTask -> this.run(), entityRetired, delay));
        } else if (this.regionScheduler != null) {
            if (this.location != null) {
                return setupTask(this.regionScheduler.runDelayed(plugin, location, scheduledTask -> this.run(), delay));
            } else if (world != null) {
                return setupTask(this.regionScheduler.runDelayed(plugin, world, chunkX, chunkZ, scheduledTask -> this.run(), delay));
            } else {
                throw new UnsupportedOperationException("The region type is not supported.");
            }
        } else if (this.asyncScheduler != null && this.timeUnit != null) {
            return setupTask(this.asyncScheduler.runDelayed(plugin, scheduledTask -> this.run(), delay, timeUnit));
        } else {
            throw new UnsupportedOperationException("The task type is not supported.");
        }
    }

    /**
     * Schedules this to repeatedly run until cancelled, starting after the
     * specified number of server ticks.
     *
     * @param plugin the reference to the plugin scheduling task
     * @param delay  the ticks to wait before running the task
     * @param period the ticks to wait between runs
     * @return a ScheduledTask that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException    if this was already scheduled
     */
    public ScheduledTask runAtFixedRate(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        delay = Math.max(1, delay);
        period = Math.max(1, period);
        if (this.globalRegionScheduler != null) {
            return setupTask(this.globalRegionScheduler.runAtFixedRate(plugin, scheduledTask -> this.run(), delay, period));
        } else if (this.entityScheduler != null) {
            return setupTask(this.entityScheduler.runAtFixedRate(plugin, scheduledTask -> this.run(), entityRetired, delay, period));
        } else if (this.regionScheduler != null) {
            if (this.location != null) {
                return setupTask(this.regionScheduler.runAtFixedRate(plugin, location, scheduledTask -> this.run(), delay, period));
            } else if (world != null) {
                return setupTask(this.regionScheduler.runAtFixedRate(plugin, world, chunkX, chunkZ, scheduledTask -> this.run(), delay, period));
            } else {
                throw new UnsupportedOperationException("The region type is not supported.");
            }
        } else if (this.asyncScheduler != null && this.timeUnit != null) {
            return setupTask(this.asyncScheduler.runAtFixedRate(plugin, scheduledTask -> this.run(), delay, period, timeUnit));
        } else {
            throw new UnsupportedOperationException("The task type is not supported.");
        }
    }

    /**
     * Gets the task id for this runnable.
     *
     * @return the task id that this runnable was scheduled as
     * @throws IllegalStateException if task was not scheduled yet
     */
    public int getTaskId() throws IllegalStateException {
        checkScheduled();
        return task.hashCode();
    }

    private void checkScheduled() {
        if (task == null) {
            throw new IllegalStateException("Not scheduled yet");
        }
    }

    private void checkNotYetScheduled() {
        if (task != null) {
            throw new IllegalStateException("Already scheduled as " + task.hashCode());
        }
    }

    @NotNull
    private ScheduledTask setupTask(final ScheduledTask task) {
        this.task = task;
        return task;
    }
}
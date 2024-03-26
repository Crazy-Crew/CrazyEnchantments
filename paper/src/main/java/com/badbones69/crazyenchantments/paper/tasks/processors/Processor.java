package com.badbones69.crazyenchantments.paper.tasks.processors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Processor<T> {

    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>(5000);
    private final Thread thread;

    public Processor() {
        this.thread = new Thread(() -> {
            // It might be crucial to make an executor service here.
            while (!Thread.interrupted()) {
                try {
                    T process = this.queue.take();
                    process(process);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public void add(final T process) {
        try {
            this.queue.put(process);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    abstract void process(final T process);

    public void start() {
        this.thread.start();
    }

    public void stop() {
        this.thread.interrupt();
    }
}
package me.badbones69.crazyenchantments.processors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Processor<T> {
    
    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();
    private final Thread thread;
    
    public Processor() {
        thread = new Thread(() -> {
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
            queue.put(process);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    abstract void process(final T process);
    
    public void start() {
        thread.start();
    }
    
    public void stop() {
        thread.interrupt();
    }
    
}
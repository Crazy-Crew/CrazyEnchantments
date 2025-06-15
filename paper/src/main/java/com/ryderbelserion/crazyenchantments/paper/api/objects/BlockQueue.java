package com.ryderbelserion.crazyenchantments.paper.api.objects;

import com.ryderbelserion.crazyenchantments.paper.api.objects.types.CEBlock;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.Queue;

public class BlockQueue {

    private final Queue<CEBlock> queue = new LinkedList<>();

    private int size = 0;

    public void addBlock(@NotNull final Block block, final int distance) {
        this.queue.add(new CEBlock(block, distance));

        this.size++;
    }

    public final Queue<CEBlock> getQueue() {
        return this.queue;
    }

    public final int getSize() {
        return this.size;
    }

    public void purge() {
        this.queue.clear();
    }
}
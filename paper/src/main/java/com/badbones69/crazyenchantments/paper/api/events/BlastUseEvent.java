package com.badbones69.crazyenchantments.paper.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import java.util.List;

public class BlastUseEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private boolean cancelled;
    private final List<Block> blockList;
    
    public BlastUseEvent(Player player, List<Block> blockList) {
        this.player = player;
        this.cancelled = false;
        this.blockList = blockList;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public List<Block> getBlockList() {
        return blockList;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Gets a list of handlers handling this event.
     *
     * @return A list of handlers handling this event.
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
package com.badbones69.crazyenchantments.paper.api.builders.gui;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class InventoryBuilder {

    protected final Player player;

    public InventoryBuilder(@NotNull final Player player) {
        this.player = player;
    }
}
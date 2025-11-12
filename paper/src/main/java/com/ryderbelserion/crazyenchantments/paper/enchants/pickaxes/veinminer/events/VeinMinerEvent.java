package com.ryderbelserion.crazyenchantments.paper.enchants.pickaxes.veinminer.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class VeinMinerEvent extends BlockBreakEvent {

    public VeinMinerEvent(@NotNull final Block block, @NotNull final Player player, final int experience) {
        super(block, player);

        setExpToDrop(experience);
    }
}
package com.ryderbelserion.crazyenchantments.paper.enchants.pickaxes.veinminer.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class VeinMinerEvent extends BlockBreakEvent {

    public VeinMinerEvent(@NotNull final Block block, @NotNull final Player player, final int experience) {
        super(block, player);

        setExpToDrop(experience);

        /*final Collection<ItemStack> drops = block.getDrops(this.itemStack = itemStack, player);

        final ServerLevel world = ((CraftWorld) player.getWorld()).getHandle();
        final Location location = player.getLocation();

        final double x = location.getX();
        final double y = location.getY();
        final double z = location.getZ();

        for (final ItemStack index : drops) {
            if (index.isEmpty()) continue;

            final ItemEntity item = new ItemEntity(world, x, y, z, CraftItemStack.asNMSCopy(index));

            this.drops.add((Item) item.getBukkitEntity());
        }*/
    }

    /*public @NotNull final ArrayList<Item> getDrops() {
        return new ArrayList<>(this.drops.stream().toList());
    }

    public @NotNull final ItemStack getItemStack() {
        return this.itemStack;
    }*/
}
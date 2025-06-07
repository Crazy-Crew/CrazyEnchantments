package com.ryderbelserion.crazyenchantments.paper.listeners;

import com.ryderbelserion.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.fusion.paper.api.scheduler.FoliaScheduler;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class VeinMinerEnchant implements Listener {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private static final List<String> ores = List.of(
            "minecraft:coal_ore",
            "minecraft:deepslate_coal_ore",
            "minecraft:copper_ore",
            "minecraft:deepslate_copper_ore",
            "minecraft:diamond_ore",
            "minecraft:deepslate_diamond_ore",
            "minecraft:emerald_ore",
            "minecraft:deepslate_emerald_ore",
            "minecraft:gold_ore",
            "minecraft:deepslate_gold_ore",
            "minecraft:iron_ore",
            "minecraft:deepslate_iron_ore",
            "minecraft:lapis_ore",
            "minecraft:deepslate_lapis_ore",
            "minecraft:redstone_ore",
            "minecraft:deepslate_redstone_ore",
            "minecraft:nether_gold_ore",
            "minecraft:nether_quartz_ore"
    );

    private static final List<String> tools = List.of(
            "minecraft:wooden_pickaxe",
            "minecraft:stone_pickaxe",
            "minecraft:golden_pickaxe",
            "minecraft:iron_pickaxe",
            "minecraft:diamond_pickaxe",
            "minecraft:netherite_pickaxe"
    );

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isDropItems()) return;

        final Player player = event.getPlayer();

        if (player.isSneaking()) return;

        final Block block = event.getBlock();

        if (event instanceof VeinMinerEvent) { // return early if we called as to not endlessly loop.
            return;
        }

        final ItemStack tool = player.getInventory().getItemInMainHand();
        final String toolType = tool.getType().getKey().asString();

        final String type = block.getType().getKey().asString();

        if (!tools.contains(toolType) || !ores.contains(type)) return;

        final Queue<BlockVein> queue = new LinkedList<>();

        queue.add(new BlockVein(block, 0, getExperience(block, tool)));

        final Set<Block> processed = new HashSet<>();

        final int maxSearch = 100;

        final boolean breakBlock = true;

        while (!queue.isEmpty()) {
            final BlockVein vein = queue.poll();
            final Block veinBlock = vein.block();

            final Material material = veinBlock.getType();

            if (material.isAir() || processed.contains(veinBlock) || !ores.contains(material.getKey().asString())) continue;
            if (processed.size() >= maxSearch) continue;

            int radius = 3;

            if (breakBlock) {
                int delay = 3 * vein.distance;

                new FoliaScheduler(this.plugin, block.getLocation()) {
                    @Override
                    public void run() {
                        destroy(player, tool, vein);
                    }
                }.runDelayed(delay);
            }

            processed.add(veinBlock);

            final World world = veinBlock.getWorld();

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x == 0 && y == 0 && z == 0) {
                            continue;
                        }

                        final Block worldBlock = world.getBlockAt(block.getX() + x, block.getY() + y, block.getZ() + z);

                        queue.add(new BlockVein(worldBlock, vein.distance + 1, getExperience(worldBlock, tool)));
                    }
                }
            }
        }
    }

    public void destroy(final Player player, final ItemStack tool, final BlockVein veinBlock) {
        final Block block = veinBlock.block();

        final VeinMinerEvent event = new VeinMinerEvent(block, player, veinBlock.experience());

        if (!event.callEvent()) return;
        
        block.breakNaturally(tool, true, event.getExpToDrop() > 0);

        // damage the tool if config option is true, adding it later...
        final ItemStack itemStack = player.damageItemStack(tool, 1);

        if (itemStack.isEmpty()) {
            player.playSound(player, Sound.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 0.8F, ThreadLocalRandom.current().nextFloat(0.8F, 1.2F));
        }
    }

    public int getExperience(@NotNull final Block block, @NotNull final ItemStack itemStack) {
        final CraftBlock craftBlock = (CraftBlock) block;

        final BlockState blockState = craftBlock.getNMS();
        final net.minecraft.world.level.block.Block worldBlock = blockState.getBlock();
        final net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        return worldBlock.getExpDrop(blockState, craftBlock.getHandle().getMinecraftWorld(), craftBlock.getPosition(), nmsItem, true);
    }

    public record BlockVein(Block block, int distance, int experience) {}

    static class VeinMinerEvent extends BlockBreakEvent {

        public VeinMinerEvent(@NotNull final Block block, @NotNull final Player player, final int experience) {
            super(block, player);

            setExpToDrop(experience);
        }
    }
}
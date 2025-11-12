package com.ryderbelserion.crazyenchantments.paper.enchants.pickaxes.veinminer;

import com.ryderbelserion.crazyenchantments.paper.CrazyEnchantmentsPlatform;
import com.ryderbelserion.crazyenchantments.paper.CrazyEnchantmentsPlugin;
import com.ryderbelserion.crazyenchantments.paper.api.objects.types.CEBlock;
import com.ryderbelserion.crazyenchantments.paper.api.registry.EnchantmentRegistry;
import com.ryderbelserion.crazyenchantments.paper.enchants.pickaxes.veinminer.events.VeinMinerEvent;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class VeinMinerListener implements Listener {

    private final Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    private final Key key = VeinMinerEnchant.veinminer_key;
    private final Enchantment veinminer = this.registry.get(this.key);

    private final EnchantmentRegistry enchantmentRegistry;
    private final CrazyEnchantmentsPlugin plugin;
    private final boolean isYardWatchEnabled;

    public VeinMinerListener(@NotNull final CrazyEnchantmentsPlugin plugin, @NotNull final EnchantmentRegistry enchantmentRegistry) {
        this.enchantmentRegistry = enchantmentRegistry;

        final CrazyEnchantmentsPlatform platform = plugin.getPlatform();

        this.isYardWatchEnabled = platform.isYardWatchEnabled();
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();

        if (!event.isDropItems() || player.getGameMode() == GameMode.CREATIVE || this.veinminer == null) return;

        final ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();

        if (!tool.containsEnchantment(this.veinminer) || player.isSneaking()) return;

        final VeinMinerEnchant enchant = (VeinMinerEnchant) this.enchantmentRegistry.getEnchantment(this.key);

        if (!enchant.isEnabled()) return;

        if (event instanceof VeinMinerEvent) return;

        final Block initialBlock = event.getBlock();

        if (!enchant.canBreakBlock(this.isYardWatchEnabled, player, initialBlock)) return;

        final String blockType = initialBlock.getType().getKey().asString();

        final List<String> ores = enchant.getOres();

        if (!ores.contains(blockType)) return;

        final CommentedConfigurationNode config = enchant.getConfig();

        final Queue<CEBlock> queue = new LinkedList<>();
        queue.add(new CEBlock(initialBlock, 0).setExperience(getExperience(initialBlock, tool)));

        final Set<Block> processed = new HashSet<>();

        final int currentLevel = tool.getEnchantmentLevel(this.veinminer);

        final boolean isScalingChain = config.node("enchant", "settings", "chain", "scale").getBoolean(false);
        final int scalingChain = config.node("enchant", "settings", "chain", "max").getInt(10);
        final int maxChain = isScalingChain ? scalingChain * currentLevel : scalingChain;

        final boolean isScalingRadius = config.node("enchant", "settings", "search", "scale").getBoolean(false);
        final int radius = config.node("enchant", "settings", "search", "max").getInt(10);
        final int searchRadius = isScalingRadius ? radius * currentLevel : radius;

        final boolean damageItem = config.node("enchant", "settings", "damage-item").getBoolean(false);

        final boolean requiresCorrectTool = config.node("enchant", "settings", "need-correct-tool").getBoolean(false);

        final int delay = config.node("enchant", "settings", "delay").getInt(0);

        while (!queue.isEmpty()) {
            final CEBlock ceBlock = queue.poll();
            final Block block = ceBlock.getBlock();

            final Material material = block.getType();

            if (material.isAir() || !ores.contains(material.getKey().asString())) continue; // if is air, and ores do not contain the material string, continue.
            if (processed.size() >= maxChain || requiresCorrectTool && tool.isEmpty()) break; // if the processed size is greater than max chain, break... or if the tool is empty, break...
            if (!enchant.canBreakBlock(this.isYardWatchEnabled, player, block)) { // prevent blocks player can't break
                processed.add(block); // add to processed, because the player can't break the block.

                continue;
            }

            if (processed.contains(block)) continue; // if processed continue

            new FoliaScheduler(this.plugin, block.getLocation()) {
                @Override
                public void run() {
                    destroy(player, tool, ceBlock, damageItem);
                }
            }.runDelayed((long) delay * ceBlock.getDistance());

            processed.add(block);

            final World world = block.getWorld();

            for (int x = -searchRadius; x <= searchRadius; x++) {
                for (int y = -searchRadius; y <= searchRadius; y++) {
                    for (int z = -searchRadius; z <= searchRadius; z++) {
                        if (queue.size() >= maxChain) break; // break if queue size is greater than the max search
                        if (x == 0 && y == 0 && z == 0) continue; // Skip initial block

                        final Block worldBlock = world.getBlockAt(block.getX() + x, block.getY() + y, block.getZ() + z);
                        final String worldBlockType = worldBlock.getType().getKey().asString();

                        if (!worldBlockType.equals(blockType)) continue; // do not add blocks if the types don't match.

                        queue.add(new CEBlock(worldBlock, ceBlock.getDistance() + 1).setExperience(getExperience(worldBlock, tool)));
                    }
                }
            }
        }
    }

    private void destroy(@NotNull final Player player, @NotNull final ItemStack tool, @NotNull final CEBlock ceBlock, final boolean damageItem) {
        final Block block = ceBlock.getBlock();

        final VeinMinerEvent event = new VeinMinerEvent(block, player, ceBlock.getExperience());

        if (!event.callEvent()) return;

        block.breakNaturally(tool, true, event.getExpToDrop() > 0);

        if (damageItem) {
            final ItemStack itemStack = player.damageItemStack(tool, 1);

            if (itemStack.isEmpty()) {
                player.playSound(player, Sound.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 0.8F, ThreadLocalRandom.current().nextFloat(0.8F, 1.2F));
            }
        }
    }

    private int getExperience(@NotNull final Block block, @NotNull final ItemStack itemStack) {
        final CraftBlock craftBlock = (CraftBlock) block;

        final BlockState blockState = craftBlock.getNMS();
        final net.minecraft.world.level.block.Block worldBlock = blockState.getBlock();
        final net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        return worldBlock.getExpDrop(blockState, craftBlock.getHandle().getMinecraftWorld(), craftBlock.getPosition(), nmsItem, true);
    }
}
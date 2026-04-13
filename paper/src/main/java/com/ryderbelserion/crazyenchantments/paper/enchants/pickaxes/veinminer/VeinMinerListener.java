package com.ryderbelserion.crazyenchantments.paper.enchants.pickaxes.veinminer;

import com.ryderbelserion.crazyenchantments.paper.api.CrazyEnchantmentsPaper;
import com.ryderbelserion.crazyenchantments.paper.CrazyPlugin;
import com.ryderbelserion.crazyenchantments.paper.api.objects.types.CEBlock;
import com.ryderbelserion.crazyenchantments.paper.api.registry.enchants.EnchantmentRegistry;
import com.ryderbelserion.crazyenchantments.paper.enchants.pickaxes.veinminer.events.VeinMinerEvent;
import com.ryderbelserion.fusion.paper.builders.folia.FoliaScheduler;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.*;
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
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class VeinMinerListener implements Listener {

    private final Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    private final Key key = VeinMinerEnchant.veinminer_key;
    private final Enchantment enchantment = this.registry.get(this.key);

    private final EnchantmentRegistry enchantmentRegistry;
    private final boolean isYardWatchEnabled;
    private final CrazyPlugin plugin;

    public VeinMinerListener(@NotNull final CrazyPlugin plugin, @NotNull final EnchantmentRegistry enchantmentRegistry) {
        this.enchantmentRegistry = enchantmentRegistry;

        final CrazyEnchantmentsPaper platform = plugin.getPlatform();

        this.isYardWatchEnabled = platform.isYardWatchEnabled();
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.enchantment == null || !event.isDropItems()) return;

        final Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE || player.isSneaking()) return;

        final ItemStack tool = player.getInventory().getItemInMainHand();

        if (tool.isEmpty()) return;

        if (!tool.containsEnchantment(this.enchantment)) return;

        final VeinMinerEnchant enchant = (VeinMinerEnchant) this.enchantmentRegistry.getEnchantment(this.key);

        if (!enchant.isEnabled()) return;

        final Block index = event.getBlock();

        if (index.isEmpty()) return;

        if (!enchant.canBreakBlock(this.isYardWatchEnabled, player, index) || !enchant.hasOre(index)) return;

        if (event instanceof VeinMinerEvent) return;

        final Queue<CEBlock> queue = new LinkedList<>();

        final CEBlock initial = new CEBlock(index, 0);

        queue.add(initial);

        final Set<UUID> valid = new HashSet<>();

        final int level = tool.getEnchantmentLevel(this.enchantment);

        final int chain = enchant.isScalingChain() ? enchant.getScalingChain() * level : enchant.getScalingChain();
        final int radius = enchant.isScalingRadius() ? enchant.getScaleRadius() * level : enchant.getScaleRadius();

        while (!queue.isEmpty()) {
            final CEBlock block = queue.poll();
            final Block output = block.getBlock();

            final UUID uuid = block.getUuid();

            if (output.isEmpty() || !enchant.hasOre(output)) continue;

            if (valid.size() >= chain || enchant.isRequiresCorrectTool() && tool.isEmpty()) break;

            if (!enchant.canBreakBlock(this.isYardWatchEnabled, player, index)) {
                valid.add(uuid);

                continue;
            }

            if (valid.contains(uuid)) continue;

            new FoliaScheduler(this.plugin, output.getLocation()) {
                @Override
                public void run() {
                    destroy(player, tool, block, enchant.isDamageItem());
                }
            }.runDelayed((long) enchant.getDelay() * block.getDistance());

            valid.add(uuid);

            final World world = output.getWorld();

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (queue.size() >= chain) break; // break if queue size is greater than the max search

                        if (x == 0 && y == 0 && z == 0) continue; // Skip initial block

                        final Block worldBlock = world.getBlockAt(output.getX() + x, output.getY() + y, output.getZ() + z);

                        if (initial.isSimilar(worldBlock)) continue; // do not add blocks if the types don't match.

                        queue.add(new CEBlock(worldBlock, block.getDistance() + 1).setExperience(getExperience(worldBlock, tool)));
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
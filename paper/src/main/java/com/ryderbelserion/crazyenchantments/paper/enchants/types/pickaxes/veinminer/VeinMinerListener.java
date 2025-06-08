package com.ryderbelserion.crazyenchantments.paper.enchants.types.pickaxes.veinminer;

import com.ryderbelserion.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.crazyenchantments.paper.enchants.EnchantmentRegistry;
import com.ryderbelserion.crazyenchantments.paper.enchants.types.pickaxes.veinminer.events.VeinMinerEvent;
import com.ryderbelserion.crazyenchantments.paper.enchants.types.pickaxes.veinminer.objects.BlockVein;
import com.ryderbelserion.fusion.paper.api.scheduler.FoliaScheduler;
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
import org.bukkit.plugin.java.JavaPlugin;
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
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final EnchantmentRegistry enchantmentRegistry = this.plugin.getEnchantmentRegistry();

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
        final String blockType = initialBlock.getType().getKey().asString();

        if (!ores.contains(blockType)) return;

        final CommentedConfigurationNode config = enchant.getConfig();

        final Queue<BlockVein> queue = new LinkedList<>();
        queue.add(new BlockVein(initialBlock, 0, getExperience(initialBlock, tool)));

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
            final BlockVein blockVein = queue.poll();
            final Block block = blockVein.block();

            final Material material = block.getType();

            if (material.isAir() || processed.contains(block) || !ores.contains(material.getKey().asString())) continue;
            if (processed.size() >= maxChain || requiresCorrectTool && tool.isEmpty()) break;

            new FoliaScheduler(this.plugin, block.getLocation()) {
                @Override
                public void run() {
                    destroy(player, tool, blockVein, damageItem);
                }
            }.runDelayed((long) delay * blockVein.distance());

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

                        queue.add(new BlockVein(worldBlock, blockVein.distance() + 1, getExperience(worldBlock, tool)));
                    }
                }
            }
        }
    }

    private void destroy(@NotNull final Player player, @NotNull final ItemStack tool, @NotNull final BlockVein veinBlock, final boolean damageItem) {
        final Block block = veinBlock.block();

        final VeinMinerEvent event = new VeinMinerEvent(block, player, veinBlock.experience());

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